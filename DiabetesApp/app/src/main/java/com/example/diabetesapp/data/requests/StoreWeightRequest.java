package com.example.diabetesapp.data.requests;

public class StoreWeightRequest extends PatientRequest{
    private Integer patientID;
    private String time;
    private Float weightKG;
    public final String requestRoute = "storeWeight";
}
