package com.example.diabetesapp.data.requests;

import com.android.volley.Request;
import com.example.diabetesapp.data.responses.GetPatientIDResponse;

public class GetPatientIDRequest extends PatientRequest {
    private Integer doctorID;
    private String firstName;
    private String lastName;

    public GetPatientIDRequest(Integer doctorID, String firstName, String lastName) {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String requestRoute() {
        return "getPatientID";
    }

    @Override
    public Class responseClass() {
        return GetPatientIDResponse.class;
    }

    @Override
    public int requestType() {
        return Request.Method.POST;
    }

    @Override
    public void setTokenID(String tokenID) {

    }
}
