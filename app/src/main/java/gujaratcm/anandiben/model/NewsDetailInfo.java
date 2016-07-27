package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by Virag kuvadia on 06-07-2016.
 */
public class NewsDetailInfo extends ActiveRecordBase {

    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int newsid = 0;
    @ModelMapper(JsonKey = "date")
    public String date = "";
    @ModelMapper(JsonKey = "slug")
    public String slug = "";

    @ModelMapper(JsonKey = "rendered")
    public String detail = "";
    @ModelMapper(JsonKey = "more_detail")
    public String more_detail = "";


    public static NewsDetailInfo getDetailByContainId(int id) {
        try {
            List<NewsDetailInfo> list = CMApplication.Connection().find(NewsDetailInfo.class,
                    CamelNotationHelper.toSQLName("newsid") + "=?",
                    new String[]{id + ""});
            if (list != null && list.size() > 0) {
                return list.get(0);
            }

        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return null;
    }
}
