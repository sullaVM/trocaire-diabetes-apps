package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.GetGraphingDataResponse;

import androidx.annotation.Nullable;

public class GetGraphingDataRequest extends Request<GetGraphingDataResponse> {
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
        return "api/getGraphingData";
    }

    @Override
    public Class responseClass() {
        return GetGraphingDataResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
