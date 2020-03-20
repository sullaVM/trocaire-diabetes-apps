package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.PatientLogoutResponse;

public class PatientLogoutRequest extends PatientRequest<PatientLogoutResponse> {
    private Integer patientID;

    public PatientLogoutRequest(Integer patientID) {
        this.patientID = patientID;
    }

    @Override
    public String requestRoute() {
        return "patientLogout";
    }

    @Override
    public Class responseClass() {
        return PatientLogoutResponse.class;
    }

    @Override
    public int requestType() {
        return Request.Method.POST;
    }

    @Override
    public void setTokenID(String tokenID) {

    }
}
