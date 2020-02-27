package com.example.doctor_app.data.requests;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.doctor_app.GsonRequest;
import com.google.gson.Gson;

public abstract class PatientRequest<PatientResponse> {
    public static final String API_BASE_URL = "https://swe.sullamontes.com/api/";

    public void makeRequest(Context context, Response.Listener<PatientResponse> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        GsonRequest gsonRequest = new GsonRequest(API_BASE_URL + this.requestRoute(), jsonString, this.responseClass(), listener, errorListener);
        requestQueue.add(gsonRequest);
    }

    public abstract String requestRoute();

    public abstract Class responseClass();
}
