package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.StoreWeightResponse;

public class StoreWeightRequest extends PatientRequest<StoreWeightResponse> {
    private Integer patientID;
    private String time;
    private Float weightKG;
    private String tokenID;

    public StoreWeightRequest(Integer patientID, String time, Float weightKG) {
        this.patientID = patientID;
        this.time = time;
        this.weightKG = weightKG;
    }

    @Override
    public String requestRoute() {
        return "storeWeight";
    }

    @Override
    public Class responseClass() {
        return StoreWeightResponse.class;
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
