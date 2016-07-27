package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 21-07-2016.
 */
public class InfographyInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int infoid = 0;
    @ModelMapper(JsonKey = "rendered")
    public String content = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";
    @ModelMapper(JsonKey = "date")
    public String title = "";
    public String attachmentsurl = "";


    public static ArrayList<InfographyInfo> getAll() {
        ArrayList<InfographyInfo> m_modelList = new ArrayList<>();
        try {
            List<InfographyInfo> lst = CMApplication.Connection().findAll(
                    InfographyInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<InfographyInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }

    public static InfographyInfo getNewsById(int blogid) {
        try {
            List<InfographyInfo> lst = CMApplication.Connection().find(
                    InfographyInfo.class, CamelNotationHelper.toSQLName("infoid") + "=?", new String[]{blogid + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }


}