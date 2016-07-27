package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by Virag kuvadia on 09-07-2016.
 */
public class PhotoDayInfo extends ActiveRecordBase {

    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int newsid = 0;
    @ModelMapper(JsonKey = "caption")
    public String content = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String created_date = "";
    @ModelMapper(JsonKey = "date")
    public String title = "";
    public String attachmentsurl = "";

    public String thumbnail = "";
    public String medium = "";
    public String fullimage = "";


    public static ArrayList<PhotoDayInfo> getAll() {
        ArrayList<PhotoDayInfo> m_modelList = new ArrayList<>();
        try {
            List<PhotoDayInfo> lst = CMApplication.Connection().findAll(
                    PhotoDayInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<PhotoDayInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }
}
