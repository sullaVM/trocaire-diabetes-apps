package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.GetPatientLogsResponse;

public class GetPatientLogsRequest extends Request<GetPatientLogsResponse> {
    private Integer patientID;
    private String intervalStart;
    private String intervalEnd;

    public GetPatientLogsRequest(Integer patientID, String intervalStart, String intervalEnd) {
        this.patientID = patientID;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
    }

    @Override
    public String requestRoute() {
        return "api/getPatientLogs";
    }

    @Override
    public Class responseClass() {
        return GetPatientLogsResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
