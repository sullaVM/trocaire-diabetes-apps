package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.CreatePatientResponse;

public class CreatePatientRequest extends DoctorRequest<CreatePatientResponse> {
    @Override
    public String requestRoute() {
        return "createPatient";
    }

    @Override
    public Class responseClass() {
        return CreatePatientResponse.class;
    }
}
