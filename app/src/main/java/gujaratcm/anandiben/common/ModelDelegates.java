package gujaratcm.anandiben.common;


import java.util.ArrayList;

import gujaratcm.anandiben.model.FacebookPostInfo;
import gujaratcm.anandiben.model.NewsDetailInfo;
import gujaratcm.anandiben.servicehelper.ServiceResponse;


public class ModelDelegates {

    public interface LoginDelegate {
        public void LoginDidSuccess();

        public void LoginFailedWithError(String error);
    }

    // public interface DeviceRegDelegate {
    // public void DeviceRegDidSuccess(String msg);
    //
    // public void DeviceRegFailedWithError(String error);
    // }
    //
    // public interface LogoutDelegate {
    // public void LogoutDidSuccess();
    //
    // public void LogoutFailedWithError(String error);
    // }
    //
    public interface CommonInfoDelegate<T> {

        public void CallFailedWithError(String error);

        public void CallDidSuccess(T info);
    }

    public interface UniversalDelegate {
        public void CallDidSuccess(ServiceResponse res);

        public void CallFailedWithError(String error);
    }


    public interface UniversalNewsDetailDelegate {
        public void CallDidSuccess(NewsDetailInfo res);

        public void CallFailedWithError(String error);
    }

    public interface CommonDelegate1 {
        public void CallDidSuccess(String msg);

        public void CallFailedWithError(String error);
    }

    public interface ModelDelegate<T> {
        public void ModelLoaded(ArrayList<T> list);

        public void ModelLoadFailedWithError(String error);

    }


    public interface LatLangDelegate {
        public void CallSuccess(double d1, double d2);

        public void CallFailureWithError(String error);
    }

    public interface FacebookPostDelegate{
        public void OnGetPost(ArrayList<FacebookPostInfo> arrayList);

        public void OnFail(String fail);
    }
}
