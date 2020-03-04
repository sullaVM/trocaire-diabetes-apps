package com.example.doctor_app.data.responses;

public class SessionResponse<T> {
    public String sessionToken;
    public T request;

    public SessionResponse(String sessionToken, T request) {
        this.sessionToken = sessionToken;
        this.request = request;
    }
}
