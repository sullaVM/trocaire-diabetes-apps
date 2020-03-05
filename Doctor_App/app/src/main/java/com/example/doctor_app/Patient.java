package com.example.doctor_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {

    private Integer doctorID;
    private String firstName;
    private String lastName;
    private String height;
    private Integer mobileNumber;
    private String photoDataUrl;
    private String password;
    private String bslUnit;

    public Patient(Integer doctorID, String firstName, String lastName, String height,
                   Integer mobileNumber, String photoDataUrl, String password, String bslUnit)
    {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.mobileNumber = mobileNumber;
        this.photoDataUrl = photoDataUrl;
        this.password = password;
        this.bslUnit = bslUnit;

    }

    protected Patient(Parcel in) {
        doctorID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        height = in.readString();
        mobileNumber = in.readInt();
        photoDataUrl = in.readString();
        password = in.readString();
        bslUnit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(doctorID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(height);
        dest.writeInt(mobileNumber);
        dest.writeString(photoDataUrl);
        dest.writeString(password);
        dest.writeString(bslUnit);
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

    public Integer getDoctorID()
    {
        return this.doctorID;
    }

    public String getName()
    {
        return (this.firstName + " " + this.lastName);
    }

    public String getHeight() {
        return this.height;
    }

    public Integer getNumber() {
        return this.mobileNumber;
    }

    public String getPhotoDataUrl() {
        return this.photoDataUrl;
    }

    public String getPassword() {
        return this.password;
    }

    public String getBslUnit() {
        return this.bslUnit;
    }
}
