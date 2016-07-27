package gujaratcm.anandiben.servicehelper;


import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.common.Utils;


public class ServiceHelper {

    // private static String TAG = "Talkingbooks.ServiceHelper";
    private static String TAG = "";
    public static final String COMMON_ERROR = "Data not found!\n Please try again.";

    public static final String BIOGRAPHY = "knwothecm";
    public static final String QUOTES = "quotes";
    public static final String NEWS = "news";
    public static final String PRINT_MEDIA = "print-media";
    public static final String IMPORTANTDECISION = "important";
    public static final String SCHEMES = "schemes";
    public static final String MEDIACOVERAGE = "mediacoverage";

    public static final String INTERECT_CM = "write2cm.php";
    public static final String OCCUPATION = "write2cm.php?";


    private static boolean IS_DEBUG = true;

    public enum RequestMethod {
        GET, POST
    }

    public interface ServiceHelperDelegate {
        /**
         * Calls when got the response from the API
         *
         * @param res Service Response Obejct
         */
        public void CallFinish(ServiceResponse res);

        /**
         * Service call fail with error message
         *
         * @param ErrorMessage Error Message
         */
        public void CallFailure(String ErrorMessage);
    }

    // String m_methodName = null;
    private ServiceHelperDelegate m_delegate = null;


    // Live server
    public static String BASEURL = "http://anandibenpatel.com/";
    // testing server
    //public static String BASEURL = "http://103.20.104.96/";

    public static String URL = BASEURL + "en/wp-json/wp/v2/posts";
    public static String BIOGRAPHYURL = BASEURL + "en/wp-json/wp/v2/pages";
    public static String PHOTOOF_DAY = BASEURL + "en/wp-json/wp/v2/media";
    public static String INERECT_URL = BASEURL + "api-end/";
    public static String youtbe_url = BASEURL + "api-end/youtube-api.php";


    private ArrayList<String> m_params = new ArrayList<String>();
    private HashMap<String, String> m_hashParams = new HashMap<String, String>();
    private static int REQUEST_TIMEOUT = 90000;
    public RequestMethod RequestMethodType = RequestMethod.GET;
    String m_methodName = null;
    int REQUEST_TAG = 0;

    public ServiceHelper(String method) {
        m_methodName = method;
    }

    /**
     * When using more than one call from one class and same delegate is used.
     * So the call response is identify by TAG
     */
    public ServiceHelper(String method, int tag) {
        m_methodName = method;
        REQUEST_TAG = tag;
        RequestMethodType = RequestMethod.GET;
    }

    public ServiceHelper(String method, RequestMethod requestMethod) {
        m_methodName = method;
        RequestMethodType = requestMethod;
    }

    public void setTAG(String tag) {
        TAG = tag;
    }

    // public void addParam(String key, Object value) {
    // m_hashParams.put(key, String.valueOf(value));
    // }

    public void addParam(String key, Object value) {
        m_params.add(key + "=" + value);
    }

    // public void addParam(String key, int value) {
    // m_params.add(key + "=" + String.valueOf(value));
    // }

    public void setParams(ArrayList<String> params) {
        m_params = new ArrayList<String>(params);
    }

    public String getFinalUrl() {
        StringBuilder sb = new StringBuilder();
        if (m_methodName.equalsIgnoreCase(BIOGRAPHY)) {
            sb.append(BIOGRAPHYURL);
            sb.append("?");
        } else if (m_methodName.equalsIgnoreCase(INTERECT_CM)) {
            sb.append(INERECT_URL + m_methodName);
        } else if (m_methodName.equalsIgnoreCase(OCCUPATION)) {
            sb.append(INERECT_URL + m_methodName);
        } else {
            sb.append(URL);
            sb.append("?");
        }

//        sb.append(m_methodName.toString());

        if (RequestMethodType == RequestMethod.GET) {
            if (m_params.size() > 0) {
                String queryString = Utils.Instance().join(m_params, "&");
                sb.append("&");
                sb.append(queryString);
            }
        }
        return sb.toString();
    }

    // private ServiceHelperDelegate m_delegate = null;

    public void call(ServiceHelperDelegate delegate) {
        m_delegate = delegate;
        // if (NetworkConnectivity.isConnected()) {
        CallServiceAsync calling = new CallServiceAsync(null);
        calling.execute();

    }

    public void call(String url, ServiceHelperDelegate delegate) {
        m_delegate = delegate;
        // if (NetworkConnectivity.isConnected()) {
        CallServiceAsync calling = new CallServiceAsync(url);
        calling.execute();

    }

