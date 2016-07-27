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
public class NewsInfo extends ActiveRecordBase {


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


    public static ArrayList<NewsInfo> getAll() {
        ArrayList<NewsInfo> m_modelList = new ArrayList<>();
        try {
            List<NewsInfo> lst = CMApplication.Connection().findAll(
                    NewsInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<NewsInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }

    public static NewsInfo getNewsById(int newsid) {
        try {
            List<NewsInfo> lst = CMApplication.Connection().find(
                    NewsInfo.class, CamelNotationHelper.toSQLName("newsid") + "=?", new String[]{newsid + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }


}
