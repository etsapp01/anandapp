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
public class SchemesInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int schemeid = 0;
    public String content = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";
    public String title = "";
    public String attachmentsurl = "";


    public static ArrayList<SchemesInfo> getAll() {
        ArrayList<SchemesInfo> m_modelList = new ArrayList<>();
        try {
            List<SchemesInfo> lst = CMApplication.Connection().findAll(
                    SchemesInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<SchemesInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }
    public static SchemesInfo getDataById(int id) {
        try {
            List<SchemesInfo> lst = CMApplication.Connection().find(
                    SchemesInfo.class, CamelNotationHelper.toSQLName("schemeid") + "=?", new String[]{id + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }


}