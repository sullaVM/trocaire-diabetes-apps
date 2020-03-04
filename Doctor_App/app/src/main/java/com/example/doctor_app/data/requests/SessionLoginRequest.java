package com.example.doctor_app.data.requests;

import com.example.doctor_app.data.responses.SessionLoginResponse;

public class SessionLoginRequest extends Request<SessionLoginResponse> {
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
    public int requestType() {
        return com.android.volley.Request.Method.POST;
    }
}
