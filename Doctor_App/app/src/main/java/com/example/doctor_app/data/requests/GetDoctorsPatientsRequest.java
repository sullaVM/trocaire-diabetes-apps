package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.GetDoctorsPatientsResponse;

public class GetDoctorsPatientsRequest extends Request<GetDoctorsPatientsResponse> {
    private Integer doctorID;

    public GetDoctorsPatientsRequest(Integer doctorID) {
        this.doctorID = doctorID;
    }

    @Override
    public String requestRoute() {
        return "api/getDoctorsPatients";
    }

    @Override
    public Class responseClass() {
        return GetDoctorsPatientsResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
