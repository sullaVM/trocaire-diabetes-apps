package com.example.diabetesapp.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.diabetesapp.R;

public class User {
    public static final String DEFAULT_SESSION = null;
    public static final int DEFAULT_PATIENT_ID = -1;

    public String token;
    public int patientID;

    public User(String token, int patientID) {
        this.token = token;
        this.patientID = patientID;
    }

    public static User getLoggedInUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.session), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(context.getString(R.string.session), DEFAULT_SESSION);
        int patientID = sharedPreferences.getInt(context.getString(R.string.patient_id), DEFAULT_PATIENT_ID);
        if ((token != null) && !(token.equals(DEFAULT_SESSION) || patientID == DEFAULT_PATIENT_ID)) {
            return new User(token, patientID);
        } else {
            return null;
        }
    }

    public static void logOut(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.session), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.session), DEFAULT_SESSION);
        editor.putInt(context.getString(R.string.patient_id), DEFAULT_PATIENT_ID);
        editor.apply();
    }
}
