package com.example.doctor_app;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends JsonRequest<T> {
    private final Gson gson = new Gson();
    private final Response.Listener<T> listener;
    private final Class<T> responseClass;
    private String mToken;

    public GsonRequest(String url, String jsonRequest, Class<T> responseClass, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, jsonRequest, listener, errorListener);
        this.listener = listener;
        this.responseClass = responseClass;
    }

    public GsonRequest(String url, String jsonRequest, String token, Class<T> responseClass, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, jsonRequest, listener, errorListener);
        this.listener = listener;
        this.mToken = token;
        this.responseClass = responseClass;
    }

    public GsonRequest(int method, String url, String jsonRequest, String token, Class<T> responseClass, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.listener = listener;
        this.mToken = token;
        this.responseClass = responseClass;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
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

    @Override
    public Map<String, String> getHeaders() {
        if (mToken != null) {
            Map<String, String> params = new HashMap();
            params.put("Cookie", "session=" + mToken);
            return params;
        } else {
            return Collections.emptyMap();
        }
    }
}