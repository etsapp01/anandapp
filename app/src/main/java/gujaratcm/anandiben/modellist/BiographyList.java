package gujaratcm.anandiben.modellist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.ModelMapHelper;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.model.BiographyInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 02-07-2016.
 */
public class BiographyList {

    protected BiographyList() {
    }

    private static volatile BiographyList _instance = null;
    public ModelDelegates.ModelDelegate<BiographyInfo> m_delegate = null;
    protected ArrayList<BiographyInfo> m_modelList = null;

    public static BiographyList Instance() {
        if (_instance == null) {
            synchronized (BiographyList.class) {
                _instance = new BiographyList();
            }
        }
        return _instance;
    }

    public void LoadTodayQuotesData(ModelDelegates.ModelDelegate<BiographyInfo> delegate) {
        m_delegate = delegate;
        loadFromDB();
        if (m_modelList == null || m_modelList.size() <= 0) {
            if (NetworkConnectivity.isConnected()) {
                ServiceHelper helper = new ServiceHelper(
                        ServiceHelper.BIOGRAPHY);
                helper.addParam("filter[name]", "know-the-cm");
                helper.call(new ServiceHelper.ServiceHelperDelegate() {
                    @Override
                    public void CallFinish(ServiceResponse res) {
                        handleTodaysMessageResponse(res);
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

    public void loadFromDB() {
        try {
            List<BiographyInfo> lst = CMApplication.Connection().findAll(
                    BiographyInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<BiographyInfo>(lst);
            } else {
                m_modelList = null;
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public void handleTodaysMessageResponse(ServiceResponse res) {
        if (res.isSuccess) {
            try {
                ClearDB();
                m_modelList = new ArrayList<BiographyInfo>();
                JSONArray list = new JSONArray(res.RawResponse);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        ModelMapHelper<BiographyInfo> mapper = new ModelMapHelper<BiographyInfo>();
                        BiographyInfo info = mapper.getObject(
                                BiographyInfo.class, book);
                        if (info != null) {
                            JSONObject title = book.getJSONObject("title");
                            info.title = title.optString("rendered");
                            JSONObject excerpt = book.getJSONObject("content");
                            info.content = excerpt.optString("rendered");
                            info.image = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSUpzfV3CT7cANiQcB9yxeCVDfy3-7YfHjeaz3WBKhl4YMk831swg";
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
            CMApplication.Connection().delete(BiographyInfo.class);
            m_modelList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
