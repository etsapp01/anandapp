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
import gujaratcm.anandiben.model.BlogInfo;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.NewsInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 21-07-2016.
 */
public class BlogList {

    protected BlogList() {
    }

    private static volatile BlogList _instance = null;
    public ModelDelegates.ModelDelegate<BlogInfo> m_delegate = null;
    protected ArrayList<BlogInfo> m_modelList = null;

    public static BlogList Instance() {
        if (_instance == null) {
            synchronized (BlogList.class) {
                _instance = new BlogList();
            }
        }
        return _instance;
    }

    public void LoadData(final int start_form, ModelDelegates.ModelDelegate<BlogInfo> delegate) {
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
                helper.addParam("filter[category_name]", "blog");
                helper.addParam("per_page", 20);
                helper.addParam("offset", start_form);
                helper.call(new ServiceHelper.ServiceHelperDelegate() {
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


    public void LoadData(String key_word, ModelDelegates.ModelDelegate<BlogInfo> delegate) {
        m_delegate = delegate;
        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.NEWS);
            helper.call(ServiceHelper.URL + "?filter[category_name]=blog&" + "search=" + key_word, new ServiceHelper.ServiceHelperDelegate() {
                        @Override
                        public void CallFinish(ServiceResponse res) {
                            handleTodaysMessageResponse(res, 0);
                        }

                        @Override
                        public void CallFailure(String ErrorMessage) {
                            if (m_delegate != null)
                                m_delegate.ModelLoadFailedWithError(ErrorMessage);
                        }
                    }

            );
        } else {
            if (m_delegate != null)
                m_delegate.ModelLoadFailedWithError("Please check internet connection.");
        }

    }

    public void loadFromDB() {
        try {
            List<BlogInfo> lst = CMApplication.Connection().findAll(
                    BlogInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<BlogInfo>(lst);
            } else {
                m_modelList = null;
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }



    public void handleTodaysMessageResponse(ServiceResponse res, int start) {
        if (res.isSuccess) {
            try {
                if (start == 0) {
                    ClearDB();
                }
                m_modelList = new ArrayList<BlogInfo>();
                JSONArray list = new JSONArray(res.RawResponse);
                if (list!=null && list.length()<20){
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
                    settings.edit().putBoolean("BLOG_LOADED", true).commit();
                }
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        ModelMapHelper<BlogInfo> mapper = new ModelMapHelper<BlogInfo>();
                        BlogInfo info = mapper.getObject(
                                BlogInfo.class, book);
                        if (info != null) {
                            JSONObject title = book.getJSONObject("title");
                            info.title = title.optString("rendered");
                            JSONObject content = book.getJSONObject("content");
                            info.content = content.optString("rendered");
                            JSONObject _link = book.optJSONObject("_links");
                            JSONArray attachments = _link.optJSONArray("wp:attachment");
                            if (attachments.length() > 0) {
                                JSONObject attachmenturlobj = attachments.getJSONObject(0);
                                info.attachmentsurl = attachmenturlobj.optString("href");
                            }
                            StartLoadImages(info.attachmentsurl);
                            info.save();
                            m_modelList.add(info);
                        }
                    }
                }
            } catch (Exception e) {
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
            CMApplication.Connection().delete(BlogInfo.class);
            m_modelList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void StartLoadImages(String url) {
        if (url != null && !url.isEmpty()) {
            if (NetworkConnectivity.isConnected()) {
                ServiceHelper helper = new ServiceHelper(
                        ServiceHelper.NEWS);
                helper.call(url, new ServiceHelper.ServiceHelperDelegate() {
                    @Override
                    public void CallFinish(ServiceResponse res) {
                        if (res.isSuccess) {
                            try {
                                JSONArray list = new JSONArray(res.RawResponse);
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject book = list.optJSONObject(i);
                                    if (book != null) {
                                        NewsImagesInfo info = CMApplication.Connection().newEntity(NewsImagesInfo.class);
                                        info.imageid = book.optInt("id");
                                        info.imgWidth = book.optJSONObject("media_details").optInt("width");
                                        info.imgHeight = book.optJSONObject("media_details").optInt("height");
                                        info.imgUrl = book.optString("source_url");
                                        info.contantid = book.optInt("post");
                                        info.save();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ActiveRecordException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void CallFailure(String ErrorMessage) {
                    }
                });
            }
        }

    }

}