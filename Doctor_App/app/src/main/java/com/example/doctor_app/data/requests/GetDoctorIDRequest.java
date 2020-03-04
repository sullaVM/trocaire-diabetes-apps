package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.GetDoctorIDResponse;

public class GetDoctorIDRequest extends Request {
    public String email;

    public GetDoctorIDRequest(String email) {
        this.email = email;
    }

    @Override
    public String requestRoute() {
        return "api/getDoctorID";
    }

    @Override
    public Class responseClass() {
        return GetDoctorIDResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.GET;
    }
}
