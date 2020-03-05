package com.example.doctor_app.data.requests;

import androidx.annotation.Nullable;

import com.example.doctor_app.data.responses.CreatePatientResponse;

public class CreatePatientRequest extends Request<CreatePatientResponse> {
    public static final int PREGNANT = 1;
    public static final int NOT_PREGNANT = 0;

    private Integer doctorID;
    private String firstName;
    private String lastName;
    private String height;
    private Integer mobileNumber;
    private String photoDataUrl;
    private String password;
    private Integer pregnant;
    @Nullable
    private String bslUnit;

    public CreatePatientRequest(Integer doctorID, String firstName, String lastName, String height, Integer mobileNumber, String photoDataUrl, String password, @Nullable String bslUnit, Integer pregnant) {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.mobileNumber = mobileNumber;
        this.photoDataUrl = photoDataUrl;
        this.password = password;
        this.bslUnit = bslUnit;
        this.pregnant = pregnant;
    }

    @Override
    public String requestRoute() {
        return "api/createPatient";
    }

    @Override
    public Class responseClass() {
        return CreatePatientResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
