package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.StorePatientLogResponse;

public class StorePatientLogRequest extends Request<StorePatientLogResponse> {
    private Integer patientID;
    private String time;
    private String note;
    private int followUpReminderInWeeks;

    public StorePatientLogRequest(Integer patientID, String time, String note, int weeks) {
        this.patientID = patientID;
        this.time = time;
        this.note = note;
        this.followUpReminderInWeeks = weeks;
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
