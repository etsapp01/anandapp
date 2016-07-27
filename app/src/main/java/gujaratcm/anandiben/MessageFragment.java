package gujaratcm.anandiben;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.MessagesInfo;
import gujaratcm.anandiben.model.SchemesInfo;
import gujaratcm.anandiben.modellist.MessageList;

/**
 * Created by ACER on 01-07-2016.
 */
public class MessageFragment extends BaseFragment {
    ListView listView;
    ListViewCustomAdapter adapter;
    private FloatingActionMenu MainShare;
    FloatingActionButton facebook_share, google_share, tweet;


    public static MessageFragment getInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.message_of_cm, container, false);
        listView = (ListView) v.findViewById(R.id.lstMessages);


        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        MainShare.setIconAnimated(false);


        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/quotes/");
            }
        });

        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(getActivity(), "http://anandibenpatel.com/en/category/quotes/");
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
        LoadQoutesFromServer();
        return v;
    }

    public void LoadQoutesFromServer() {
        showProgress();
        MessageList.Instance().LoadTodayQuotesData(new ModelDelegates.ModelDelegate<MessagesInfo>() {
            @Override
            public void ModelLoaded(ArrayList<MessagesInfo> list) {
                hideProgress();
                ArrayList<MessagesInfo> messagesInfoArrayList = MessagesInfo.getAll();
                BindList(messagesInfoArrayList);

            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                Utils.Instance().ShowSnack(listView, error);
                hideProgress();

            }
        });
    }

    public void BindList(ArrayList<MessagesInfo> messagesInfoArrayList) {
        if (messagesInfoArrayList != null && messagesInfoArrayList.size() > 0) {
            adapter = new ListViewCustomAdapter(getActivity(), messagesInfoArrayList);
            listView.setAdapter(adapter);
        } else {
            Utils.Instance().ShowSnack(listView, "No quotes found.");
        }
    }

    public class ListViewCustomAdapter extends BaseAdapter {
        public Activity context;
        LayoutInflater inflater;
        ArrayList<MessagesInfo> m_list;

        public ListViewCustomAdapter(Activity context, ArrayList<MessagesInfo> list)

        {
            super();
            this.context = context;
            this.m_list = list;
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
            TextView name, txtMessage, txtDate;
            LinearLayout llToday, llYesterDay;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            Holder hv;

            if (arg1 == null) {
                hv = new Holder();
                arg1 = inflater.inflate(R.layout.message_item, null);
                hv.name = (TextView) arg1.findViewById(R.id.txtQuote);
                hv.txtMessage = (TextView) arg1.findViewById(R.id.txtMessage);
                hv.txtDate = (TextView) arg1.findViewById(R.id.txtDate);
                hv.llToday = (LinearLayout) arg1.findViewById(R.id.llToday);
                hv.llYesterDay = (LinearLayout) arg1.findViewById(R.id.llYesterdays);

                arg1.setTag(hv);
            } else {
                hv = (Holder) arg1.getTag();
            }
            final MessagesInfo messagesInfo = m_list.get(arg0);
            if (arg0 == 0) {
                hv.llToday.setVisibility(View.VISIBLE);
                hv.llYesterDay.setVisibility(View.GONE);
                hv.txtMessage.setText(messagesInfo.quote);

            } else {
                hv.llYesterDay.setVisibility(View.VISIBLE);
                hv.llToday.setVisibility(View.GONE);
                hv.name.setText(messagesInfo.quote);
                hv.txtDate.setText(messagesInfo.created_date);

                String currentdat = messagesInfo.created_date;
                if (currentdat != null && currentdat.length() > 0) {
                    String formated = Utils.Instance().ChangeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", currentdat);
                    hv.txtDate.setText(formated);
                }
            }
            arg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent start = new Intent(getActivity(), BalanceDialog.class);
                        start.putExtra("share_text", messagesInfo.quote);
                        startActivity(start);
                    } catch (Exception ex) {

                    }
                }
            });

            return arg1;
        }

    }
}
