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
import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.ModelMapHelper;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.model.BlogInfo;
import gujaratcm.anandiben.model.NewsImagesInfo;
import gujaratcm.anandiben.model.OccupationInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 25-07-2016.
 */
public class OccupationList {

    protected OccupationList() {
    }

    private static volatile OccupationList _instance = null;
    public ModelDelegates.ModelDelegate<OccupationInfo> m_delegate = null;
    protected ArrayList<OccupationInfo> m_modelList = null;

    public static OccupationList Instance() {
        if (_instance == null) {
            synchronized (OccupationList.class) {
                _instance = new OccupationList();
            }
        }
        return _instance;
    }

    public void LoadData(ModelDelegates.ModelDelegate<OccupationInfo> delegate) {
        m_delegate = delegate;
        loadFromDB();
        if (NetworkConnectivity.isConnected()) {
            if (m_modelList == null || m_modelList.size() <= 0) {
                ServiceHelper helper = new ServiceHelper(
                        ServiceHelper.OCCUPATION);
                helper.addParam("write2cm", "1");
                helper.addParam("occupationlist", 1);
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
            } else {
                if (m_delegate != null) {
                    m_delegate.ModelLoaded(m_modelList);
                }
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
            List<OccupationInfo> lst = CMApplication.Connection().findAll(
                    OccupationInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<OccupationInfo>(lst);
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
                m_modelList = new ArrayList<OccupationInfo>();
                JSONObject mainobj = new JSONObject(res.RawResponse);
                JSONArray list = mainobj.optJSONArray("occupationlist");
                for (int i = 0; i < list.length(); i++) {
                    JSONArray array = list.optJSONArray(i);
                    OccupationInfo info = CMApplication.Connection().newEntity(OccupationInfo.class);
                    info.occupation = array.optString(1);
                    info.save();
                    m_modelList.add(info);
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
            CMApplication.Connection().delete(OccupationInfo.class);
            m_modelList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}