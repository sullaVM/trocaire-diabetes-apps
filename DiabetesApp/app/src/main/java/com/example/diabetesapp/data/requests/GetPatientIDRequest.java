package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.GetPatientIDResponse;

public class GetPatientIDRequest extends PatientRequest {
    private String userName;

    public GetPatientIDRequest(String userName) {
        this.userName = userName;
    }

    @Override
    public String requestRoute() {
        return "getPatientID";
    }

    @Override
    public Class responseClass() {
        return GetPatientIDResponse.class;
    }

    @Override
    public int requestType() {
        return Request.Method.POST;
    }

    @Override
    public void setTokenID(String tokenID) {

    }
}
