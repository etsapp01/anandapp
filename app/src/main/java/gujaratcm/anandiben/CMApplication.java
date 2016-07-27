package gujaratcm.anandiben;

import android.app.Application;
import android.content.Context;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.Database;
import gujaratcm.anandiben.activerecordbase.DatabaseBuilder;
import gujaratcm.anandiben.model.BiographyInfo;
import gujaratcm.anandiben.model.BlogInfo;
import gujaratcm.anandiben.model.FacebookPostInfo;
import gujaratcm.anandiben.model.ImportantDecisionInfo;
import gujaratcm.anandiben.model.InfographyInfo;
import gujaratcm.anandiben.model.MediaCoverageInfo;
import gujaratcm.anandiben.model.MessagesInfo;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.model.OccupationInfo;
import gujaratcm.anandiben.model.PhotoDayInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.model.SchemesInfo;
import gujaratcm.anandiben.model.YoutubeInfo;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ACER on 02-07-2016.
 */
public class CMApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "MAUOG9caQAjgWZYR24cpPvq8L";
    private static final String TWITTER_SECRET = "Bo1pbFzIx4Bdv2qbCfsRdMJWiN1y6fvvvoj2WqJtnfq67ARK6M";

    private static CMApplication _intance = null;
    private static ActiveRecordBase mDatabase;
    public static String DATABSENAME = "gujaratdatabase.db";
    public static int DATABASE_VERSION = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseBuilder builder = new DatabaseBuilder(DATABSENAME);
        builder.addClass(MessagesInfo.class);
        builder.addClass(BiographyInfo.class);
        builder.addClass(NewsInfo.class);
        builder.addClass(NewsImagesInfo.class);
        builder.addClass(ImportantDecisionInfo.class);
        builder.addClass(SchemesInfo.class);
        builder.addClass(MediaCoverageInfo.class);
        builder.addClass(NewsDetailInfo.class);
        builder.addClass(PrintMediaInfo.class);
        builder.addClass(PhotoDayInfo.class);
        builder.addClass(YoutubeInfo.class);
        builder.addClass(BlogInfo.class);
        builder.addClass(InfographyInfo.class);
        builder.addClass(FacebookPostInfo.class);
        builder.addClass(OccupationInfo.class);
        Database.setBuilder(builder);
        try {
            mDatabase = ActiveRecordBase.open(this, DATABSENAME,
                    DATABASE_VERSION);
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CMApplication() {
        _intance = this;
    }

    public static ActiveRecordBase Connection() {
        return mDatabase;
    }

    public static Context getContext() {
        return _intance;
    }


}
