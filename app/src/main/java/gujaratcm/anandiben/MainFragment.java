package gujaratcm.anandiben;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
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
import gujaratcm.anandiben.model.MessagesInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.modellist.ImporatantDesionList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;


/**
 * Created by Virag Kuvadia on 05-04-2016.
 */
public class MainFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    private FloatingActionMenu MainShare;
    FloatingActionButton facebook_share, google_share, tweet;
    ListView listView;
    ListViewCustomAdapter adapter;
    //    LinearLayout llInteractWIthCm;
//    TextView txtQuote, txtNewTitleOne, txtNewTitleTwo;
//    ImageView imgNewsImageOne, imgNewsImageTwo;
    SwipyRefreshLayout swipeContainer;

    public static MainFragment getInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_main_fragment, container, false);
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        listView = (ListView) v.findViewById(R.id.lstTopNews);
        MainShare.setIconAnimated(false);
        swipeContainer = (SwipyRefreshLayout) v
                .findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
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

        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/in-the-press/");
            }
        });

//        MainShare.hideMenuButton(false);
//        llInteractWIthCm = (LinearLayout) v.findViewById(R.id.llInteractWIthCm);
//        txtQuote = (TextView) v.findViewById(R.id.txtQuote);
//        txtNewTitleOne = (TextView) v.findViewById(R.id.txtNewTitleOne);
//        txtNewTitleTwo = (TextView) v.findViewById(R.id.txtNewTitleTwo);
//
//        imgNewsImageOne = (ImageView) v.findViewById(R.id.imgNewsImageOne);
//        imgNewsImageTwo = (ImageView) v.findViewById(R.id.imgNewsImageTwo);
//        llInteractWIthCm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new InteractWithCM();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame_container, fragment).commit();
//            }
//        });


        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        AlertOnBack();
                        return true;
                    }
                }
                return false;
            }
        });

        v.post(new Runnable() {
                   @Override
                   public void run() {
                       // code you want to run when view is visible for the first time
//                       BindData();
//                       BindList();
                       showProgress();
                       GetDataFromServer();
                   }
               }
        );
        return v;
    }

    public void AlertOnBack() {
        String message = "Do you want to exit from app?";
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());

        alt_bld.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alt_bld.create();
        alert.setTitle("Alert");
        alert.show();
    }

    //    public void BindData() {
