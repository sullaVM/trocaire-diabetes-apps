package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.DoctorResponse;

public class GetDoctorIDRequest extends DoctorRequest{
    @Override
    public String requestRoute() {
        return "getDoctorID";
    }

    @Override
    public Class responseClass() {
        return DoctorResponse.class;
    }
}
