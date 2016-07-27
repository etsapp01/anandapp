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
import gujaratcm.anandiben.model.MessagesInfo;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 02-07-2016.
 */
public class MessageList {

    protected MessageList() {
    }

    private static volatile MessageList _instance = null;
    public ModelDelegates.ModelDelegate<MessagesInfo> m_delegate = null;
    protected ArrayList<MessagesInfo> m_modelList = null;

    public static MessageList Instance() {
        if (_instance == null) {
            synchronized (MessageList.class) {
                _instance = new MessageList();
            }
        }
        return _instance;
    }

    public void LoadTodayQuotesData(ModelDelegates.ModelDelegate<MessagesInfo> delegate) {
        m_delegate = delegate;

        if (NetworkConnectivity.isConnected()) {
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.QUOTES);
            helper.addParam("filter[category_name]", "quotes");
            helper.addParam("per_page", 20);
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
            List<MessagesInfo> lst = CMApplication.Connection().findAll(
                    MessagesInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<MessagesInfo>(lst);
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
                m_modelList = new ArrayList<MessagesInfo>();
                JSONArray list = new JSONArray(res.RawResponse);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject book = list.optJSONObject(i);
                    if (book != null) {
                        ModelMapHelper<MessagesInfo> mapper = new ModelMapHelper<MessagesInfo>();
                        MessagesInfo info = mapper.getObject(
                                MessagesInfo.class, book);
                        if (info != null) {
                            JSONObject title = book.getJSONObject("title");
                            info.quote = title.optString("rendered");
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
            CMApplication.Connection().delete(MessagesInfo.class);
            m_modelList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}