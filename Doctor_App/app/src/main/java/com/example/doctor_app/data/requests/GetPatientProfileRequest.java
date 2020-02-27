package com.example.doctor_app.data.requests;


import com.example.doctor_app.data.responses.GetPatientProfileResponse;

public class GetPatientProfileRequest extends PatientRequest<GetPatientProfileResponse> {
    private Integer patientID;

    public GetPatientProfileRequest(Integer patientID) {
        this.patientID = patientID;
    }

    @Override
    public String requestRoute() {
        return "getPatientProfile";
    }

    @Override
    public Class responseClass() {
        return GetPatientProfileResponse.class;
    }
}
