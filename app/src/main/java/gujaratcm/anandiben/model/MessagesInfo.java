package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 02-07-2016.
 */
public class MessagesInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int messageId = 0;
    @ModelMapper(JsonKey = "rendered")
    public String quote = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";


    public static ArrayList<MessagesInfo> getAll() {
        ArrayList<MessagesInfo> m_modelList = new ArrayList<>();
        try {
            List<MessagesInfo> lst = CMApplication.Connection().findAll(
                    MessagesInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<MessagesInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }

    public static MessagesInfo getToday() {
        ArrayList<MessagesInfo> m_modelList = getAll();
        if (m_modelList != null && m_modelList.size() > 0) {
            return m_modelList.get(0);
        }
        return null;
    }
}
