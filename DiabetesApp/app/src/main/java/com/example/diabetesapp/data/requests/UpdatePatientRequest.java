package com.example.diabetesapp.data.requests;

import androidx.annotation.Nullable;

public class UpdatePatientRequest {
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
    public final String requestRoute = "updatePatient";
}
