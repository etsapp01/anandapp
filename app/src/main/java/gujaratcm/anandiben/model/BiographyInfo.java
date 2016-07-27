package gujaratcm.anandiben.model;

import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.common.ModelMapper;

/**
 * Created by ACER on 02-07-2016.
 */
public class BiographyInfo extends ActiveRecordBase {

    public String content = "";
    public String title = "";
    @ModelMapper(JsonKey = "link")
    public String link = "";
    @ModelMapper(JsonKey = "date")
    public String creataed_date = "";
    @ModelMapper(JsonKey = "modified")
    public String modifieddate = "";
    @ModelMapper(JsonKey = "id", IsUnique = true)
    public int BId = 0;
    public String image = "";
}
