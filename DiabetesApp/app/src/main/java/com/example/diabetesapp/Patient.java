package com.example.diabetesapp;

public class Patient {
    String patientName;
    int patientImage;

    public Patient(String patientName,int patientImage)
    {
        this.patientName=patientName;
        this.patientImage=patientImage;
    }
    public String getPatientName()
    {
        return patientName;
    }
    public int getPatientImage()
    {
        return patientImage;
    }
}
