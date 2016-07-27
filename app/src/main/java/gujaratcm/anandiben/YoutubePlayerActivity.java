package gujaratcm.anandiben;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.SwipeDetector;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.modellist.YoutubeList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.SlidingDrawer;

public class YoutubePlayerActivity extends BaseFragment {

    ListView listView;
    private FloatingActionMenu MainShare;
    //int a;

    ArrayList<Integer> arr = new ArrayList<>();

    FloatingActionButton facebook_share, google_share, tweet;

    public static LatestNewsFragment getInstance() {
        LatestNewsFragment fragment = new LatestNewsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_youtube_player, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        MainShare.setIconAnimated(false);
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
                    }
                }
                return false;
            }
        });

        showProgress();
        YoutubeList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<YoutubeInfo>() {
            @Override
            public void ModelLoaded(ArrayList<YoutubeInfo> list) {
                hideProgress();
                if (list != null && list.size() > 0) {
                    BindData(0);
                }

            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                hideProgress();

            }
        });

        return v;
    }


    public void BindData(int scrollto) {
        ArrayList<YoutubeInfo> newsInfos = YoutubeInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0) {
            arr = new ArrayList<>();
            for (int i = 0; i < newsInfos.size(); i++) {
                arr.add(0);
            }
            ListViewCustomAdapter adapter = new ListViewCustomAdapter(getActivity(), newsInfos);
            listView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                listView.scrollListBy(scrollto);
            }
        } else {
            listView.setAdapter(null);
            Utils.Instance().ShowSnack(listView, "No data found");
        }
    }


    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<YoutubeInfo> m_list;
        int openposition = -1;

        public ListViewCustomAdapter(Activity context, ArrayList<YoutubeInfo> newsInfos)

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
            ImageView imgFacebok, imgTwt;
            SlidingDrawer slidingDrawer;
            ImageView handle;
            RelativeLayout rlClick;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            final Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.youbute_item, null);
                hv.name = (TextView) arg1.findViewById(R.id.textView1);
                hv.phone = (TextView) arg1.findViewById(R.id.textView2);
                hv.img1 = (ImageView) arg1.findViewById(R.id.imgLoad);
                hv.imgfb = (ImageView) arg1.findViewById(R.id.imgfb);

                hv.imgtwt = (ImageView) arg1.findViewById(R.id.imgtwt);
                hv.imgMsg = (ImageView) arg1.findViewById(R.id.imgMsg);

                hv.llShare = (LinearLayout) arg1.findViewById(R.id.llShare);

                hv.handle = (ImageView) arg1.findViewById(R.id.handle);
                hv.slidingDrawer = (SlidingDrawer) arg1.findViewById(R.id.drawer);
                hv.imgFacebok = (ImageView) arg1.findViewById(R.id.imgFacebok);
                hv.imgTwt = (ImageView) arg1.findViewById(R.id.imgTwt);

                hv.rlClick = (RelativeLayout) arg1.findViewById(R.id.rlClick);


                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            final YoutubeInfo newsInfo = m_list.get(arg0);
            if (newsInfo.title != null && newsInfo.title.length() > 0) {
                hv.name.setText(newsInfo.title);
            } else {

            }


            int check = arr.get(arg0);
            if (check == 1) {
                hv.llShare.setVisibility(View.VISIBLE);
            } else {
                hv.llShare.setVisibility(View.GONE);
            }

            hv.llShare.setVisibility(View.GONE);


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

            hv.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YoutubeInfo PrintMediaInfo = m_list.get(arg0);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + PrintMediaInfo.videoId));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + PrintMediaInfo.videoId));
                        startActivity(intent);
                    }


                }
            });


//            Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
//            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.url_thumbnail);
//            if (imagesInfos != null && imagesInfos.size() > 0) {
//
//                for (NewsImagesInfo imagesInfo : imagesInfos) {
//                    if (imagesInfo.imgWidth < 400) {
//
//                        break;
//                    }
//                }
//            } else {
//                Picasso.with(getActivity()).load(R.drawable.default_image).transform(new CircleTransform()).into(hv.img1);
//            }

            Picasso.with(getActivity())
                    .load(newsInfo.url_thumbnail).transform(new CircleTransform())
                    .into(hv.img1);

            arg1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {

                                            } catch (
                                                    Exception ex
                                                    )

                                            {

                                            }

                                        }
                                    }

            );


            hv.imgFacebok.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {

                                                 }
                                             }

            );

            hv.imgTwt.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {

                                             }
                                         }

            );

            if (openposition >= 0)

            {
                if (openposition != arg0) {
                    hv.slidingDrawer.close();
                }
            }

            hv.slidingDrawer.setOnDrawerOpenListener(new

                                                             OnDrawerOpenListener() {
                                                                 @Override
                                                                 public void onDrawerOpened() {
                                                                     openposition = arg0;
                                                                     notifyDataSetChanged();
                                                                 }
                                                             }

            );


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
