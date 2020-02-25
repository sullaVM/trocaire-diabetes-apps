package com.example.diabetesapp.data.requests;

import com.example.diabetesapp.data.responses.StoreWeightResponse;

public class StoreWeightRequest extends PatientRequest<StoreWeightResponse> {
    private Integer patientID;
    private String time;
    private Float weightKG;

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
}
