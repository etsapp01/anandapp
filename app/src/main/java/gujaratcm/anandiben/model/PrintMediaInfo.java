package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 03-07-2016.
 */
public class PrintMediaInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int newsid = 0;
    @ModelMapper(JsonKey = "rendered")
    public String content = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";
    @ModelMapper(JsonKey = "date")
    public String title = "";
    public String attachmentsurl = "";


    public static ArrayList<PrintMediaInfo> getAll() {
        ArrayList<PrintMediaInfo> m_modelList = new ArrayList<>();
        try {
            List<PrintMediaInfo> lst = CMApplication.Connection().findAll(
                    PrintMediaInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<PrintMediaInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }
    public static PrintMediaInfo getDataById(int id) {
        try {
            List<PrintMediaInfo> lst = CMApplication.Connection().find(
                    PrintMediaInfo.class, CamelNotationHelper.toSQLName("newsid") + "=?", new String[]{id + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }

}
