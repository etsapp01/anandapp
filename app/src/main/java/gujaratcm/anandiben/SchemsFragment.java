package gujaratcm.anandiben;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.ImportantDecisionInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.SchemesInfo;
import gujaratcm.anandiben.modellist.ImporatantDesionList;
import gujaratcm.anandiben.modellist.SchemesList;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayout;
import gujaratcm.anandiben.swiperefresh.SwipyRefreshLayoutDirection;

/**
 * Created by ACER on 01-07-2016.
 */
public class SchemsFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    ListViewCustomAdapter adapter;
    ListView lstSchems;
    private FloatingActionMenu MainShare;
    //    ProgressDialog mPd;
    FloatingActionButton facebook_share, google_share, tweet;
    SwipyRefreshLayout swipeContainer;

    public static SchemsFragment getInstance() {
        SchemsFragment fragment = new SchemsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.scheme_fragment, container, false);
        lstSchems = (ListView) v.findViewById(R.id.lstSchems);
//        mPd = new ProgressDialog(getActivity());
//        mPd.setMessage("Please wait...");
        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
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

        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);


        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/our-commitment/schemes-in-name-of-cm/");
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), " http://anandibenpatel.com/en/category/our-commitment/schemes-in-name-of-cm/");
            }
        });

        google_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSharing(getActivity());
            }
        });

        showProgress();
        GetDataFromServer(0);
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
        return v;
    }

    public void GetDataFromServer(final int start_from) {
        SchemesList.Instance().LoadData(start_from, new ModelDelegates.ModelDelegate<SchemesInfo>() {
            @Override
            public void ModelLoaded(ArrayList<SchemesInfo> list) {
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
                Utils.Instance().ShowSnack(lstSchems, error);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void BindData(int scrollto) {
        ArrayList<SchemesInfo> newsInfos = SchemesInfo.getAll();
        if (newsInfos != null && newsInfos.size() > 0) {
            adapter = new ListViewCustomAdapter(getActivity(), newsInfos);
            lstSchems.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                lstSchems.scrollListBy(scrollto);
            }
        } else {
            lstSchems.setAdapter(null);
            Utils.Instance().ShowSnack(lstSchems, "No data found");
        }
//        if (swipeContainer != null) {
//            swipeContainer.setRefreshing(false);
//        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipeContainer.setRefreshing(true);
        SchemesList.Instance().ClearDB();
        GetDataFromServer(0);
    }

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<SchemesInfo> m_list;

        public ListViewCustomAdapter(Activity context, ArrayList<SchemesInfo> newsInfos)

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
            ImageView img1, imgSahre;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.schems_list_item, null);
                hv.img1 = (ImageView) arg1.findViewById(R.id.imgLoad);
                hv.imgSahre = (ImageView) arg1.findViewById(R.id.imgSahre);


                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            final SchemesInfo newsInfo = m_list.get(arg0);
            Picasso.with(getActivity()).load(R.drawable.eve2).into(hv.img1);
            ArrayList<NewsImagesInfo> imagesInfos = NewsImagesInfo.getImagesByNewsId(newsInfo.schemeid);
            if (imagesInfos != null && imagesInfos.size() > 0) {

                for (NewsImagesInfo imagesInfo : imagesInfos) {
                    if (imagesInfo.imgWidth > 400) {
                        Picasso.with(getActivity())
                                .load(imagesInfo.imgUrl)
                                .into(hv.img1);
                        break;
                    }
                }
                hv.img1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) hv.img1.getLayoutParams();
                param.height = 250;
                hv.img1.setLayoutParams(param);
                Picasso.with(getActivity()).load(R.drawable.eve2).into(hv.img1);
            }
            hv.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = newsInfo.link;
                        if (url != null && !url.isEmpty()) {
                            Intent start = new Intent(getActivity(), ImportantDecisionDetailActivity.class);
                            start.putExtra("id", newsInfo.schemeid);
                            startActivity(start);
                        }
                    } catch (Exception ex) {

                    }
                }
            });

            hv.imgSahre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SchemesInfo newsInfo = m_list.get(arg0);
                        Intent start = new Intent(getActivity(), BalanceDialog.class);
                        start.putExtra("share_text", newsInfo.link);
                        startActivity(start);
                    } catch (Exception ex) {

                    }
                }
            });


            return arg1;
        }

    }
}