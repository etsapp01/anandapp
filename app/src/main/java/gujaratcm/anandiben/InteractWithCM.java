package gujaratcm.anandiben;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gujaratcm.anandiben.common.ModelDelegates;
import gujaratcm.anandiben.common.NetworkConnectivity;
import gujaratcm.anandiben.common.Utils;
import gujaratcm.anandiben.model.OccupationInfo;
import gujaratcm.anandiben.modellist.OccupationList;
import gujaratcm.anandiben.servicehelper.ServiceHelper;
import gujaratcm.anandiben.servicehelper.ServiceResponse;

/**
 * Created by ACER on 01-07-2016.
 */
public class InteractWithCM extends BaseFragment {

    Button btnSubmit;
    EditText edtName, edtEmail, edtMessage, edtOccupation, edtMobileNo;
    Spinner spnOccupation;

    public static InteractWithCM getInstance() {
        InteractWithCM fragment = new InteractWithCM();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.interact_with_cm, container, false);
        btnSubmit = (Button) v.findViewById(R.id.btnSubmit);

        edtName = (EditText) v.findViewById(R.id.edtName);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtMessage = (EditText) v.findViewById(R.id.edtMessage);

        edtOccupation = (EditText) v.findViewById(R.id.edtOccupation);
        edtMobileNo = (EditText) v.findViewById(R.id.edtMobileNo);

        spnOccupation = (Spinner) v.findViewById(R.id.spnOccupation);


        v.setFocusableInTouchMode(true);
        v.requestFocus();
        LoadOccupation();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager frgManager = getFragmentManager();
                        Fragment fragment = new MainFragment();
                        frgManager
                                .beginTransaction()
                                .replace(R.id.frame_container, fragment).commit();
                        return true;
                    }
                }
                return false;
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nm = edtName.getText().toString().trim();
                String em = edtEmail.getText().toString().trim();
                String msg = edtMessage.getText().toString().trim();

//                String occupation = edtOccupation.getText().toString().trim();
                String occupation = spnOccupation.getSelectedItem().toString();
                String mobile = edtMobileNo.getText().toString().trim();

                boolean flag = true;
                String msgg = "";

                if (nm != null && nm.length() == 0) {
                    flag = false;
                    msgg = "Please enter your name";
                } else if (em != null && em.length() == 0) {
                    flag = false;
                    msgg = "Please enter your email id";

                } else if (msg != null && msg.length() == 0) {
                    flag = false;
                    msgg = "Please add your message";
                } else if (!isValidEmail(em)) {
                    flag = false;
                    msgg = "Please enter valid email id";
                } else if (occupation.equalsIgnoreCase("Select Occupation")) {
                    flag = false;
                    msgg = "Please enter your occupation";

                } else if (mobile != null && mobile.length() == 0) {
                    flag = false;
                    msgg = "Please add your mobile number";
                } else if (mobile != null && mobile.length() < 10) {
                    flag = false;
                    msgg = "Please enter valid mobile number";
                }

                if (flag) {
                    setService(nm, em, msg, occupation, mobile);
                } else {
                    Utils.Instance().ShowSnack(btnSubmit, msgg);
                }
            }
        });

        return v;
    }

    public void BindOccupation(ArrayList<OccupationInfo> m_list) {
        ArrayList<String> m_string = new ArrayList<>();
        m_string.add("Select Occupation");

        if (m_list != null) {
            for (OccupationInfo oc : m_list) {
                m_string.add(oc.occupation);
            }
        }
        ArrayAdapter<String> karant_adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, m_string);
        spnOccupation.setAdapter(karant_adapter);
    }

    public void setService(String nm, String email, String message, String occupation, String mo) {
        if (NetworkConnectivity.isConnected()) {

            showProgress();
            ServiceHelper helper = new ServiceHelper(
                    ServiceHelper.INTERECT_CM, ServiceHelper.RequestMethod.POST);
            helper.addParam("write2cm", "1");
            helper.addParam("name", nm);
            helper.addParam("email", email);
            helper.addParam("message", message);
            helper.addParam("occupation", occupation);
            helper.addParam("mobileno", mo);
            helper.call(new ServiceHelper.ServiceHelperDelegate() {
                @Override
                public void CallFinish(ServiceResponse res) {
                    hideProgress();
                    Utils.Instance().ShowSnack(btnSubmit, "Thank you for your submit.");
                    edtMessage.setText("");
                    edtName.setText("");
                    edtEmail.setText("");
                    edtMobileNo.setText("");
                    edtOccupation.setText("");
                    edtName.requestFocus();
                    hidekeyboard();
                }

                @Override
                public void CallFailure(String ErrorMessage) {
                    hideProgress();
                    Utils.Instance().ShowSnack(btnSubmit, ErrorMessage);
                    hidekeyboard();
                }
            });
        } else {
            Utils.Instance().ShowSnack(btnSubmit, "Please check your internet connection.");
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void hidekeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void LoadOccupation() {
        OccupationList.Instance().LoadData(new ModelDelegates.ModelDelegate<OccupationInfo>() {
            @Override
            public void ModelLoaded(ArrayList<OccupationInfo> list) {
                BindOccupation(list);
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                BindOccupation(null);
            }
        });
    }
}
