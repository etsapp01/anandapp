package gujaratcm.anandiben.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import gujaratcm.anandiben.CMApplication;

public class NetworkConnectivity {

    public static boolean isConnected() {
        try {
            ConnectivityManager manager = (ConnectivityManager) CMApplication
                    .getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] infos = manager.getAllNetworkInfo();

            for (NetworkInfo info : infos) {
                if (info.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

        // try {
        // ConnectivityManager cm = (ConnectivityManager) FieldworkApplication
        // .getContext()
        // .getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //
        // if (netInfo != null && netInfo.isConnected()) {
        // return true;
        // } else {
        // return false;
        // }
        // } catch (Exception e) {
        // return false;
        // }
    }

    public static String getNetworkType() {
        String type = "";
        final ConnectivityManager connMgr = (ConnectivityManager)
                CMApplication
                        .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            //Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
            type = "WIFI";
        } else if (mobile.isConnectedOrConnecting()) {
            type = "DATA";
            //Toast.makeText(this, "Mobile 3G ", Toast.LENGTH_LONG).show();
        } else {
            type = "NOINTERNET";
            //Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return type;
    }
}
