package gujaratcm.anandiben.model;

import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 25-07-2016.
 */
public class OccupationInfo extends ActiveRecordBase {
    public String occupation = "";

    public static ArrayList<OccupationInfo> getAll() {
        ArrayList<OccupationInfo> m_modelList = new ArrayList<>();
        try {
            List<OccupationInfo> lst = CMApplication.Connection().findAll(
                    OccupationInfo.class);
            if (lst != null && lst.size() > 0) {
                m_modelList = new ArrayList<OccupationInfo>(lst);
            }
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return m_modelList;
    }
}
