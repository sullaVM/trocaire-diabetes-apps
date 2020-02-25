package com.example.doctor_app.data.responses;

import androidx.annotation.Nullable;

public class GetGraphingDataResponse extends PatientResponse {
    @Nullable
    public RBPRecord[] RBP;
    @Nullable
    public BSLRecord[] BSL;
    @Nullable
    public WeightRecord[] Weight;
}

class RBPRecord {
    public String time;
    public Float systole;
    public Float diastole;
}

class BSLRecord {
    public String time;
    public Float value;
}

class WeightRecord {
    public String time;
    public Float value;
}