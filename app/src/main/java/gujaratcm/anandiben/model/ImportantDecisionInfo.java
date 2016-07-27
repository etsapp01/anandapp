package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 04-07-2016.
 */
public class ImportantDecisionInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int desicionid = 0;
    public String content = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";
    public String title = "";
    public String attachmentsurl = "";


    public static ArrayList<ImportantDecisionInfo> getAll() {
        ArrayList<ImportantDecisionInfo> m_modelList = new ArrayList<>();
        try {
            List<ImportantDecisionInfo> lst = CMApplication.Connection().findAll(
                    ImportantDecisionInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<ImportantDecisionInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }

    public static ImportantDecisionInfo getDataById(int id) {
        try {
            List<ImportantDecisionInfo> lst = CMApplication.Connection().find(
                    ImportantDecisionInfo.class, CamelNotationHelper.toSQLName("desicionid") + "=?", new String[]{id + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }

}