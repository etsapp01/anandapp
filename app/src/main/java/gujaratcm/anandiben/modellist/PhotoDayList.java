package gujaratcm.anandiben.modellist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.ModelMapHelper;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.model.ImportantDecisionInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.PhotoDayInfo;
import gujaratcm.anandiben.model.PhotoDayInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 04-07-2016.
 */
public class PhotoDayList {
    //    2941
    protected PhotoDayList() {
    }

    private static volatile PhotoDayList _instance = null;
    public ModelDelegates.ModelDelegate<PhotoDayInfo> m_delegate = null;
    protected ArrayList<PhotoDayInfo> m_modelList = null;

    public static PhotoDayList Instance() {
        if (_instance == null) {
            synchronized (PhotoDayList.class) {
                _instance = new PhotoDayList();
            }
        }
        return _instance;
    }

    public void LoadData(final int start_form, ModelDelegates.ModelDelegate<PhotoDayInfo> delegate) {
        m_delegate = delegate;
        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.SCHEMES);
//                helper.addParam("per_page", 20);
//                helper.addParam("offset", start_form);
//                helper.call(ServiceHelper.PHOTOOF_DAY + "?filter[tag]=photo-of-the-day", new ServiceHelper.ServiceHelperDelegate() {
            helper.call(ServiceHelper.PHOTOOF_DAY, new ServiceHelper.ServiceHelperDelegate() {
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
        } else {
            loadFromDB();
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

    public void loadFromDB() {
        try {
            List<PhotoDayInfo> lst = CMApplication.Connection().findAll(
                    PhotoDayInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<PhotoDayInfo>(lst);
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
//                if (start == 0) {
                ClearDB();
//                }
                m_modelList = new ArrayList<PhotoDayInfo>();
                JSONArray list = new JSONArray(res.RawResponse);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        boolean isPhotooftheday = false;
                        JSONArray jarr = book.getJSONArray("tags");
                        if (jarr != null && jarr.length() > 0) {
                            for (int j = 0; j < jarr.length(); j++) {
                                String val = jarr.getString(j);
                                if (Integer.parseInt(val) == 3061) {
                                    isPhotooftheday = true;
                                    break;
                                }
                            }
                        } else {
                            isPhotooftheday = false;
                        }
                        if (isPhotooftheday) {
                            ModelMapHelper<PhotoDayInfo> mapper = new ModelMapHelper<PhotoDayInfo>();
                            PhotoDayInfo info = mapper.getObject(
                                    PhotoDayInfo.class, book);
                            if (info != null) {
                                JSONObject title = book.getJSONObject("title");
                                info.title = title.optString("rendered");

                                if (book.has("media_details")) {
                                    JSONObject mediadetail = book.getJSONObject("media_details");
                                    if (mediadetail != null) {
                                        if (mediadetail.has("sizes")) {
                                            JSONObject sizes = mediadetail.getJSONObject("sizes");
                                            if (sizes != null) {
                                                if (sizes.has("thumbnail")) {
                                                    JSONObject image = sizes.getJSONObject("thumbnail");
                                                    if (image != null) {
                                                        info.thumbnail = image.optString("source_url");
                                                    }
                                                }
                                                if (sizes.has("medium")) {
                                                    JSONObject image = sizes.getJSONObject("medium");
                                                    if (image != null) {
                                                        info.medium = image.optString("source_url");
                                                    }
                                                }
                                                if (sizes.has("full")) {
                                                    JSONObject image = sizes.getJSONObject("full");
                                                    if (image != null) {
                                                        info.fullimage = image.optString("source_url");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
//                            JSONArray attachments = _link.optJSONArray("wp:attachment");
//                            if (attachments.length() > 0) {
//                                JSONObject attachmenturlobj = attachments.getJSONObject(0);
//                                info.attachmentsurl = attachmenturlobj.optString("href");
//                            }
//                            StartLoadImages(info.attachmentsurl);
                                info.save();
                                m_modelList.add(info);
                            }
                        }
                    }
                }
            } catch (Exception e) {
//                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CMApplication.getContext());
//                settings.edit().putBoolean("NEWS_LOADED", true).commit();
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
            CMApplication.Connection().delete(PhotoDayInfo.class);
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