package gujaratcm.anandiben;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity {
    ProgressDialog m_pd = null;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    ShareDialog shareDialog;
    private static final String TWITTER_KEY = "MAUOG9caQAjgWZYR24cpPvq8L";
    private static final String TWITTER_SECRET = "Bo1pbFzIx4Bdv2qbCfsRdMJWiN1y6fvvvoj2WqJtnfq67ARK6M";
    //int b;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    AccessTokenTracker mAccessTokenTracker = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_pd = new ProgressDialog(BaseActivity.this);
        m_pd.setMessage(Html.fromHtml("Please wait..."));
        m_pd.setCancelable(false);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        gestureDetector = new GestureDetector(new SwipeDetector());
    }

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
            // then dismiss the swipe.
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // Swipe from right to left.
            // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
            // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                next();
                return true;
            }

            // Swipe from left to right.
            // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
            // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                previous();
                return true;
            }

            return false;
        }

    }

    protected abstract void previous();

    protected abstract void next();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TouchEvent dispatcher.
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    //hiren fb id=10205201868245284
    private void getFacebookPosts() {
        FacebookSdk.sdkInitialize(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                    } else {
                        final Bundle params = new Bundle();
                        params.putString("fields", "message,created_time,id,full_picture,status_type,source,comments.summary(true),likes.summary(true)");
//        params.putString("limit", "10");
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + "570916289588054" + "/posts", params, HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                                        try {
                                            JSONObject jObjResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        ).executeAsync();
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        } else {
            LoginFB("POSTS", "");
        }

    }

    public void FacebookSharing(Context con, final String url) {
        FacebookSdk.sdkInitialize(this);
        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                    } else {
                        shareDialog = new ShareDialog(BaseActivity.this);

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
            LoginFB("SHARE", url);
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
            final Intent intent = new ComposerActivity.Builder(this)
                    .session(session)
                    .createIntent();
            startActivity(intent);
        } else {
            TweetComposer.Builder builder = new TweetComposer.Builder(this)
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
                        getApplicationContext());
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
                        getApplicationContext());
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
                        getApplicationContext());
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

    public void GetTweet() {
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("fabric")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();

//        setListAdapter(adapter);

        // TODO: Use a more specific parent
        final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
        // TODO: Base this Tweet ID on some data from elsewhere in your app
        long tweetId = 631879971628183552L;
        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                TweetView tweetView = new TweetView(BaseActivity.this, result.data);
                parentView.addView(tweetView);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Load Tweet failure", exception);
            }
        });
    }

    CallbackManager callbackManager;

    public void LoginFB(final String type, final String sharetext) {
//        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback() {
            @Override
            public void onSuccess(Object o) {
                if (type.equalsIgnoreCase("SHARE")) {
                    FacebookSharing(BaseActivity.this, sharetext);
                } else if (type.equalsIgnoreCase("POSTS")) {
                    getFacebookPosts();
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "user_status", "user_posts"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}