    private ServiceResponse call() {
        ServiceResponse resresponse = new ServiceResponse();
        StringBuilder builder = new StringBuilder();
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(25, TimeUnit.SECONDS);
        client.setReadTimeout(25, TimeUnit.SECONDS);
        client.setWriteTimeout(25, TimeUnit.SECONDS);
        Request request = null;
        if (RequestMethodType == RequestMethod.GET) {
            final MediaType JSON
                    = MediaType.parse("text/plain; charset=utf-8");
            request = new Request.Builder()
                    .url(getFinalUrl())
                    .build();
        } else {
//			request = new HttpPost(getFinalUrl());
//			// request.setHeader("Accept", "application/json");
//			request.setHeader("Content-Type",
//					"application/x-www-form-urlencoded");
            final MediaType JSON
                    = MediaType.parse("application/x-www-form-urlencoded");
            if (m_params.size() > 0) {
                // JSONObject json = JsonCreator.getJsonObject(m_params);
                String queryString = Utils.Instance().join(m_params, "&");
                RequestBody body = RequestBody.create(JSON, queryString);
                request = new Request.Builder()
                        .url(getFinalUrl())
                        .post(body)
                        .build();
            } else {
                RequestBody body = RequestBody.create(JSON, "");
                request = new Request.Builder()
                        .url(getFinalUrl())
                        .post(body)
                        .build();
            }
        }
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                resresponse.Message = "Loaded Successfully";
                resresponse.isSuccess = true;
                resresponse.statuscode = 200;
                resresponse.RawResponse = builder.append(response.body().string()).toString();

            } else {
                resresponse.Message = builder.append(response.body().string()).toString();
                resresponse.isSuccess = false;
                resresponse.statuscode = response.code();
                resresponse.RawResponse = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
            resresponse.Message = "Internet connection problem.\nPlease try again.";
            resresponse.isSuccess = false;
            resresponse.statuscode = 1;
            resresponse.RawResponse = "";
        }
        return resresponse;
    }

    private ServiceResponse callurl(String url) {
        ServiceResponse resresponse = new ServiceResponse();
        StringBuilder builder = new StringBuilder();
        OkHttpClient client = getNewHttpClient();
        Request request = null;


        if (RequestMethodType == RequestMethod.GET) {
            final MediaType JSON
                    = MediaType.parse("text/plain; charset=utf-8");
            request = new Request.Builder()
                    .url(url)
                    .build();
        } else {
//			request = new HttpPost(getFinalUrl());
//			// request.setHeader("Accept", "application/json");
//			request.setHeader("Content-Type",
//					"application/x-www-form-urlencoded");
            final MediaType JSON
                    = MediaType.parse("application/x-www-form-urlencoded");
            if (m_params.size() > 0) {
                // JSONObject json = JsonCreator.getJsonObject(m_params);
                String queryString = Utils.Instance().join(m_params, "&");
                RequestBody body = RequestBody.create(JSON, queryString);
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            } else {
                RequestBody body = RequestBody.create(JSON, "");
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            }
        }
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                resresponse.Message = "Loaded Successfully";
                resresponse.isSuccess = true;
                resresponse.statuscode = 200;
                resresponse.RawResponse = builder.append(response.body().string()).toString();

            } else {
                resresponse.Message = builder.append(response.body().string()).toString();
                resresponse.isSuccess = false;
                resresponse.statuscode = response.code();
                resresponse.RawResponse = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
            resresponse.Message = "Internet connection problem.\nPlease try again.";
            resresponse.isSuccess = false;
            resresponse.statuscode = 1;
            resresponse.RawResponse = "";
        }
        return resresponse;
    }

//    public void callUrl(String url, ServiceHelperDelegate delegate) {
//        m_delegate = delegate;
//        CallServiceAsync calling = new CallServiceAsync(url);
//        calling.execute();
//    }

    private static OkHttpClient getNewHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    class CallServiceAsync extends AsyncTask<Void, Void, ServiceResponse> {

        //        boolean m_withFullHeader = false;
        String m_url = null;

        //        public CallServiceAsync(boolean withFullHeader) {
//            m_withFullHeader = withFullHeader;
//        }
//
        public CallServiceAsync(String url) {
            m_url = url;
        }

        @Override
        protected ServiceResponse doInBackground(Void... params) {
            ServiceResponse response = null;
            if (NetworkConnectivity.isConnected()) {
                if (m_url == null) {
                    response = call();
                } else {
                    response = callurl(m_url);
                }
                return response;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ServiceResponse result) {
            if (result != null) {
                if (result.statuscode == 200) {
                    if (m_delegate != null) {
                        m_delegate.CallFinish(result);
                    }
                } else {
                    if (m_delegate != null) {
                        m_delegate.CallFailure(result.Message);
                    }
                }
            } else {
                if (m_delegate != null) {
                    m_delegate.CallFailure(ServiceHelper.COMMON_ERROR);
                }
            }
            super.onPostExecute(result);
        }

    }
}
