package gujaratcm.anandiben;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.crittercism.internal.g;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.SwipeDetector;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.SlidingDrawer;

public class LatestNewsFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    ListView listView;
    ListViewCustomAdapter adapter;
    SwipyRefreshLayout swipeContainer;
    private FloatingActionMenu MainShare;
    SwipeDetector swipeDetector;
    Button btnSearch;
    EditText edtSearch;

    private static final int ID_ADD = 1;
    private static final int ID_ACCEPT = 2;
    private static final int ID_UPLOAD = 3;
    ArrayList<Integer> arr = new ArrayList<>();

    FloatingActionButton facebook_share, google_share, tweet;


    public static LatestNewsFragment getInstance() {
        LatestNewsFragment fragment = new LatestNewsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.latest_news_fragment, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        MainShare.setIconAnimated(false);
        edtSearch = (EditText) v.findViewById(R.id.edtSearch);
        btnSearch = (Button) v.findViewById(R.id.btnTempSearch);
//        swipeDetector = new SwipeDetector();
//        listView.setOnTouchListener(swipeDetector);


        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);


        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/in-the-press/");
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/in-the-press/");
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(getActivity());
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
                    NewsList.Instance().ClearDB();
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
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });


        GetDataFromServer(0);
//        getPosts();
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager frgManager = getFragmentManager();
                        Fragment fragment = new MainFragment();
                        frgManager
                                .beginTransaction()
                                .replace(R.id.frame_container, fragment).commit();
                        return true;
                    } else {
                        edtSearch.clearFocus();
                    }
                }
                return false;
            }
        });

        return v;
    }

    public void search() {
        String str = edtSearch.getText().toString();
        if (str != null && str.length() > 0) {
            showProgress();
            ArrayList<NewsInfo> arr = new ArrayList<NewsInfo>();
            adapter = new ListViewCustomAdapter(getActivity(), arr);
            NewsList.Instance().LoadData(str, new ModelDelegates.ModelDelegate<NewsInfo>() {
                @Override
                public void ModelLoaded(ArrayList<NewsInfo> list) {
                    hideProgress();
                    swipeContainer.setRefreshing(false);
                    BindData(0);
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgress();
                    swipeContainer.setRefreshing(false);
                    ArrayList<NewsInfo> arr = new ArrayList<NewsInfo>();
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
        NewsList.Instance().LoadData(start_from, new ModelDelegates.ModelDelegate<NewsInfo>() {
            @Override
            public void ModelLoaded(ArrayList<NewsInfo> list) {
                swipeContainer.setRefreshing(false);
                BindData(start_from - 1);
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(listView, error);
            }
        });
    }

    public void BindData(int scrollto) {
        ArrayList<NewsInfo> newsInfos = NewsInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0) {
            arr = new ArrayList<>();
            for (int i = 0; i < newsInfos.size(); i++) {
                arr.add(0);
            }
            adapter = new ListViewCustomAdapter(getActivity(), newsInfos);
            listView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                listView.scrollListBy(scrollto);
            }
        } else {
            listView.setAdapter(null);
            Utils.Instance().ShowSnack(listView, "No data found");
        }
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<NewsInfo> m_list;
        int openposition = -1;

        public ListViewCustomAdapter(Activity context, ArrayList<NewsInfo> newsInfos)

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
            RelativeLayout llMain;
            RelativeLayout rlClick;
            ImageView  imgFacebok, imgTwt;
            SlidingDrawer slidingDrawer;
            ImageView handle;

        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            final Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.custom, null);
                hv.name = (TextView) arg1.findViewById(R.id.textView1);
                hv.phone = (TextView) arg1.findViewById(R.id.textView2);
                hv.img1 = (ImageView) arg1.findViewById(R.id.imgLoad);
                hv.llMain = (RelativeLayout) arg1.findViewById(R.id.llMain);
                hv.rlClick = (RelativeLayout) arg1.findViewById(R.id.rlClick);
                hv.handle = (ImageView) arg1.findViewById(R.id.handle);
                hv.slidingDrawer = (SlidingDrawer) arg1.findViewById(R.id.drawer);
                hv.imgFacebok = (ImageView) arg1.findViewById(R.id.imgFacebok);
                hv.imgTwt = (ImageView) arg1.findViewById(R.id.imgTwt);


                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            NewsInfo newsInfo = m_list.get(arg0);
            if (newsInfo.title != null && newsInfo.title.length() > 0) {
                hv.name.setText(newsInfo.title);
            } else {

            }

            if (newsInfo.created_date != null && newsInfo.created_date.length() > 0) {
//                hv.phone.setText(newsInfo.created_date);
                String currentdat = newsInfo.created_date;
                if (currentdat != null && currentdat.length() > 0) {
                    String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                    hv.phone.setText(formated);
                }
            } else {

            }
            hv.handle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hv.slidingDrawer.isOpened()) {
                        hv.slidingDrawer.open();
                    } else {
                        hv.slidingDrawer.close();
                    }
                }
            });

            Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.newsid);
            if (imagesInfos != null && imagesInfos.size() > 0) {

                for (NewsImagesInfo imagesInfo : imagesInfos) {
                    if (imagesInfo.imgWidth < 400) {
                        Picasso.with(getActivity())
                                .load(imagesInfo.imgUrl).transform(new CircleTransform())
                                .into(hv.img1);
                        break;
                    }
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
            }

            hv.imgFacebok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsInfo PrintMediaInfo = m_list.get(arg0);
                    FacebookSharing(getActivity(), PrintMediaInfo.link);
                    hv.slidingDrawer.close();
                }
            });

            hv.imgTwt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsInfo PrintMediaInfo = m_list.get(arg0);
                    DoShare(PrintMediaInfo.link);
                    hv.slidingDrawer.close();
                }
            });