//        MessagesInfo messagesInfo = MessagesInfo.getToday();
//        if (messagesInfo != null) {
//            String sss = messagesInfo.quote;
////            Drawable myIcon1 = getActivity().getResources().getDrawable(R.mipmap.quote_small1);
////            Drawable myIcon2 = getActivity().getResources().getDrawable(R.mipmap.quote_small2);
////            int lineHeight = txtQuote.getLineHeight() + 10;
////
////            myIcon1.setBounds(0, 0, lineHeight, lineHeight);
////            myIcon2.setBounds(0, 0, lineHeight, lineHeight);
////
////
////            Rect bounds = new Rect();
////            Paint paint = new Paint();
////            paint.setTextSize(14);
////            paint.getTextBounds(sss, 0, sss.length(), bounds);
////
////            int width = (int) Math.ceil((float) bounds.width() / 14);
////
////            int line = width / txtQuote.getWidth();
////
////            int totalCharstoFit = txtQuote.getPaint().breakText(sss, 0, sss.length(),
////                    true, txtQuote.getWidth(), null);
////
////            int charctersinlastline = totalCharstoFit - (sss.length() - (totalCharstoFit * 2));
////
////
////            String space = "";
////
////            for (int i = 0; i < (totalCharstoFit - charctersinlastline); i++) {
////                space = space + " ";
////            }
////
////            SpannableStringBuilder builder = new SpannableStringBuilder();
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                builder.append(" ", new ImageSpan(myIcon1), 0)
////                        .append("   " + sss + space)
////                        .append(" ", new ImageSpan(myIcon2), 0);
////            }
//            txtQuote.setText(sss);
//            txtQuote.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Fragment fragment = new MessageFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.frame_container, fragment).commit();
//                }
//            });
//        } else {
//            txtQuote.setText("No quote for today.");
//        }
//
//        final ArrayList<NewsInfo> newsInfos = NewsInfo.getAll();
//        if (newsInfos != null && newsInfos.size() > 0) {
//            txtNewTitleOne.setText(newsInfos.get(0).title);
//            txtNewTitleTwo.setText(newsInfos.get(1).title);
//            for (NewsInfo newsInfo : newsInfos) {
//                ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.postid);
//                if (imagesInfos != null && imagesInfos.size() > 0) {
//                    int pos = 0;
//                    for (NewsImagesInfo imagesInfo : imagesInfos) {
//                        if (imagesInfo.imgWidth < 400) {
//                            if (pos == 0) {
//                                Picasso.with(getActivity())
//                                        .load(imagesInfo.imgUrl)
//                                        .into(imgNewsImageOne);
//                            } else {
//                                Picasso.with(getActivity())
//                                        .load(imagesInfo.imgUrl)
//                                        .into(imgNewsImageTwo);
//                            }
//                            break;
//                        }
//                        pos++;
//                    }
//                } else {
//                    Picasso.with(getActivity()).load(R.drawable.eve2).into(imgNewsImageOne);
//                    Picasso.with(getActivity()).load(R.drawable.eve2).into(imgNewsImageTwo);
//                }
//            }
//            imgNewsImageOne.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent start = new Intent(getActivity(), LatestNewsDetail.class);
//                    start.putExtra("id", newsInfos.get(0).postid);
//                    startActivity(start);
//                }
//            });
//        } else {
//            imgNewsImageOne.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Utils.Instance().ShowSnack(imgNewsImageOne, "No news to show.");
//                }
//            });
//        }
//
//    }

    public void GetDataFromServer() {
        NewsList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<NewsInfo>() {
            @Override
            public void ModelLoaded(ArrayList<NewsInfo> list) {
                swipeContainer.setRefreshing(false);
                hideProgress();
                BindList();
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                swipeContainer.setRefreshing(false);
                Utils.Instance().ShowSnack(listView, error);
                hideProgress();
            }
        });
    }

    public void BindList() {
        ArrayList<NewsInfo> newsInfos = NewsInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0) {
            ArrayList<NewsInfo> m_arr = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                m_arr.add(newsInfos.get(i));
            }
            adapter = new ListViewCustomAdapter(getActivity(), m_arr);
            listView.setAdapter(adapter);
        } else {
            Utils.Instance().ShowSnack(listView, "Something went wrong.\nPlease try again.");
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipeContainer.setRefreshing(true);
        NewsList.Instance().ClearDB();
        GetDataFromServer();
    }

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<NewsInfo> m_list;

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
            TextView txtNewTitle;
            ImageView imgNewsImage;

        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.main_fragment_item, null);
                hv.txtNewTitle = (TextView) arg1.findViewById(R.id.txtNewTitleOne);
                hv.imgNewsImage = (ImageView) arg1.findViewById(R.id.imgNewsImageOne);
                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            NewsInfo newsInfo = m_list.get(arg0);
            if (newsInfo.title != null && newsInfo.title.length() > 0) {
                hv.txtNewTitle.setText(newsInfo.title);
            } else {

            }
            Picasso.with(getActivity()).load(R.drawable.default_image).into(hv.imgNewsImage);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.newsid);
            if (imagesInfos != null && imagesInfos.size() > 0) {
                for (NewsImagesInfo imagesInfo : imagesInfos) {
                    if (imagesInfo.imgWidth > 400) {
                        Picasso.with(getActivity())
                                .load(imagesInfo.imgUrl)
                                .into(hv.imgNewsImage);
                        break;
                    }
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_image).into(hv.imgNewsImage);
            }
            arg1.setOnClickListener(new View.OnClickListener() {
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


}