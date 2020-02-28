package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.DoctorResponse;

public class GetDoctorIDRequest extends DoctorRequest {
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
        return DoctorResponse.class;
    }
}
