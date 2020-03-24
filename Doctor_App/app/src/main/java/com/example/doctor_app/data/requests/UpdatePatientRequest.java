package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.UpdatePatientResponse;

import androidx.annotation.Nullable;

public class UpdatePatientRequest extends Request<UpdatePatientResponse> {
    @Nullable
    private Integer doctorID;
    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private String username;
    @Nullable
    private String height;
    @Nullable
    private String pregnant;
    @Nullable
    private Integer mobileNumber;
    @Nullable
    private String photoDataUrl;
    @Nullable
    private String bslUnit;
    private Integer patientID;

    public UpdatePatientRequest(@Nullable Integer doctorID, @Nullable String firstName,
                                @Nullable String lastName,  @Nullable String username, @Nullable String height,
                                @Nullable String pregnant, @Nullable Integer mobileNumber,
                                @Nullable String photoDataUrl, @Nullable String bslUnit, Integer patientID) {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.height = height;
        this.pregnant = pregnant;
        this.mobileNumber = mobileNumber;
        this.photoDataUrl = photoDataUrl;
        this.bslUnit = bslUnit;
        this.patientID = patientID;
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
