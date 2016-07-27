package gujaratcm.anandiben.model;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.activerecordbase.CamelNotationHelper;

/**
 * Created by ACER on 03-07-2016.
 */
public class NewsImagesInfo extends ActiveRecordBase {

    public int imageid = 0;
    public int imgWidth = 0;
    public int imgHeight = 0;
    public String imgUrl = "";
    public int contantid = 0;


    public static ArrayList<NewsImagesInfo> getImagesByNewsId(int newsid) {

        ArrayList<NewsImagesInfo> m_list = new ArrayList<>();
        try {
            List<NewsImagesInfo> list = CMApplication.Connection().find(NewsImagesInfo.class,
                    CamelNotationHelper.toSQLName("contantid") + "=?",
                    new String[]{newsid + ""});
            if (list != null && list.size() > 0) {
                m_list = new ArrayList<>(list);
            }

        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_list;
    }
}
