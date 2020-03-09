package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.StoreRBPResponse;

public class StoreRBPRequest extends PatientRequest<StoreRBPResponse> {
    private Integer patientID;
    private String time;
    private Float systole;
    private Float diastole;
    private String tokenID;

    public StoreRBPRequest(Integer patientID, String time, Float systole, Float diastole) {
        this.patientID = patientID;
        this.time = time;
        this.systole = systole;
        this.diastole = diastole;
    }

    @Override
    public String requestRoute() {
        return "storeRBP";
    }

    @Override
    public Class responseClass() {
        return StoreRBPResponse.class;
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
