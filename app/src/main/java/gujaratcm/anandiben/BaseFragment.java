package gujaratcm.anandiben;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.params.Face;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.ModelMapHelper;
import gujaratcm.anandiben.common.ModelMapper;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.model.FacebookPostInfo;
import io.fabric.sdk.android.Fabric;


public class BaseFragment extends android.support.v4.app.Fragment {
    ProgressDialog m_pd = null;
    ShareDialog shareDialog;
    private static final String TWITTER_KEY = "MAUOG9caQAjgWZYR24cpPvq8L";
    private static final String TWITTER_SECRET = "Bo1pbFzIx4Bdv2qbCfsRdMJWiN1y6fvvvoj2WqJtnfq67ARK6M";
    AccessTokenTracker mAccessTokenTracker = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_pd = new ProgressDialog(getActivity());
        m_pd.setMessage(Html.fromHtml("Please wait..."));
        m_pd.setCancelable(false);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));
    }

    public void showProgress() {
        if (m_pd != null) {
            m_pd.show();
        }
    }

    public void hideProgress() {
        if (m_pd != null) {
            m_pd.dismiss();
        }
    }


    //hiren fb id=10205201868245284
    public void getFacebookPosts(final ModelDelegates.FacebookPostDelegate delegate) {
        FacebookSdk.sdkInitialize(getActivity());

        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                    } else {
                        if (NetworkConnectivity.isConnected()) {
                            final Bundle params = new Bundle();
                            params.putString("fields", "message,created_time,id,full_picture,status_type,source,comments.summary(true),likes.summary(true)");
//        params.putString("limit", "10");
                            new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + "570916289588054" + "/posts", params, HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                        /* handle the result */
                                            ArrayList<FacebookPostInfo> m_list = new ArrayList<FacebookPostInfo>();
                                            try {
                                                JSONObject jObjResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                                                if (jObjResponse != null && jObjResponse.has("data")) {
                                                    JSONArray dataarr = jObjResponse.optJSONArray("data");
                                                    if (dataarr != null && dataarr.length() > 0) {
                                                        FacebookPostInfo.ClearDb();
                                                        for (int i = 0; i < dataarr.length(); i++) {
                                                            JSONObject object = dataarr.optJSONObject(i);
                                                            ModelMapHelper<FacebookPostInfo> mapper = new ModelMapHelper<FacebookPostInfo>();
                                                            FacebookPostInfo postInfo = mapper.getObject(FacebookPostInfo.class, object);
                                                            if (postInfo != null) {

                                                                JSONObject likes = object.optJSONObject("likes");
                                                                if (likes != null) {
                                                                    postInfo.likecount = likes.optJSONObject("summary").optInt("total_count");
                                                                }

                                                                JSONObject comments = object.optJSONObject("comments");
                                                                if (comments != null) {
                                                                    postInfo.commentcount = comments.optJSONObject("summary").optInt("total_count");
                                                                }
                                                                postInfo.save();
                                                                m_list.add(postInfo);
                                                            }
                                                        }
                                                    } else {
                                                        if (delegate != null)
                                                            delegate.OnFail("Social messages not found.");
                                                    }
                                                } else {
                                                    if (delegate != null)
                                                        delegate.OnFail("Social messages not found.");
                                                }
                                            } catch (Exception e) {
                                                if (delegate != null)
                                                    delegate.OnFail("Social messages not found.");
                                                e.printStackTrace();
                                            }
                                            if (delegate != null)
                                                delegate.OnGetPost(m_list);
                                        }
                                    }
                            ).executeAsync();
                        } else {
                            ArrayList<FacebookPostInfo> m_list = FacebookPostInfo.getAll();
                            if (m_list != null && m_list.size() > 0) {
                                if (delegate != null)
                                    delegate.OnGetPost(m_list);
                            } else {
                                if (delegate != null)
                                    delegate.OnFail("Please check your internet connection");
                            }

                        }
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        } else {
            LoginFB("POSTS", "", delegate);
        }
    }

    public void FacebookSharing(Context con, final String url) {
        FacebookSdk.sdkInitialize(getActivity());
        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                    } else {
                        shareDialog = new ShareDialog(getActivity());

                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Anandiben Patel")
                                    .setImageUrl(Uri.parse("https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-xtl1/v/t1.0-9/13533041_1225662550780088_5871760602148577415_n.png?oh=370396ec72890c9fa3863784f15f3b3b&oe=5825CFA1&__gda__=1479806866_64ac591d0d772ee49bfd0bb5a498f7b3"))
                                    .setContentDescription(
                                            "I am sharing this using Anandiben Patel Application")
                                    .setContentUrl(Uri.parse(url))
                                    .build();

                            shareDialog.show(linkContent);  // Show facebook ShareDialog
                        }
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        } else {
            LoginFB("SHARE", url, null);
        }

    }

    public void GoogleSharing(Context con) {
        Intent shareIntent = new PlusShare.Builder(con)
                .setType("text/plain")
                .setText("I am sharing this using Anandiben Patel Application")
                .setContentUrl(Uri.parse("http://anandibenpatel.com/wp-content/uploads/457.jpg"))
                .getIntent();
        startActivityForResult(shareIntent, 0);
    }

    public void DoShare(String text) {

        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        if (session != null) {
            final Intent intent = new ComposerActivity.Builder(getActivity())
                    .session(session)
                    .createIntent();
            startActivity(intent);
        } else {
            TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                    .text(text);
            builder.show();
        }
    }

    public void DoEmail(String share_text) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
//        sendIntent.setData(Uri.parse("test@gmail.com"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Anandiben Patel Application");
        sendIntent.putExtra(Intent.EXTRA_TEXT, share_text);
        startActivity(sendIntent);
    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void DoSwipeMenu(SwipeMenuListView listView) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(70));
                // set item title
//                openItem.setTitle("Open");
                openItem.setIcon(R.drawable.twtter);
                // set item title fontsize
//                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);


                SwipeMenuItem deleteItem1 = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem1.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                // set item width
                deleteItem1.setWidth(dp2px(1));
                // set a icon
                openItem.setTitle("t");
                openItem.setTitleColor(Color.WHITE);

                // add to menu
                menu.addMenuItem(deleteItem1);


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set a icon
                deleteItem.setIcon(R.mipmap.fb_icon);

                // add to menu
                menu.addMenuItem(deleteItem);

            }
        };

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
    }

    CallbackManager callbackManager;

    public void LoginFB(final String type, final String sharetext, final ModelDelegates.FacebookPostDelegate delegate) {
//        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback() {
            @Override
            public void onSuccess(Object o) {
                if (type.equalsIgnoreCase("SHARE")) {
                    FacebookSharing(getActivity(), sharetext);
                } else if (type.equalsIgnoreCase("POSTS")) {
                    getFacebookPosts(delegate);
                }
            }

            @Override
            public void onCancel() {
                if (type.equalsIgnoreCase("POSTS")) {
                    if (delegate != null) {
                        delegate.OnFail("Login Fail");
                    }
                }
            }

            @Override
            public void onError(FacebookException exception) {
                if (type.equalsIgnoreCase("POSTS")) {
                    if (delegate != null) {
                        delegate.OnFail("Login Fail");
                    }
                }
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "user_status", "user_posts"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
