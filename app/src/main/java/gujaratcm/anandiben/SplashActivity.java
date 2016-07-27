package gujaratcm.anandiben;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.crittercism.app.Crittercism;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Crittercism.initialize(getApplicationContext(), "10cb2da49df04117bc2a8794d74e7e6800555300");
        Picasso.with(this).load(R.mipmap.splashscreen).fit().centerCrop().into((ImageView) findViewById(R.id.imgSplash));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                boolean iswelcome = false;

                iswelcome = sharedPreferences.getBoolean("WELCOME", false);
                if (iswelcome) {
                    Intent i = new Intent(SplashActivity.this, DrawerActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, WelcomeScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
        generateHashkey();
    }

    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "gujaratcm.anandiben",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("HashKey", Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("", e.getMessage(), e);
        }
    }
}
