package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.GetPatientProfileResponse;

public class GetPatientProfileRequest extends PatientRequest<GetPatientProfileResponse> {
    private Integer patientID;
    private String tokenID;

    public GetPatientProfileRequest(Integer patientID) {
        this.patientID = patientID;
    }

    @Override
    public String requestRoute() {
        return "getPatientProfile";
    }

    @Override
    public Class responseClass() {
        return GetPatientProfileResponse.class;
    }

    @Override
    public int requestType() {
        return Request.Method.POST;
    }

    @Override
    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }
}
