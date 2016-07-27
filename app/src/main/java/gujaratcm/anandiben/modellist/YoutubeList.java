package gujaratcm.anandiben.modellist;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;
import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.ModelMapHelper;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.model.YoutubeInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 03-07-2016.
 */
public class YoutubeList {

    protected YoutubeList() {
    }

    private static volatile YoutubeList _instance = null;
    public ModelDelegates.ModelDelegate<YoutubeInfo> m_delegate = null;
    protected ArrayList<YoutubeInfo> m_modelList = null;
    ModelDelegates.UniversalNewsDetailDelegate detail_delegate;

    public static YoutubeList Instance() {
        if (_instance == null) {
            synchronized (YoutubeList.class) {
                _instance = new YoutubeList();
            }
        }
        return _instance;
    }

    public void LoadData(final int start_form, ModelDelegates.ModelDelegate<YoutubeInfo> delegate) {
        m_delegate = delegate;
        if (start_form == 0) {
            loadFromDB();
        } else {
            m_modelList = null;
        }
        if (m_modelList == null || m_modelList.size() <= 0) {
            if (NetworkConnectivity.isConnected()) {
                ServiceHelper helper = new ServiceHelper(
                        ServiceHelper.NEWS);
                helper.call(ServiceHelper.youtbe_url, new ServiceHelper.ServiceHelperDelegate() {
                    @Override
                    public void CallFinish(ServiceResponse res) {
                        handleTodaysMessageResponse(res, start_form);
                    }

                    @Override
                    public void CallFailure(String ErrorMessage) {
                        if (m_delegate != null)
                            m_delegate.ModelLoadFailedWithError(ErrorMessage);
                    }
                });
            }
        } else {
            if (m_modelList != null && m_modelList.size() > 0
                    && m_delegate != null) {
                m_delegate.ModelLoaded(m_modelList);
            } else {
                if (m_delegate != null)
                    m_delegate
                            .ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
            }
        }
    }


//    public void LoadData(String key_word, ModelDelegates.ModelDelegate<YoutubeInfo> delegate) {
//        m_delegate = delegate;
//        if (NetworkConnectivity.isConnected()) {
//            ServiceHelper helper = new ServiceHelper(
//                    ServiceHelper.NEWS);
//            helper.call(ServiceHelper.URL + "?filter[category_name]=in-the-press&" + "search=" + key_word, new ServiceHelper.ServiceHelperDelegate() {
//                        @Override
//                        public void CallFinish(ServiceResponse res) {
//                            handleTodaysMessageResponse(res, 0);
//                        }
//
//                        @Override
//                        public void CallFailure(String ErrorMessage) {
//                            if (m_delegate != null)
//                                m_delegate.ModelLoadFailedWithError(ErrorMessage);
//                        }
//                    }
//
//            );
//        } else {
//            if (m_delegate != null)
//                m_delegate.ModelLoadFailedWithError("Please check internet connection.");
//        }
//
//    }

    public void loadFromDB() {
        try {
            List<YoutubeInfo> lst = CMApplication.Connection().findAll(
                    YoutubeInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<YoutubeInfo>(lst);
            } else {
                m_modelList = null;
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }


    public void handleTodaysMessageResponse(ServiceResponse res, int start) {
        if (res.RawResponse != null) {
            try {
                if (start == 0) {
                    ClearDB();
                }
                m_modelList = new ArrayList<YoutubeInfo>();
                JSONObject job = new JSONObject(res.RawResponse);
                JSONArray list = job.optJSONArray("items");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        ModelMapHelper<YoutubeInfo> mapper = new ModelMapHelper<YoutubeInfo>();
                        YoutubeInfo info = mapper.getObject(
                                YoutubeInfo.class, book);
                        if (info != null) {

                            JSONObject snippet_title = book.getJSONObject("snippet");
                            info.title = snippet_title.optString("title");
                            info.createdDate = snippet_title.optString("publishedAt");
                            info.description = snippet_title.optString("description");
                            JSONObject content = snippet_title.optJSONObject("thumbnails");
                            JSONObject content_def = content.optJSONObject("high");
                            info.url_thumbnail = content_def.optString("url");
                            JSONObject _link = snippet_title.optJSONObject("resourceId");
                            info.videoId = _link.optString("videoId");
                            info.save();
                            m_modelList.add(info);
                        }
                    }
                }
            } catch (Exception e) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
                settings.edit().putBoolean("NEWS_LOADED", true).commit();
                e.printStackTrace();
            }
            if (m_delegate != null) {
                m_delegate.ModelLoaded(m_modelList);
            }
        } else {
            if (m_delegate != null)
                m_delegate.ModelLoadFailedWithError(res.Message);
        }
    }

    public void ClearDB() {
        try {
            CMApplication.Connection().delete(YoutubeInfo.class);
            m_modelList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}