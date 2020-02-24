package com.example.diabetesapp.data.requests;

public class StoreRBPRequest extends PatientRequest {
    private Integer patientID;
    private String time;
    private Float systole;
    private Float diastole;
    public final String requestRoute = "storeRBP";
}
