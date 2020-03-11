package com.example.doctor_app.data.requests;

import androidx.annotation.Nullable;

import com.example.doctor_app.data.responses.CreatePatientResponse;

public class CreatePatientRequest extends Request<CreatePatientResponse> {
    public static final int PREGNANT = 1;
    public static final int NOT_PREGNANT = 0;

    private Integer doctorID;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String height;
    private Integer mobileNumber;
    private String photoDataUrl;
    private Integer pregnant;
    @Nullable
    private String bslUnit;

    public CreatePatientRequest(Integer doctorID, String userName, String password, String firstName, String lastName, String height, Integer mobileNumber, String photoDataUrl, Integer pregnant, @Nullable String bslUnit) {
        this.doctorID = doctorID;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.mobileNumber = mobileNumber;
        this.photoDataUrl = photoDataUrl;
        this.pregnant = pregnant;
        this.bslUnit = bslUnit;
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
