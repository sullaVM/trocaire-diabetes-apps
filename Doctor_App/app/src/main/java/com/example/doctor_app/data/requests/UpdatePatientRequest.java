package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.UpdatePatientResponse;

import androidx.annotation.Nullable;

public class UpdatePatientRequest extends Request<UpdatePatientResponse> {
    private Integer patientID;
    @Nullable
    private Integer doctorID;
    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private String height;
    @Nullable
    private Integer mobileNumber;
    @Nullable
    private String photoDataUrl;
    @Nullable
    private String password;
    @Nullable
    private String bslUnit;

    public UpdatePatientRequest(Integer patientID, @Nullable Integer doctorID, @Nullable String firstName, @Nullable String lastName, @Nullable String height, @Nullable Integer mobileNumber, @Nullable String photoDataUrl, @Nullable String password, @Nullable String bslUnit) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.mobileNumber = mobileNumber;
        this.photoDataUrl = photoDataUrl;
        this.password = password;
        this.bslUnit = bslUnit;
    }

    @Override
    public String requestRoute() {
        return "api/updatePatient";
    }

    @Override
    public Class responseClass() {
        return UpdatePatientResponse.class;
    }

    @Override
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
