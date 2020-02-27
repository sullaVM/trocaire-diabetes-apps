package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.GetDoctorsPatientsResponse;

public class GetDoctorsPatientsRequest extends DoctorRequest<GetDoctorsPatientsResponse> {
    @Override
    public String requestRoute() {
        return "getDoctorsPatients";
    }

    @Override
    public Class responseClass() {
        return GetDoctorsPatientsResponse.class;
    }
}
