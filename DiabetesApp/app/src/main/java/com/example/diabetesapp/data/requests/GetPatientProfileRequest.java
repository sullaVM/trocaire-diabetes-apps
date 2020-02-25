package com.example.diabetesapp.data.requests;

import com.android.volley.Response;
import com.example.diabetesapp.GsonRequest;
import com.example.diabetesapp.data.responses.GetPatientProfileResponse;
import com.google.gson.Gson;

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
