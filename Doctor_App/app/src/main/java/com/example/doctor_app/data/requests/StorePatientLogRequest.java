package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.StorePatientLogResponse;

public class StorePatientLogRequest extends Request<StorePatientLogResponse> {
    private Integer patientID;
    private String time;
    private String note;

    public StorePatientLogRequest(Integer patientID, String time, String note) {
        this.patientID = patientID;
        this.time = time;
        this.note = note;
    }

    @Override
    public String requestRoute() {
        return "api/storePatientLog";
    }

    @Override
    public Class responseClass() {
        return StorePatientLogResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
