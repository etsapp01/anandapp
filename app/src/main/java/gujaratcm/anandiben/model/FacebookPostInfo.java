package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 23-07-2016.
 */
public class FacebookPostInfo extends ActiveRecordBase {

    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int postid = 0;
    @ModelMapper(JsonKey = "message")
    public String message = "";
    @ModelMapper(JsonKey = "full_picture")
    public String full_picture = "";
    @ModelMapper(JsonKey = "created_time")
    public String created_time = "";
    public int likecount = 0;
    public int commentcount = 0;

    public static void ClearDb() {
        try {
            CMApplication.Connection().delete(FacebookPostInfo.class);
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FacebookPostInfo> getAll() {
        ArrayList<FacebookPostInfo> m_list = new ArrayList<>();

        try {
            List<FacebookPostInfo> lst = CMApplication.Connection().findAll(FacebookPostInfo.class);
            if (lst != null) {
                m_list = new ArrayList<>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_list;
    }

}
