package gujaratcm.anandiben;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

public class BalanceDialog extends Activity {

    private DatePicker dpResult;
    private int year;
    private int month;
    private int day;
    EditText txtDob;
    TextView txtFacebook,
            txtTwitter, txtMessage;
    ShareDialog shareDialog;
    private static final String TWITTER_KEY = "MAUOG9caQAjgWZYR24cpPvq8L";
    private static final String TWITTER_SECRET = "Bo1pbFzIx4Bdv2qbCfsRdMJWiN1y6fvvvoj2WqJtnfq67ARK6M";


    static final int DATE_DIALOG_ID = 999;
    String share_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.balance_dialog);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            share_text = b.getString("share_text");
        }

        txtFacebook = (TextView) findViewById(R.id.txtFacebook);
        txtTwitter = (TextView) findViewById(R.id.txtTwitter);
        txtMessage = (TextView) findViewById(R.id.txtMessage);


        txtFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSharing(BalanceDialog.this, share_text);
            }
        });

        txtTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoShare(share_text);
            }
        });

        txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoEmail(share_text);
            }
        });

    }

    public void FacebookSharing(Context con, String url) {
        FacebookSdk.sdkInitialize(con);
        shareDialog = new ShareDialog(this);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Anandiben Patel")
                    .setImageUrl(Uri.parse("http://anandibenpatel.com/wp-content/uploads/457.jpg"))
                    .setContentDescription(
                            "I am sharing this using Anandiben Patel Application")
                    .setContentUrl(Uri.parse(url))
                    .build();

            shareDialog.show(linkContent);  // Show facebook ShareDialog
        }
    }


//
//    public void setCurrentDateOnView() {
//
//        dpResult = (DatePicker) findViewById(R.id.dpResult);
//
//        final Calendar c = Calendar.getInstance();
//        year = c.get(Calendar.YEAR);
//        month = c.get(Calendar.MONTH);
//        day = c.get(Calendar.DAY_OF_MONTH);
//        dpResult.init(year, month, day, null);
//
//    }
//
//    public void addListenerOnButton() {
//
//
//        showDialog(DATE_DIALOG_ID);
//
//
//    }
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//            case DATE_DIALOG_ID:
//                // set date picker as current date
//                return new DatePickerDialog(this, datePickerListener,
//                        year, month, day);
//        }
//        return null;
//    }
//
//    private DatePickerDialog.OnDateSetListener datePickerListener
//            = new DatePickerDialog.OnDateSetListener() {
//
//        // when dialog box is closed, below method will be called.
//        public void onDateSet(DatePicker view, int selectedYear,
//                              int selectedMonth, int selectedDay) {
//            year = selectedYear;
//            month = selectedMonth;
//            day = selectedDay;
//
//            // set selected date into textview
//            txtDob.setText(new StringBuilder().append(month + 1)
//                    .append("-").append(day).append("-").append(year)
//                    .append(" "));
//
//            // set selected date into datepicker also
//            dpResult.init(year, month, day, null);
//
//        }
//    };

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

}
