package com.example.diabetesapp.data.requests;

import androidx.annotation.Nullable;

public class GetGraphingDataRequest extends PatientRequest {
    private Integer patientID;
    private String intervalStart;
    private String intervalEnd;
    @Nullable
    private String bslUnit;
    public final String requestRoute = "getGraphingData";
}
