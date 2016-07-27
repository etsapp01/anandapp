package gujaratcm.anandiben;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.SwipeDetector;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.modellist.ImporatantDesionList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.modellist.PrintMediaList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;
import hollowsoft.slidingdrawer.SlidingDrawer;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;

/**
 * Created by ACER on 02-07-2016.
 */
public class PrintMediaFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    ListView listView;
    ListViewCustomAdapter adapter;
    private FloatingActionMenu MainShare;
    SwipeDetector swipeDetector;
    SwipyRefreshLayout swipeContainer;
    private static final int ID_ADD = 1;
    private static final int ID_ACCEPT = 2;
    private static final int ID_UPLOAD = 3;
    FloatingActionButton facebook_share, google_share, tweet;


    ArrayList<Integer> arr = new ArrayList<>();

    Button btnSearch;
    EditText edtSearch;

    public static PrintMediaFragment getInstance() {
        PrintMediaFragment fragment = new PrintMediaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.print_media, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        MainShare.setIconAnimated(false);
        edtSearch = (EditText) v.findViewById(R.id.edtSearch);
        btnSearch = (Button) v.findViewById(R.id.btnTempSearch);


        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/cm-interviews/print-media/");
            }
        });

        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/cm-interviews/print-media/");
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(getActivity());
            }
        });


        arr = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arr.add(0);
        }

        showProgress();
        GetDataFromServer(0);
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
                    PrintMediaList.Instance().ClearDB();
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
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        return v;
    }


    public void search() {
        String str = edtSearch.getText().toString();
        if (str != null && str.length() > 0) {
            showProgress();
            ArrayList<PrintMediaInfo> arr = new ArrayList<PrintMediaInfo>();
            adapter = new ListViewCustomAdapter(getActivity(), arr);
            PrintMediaList.Instance().LoadData(str, new ModelDelegates.ModelDelegate<PrintMediaInfo>() {
                @Override
                public void ModelLoaded(ArrayList<PrintMediaInfo> list) {
                    hideProgress();
                    swipeContainer.setRefreshing(false);
                    BindData(0);
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                    hideProgress();
                    swipeContainer.setRefreshing(false);
                    ArrayList<PrintMediaInfo> arr = new ArrayList<PrintMediaInfo>();
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
        PrintMediaList.Instance().LoadData(start_from, new ModelDelegates.ModelDelegate<PrintMediaInfo>() {
            @Override
            public void ModelLoaded(ArrayList<PrintMediaInfo> list) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                BindData(start_from - 1);
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(listView, error);
            }
        });
    }

    public void BindData(int scrollto) {
        ArrayList<PrintMediaInfo> newsInfos = PrintMediaInfo.getAll();
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

    int openposition = -1;

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        int openposition = -1;
        ArrayList<PrintMediaInfo> m_list;

        public ListViewCustomAdapter(Activity context, ArrayList<PrintMediaInfo> newsInfos)

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
            ImageView img1, imgfb, imgtwt, imgMsg;
            LinearLayout llShare;
            RelativeLayout rlClick;
            RelativeLayout llMain;

            ImageView imgFacebok, imgTwt;
            SlidingDrawer slidingDrawer;
            ImageView handle;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            final Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.print_media_item, null);
                hv.name = (TextView) arg1.findViewById(R.id.textView1);
                hv.phone = (TextView) arg1.findViewById(R.id.textView2);
                hv.img1 = (ImageView) arg1.findViewById(R.id.imgLoad);
                hv.llMain = (RelativeLayout) arg1.findViewById(R.id.llMain);
                hv.llShare = (LinearLayout) arg1.findViewById(R.id.llShare);
                hv.imgfb = (ImageView) arg1.findViewById(R.id.imgfb);
                hv.rlClick = (RelativeLayout) arg1.findViewById(R.id.rlClick);
                hv.imgtwt = (ImageView) arg1.findViewById(R.id.imgtwt);
                hv.imgMsg = (ImageView) arg1.findViewById(R.id.imgMsg);


                hv.handle = (ImageView) arg1.findViewById(R.id.handle);
                hv.slidingDrawer = (SlidingDrawer) arg1.findViewById(R.id.drawer);
                hv.imgFacebok = (ImageView) arg1.findViewById(R.id.imgFacebok);
                hv.imgTwt = (ImageView) arg1.findViewById(R.id.imgTwt);


                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            hv.rlClick.setOnTouchListener(swipeDetector);
            final PrintMediaInfo PrintMediaInfo = m_list.get(arg0);
            if (PrintMediaInfo.title != null && PrintMediaInfo.title.length() > 0) {
                hv.name.setText(PrintMediaInfo.title);
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


            if (PrintMediaInfo.created_date != null && PrintMediaInfo.created_date.length() > 0) {
//                hv.phone.setText(PrintMediaInfo.created_date);
                String currentdat = PrintMediaInfo.created_date;
                if (currentdat != null && currentdat.length() > 0) {
                    String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                    hv.phone.setText(formated);
                }
            } else {

            }
            int check = arr.get(arg0);
            if (check == 1) {
                hv.llShare.setVisibility(View.VISIBLE);
            } else {
                hv.llShare.setVisibility(View.GONE);
            }

            hv.imgfb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            hv.imgMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintMediaInfo PrintMediaInfo = m_list.get(arg0);
                    DoEmail(PrintMediaInfo.link);
                }
            });

            hv.imgtwt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });


            hv.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PrintMediaInfo Info = m_list.get(arg0);
                    if (Info != null) {
                        Intent start = new Intent(getActivity(), PrintMediaDetailActivity.class);
                        start.putExtra("id", Info.newsid);
                        startActivity(start);
                    }

                }
            });


            Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(PrintMediaInfo.newsid);
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

            hv.imgFacebok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PrintMediaInfo PrintMediaInfo = m_list.get(arg0);
                    FacebookSharing(getActivity(), PrintMediaInfo.link);
                    hv.slidingDrawer.close();

                }
            });

            hv.imgTwt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintMediaInfo PrintMediaInfo = m_list.get(arg0);
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

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
            ArrayList<PrintMediaInfo> books = PrintMediaInfo.getAll();
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
            PrintMediaList.Instance().ClearDB();
            swipeContainer.setRefreshing(true);
            GetDataFromServer(0);
        }
    }


}
