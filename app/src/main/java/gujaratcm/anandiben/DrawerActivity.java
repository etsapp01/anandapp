package gujaratcm.anandiben;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.model.MessagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.OccupationInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.modellist.MessageList;
import gujaratcm.anandiben.modellist.NewsList;
import gujaratcm.anandiben.modellist.OccupationList;
import gujaratcm.anandiben.modellist.PrintMediaList;

public class DrawerActivity extends AppCompatActivity implements MyModelDelegated.DoQuickmenu {

    protected DrawerLayout mDrawerLayout;
    FrameLayout actContent;
    private ListView mDrawerList;
    private RelativeLayout rlDrawer, rlUserAll;

    boolean isOpen = false;
    // nav drawer title
    private CharSequence mDrawerTitle;
    String[] androidStrings;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    public TextView tvtitle;
    public Toolbar toolbar;
    ImageView imgClick;
    QuickAction mQuickAction;

    private static final int ID_ADD = 1;
    private static final int ID_ACCEPT = 2;
    private static final int ID_UPLOAD = 3;
    ProgressDialog mPd;
    int current_item = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        setActionBar();
        mPd = new ProgressDialog(this);
        mPd.setMessage("Please wait...");
//        LoadQoutesFromServer();
//        loginToMyFbApp();
        LoadNewsFromServer();

    }

    public void LoadNewsFromServer() {
        mPd.show();
        NewsList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<NewsInfo>() {
            @Override
            public void ModelLoaded(ArrayList<NewsInfo> list) {

                PrintMediaList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<PrintMediaInfo>() {
                    @Override
                    public void ModelLoaded(ArrayList<PrintMediaInfo> list) {
                        mPd.dismiss();
                        initTab();
                    }

                    @Override
                    public void ModelLoadFailedWithError(String error) {
                        mPd.dismiss();
                        initTab();
                    }
                });

            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                PrintMediaList.Instance().LoadData(0, new ModelDelegates.ModelDelegate<PrintMediaInfo>() {
                    @Override
                    public void ModelLoaded(ArrayList<PrintMediaInfo> list) {
                        mPd.dismiss();
                        initTab();
                    }

                    @Override
                    public void ModelLoadFailedWithError(String error) {
                        mPd.dismiss();
                        initTab();
                    }
                });
            }
        });
    }


    public void initTab() {
        mTitle = mDrawerTitle = getTitle();
        // ParseInstallation.getCurrentInstallation().saveInBackground();
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rlDrawer = (RelativeLayout) findViewById(R.id.rldrawer);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        imgClick = (ImageView) findViewById(R.id.imgClick);

        imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        ActionItem addItem = new ActionItem(ID_ADD, "Add", getResources()
                .getDrawable(R.drawable.fab_add));

        mQuickAction = new QuickAction(this);


        mQuickAction
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                                                  @Override
                                                  public void onItemClick(QuickAction quickAction, int pos,
                                                                          int actionId) {
                                                      ActionItem actionItem = quickAction.getActionItem(pos);

                                                      if (actionId == ID_ADD) {
                                                          Toast.makeText(getApplicationContext(),
                                                                  "Add item selected", Toast.LENGTH_SHORT)
                                                                  .show();
                                                      } else

                                                      {
                                                          Toast.makeText(getApplicationContext(),
                                                                  actionItem.getTitle() + " selected",
                                                                  Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              }

                );

        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener()

                                          {
                                              @Override
                                              public void onDismiss() {
                                                  Toast.makeText(getApplicationContext(), "Ups..dismissed",
                                                          Toast.LENGTH_SHORT).show();
                                              }
                                          }

        );


        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[0], R.mipmap.menu_home)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[1], R.mipmap.menu_biography)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[2], R.mipmap.menu_latest_news)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[3], R.mipmap.menu_digital_ambesador)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[4], R.mipmap.menu_messagefromcm)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[5], R.mipmap.menu_media_coverage)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[6], R.mipmap.menu_photooftheday)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[7], R.mipmap.menu_interectwithcm)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[8], R.mipmap.menu_prime_initiatives)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[9], R.mipmap.menu_speechesandinterviews)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[10], R.mipmap.menu_interectwithcm)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[11], R.mipmap.menu_prime_initiatives)

        );
        navDrawerItems.add(new

                        NavDrawerItem(navMenuTitles[12], R.mipmap.menu_speechesandinterviews)

        );

        navMenuIcons.recycle();

        adapter = new

                NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems, false, this);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 7) {
//                    mQuickAction.show(view);
                } else {
//                    mQuickAction.dismiss();
                }
            }
        });

        mDrawerList.setOnItemClickListener(new

                        SlideMenuClickListener()

        );


        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()

                .

                        setHomeButtonEnabled(true);


        ActionBarDrawerToggle mDrawerToggle = new
                ActionBarDrawerToggle(this, mDrawerLayout,
                        R.mipmap.ic_launcher, // nav menu toggle icon
                        R.string.app_name, // nav drawer open - description for
                        // accessibility
                        R.string.app_name // nav drawer close - description for
                        // accessibility
                ) {
                    public void onDrawerClosed(View view) {
//                getActionBar().setTitle("");
                        invalidateOptionsMenu();
                    }

                    public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle("");
                        invalidateOptionsMenu();
                    }
                };


        mDrawerLayout.setDrawerListener(mDrawerToggle);
        displayView(0, null);
    }

    @Override
    public void DoSchemeClick() {
        getSupportActionBar().show();
        tvtitle.setText("Schemes in name of CM");
//        tvtitle.setText(navMenuTitles[position]);
        Fragment fragment = null;
        fragment = new SchemsFragment();
        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(8, true);
            mDrawerList.setSelection(8);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void DoDecisionClick() {
        getSupportActionBar().show();
        tvtitle.setText("Important Decision");
//        tvtitle.setText(navMenuTitles[position]);
        Fragment fragment = null;
        fragment = new ImportantDecisionFragment();
        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(8, true);
            mDrawerList.setSelection(8);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void setActionBar() {
//        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_list));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        tvtitle = (TextView) toolbar.findViewById(R.id.tvtitle);
        //tvtitle.setText(getResources().getString(R.string.home_title));
        Drawable upArrow;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            upArrow = getResources().getDrawable(null, DrawerActivity.this.getTheme());
//        } else {
//            upArrow = getResources().getDrawable(R.drawable.ic_menu);
//        }

//        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        //upArrow.setColorFilter(Color.parseColor("#33cc90"), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(null);
    }


    public class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position, view);
        }

    }


    private void displayView(int position, View v) {
        // update the main content by replacing fragments
        getSupportActionBar().show();
        tvtitle.setText(navMenuTitles[position]);
//        tvtitle.setText(navMenuTitles[position]);
        Fragment fragment = null;
        switch (position) {
            case 0:
                tvtitle.setText("Anandiben Patel");
                fragment = new MainFragment();
                break;
            case 1:
                fragment = new BiographyFragment();
                break;
            case 2:
//                toolbar.setVisibility(View.GONE);
//                fragment = new BiographyFragment();
//                int
                fragment = new LatestNewsFragment();
                break;
            case 3:

                break;
            case 4:
                fragment = new MessageFragment();
                break;
            case 5:
                fragment = new MediaCoverage();
                break;
            case 6:
                fragment = new PhotoOfDayFragment();
                break;
            case 7:
//                fragment = new OrdersFragment();
                fragment = new InteractWithCM();
                break;
            case 8:

                if (!isOpen) {
                    isOpen = true;
                    adapter = new

                            NavDrawerListAdapter(getApplicationContext(),

                            navDrawerItems, true, this);
                    mDrawerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    isOpen = false;
                    adapter = new

                            NavDrawerListAdapter(getApplicationContext(),

                            navDrawerItems, false, this);
                    mDrawerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                mDrawerList.post(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerList.setSelection(adapter.getCount() - 1);

                    }
                });
                break;
            case 9:
                fragment = new InterviewFragment();
                break;
            case 10:
                fragment = new BlogFragment();
                break;
            case 11:
                fragment = new InfographyFragment();
                break;
            case 12:
                fragment = new SocialMediaFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            // error in creating fragment
//            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        if (current_item == 0) {
            Snackbar snackbar = Snackbar
                    .make(mDrawerList, "Tap back again to exit!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.parseColor("#FF0000"));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#009688"));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            current_item--;

                        }
                    }, Snackbar.LENGTH_LONG);
                }
            });
            snackbar.show();
            current_item++;
        } else {
            current_item = 0;
            finish();
        }
    }


    AccessTokenTracker mAccessTokenTracker = null;

    private void loginToMyFbApp() {

        FacebookSdk.sdkInitialize(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                        //(the user has revoked your permissions -
                        //by going to his settings and deleted your app)
                        //do the simple login to FaceBook
                        //case 1
                    } else {
                        //you've got the new access token now.
                        //AccessToken.getToken() could be same for both
                        //parameters but you should only use "currentAccessToken"
                        //case 2
                        fetchProfile();
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        } else {
            LoginFB();
            //do the simple login to FaceBook
            //case 1
        }
    }

    //hiren fb id=10205201868245284
    private void fetchProfile() {
        final Bundle params = new Bundle();
        params.putString("fields", "message,created_time,id,full_picture,status_type,source,comments.summary(true),likes.summary(true)");
//        params.putString("limit", "10");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // this is where you should have the profile
                        Log.v("fetched info", object.toString());
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + "570916289588054" + "/posts", params, HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                                        System.out.println("Festival Page response::" + String.valueOf(response.getJSONObject()));

                                        try {
                                            JSONObject jObjResponse = new JSONObject(String.valueOf(response.getJSONObject()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        ).executeAsync();
//                        new GraphRequest(
//                                AccessToken.getCurrentAccessToken(),
//                                "/570916289588054/posts",
//                                null,
//                                HttpMethod.GET,
//                                new GraphRequest.Callback() {
//                                    public void onCompleted(GraphResponse response) {
//            /* handle the result */
//                                        System.out.println("Festival Page response::" + String.valueOf(response.getJSONObject()));
//                                    }
//                                }
//                        ).executeAsync();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link"); //write the fields you need
        request.setParameters(parameters);
        request.executeAsync();
    }

    CallbackManager callbackManager;

    public void LoginFB() {
//        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback() {
            @Override
            public void onSuccess(Object o) {
                if (Profile.getCurrentProfile() != null) {
                    Log.e("Pofile", Profile.getCurrentProfile().getName() + Profile.getCurrentProfile().getFirstName() + Profile.getCurrentProfile().getLastName() + Profile.getCurrentProfile().getId());
                } else {
                    Profile.fetchProfileForCurrentAccessToken();
                    Log.e("Pofile", Profile.getCurrentProfile().getName() + Profile.getCurrentProfile().getFirstName() + Profile.getCurrentProfile().getLastName() + Profile.getCurrentProfile().getId());
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
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag("");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}


