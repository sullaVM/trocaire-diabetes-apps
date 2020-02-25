package com.example.diabetesapp.data.requests;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.example.diabetesapp.GsonRequest;
import com.example.diabetesapp.data.responses.GetGraphingDataResponse;
import com.google.gson.Gson;

public class GetGraphingDataRequest extends PatientRequest<GetGraphingDataResponse> {
    private Integer patientID;
    private String intervalStart;
    private String intervalEnd;
    @Nullable
    private String bslUnit;

    public GetGraphingDataRequest(Integer patientID, String intervalStart, String intervalEnd, @Nullable String bslUnit) {
        this.patientID = patientID;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.bslUnit = bslUnit;
    }

    @Override
    public String requestRoute() {
        return "getGraphingData";
    }

    @Override
    public Class responseClass() {
        return GetGraphingDataResponse.class;
    }
}
