package com.example.diabetesapp.data.responses;

import androidx.annotation.Nullable;

public class GetGraphingDataResponse {
    private Boolean success;
    @Nullable
    private RBPRecord[] RBP;
    @Nullable
    private BSLRecord[] BSL;
    @Nullable
    private WeightRecord[] Weight;
}

class RBPRecord {
    private String time;
    private Float systole;
    private Float diastole;
}

class BSLRecord {
    private String time;
    private Float value;
}

class WeightRecord {
    private String time;
    private Float value;
}