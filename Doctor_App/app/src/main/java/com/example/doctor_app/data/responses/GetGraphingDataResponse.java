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

