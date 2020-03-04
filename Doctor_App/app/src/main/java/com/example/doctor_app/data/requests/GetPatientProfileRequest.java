package com.example.doctor_app.data.requests;


import com.example.doctor_app.data.responses.GetPatientProfileResponse;

public class GetPatientProfileRequest extends Request<GetPatientProfileResponse> {
    private Integer patientID;

    public GetPatientProfileRequest(Integer patientID) {
        this.patientID = patientID;
    }

    @Override
    public String requestRoute() {
        return "api/getPatientProfile";
    }

    @Override
    public Class responseClass() {
        return GetPatientProfileResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.GET;
    }
}