//            arg1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent start = new Intent(getActivity(), LatestNewsDetail.class);
//                    startActivity(start);
//                }
//            });

            hv.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<NewsInfo> newsInfos = NewsInfo.getAll();
                    if (newsInfos != null && newsInfos.size() > 0) {
                        Intent start = new Intent(getActivity(), LatestNewsDetail.class);
                        start.putExtra("id", newsInfos.get(arg0).newsid);
                        startActivity(start);
                    }

                }
            });

//            arg1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent start = new Intent(getActivity(), LatestNewsDetail.class);
//                    startActivity(start);
//                }
//            });


//            arg1.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (MotionEvent.ACTION_UP == event.getAction()) {
//                        ArrayList<NewsInfo> newsInfos = NewsInfo.getAll();
//                        Intent start = new Intent(getActivity(), LatestNewsDetail.class);
//                        start.putExtra("id", newsInfos.get(arg0).newsid);
//                        startActivity(start);
//                    }
//                    return true;
//                }
//            });
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


    //    public class CircleTransform implements Transformation {
//        @Override
//        public Bitmap transform(Bitmap source) {
//            int size = Math.min(source.getWidth(), source.getHeight());
//
//            int x = (source.getWidth() - size) / 2;
//            int y = (source.getHeight() - size) / 2;
//
//            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
//            if (squaredBitmap != source) {
//                source.recycle();
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
//
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            BitmapShader shader = new BitmapShader(squaredBitmap,
//                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
//            paint.setShader(shader);
//            paint.setAntiAlias(true);
//
//            float r = size / 2f;
//            canvas.drawCircle(r, r, r, paint);
//
//            squaredBitmap.recycle();
//            return bitmap;
//        }
//
//        @Override
//        public String key() {
//            return "circle";
//        }
//    }
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int margin = 2;
            int size = Math.min(source.getWidth(), source.getHeight());
            float radius = size / 2f;
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawCircle((source.getWidth() - margin) / 2, (source.getHeight() - margin) / 2, radius - 2, paint);

            if (source != output) {
                source.recycle();
            }

            Paint paint1 = new Paint();
            paint1.setColor(Color.parseColor("#eeeeee"));
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAntiAlias(true);
            paint1.setStrokeWidth(2);
            canvas.drawCircle((source.getWidth() - margin) / 2, (source.getHeight() - margin) / 2, radius - 2, paint1);


            return output;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
            ArrayList<NewsInfo> books = NewsInfo.getAll();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
            boolean isloaded = settings.getBoolean("NEWS_LOADED", false);
            if (!isloaded) {
                swipeContainer.setRefreshing(true);
                GetDataFromServer(books.size());
            } else {
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(listView, "Already Up to Date");
            }
        } else {
            NewsList.Instance().ClearDB();
            swipeContainer.setRefreshing(true);
            GetDataFromServer(0);
        }

    }

}
