package gujaratcm.anandiben;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.BiographyInfo;
import gujaratcm.anandiben.modellist.BiographyList;


/**
 * Created by Virag Kuvadia on 05-04-2016.
 */


public class BiographyFragment extends BaseFragment {

    //int a
//int b
//    TextView txtTitle, txtDescription;
//    ImageView imgCm;
    ProgressDialog mPd;
    private FloatingActionMenu MainShare;
    WebView webView;
    FloatingActionButton facebook_share, google_share, tweet;


    public static BiographyFragment getInstance() {
        BiographyFragment fragment = new BiographyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.webbrowser, container, false);
        webView = (WebView) v.findViewById(R.id.webview);
//        txtTitle = (TextView) v.findViewById(R.id.txtTitle);
//        imgCm = (ImageView) v.findViewById(R.id.backdrop);
//        txtDescription = (TextView) v.findViewById(R.id.txtDescription);
//        CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle("");

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");


        MainShare = (FloatingActionMenu) v.findViewById(R.id.menu_fab);
        facebook_share = (FloatingActionButton) v.findViewById(R.id.accion_delete1);
        google_share = (FloatingActionButton) v.findViewById(R.id.accion_delete);
        tweet = (FloatingActionButton) v.findViewById(R.id.accion_edit);

        MainShare.setIconAnimated(false);

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

        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare("http://anandibenpatel.com/en/category/quotes/");
            }
        });


        mPd = new ProgressDialog(getActivity());
        mPd.setMessage("Please wait...");
//        collapsingToolbar.setScrimsShown(true);
        loadDataFromServer();

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

    public void loadDataFromServer() {
        mPd.show();
        BiographyList.Instance().LoadTodayQuotesData(new ModelDelegates.ModelDelegate<BiographyInfo>() {
            @Override
            public void ModelLoaded(ArrayList<BiographyInfo> list) {
                mPd.dismiss();
                if (list != null && list.size() > 0) {
                    BindData(list.get(0));
                } else {
                    Utils.Instance().ShowSnack(webView, "Data not found.\n Please check internet connection!");
                    BindData(null);
                }
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                mPd.dismiss();
                Utils.Instance().ShowSnack(webView, error);
                BindData(null);
            }
        });
    }

    public void BindData(BiographyInfo bio) {
        if (bio != null) {
//            webView.loadData(bio.content, "text/html", "UTF-8");
            webView.loadData(bio.content, "text/html; charset=utf-8", "UTF-8");
//            txtTitle.setText(bio.title);
//            txtDescription.setText(Html.fromHtml(bio.content));
//            String url = "http://103.20.104.96/wp-content/uploads/cmprofile-img1.jpg";
//            Picasso.with(getActivity())
//                    .load(url)
//                    .into(imgCm);
        } else {
//            txtTitle.setText("");
//            txtDescription.setText("");
        }
    }

}