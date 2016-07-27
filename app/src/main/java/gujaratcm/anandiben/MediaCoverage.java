package gujaratcm.anandiben;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.ImportantDecisionInfo;
import gujaratcm.anandiben.model.MediaCoverageInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.modellist.ImporatantDesionList;
import gujaratcm.anandiben.modellist.MediaList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.SlidingDrawer;

/**
 * Created by ACER on 01-07-2016.
 */
public class MediaCoverage extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    ListView listView;
    ListViewCustomAdapter adapter;
    private FloatingActionMenu MainShare;
    //    ProgressDialog mPd;
    EditText edtSearch;
    Button btnSearch;
    FloatingActionButton facebook_share, google_share, tweet;
    SwipyRefreshLayout swipeContainer;

    public static MediaCoverage getInstance() {
        MediaCoverage fragment = new MediaCoverage();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.media_coverage, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        btnSearch = (Button) v.findViewById(R.id.btnSearches);
        edtSearch = (EditText) v.findViewById(R.id.edtSearch);
        MainShare.setIconAnimated(false);
        MainShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Share this post on your timeline.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
//        mPd = new ProgressDialog(getActivity());
//        mPd.setMessage("Please wait...");
//        mPd.show();
        showProgress();
        GetDataFromServer(0);


        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);


        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/media-coverage/");
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/media-coverage/");
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(getActivity());
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    MainShare.setVisibility(View.GONE);
                } else {
                    MainShare.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    MainShare.setVisibility(View.GONE);
                } else {
                    MainShare.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    MainShare.setVisibility(View.GONE);
                } else {
                    MediaList.Instance().ClearDB();
//                    mPd.show();
                    showProgress();
                    GetDataFromServer(0);
                    MainShare.setVisibility(View.VISIBLE);
                }
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    public void search() {
//        mPd.show();
        showProgress();
        String str = edtSearch.getText().toString();
        if (str != null && str.length() > 0) {
            ArrayList<MediaCoverageInfo> arr = new ArrayList<MediaCoverageInfo>();
            adapter = new ListViewCustomAdapter(getActivity(), arr);
            listView.setAdapter(adapter);
            MediaList.Instance().LoadData(str, new ModelDelegates.ModelDelegate<MediaCoverageInfo>() {
                @Override
                public void ModelLoaded(ArrayList<MediaCoverageInfo> list) {
//                    mPd.dismiss();
                    hideProgress();
                    BindData(0);

                }

                @Override
                public void ModelLoadFailedWithError(String error) {
//                    mPd.dismiss();
                    hideProgress();
                    ArrayList<MediaCoverageInfo> arr = new ArrayList<MediaCoverageInfo>();
                    adapter = new ListViewCustomAdapter(getActivity(), arr);
                    listView.setAdapter(adapter);
                    Utils.Instance().ShowSnack(listView, "No Data Found");
//                            Utils.Instance().ShowSnack(listView, error);
                }
            });
        } else {
            Utils.Instance().ShowSnack(listView, "Please enter search keyword");
        }
    }

    public void GetDataFromServer(final int start_from) {
        MediaList.Instance().LoadData(start_from, new ModelDelegates.ModelDelegate<MediaCoverageInfo>() {
            @Override
            public void ModelLoaded(ArrayList<MediaCoverageInfo> list) {
//                swipeContainer.setRefreshing(false);
//                mPd.dismiss();
                hideProgress();
                BindData(start_from - 1);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
//                swipeContainer.setRefreshing(false);
//                mPd.dismiss();
                hideProgress();
                Utils.Instance().ShowSnack(listView, error);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void BindData(int scrollto) {
        ArrayList<MediaCoverageInfo> newsInfos = MediaCoverageInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0) {
            adapter = new ListViewCustomAdapter(getActivity(), newsInfos);
            listView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                listView.scrollListBy(scrollto);
            }
        } else {
            listView.setAdapter(null);
            Utils.Instance().ShowSnack(listView, "No data found");
        }
//        if (swipeContainer != null) {
//            swipeContainer.setRefreshing(false);
//        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipeContainer.setRefreshing(true);
        MediaList.Instance().ClearDB();
        GetDataFromServer(0);
    }

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<MediaCoverageInfo> m_list;
        int openposition = -1;
        public ListViewCustomAdapter(Activity context, ArrayList<MediaCoverageInfo> newsInfos)

        {
            super();
            this.context = context;
            this.m_list = newsInfos;
            this.inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return m_list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return m_list.get(arg0).hashCode();
        }

        public class Holder {
            TextView name;
            TextView phone;
            ImageView img1;
            ImageView imgFacebok, imgTwt;
            SlidingDrawer slidingDrawer;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            final Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.media_coverage_item, null);
                hv.name = (TextView) arg1.findViewById(R.id.textView1);
                hv.phone = (TextView) arg1.findViewById(R.id.textView2);
                hv.img1 = (ImageView) arg1.findViewById(R.id.imgLoad);
                hv.slidingDrawer = (SlidingDrawer) arg1.findViewById(R.id.drawer);
                hv.imgFacebok = (ImageView) arg1.findViewById(R.id.imgFacebok);
                hv.imgTwt = (ImageView) arg1.findViewById(R.id.imgTwt);

                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            final MediaCoverageInfo newsInfo = m_list.get(arg0);
            if (newsInfo.title != null && newsInfo.title.length() > 0) {
                hv.name.setText(newsInfo.title);
            } else {

            }

            if (newsInfo.created_date != null && newsInfo.created_date.length() > 0) {
                String currentdat = newsInfo.created_date;
                if (currentdat != null && currentdat.length() > 0) {
                    String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                    hv.phone.setText(formated);
                }
//                hv.phone.setText(newsInfo.created_date);

            } else {

            }
            Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.mediaid);
            if (imagesInfos != null && imagesInfos.size() > 0) {

                for (NewsImagesInfo imagesInfo : imagesInfos) {
                    if (imagesInfo.imgWidth < 400) {
                        Picasso.with(getActivity())
                                .load(imagesInfo.imgUrl)
                                .into(hv.img1);
                        break;
                    }
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_image).into(hv.img1);
            }

            arg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent start = new Intent(getActivity(), MediaCoverageDetailActivity.class);
                        start.putExtra("id", newsInfo.mediaid);
                        startActivity(start);
                    } catch (Exception ex) {

                    }
                }
            });
            hv.imgFacebok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaCoverageInfo mediaCoverageInfo = m_list.get(arg0);
                    FacebookSharing(getActivity(), mediaCoverageInfo.link);
                    hv.slidingDrawer.close();
                }
            });

            hv.imgTwt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaCoverageInfo mediaCoverageInfo = m_list.get(arg0);
                    DoShare(mediaCoverageInfo.link);
                    hv.slidingDrawer.close();
                }
            });
            if (openposition >= 0) {
                if (openposition != arg0) {
                    hv.slidingDrawer.close();
                }
            }
            hv.slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
                @Override
                public void onDrawerOpened() {
                    openposition = arg0;
                    notifyDataSetChanged();
                }
            });
            return arg1;
        }

    }


    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

//    @Override
//    public void onRefresh(SwipyRefreshLayoutDirection direction) {
//        ArrayList<NewsInfo> books = NewsInfo.getAll();
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
//        boolean isloaded = settings.getBoolean("NEWS_LOADED", false);
//        if (!isloaded) {
//            swipeContainer.setRefreshing(true);
//            GetDataFromServer(books.size());
//        } else {
//            swipeContainer.setRefreshing(false);
//            Utils.Instance().ShowSnack(listView, "Already Up to Date");
//        }
//
//    }

}