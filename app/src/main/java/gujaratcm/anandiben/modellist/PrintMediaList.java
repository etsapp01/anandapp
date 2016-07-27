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
import gujaratcm.anandiben.model.ImportantDecisionInfo;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.model.PrintMediaInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 03-07-2016.
 */
public class PrintMediaList {

    protected PrintMediaList() {
    }

    private static volatile PrintMediaList _instance = null;
    public ModelDelegates.ModelDelegate<PrintMediaInfo> m_delegate = null;
    protected ArrayList<PrintMediaInfo> m_modelList = null;
    ModelDelegates.UniversalNewsDetailDelegate detail_delegate;

    public static PrintMediaList Instance() {
        if (_instance == null) {
            synchronized (PrintMediaList.class) {
                _instance = new PrintMediaList();
            }
        }
        return _instance;
    }

    public void LoadData(final int start_form, ModelDelegates.ModelDelegate<PrintMediaInfo> delegate) {
        m_delegate = delegate;
        if (start_form == 0) {
            loadFromDB();
        } else {
            m_modelList = null;
        }
        if (m_modelList == null || m_modelList.size() <= 0) {
            if (NetworkConnectivity.isConnected()) {
                ServiceHelper helper = new ServiceHelper(
                        ServiceHelper.PRINT_MEDIA);
                helper.addParam("filter[category_name]", "print-media");
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

    public void LoadData(String keyword, ModelDelegates.ModelDelegate<PrintMediaInfo> delegate) {
        m_delegate = delegate;
        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.PRINT_MEDIA);
            helper.addParam("filter[category_name]", "print-media");
            helper.addParam("search", keyword);
//                helper.addParam("offset", start_form);
            helper.call(new ServiceHelper.ServiceHelperDelegate() {
                @Override
                public void CallFinish(ServiceResponse res) {
                    handleTodaysMessageResponse(res, 0);
                }

                @Override
                public void CallFailure(String ErrorMessage) {
                    if (m_delegate != null)
                        m_delegate.ModelLoadFailedWithError(ErrorMessage);
                }
            });
        }
    }

    public void LoadDetailData(int id, ModelDelegates.UniversalNewsDetailDelegate del) {
        detail_delegate = del;
        NewsDetailInfo info = getNewsDetail(id);
        if (info == null) {
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
                                    detail_delegate.CallDidSuccess(info);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void CallFailure(String ErrorMessage) {
                        if (detail_delegate != null)
                            detail_delegate.CallFailedWithError(ErrorMessage);
                    }
                });
            }
        } else {
            if (detail_delegate != null) {
                detail_delegate.CallDidSuccess(info);
            } else {
                if (m_delegate != null)
                    m_delegate
                            .ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
            }
        }
    }


    public void loadFromDB() {
        try {
            List<PrintMediaInfo> lst = CMApplication.Connection().findAll(
                    PrintMediaInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<PrintMediaInfo>(lst);
            } else {
                m_modelList = null;
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }


    public static NewsDetailInfo getNewsDetail(int newsid) {
        try {
            List<NewsDetailInfo> lst = CMApplication.Connection().find(
                    NewsDetailInfo.class,
                    CamelNotationHelper.toSQLName("newsid") + "=?",
                    new String[]{"" + newsid});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleTodaysMessageResponse(ServiceResponse res, int start) {
        if (res.isSuccess) {
            try {
                if (start == 0) {
                    ClearDB();
                }
                m_modelList = new ArrayList<PrintMediaInfo>();
                JSONArray list = new JSONArray(res.RawResponse);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        ModelMapHelper<PrintMediaInfo> mapper = new ModelMapHelper<PrintMediaInfo>();
                        PrintMediaInfo info = mapper.getObject(
                                PrintMediaInfo.class, book);
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
            CMApplication.Connection().delete(PrintMediaInfo.class);
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