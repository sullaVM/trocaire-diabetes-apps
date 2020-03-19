package com.example.diabetesapp.data.requests;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.StoreBSLResponse;

public class StoreBSLRequest extends PatientRequest<StoreBSLResponse> {
    private Integer patientID;
    private String time;
    private Float value;
    @Nullable
    private String unit;
    private String tokenID;

    public StoreBSLRequest(Integer patientID, String time, Float value, @Nullable String unit) {
        this.patientID = patientID;
        this.time = time;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String requestRoute() {
        return "storeBSL";
    }

    @Override
    public Class responseClass() {
        return StoreBSLResponse.class;
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
