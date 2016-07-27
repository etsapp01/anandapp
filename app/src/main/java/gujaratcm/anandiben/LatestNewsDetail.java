package gujaratcm.anandiben;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.OnSwipeTouchListener;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.modellist.NewsList;

public class LatestNewsDetail extends BaseActivity {
    private FloatingActionMenu MainShare;
    com.github.clans.fab.FloatingActionButton facebook_share, google_share, tweet;
    int newsid;
    TextView txtHeader, txtDetail;
    ImageView backdrop;
    NewsInfo newsInfo;
    NewsDetailInfo detailInfo;
    RelativeLayout rlParent;
    ArrayList<NewsInfo> m_array;
    int position = 0;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_news_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            newsid = b.getInt("id");
            newsInfo = NewsInfo.getNewsById(newsid);
            detailInfo = NewsDetailInfo.getDetailByContainId(newsid);
        }
        m_array = NewsInfo.getAll();
        if (m_array != null) {
            if (newsInfo != null) {
                for (NewsInfo news : m_array) {
                    if (news.newsid == newsid) {
                        break;
                    }
                    position++;
                }
            }
        }
        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtDetail = (TextView) findViewById(R.id.txtDetail);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        rlParent = (RelativeLayout) findViewById(R.id.rlParent);


        MainShare = (FloatingActionMenu) findViewById(R.id.menu_fab);
        facebook_share = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.accion_delete1);
        google_share = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.accion_delete);
        tweet = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.accion_edit);
        MainShare.setIconAnimated(false);

        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsInfo != null)
                    FacebookSharing(LatestNewsDetail.this, newsInfo.link);
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(LatestNewsDetail.this);
            }
        });

        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsInfo != null)
                    DoShare(newsInfo.link);
            }
        });
        if (detailInfo == null) {
            LoadDetailFromServer();
        } else {
            BindData(detailInfo);
        }
    }

    @Override
    protected void previous() {
        if (position > 0) {
            position--;
            newsInfo = m_array.get(position);
            if (newsInfo != null) {
                newsid = newsInfo.newsid;
                detailInfo = NewsDetailInfo.getDetailByContainId(newsid);
                if (detailInfo == null) {
                    LoadDetailFromServer();
                } else {
                    BindData(detailInfo);
                }
            }
        } else {
            Utils.Instance().ShowSnack(txtDetail, "No more news");
        }
    }

    @Override
    protected void next() {
        if (m_array != null && m_array.size() > 0) {
            int total = m_array.size();
            if (position <= (total - 2)) {
                position++;
                newsInfo = m_array.get(position);
                if (newsInfo != null) {
                    newsid = newsInfo.newsid;
                    detailInfo = NewsDetailInfo.getDetailByContainId(newsid);
                    if (detailInfo == null) {
                        LoadDetailFromServer();
                    } else {
                        BindData(detailInfo);
                    }
                }
            } else {
                Utils.Instance().ShowSnack(txtDetail, "No more news");
            }
        }
    }

    public void LoadDetailFromServer() {
        showProgress();
        Utils.Instance().LoadDetailData(newsid, new ModelDelegates.UniversalNewsDetailDelegate() {
            @Override
            public void CallDidSuccess(NewsDetailInfo res) {
                hideProgress();
                if (res != null) {
                    BindData(res);
                } else {
                    Utils.Instance().ShowSnack(txtDetail, "No Detail found");
                }
            }

            @Override
            public void CallFailedWithError(String error) {
                hideProgress();
                Utils.Instance().ShowSnack(txtDetail, "No Detail found");
            }
        });
    }

    public void BindData(NewsDetailInfo detailInfo) {
        ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsid);
        if (imagesInfos != null && imagesInfos.size() > 0) {

            for (NewsImagesInfo imagesInfo : imagesInfos) {
                if (imagesInfo.imgWidth > 400) {
                    Picasso.with(getApplicationContext())
                            .load(imagesInfo.imgUrl)
                            .into(backdrop);
                    break;
                }
            }
            txtHeader.setText(Html.fromHtml(detailInfo.detail));
            txtDetail.setText(Html.fromHtml(detailInfo.more_detail));

            if (newsInfo != null) {
                String currentdat = newsInfo.created_date;
                if (currentdat != null && currentdat.length() > 0) {
                    String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                    collapsingToolbarLayout.setTitle(formated);
                } else {
                    collapsingToolbarLayout.setTitle("Anandiben Patel");
                }
            } else {
                collapsingToolbarLayout.setTitle("Anandiben Patel");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
