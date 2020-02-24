package com.example.diabetesapp.data.requests;

import androidx.annotation.Nullable;

public class StoreBSLRequest extends PatientRequest {
    private Integer patientID;
    private String time;
    private Float value;
    @Nullable
    private String unit;
    public final String requestRoute = "storeRBP";
}
