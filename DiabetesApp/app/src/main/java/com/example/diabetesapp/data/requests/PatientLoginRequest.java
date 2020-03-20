package com.example.diabetesapp.data.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.util.Consumer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.diabetesapp.GsonRequest;
import com.example.diabetesapp.R;
import com.example.diabetesapp.data.responses.PatientLoginResponse;
import com.google.gson.Gson;

public class PatientLoginRequest {
    public static final String API_BASE_URL = "https://swe.sullamontes.com/patientLogin";

    private Integer patientID;
    private String password;

    public PatientLoginRequest(Integer patientID, String password) {
        this.patientID = patientID;
        this.password = password;
    }

    public void makeRequest(final Context context, final Consumer<PatientLoginResponse> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.session), Context.MODE_PRIVATE);
        String jsonString = gson.toJson(this);

        GsonRequest<PatientLoginResponse> gsonRequest = new GsonRequest(Request.Method.POST, API_BASE_URL, jsonString, PatientLoginResponse.class, new Response.Listener<PatientLoginResponse>() {
            @Override
            public void onResponse(PatientLoginResponse response) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (response.success != null && response.success && response.tokenID != null) {
                    editor.putString(context.getString(R.string.session), response.tokenID);
                    editor.putInt(context.getString(R.string.patient_id), patientID);
                    editor.apply();
                }
                callback.accept(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(Log.INFO, "/patientLogin/", "Request failed");
                callback.accept(null);
            }
        });
        requestQueue.add(gsonRequest);
    }
}
