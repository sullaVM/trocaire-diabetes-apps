package com.example.diabetesapp;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class GsonRequest<PatientResponse> extends JsonRequest<PatientResponse> {
    private final Gson gson = new Gson();
    private final Response.Listener<PatientResponse> listener;
    private final Class<PatientResponse> responseClass;

    public GsonRequest(String url, String jsonRequest, Class<PatientResponse> responseClass, Response.Listener<PatientResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, jsonRequest, listener, errorListener);
        this.listener = listener;
        this.responseClass = responseClass;
    }

    @Override
    protected void deliverResponse(PatientResponse response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<PatientResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, responseClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}