package gujaratcm.anandiben;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.BlogInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.modellist.BlogList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;

/**
 * Created by ACER on 20-07-2016.
 */
public class BlogFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView gvdata;
    LinearLayoutManager mLayoutManager;
    LatestlistAdapter adapter;
    SwipyRefreshLayout swipeContainer;

    private FloatingActionMenu MainShare;

    FloatingActionButton facebook_share, google_share, tweet;

    public static BlogFragment getInstance() {
        BlogFragment fragment = new BlogFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.blog_fragment, container, false);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        MainShare.setIconAnimated(false);
        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/blog/");
            }
        });
        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/blog/");
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gvdata = (RecyclerView) view.findViewById(R.id.gvdata);
        swipeContainer = (SwipyRefreshLayout) view
                .findViewById(R.id.swipeContainer);
//        final GridLayoutManager manager = new GridLayoutManager(getActivity(), nocols);
        mLayoutManager = new LinearLayoutManager(getActivity());
        gvdata.setLayoutManager(mLayoutManager);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(this);
        showProgress();
        GetDataFromServer(0);
    }

    public void BindData(int scrollto) {

        ArrayList<BlogInfo> newsInfos = BlogInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0 && gvdata != null) {
            adapter = new LatestlistAdapter(getActivity(), newsInfos);
            gvdata.setAdapter(adapter);
            gvdata.scrollToPosition(scrollto);
        } else {
            gvdata.setAdapter(null);
            Utils.Instance().ShowSnack(gvdata, "No data found");
        }
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }


    public class LatestlistAdapter extends RecyclerView.Adapter<LatestlistAdapter.ViewHolder> {
        //    private List<BooksObject> objects = new ArrayList<BooksObject>();
        ArrayList<BlogInfo> objects = new ArrayList<BlogInfo>();
        private Context context;
        private int lastPosition = -1;

        public LatestlistAdapter(Context context, ArrayList<BlogInfo> lists) {
            this.context = context;
            this.objects = lists;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LatestlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.blog_list_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            final BlogInfo youtubeInfo = objects.get(position);
//            viewHolder.imgCoverPage.setImageResource(obj);
            viewHolder.txtTitle.setText(youtubeInfo.title);

            String currentdat = youtubeInfo.created_date;
            if (currentdat != null && currentdat.length() > 0) {
                String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                viewHolder.txtDate.setText(formated);
            }

            viewHolder.llmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (youtubeInfo != null) {
                        Intent start = new Intent(getActivity(), BlogDetailActivity.class);
                        start.putExtra("id", youtubeInfo.blogid);
                        startActivity(start);
                    }
                }
            });
            viewHolder.imgSahre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (youtubeInfo != null) {
                        try {
                            Intent start = new Intent(getActivity(), BalanceDialog.class);
                            start.putExtra("share_text", youtubeInfo.link);
                            startActivity(start);
                        } catch (Exception ex) {

                        }
                    }
                }
            });

            Picasso.with(getActivity()).load(R.drawable.default_image).into(viewHolder.imgCoverPage);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(youtubeInfo.blogid);
            if (imagesInfos != null && imagesInfos.size() > 0) {
                for (NewsImagesInfo imagesInfo : imagesInfos) {
                    if (imagesInfo.imgWidth > 400) {
                        Picasso.with(getActivity())
                                .load(imagesInfo.imgUrl)
                                .into(viewHolder.imgCoverPage);
                        break;
                    }
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_image).into(viewHolder.imgCoverPage);
            }
            setAnimation(viewHolder.itemView, position);
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

//        public SimpleDraweeView ivbook;

            ImageView imgCoverPage, imgSahre;
            TextView txtTitle, txtDate;
            RelativeLayout llmain;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                //ivbook = (SimpleDraweeView) itemLayoutView.findViewById(R.id.ivbook);
                imgCoverPage = (ImageView) itemLayoutView.findViewById(R.id.imgCoverPage);
                imgSahre = (ImageView) itemLayoutView.findViewById(R.id.imgSahre);
                txtTitle = (TextView) itemLayoutView.findViewById(R.id.txtTitle);
                txtDate = (TextView) itemLayoutView.findViewById(R.id.txtDate);
                llmain = (RelativeLayout) itemLayoutView.findViewById(R.id.llmain);

//                int hei = (int) (imagewidth * 1.3);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imagewidth, hei);
//                imgCoverPage.setLayoutParams(layoutParams);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.hint_slide_up);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return objects.size();
        }
    }

    public void GetDataFromServer(final int start_from) {
        BlogList.Instance().LoadData(start_from, new ModelDelegates.ModelDelegate<BlogInfo>() {
            @Override
            public void ModelLoaded(ArrayList<BlogInfo> list) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                BindData(start_from - 1);
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(gvdata, error);
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
            ArrayList<BlogInfo> books = BlogInfo.getAll();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
            boolean isloaded = settings.getBoolean("BLOG_LOADED", false);
            if (!isloaded) {
                swipeContainer.setRefreshing(true);
                GetDataFromServer(books.size());
            } else {
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(gvdata, "Already Up to Date");
            }
        } else {
            NewsList.Instance().ClearDB();
            swipeContainer.setRefreshing(true);
            GetDataFromServer(0);
        }

    }
}
