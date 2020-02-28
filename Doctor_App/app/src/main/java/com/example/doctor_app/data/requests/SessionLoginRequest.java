package com.example.doctor_app.data.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.doctor_app.GsonRequest;
import com.example.doctor_app.data.responses.SessionLoginResponse;
import com.google.gson.Gson;

public class SessionLoginRequest extends DoctorRequest<SessionLoginResponse> {
    private String idToken;

    public SessionLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String requestRoute() {
        return "sessionLogin";
    }

    @Override
    public Class responseClass() {
        return SessionLoginResponse.class;
    }

    @Override
    public void makeRequest(Context context, Response.Listener<SessionLoginResponse> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST, API_BASE_URL + this.requestRoute(), jsonString, null, this.responseClass(), listener, errorListener);
        requestQueue.add(gsonRequest);
    }
}
