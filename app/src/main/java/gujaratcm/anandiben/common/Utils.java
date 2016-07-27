package gujaratcm.anandiben.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;


/**
 * The class contains common utility functions which helps whole application
 *
 * @author sam
 */
public class Utils {
    private static volatile Utils _instance = null;
    static String mFoodx_address;

    /**
     * Get the Instance of the Utils Class
     *
     * @return Utils Object
     */
    public static Utils Instance() {
        if (_instance == null) {
            synchronized (Utils.class) {
                _instance = new Utils();
            }
        }
        return _instance;
    }

    public interface AddressCallDelegate {

        public void CallSuccessFull(String add);

        public void CallFailed(String error);

    }

    /**
     * This is the function which joins the String Array with delimiter. Android
     * does not contains any kind of function works like this.
     *
     * @param s         - List of String (CharSequence)
     * @param delimiter
     * @return String
     */
    public String join(List<? extends CharSequence> s, String delimiter) {
        int capacity = 0;
        int delimLength = delimiter.length();
        Iterator<? extends CharSequence> iter = s.iterator();
        if (iter.hasNext()) {
            capacity += iter.next().length() + delimLength;
        }

        StringBuilder buffer = new StringBuilder(capacity);
        iter = s.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
            while (iter.hasNext()) {
                buffer.append(delimiter);
                buffer.append(iter.next());
            }
        }
        return buffer.toString();
    }

    public ArrayList<String> Split(String str, String delimiter) {
        ArrayList<String> arr = new ArrayList<String>();
        String[] ss = str.split(delimiter);
        for (int i = 0; i < ss.length; i++) {
            arr.add(ss[i]);
        }
        return arr;
    }

    private float m_density = 0;

    public void setDensity(float density) {
        m_density = density;
    }

    public float getDensity() {
        return m_density;
    }

    /**
     * Log Application Exception over here. To keep consistancy, this function
     * was developed
     *
     * @param ex - Exception
     */
    public static void LogException(Exception ex) {
        Log.d("Appplication Exception", "Appplication Exception -- > " + ex.getMessage()
                + "\n" + ex);
    }

    /**
     * Log any debug info over here. To keep consistancy, this function was
     * developed
     *
     * @param message
     */
    public static void LogInfo(String message) {
        Log.i("Appplication", "Appplication -- >" + message);
    }

    /**
     * Convert String value to Boolean
     *
     * @param val
     * @return boolean
     */
    public static boolean ParseBoolean(String val) {
        try {
            boolean bl = Boolean.valueOf(val);
            return bl;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get dip unit from the pixel for the perticulat density Density stored at
     * the starting point of the application, from the splash screen
     *
     * @param pixel
     * @return dip
     */
    public int getDipFromPixel(int pixel) {
        if (m_density <= 0) {
            return pixel;
        }
        float density = m_density;
        int dip = (int) (pixel * density);
        return dip;
    }

    /**
     * Convert String to Integer Value
     *
     * @param val
     * @return Integer Value
     */
    public static int ConvertToInt(String val) {
        int intval = 0;
        try {
            intval = Integer.parseInt(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return intval;
    }

    public static double ConvertToDouble(String val) {
        double d = 0;
        try {
            d = Double.parseDouble(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return d;
    }

    public static long ConvertToLong(String val) {
        long d = 0;
        try {
            d = Long.parseLong(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return d;
    }

    public static boolean ConvertToBoolean(String val, boolean defaultval) {
        boolean flag = defaultval;
        try {
            flag = Boolean.valueOf(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return flag;
    }

    /**
     * Convert String to Float value
     *
     * @param val
     * @return Float Value
     */
    public static float ConvertToFloat(String val) {
        float intval = 0;
        try {
            intval = Float.parseFloat(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return intval;
    }

    /**
     * Convert String to java.utils.Date
     *
     * @param val
     * @return Date
     */
    public static Date ConvertToDate(String val) {
        Date retval = null;
        try {
            if (val == null || val.length() <= 0) {
                return retval;
            }
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            retval = dateformat.parse(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }

        return retval;
    }

    public static Date ConvertToDate(String val, String format) {
        Date retval = null;
        try {
            if (val == null || val.length() <= 0) {
                return retval;
            }
            SimpleDateFormat dateformat = new SimpleDateFormat(format);
            retval = dateformat.parse(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }

        return retval;
    }

    public static String getFormatedDate(String strDate, String sourceFormate,
                                         String destinyFormate) {
        if (strDate.length() > 0) {
            SimpleDateFormat df;
            df = new SimpleDateFormat(sourceFormate);
            Date date = null;
            try {
                date = df.parse(strDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            df = new SimpleDateFormat(destinyFormate);
            return df.format(date);
        }
        return strDate;
    }

    public static Date ConvertToDateComarator(String val) {
        Date retval = null;
        try {
            if (val == null || val.length() <= 0) {
                return retval;
            }
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
            retval = dateformat.parse(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }

        return retval;
    }

    public static void writeContent(String content) {
        if (content.length() <= 0) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(),
                "MFOODX_LOG" + ".txt");
        try {

            Date dt = new Date();
            SimpleDateFormat format = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss");
            String date = format.format(dt);

            FileWriter writer = new FileWriter(file, true);
            writer.append(date + " : " + content + "\r\n");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            // handle exception
        } catch (Exception e) {
            // handle exception
        }

    }

    public static String getFormattedDateForWallPost(String dt) {
        String created_date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
            Date d = format.parse(dt);
            // format = new SimpleDateFormat("dd MMM yy HH:mm");
            // created_date = format.format(d);
            created_date = android.text.format.DateUtils
                    .getRelativeTimeSpanString(d.getTime()).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return created_date;
    }

    /**
     * This Function is used only on development process which reads the static
     * service response from the Asset file and do not call to service each
     * time.
     *
     * @param fileName
     * @param context
     * @return Json String
     */
    @SuppressWarnings("static-access")
    public String ReadFromfile(String fileName, Context context) {
        StringBuilder ReturnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                ReturnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return ReturnString.toString();
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getUTCForCSharp(String javaUtc) {
        try {
            return String
                    .valueOf(adjustTimezoneOffset(Long.parseLong(javaUtc)) * 10000 + 621355968000000000L);
        } catch (Exception e) {
        }
        return "";
    }

    public static long adjustTimezoneOffset(long utcMillies) {
        TimeZone tz = TimeZone.getTimeZone("CET");
        long offset = tz.getOffset(new Date().getTime());
        return utcMillies + offset;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String RenameFileToNewPath(String oldpath) {
        String path = oldpath;
        path = path.replace("/mnt/sdcard/", "");
        String new_path = path.replace(".ck", "");
        File sdcard = Environment.getExternalStorageDirectory();
        File from = new File(sdcard, oldpath);
        File to = new File(sdcard, new_path);
        from.renameTo(to);
        return to.toString();
    }

    public static void RenameFileToOldPath(String newpath) {
        String path = newpath;
        String old = path + ".ck";
        File sdcard = Environment.getExternalStorageDirectory();
        File from = new File(sdcard, newpath);
        File to = new File(sdcard, old);
        from.renameTo(to);
    }

    public static boolean isSameDate(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String s = sdf.format(d1);
        try {
            d1 = sdf.parse(s);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        s = sdf.format(d2);
        try {
            d2 = sdf.parse(s);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        int days1 = (int) (d1.getTime() / 86400);
        int days2 = (int) (d2.getTime() / 86400);
        return days1 == days2;
    }

    public static boolean isSameDate(Date d1, Date d2, String formate) {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        String s = sdf.format(d1);
        try {
            d1 = sdf.parse(s);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        s = sdf.format(d2);
        try {
            d2 = sdf.parse(s);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        int days1 = (int) (d1.getTime() / 86400);
        int days2 = (int) (d2.getTime() / 86400);
        return days1 == days2;
    }

    public static void sendEmail(String contactEmail, Activity act) {

        final Intent emailIntent = new Intent(
                Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, contactEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public static void callPhone(String number, Activity act) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
//		act.startActivity(callIntent);
    }

    public static Address getLocationFromAddress(String Add, Activity act) {
        Geocoder coder = new Geocoder(act);
        List<Address> address;
        // Add =
        // "L D College Of Engineering, 120 Circular Road,University Area,Ahmedabad,Gujarat 380015";
        try {
            address = coder.getFromLocationName(Add, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            return location;
        } catch (Exception e) {
            Utils.LogException(e);
            return null;
        }

    }

    public static String GetAddressFromLatLang(String lat, String lang,
                                               Activity act) {

        Geocoder geocoder;
        List<Address> addresses = null;
        ArrayList<String> user_address = null;
        String add = null;

        geocoder = new Geocoder(act, Locale.getDefault());
        if (lat != null && lang != null) {
            try {
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lang);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getAddressLine(1);
                    String country = addresses.get(0).getAddressLine(2);
                    String postalCode = addresses.get(0).getPostalCode();
                    mFoodx_address = address + city + country + postalCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mFoodx_address;
    }

    public static void StopGps(Activity act) {
        try {
            LocationManager lm = (LocationManager) act
                    .getSystemService(Context.LOCATION_SERVICE);
            boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPS) {
                Utils.LogInfo("GPS is Not Active ....................");
                Intent intent = new Intent(
                        "android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", false);
                act.sendBroadcast(intent);
            }
            boolean isOnGPS = lm
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isOnGPS) {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                act.sendBroadcast(poke);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void StartGps(Activity act) {
        try {
            LocationManager lm = (LocationManager) act
                    .getSystemService(Context.LOCATION_SERVICE);
            boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPS) {
                Utils.LogInfo("GPS is Not Active ....................");
                Intent intent = new Intent(
                        "android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                act.sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static Location getCurrentLocation(Activity act) {
    // try {
    // LocationManager lm = (LocationManager) act
    // .getSystemService(Context.LOCATION_SERVICE);
    // boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    //
    // if (!isGPS) {
    // Utils.LogInfo("GPS is Not Active ....................");
    // Intent intent = new Intent(
    // "android.location.GPS_ENABLED_CHANGE");
    // intent.putExtra("enabled", true);
    // act.sendBroadcast(intent);
    // }
    //
    // Location location = lm
    // .getLastKnownLocation(LocationManager.GPS_PROVIDER);
    // if (location == null) {
    // location = lm
    // .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    // // if (location == null) {
    // // act.startActivity(new Intent(
    // // Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    // // }
    // }
    // Utils.LogInfo("LOCATION Lat----->>>" + location.getLatitude()
    // + "  LOCATION Log----->>>" + location.getLongitude());
    // return location;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    //
//	public static Location getCurrentLocation(Context act) {
//		CheckGps(act);
//		try {
//			LocationManager lm = (LocationManager) act
//					.getSystemService(Context.LOCATION_SERVICE);
//			boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//			if (!isGPS) {
//				Utils.LogInfo("GPS is Not Active ....................");
//				Intent intent = new Intent(
//						"android.location.GPS_ENABLED_CHANGE");
//				intent.putExtra("enabled", true);
//				act.sendBroadcast(intent);
//			}
//			Location location = lm
//					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			if (location == null) {
//				location = lm
//						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			}
//			Utils.LogInfo("LOCATION Lat----->>>" + location.getLatitude()
//					+ "  LOCATION Log----->>>" + location.getLongitude());
//			return location;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

    public static String getIndianFormat(String text) {

        // Format format = NumberFormat
        // .getCurrencyInstance(new Locale("en", "in"));
        // DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat)
        // format).getDecimalFormatSymbols();
        // decimalFormatSymbols.setCurrencySymbol("");
        // String my_format = format.format(new BigDecimal(text));

        // NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en",
        // "in"));
        // DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf)
        // .getDecimalFormatSymbols();
        // decimalFormatSymbols.setCurrencySymbol("");
        // String my_format = nf.format(new BigDecimal(text));

        // ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        // String my_format = nf.format(text).trim();
        // System.out.println(nf.format(12345.124).trim());

        double amt = Double.parseDouble(text);
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en",
                "in"));
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf)
                .getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        String my_format = nf.format(amt).trim();

        return my_format;
    }

    public static void CheckGps(final Context act) {
        try {
            LocationManager locationManager = (LocationManager) act
                    .getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        act);
                alertDialogBuilder
                        .setMessage(
                                "GPS is disabled in your device. Enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Enable GPS",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        act.startActivity(callGPSSettingIntent);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        } catch (Exception ex) {

        }
    }

    public static int getRandomInt() {
        int max = 100;
        int min = 1;
        Random r = new Random();
        int i = r.nextInt(max - min) + min;
        i = i * (-1);
        return i;
    }

    public String ChangeDateFormat(String currentFormat,
                                   String convertedFormat, String date) {
        String newDateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
        try {
            Date dateObj = sdf.parse(date);
            SimpleDateFormat postFormater = new SimpleDateFormat(
                    convertedFormat);
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return newDateStr;
    }

    public String getLocationAddress(Context act, double lat, double lon,
                                     AddressCallDelegate dele) {
        Geocoder geocoder = new Geocoder(act, Locale.ENGLISH);
        String addr = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null) {
                if (addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    for (int i = 0; i < returnedAddress
                            .getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(
                                returnedAddress.getAddressLine(i)).append("\n");
                    }
                    addr = strReturnedAddress.toString();
                }
            } else {
                addr = "No Address returned!";
            }
        } catch (IOException e) {
            e.printStackTrace();
            addr = Utils.Instance().getAddressFromGoogle(lat, lon, dele);

        }
        if (addr.length() < 0) {
            addr = "No Address returned!";
        }
        return addr;
    }

    public String getAddressFromGoogle(double lat, double lon,
                                       AddressCallDelegate dele) {
        getAddressAsync load = new getAddressAsync(lat, lon, dele);
        load.execute();
        return "";
    }

    public class getAddressAsync extends AsyncTask<Void, Void, Void> {
        double latitude;
        double longitude;
        String responseString = "";
        AddressCallDelegate delegate;
        String google_address = "Can not get address from google!";

        public getAddressAsync(double lat, double lon, AddressCallDelegate dele) {
            latitude = lat;
            longitude = lon;
            delegate = dele;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String mainurl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + String.valueOf(latitude)
                    + ","
                    + String.valueOf(longitude) + "&sensor=true";
            HttpURLConnection connection = null;
            InputStream is = null;
            try {
                URL url = new URL(mainurl);

                connection = (HttpURLConnection) url
                        .openConnection(Proxy.NO_PROXY);
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");

                // get input stream response and convert it into String
                is = connection.getInputStream();
                responseString = convertinputStreamToString(new FlushedInputStream(
                        is));

            } catch (Exception e) {
                Utils.LogException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (responseString.length() > 0) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    JSONArray arr = jobj.getJSONArray("results");
                    if (arr.length() > 0) {
                        JSONObject first = arr.getJSONObject(0);
                        google_address = first.optString("formatted_address");
                        delegate.CallSuccessFull(google_address);
                    }
                } catch (Exception e) {
                    Utils.LogException(e);
                }
            } else {
                delegate.CallFailed(google_address);
            }
        }

    }

    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    public static String getIMEINO(Context ctx) {
        TelephonyManager tManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imeiid = "";
        imeiid = tManager.getDeviceId();
        if (imeiid == null) {
            imeiid = "";
        }
        if (imeiid.length() <= 0) {
            imeiid = Secure.getString(ctx.getContentResolver(),
                    Secure.ANDROID_ID);
        }
        // Toast.makeText(ctx, imeiid, Toast.LENGTH_LONG).show();
        return imeiid;
    }

    public static Bitmap getRoundedCornerBorder(Bitmap bitmap, int color,
                                                int cornerDips, int borderDips, Context context, int height,
                                                Drawable d) {
        // hint
        // http://stackoverflow.com/questions/11012556/border-over-a-bitmap-with-rounded-corners-in-android
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, (float) borderDips, context
                        .getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips, context
                        .getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        rect.left += 6;
        rect.top += 6;
        rect.right -= 6;
        rect.bottom -= 6;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, height, Color.DKGRAY,
                Color.WHITE, Shader.TileMode.MIRROR));
        // canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        paint.setShader(new LinearGradient(0, 0, 0, height, Color.DKGRAY,
                Color.WHITE, Shader.TileMode.MIRROR));
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, 20, 20, paint);
        return output;
    }

    public static String toBase64(byte[] b) {
        String str = Base64.encodeToString(b, Base64.DEFAULT);
        return str;
    }

    public static String toBase64(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toBase64(stream.toByteArray());
    }

    public static Bitmap fromBase64(String base64String, Activity act) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Options options = new Options();
        options.inScaled = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                decodedString.length, options);
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = decodedByte.getWidth();
        int height = decodedByte.getHeight();
        Bitmap b2 = Bitmap
                .createScaledBitmap(decodedByte, width, height, false);
        return b2;
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        Options o = new Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri),
                null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        Options o2 = new Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver()
                .openInputStream(uri), null, o2);
    }

    public String getDayFromDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String day = "", today = "";
        try {
            Date date = format.parse(dateString);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            day = outFormat.format(date);
            date = new Date();
            today = outFormat.format(date);
            if (today.equalsIgnoreCase(day)) {
                day = "Today";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day;
    }

    public boolean WriteTextLog(String data, String filename) {
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".txt");
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Date parseDateForTimeFilter(String date) {
        try {
            SimpleDateFormat myDateFormat = new SimpleDateFormat("hh:mm a");
            Date tempDate = myDateFormat.parse(date);
            return tempDate;
        } catch (ParseException e) {
            return new Date(0);
        }
    }

//    public void SortBookByRecently(ArrayList<MyAudioBook> m_list) {
//        if (m_list.size() > 0) {
//            Collections.sort(m_list,
//                    AppComparators.Instance().comparerecently);
//        }
//    }

    public static boolean isDateBefore(String curdate, String dob) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            Date date1 = df.parse(curdate);
            Date startingDate = df.parse(dob);

            if (startingDate.before(date1))

                return true;
            else
                return false;
        } catch (Exception e) {

            return false;
        }
    }

    public static boolean isDateAfter(String curdate, String dob) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = df.parse(curdate);
            Date startingDate = df.parse(dob);

            if (startingDate.after(date1))

                return true;
            else
                return false;
        } catch (Exception e) {

            return false;
        }
    }


    public static String compressImage(String filePath) {

        // String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                middleY - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "MFoodx/Images");

        // File file1 = new File(Environment.DIRECTORY_DOWNLOADS,
        // "MFoodx/Images");

        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public static int calculateInSampleSize(Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public void ShowSnack(View v, String message) {
        if (v != null && message != null && message.length() > 0 && !message.equalsIgnoreCase("null")) {
            Snackbar snackbar = Snackbar
                    .make(v, message, Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.parseColor("#FF0000"));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#009688"));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            snackbar.show();
        }
    }

    public void LoadDetailData(int id, final ModelDelegates.UniversalNewsDetailDelegate del) {
        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.NEWS);
            helper.call(ServiceHelper.URL + "/" + id + "", new ServiceHelper.ServiceHelperDelegate() {
                @Override
                public void CallFinish(ServiceResponse res) {
                    try {
                        JSONObject detail_obj = new JSONObject(res.RawResponse);
                        if (detail_obj != null) {
                            ModelMapHelper<NewsDetailInfo> mapper = new ModelMapHelper<NewsDetailInfo>();
                            NewsDetailInfo info = mapper.getObject(
                                    NewsDetailInfo.class, detail_obj);
                            if (info != null) {
                                JSONObject title = detail_obj.getJSONObject("title");
                                info.detail = title.optString("rendered");
                                JSONObject content = detail_obj.getJSONObject("content");
                                info.more_detail = content.optString("rendered");
                                info.save();
                                if (del != null)
                                    del.CallDidSuccess(info);
                            }
                        } else {
                            if (del != null)
                                del.CallFailedWithError("No Detail Found");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (del != null)
                            del.CallFailedWithError("Something went wrong");
                    }
                }

                @Override
                public void CallFailure(String ErrorMessage) {
                    if (del != null)
                        del.CallFailedWithError(ErrorMessage);
                }
            });
        } else {
            if (del != null)
                del.CallFailedWithError("Please check your internet connection.");
        }
    }

}
