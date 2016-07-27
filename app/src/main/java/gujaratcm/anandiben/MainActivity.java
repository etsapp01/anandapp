package gujaratcm.anandiben;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    ShareApi api;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                DoCall();
//                Intent shareIntent = new PlusShare.Builder(MainActivity.this)
//                        .setType("text/plain")
//                        .setText("This is sample share text.")
//                        .setContentUrl(Uri.parse("http://www.google.com"))
//                        .getIntent();
//                startActivityForResult(shareIntent, 0);
            }
        });
    }

    public void DoCall() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        shareDialog = new ShareDialog(this);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("TEST")
                    .setImageUrl(Uri.parse("https://www.numetriclabz.com/wp-content/uploads/2015/11/114.png"))
                    .setContentDescription(
                            "simple LinkedIn integration")
                    .setContentUrl(Uri.parse("https://www.google.com"))
                    .build();

            shareDialog.show(linkContent);  // Show facebook ShareDialog
        }

//        callbackManager = CallbackManager.Factory.create();
//
//        List<String> permissionNeeds = Arrays.asList("email", "publish_actions");
//
//        //this loginManager helps you eliminate adding a LoginButton to your UI
//        loginManager = LoginManager.getInstance();
//
//        loginManager.logInWithPublishPermissions(this, permissionNeeds);
//
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                sharePhotoToFacebook();
//            }
//
//            @Override
//            public void onCancel() {
//                System.out.println("onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                System.out.println("onError");
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sharePhotoToFacebook() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("This is my code an you know that!!!.")
                .build();
        List<String> permissionNeeds = Arrays.asList("publish_actions");

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                System.out.println("onError");
            }

            @Override
            public void onCancel() {
                System.out.println("onError");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
//        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
