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
public class YoutubeInfo extends ActiveRecordBase {


    @ModelMapper(JsonKey = "url")
    public String url_thumbnail = "";
    @ModelMapper(JsonKey = "playlistId")
    public String playlistId = "";
    @ModelMapper(JsonKey = "videoId")
    public String videoId = "";
    @ModelMapper(JsonKey = "title")
    public String title = "";
    @ModelMapper(JsonKey = "description")
    public String description = "";
    public String createdDate="";


    public static ArrayList<YoutubeInfo> getAll() {
        ArrayList<YoutubeInfo> m_modelList = new ArrayList<>();
        try {
            List<YoutubeInfo> lst = CMApplication.Connection().findAll(
                    YoutubeInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<YoutubeInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }

    public static YoutubeInfo getNewsById(int newsid) {
        try {
            List<YoutubeInfo> lst = CMApplication.Connection().find(
                    YoutubeInfo.class, CamelNotationHelper.toSQLName("newsid") + "=?", new String[]{newsid + ""});
            if (lst != null && lst.size() > 0) {
                return lst.get(0);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }


}
