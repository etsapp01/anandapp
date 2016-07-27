package gujaratcm.anandiben;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.crittercism.app.Crittercism;
import com.squareup.picasso.Picasso;

/**
 * Created by ACER on 23-07-2016.
 */
public class WelcomeScreenActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        Crittercism.initialize(getApplicationContext(), "10cb2da49df04117bc2a8794d74e7e6800555300");
        Picasso.with(this).load(R.drawable.welcome).fit().centerCrop().into((ImageView) findViewById(R.id.imgSplash));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putBoolean("WELCOME", true).commit();
                Intent i = new Intent(WelcomeScreenActivity.this, DrawerActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void previous() {

    }

    @Override
    protected void next() {

    }
}
