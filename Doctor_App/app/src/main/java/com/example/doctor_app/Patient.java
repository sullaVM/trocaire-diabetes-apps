package com.example.doctor_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    String patientName;
    int patientImage;

    public Patient(String patientName,int patientImage)
    {
        this.patientName=patientName;
        this.patientImage=patientImage;
    }

    protected Patient(Parcel in) {
        patientName = in.readString();
        patientImage = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientName);
        dest.writeInt(patientImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getPatientName()
    {
        return patientName;
    }
    public int getPatientImage()
    {
        return patientImage;
    }
}
