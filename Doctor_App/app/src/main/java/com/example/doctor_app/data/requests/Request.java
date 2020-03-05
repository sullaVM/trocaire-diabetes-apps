package com.example.doctor_app.data.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.doctor_app.GsonRequest;
import com.example.doctor_app.R;
import com.example.doctor_app.data.responses.SessionResponse;
import com.google.gson.Gson;

import androidx.core.util.Consumer;

public abstract class Request<T> {
    public static final String API_BASE_URL = "https://swe.sullamontes.com/";

    public void makeRequest(final Context context, final Consumer<T> callback) {
        Gson gson = new Gson();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.session), Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String token = sharedPreferences.getString(context.getString(R.string.session), null);
        String jsonString = gson.toJson(this);

        GsonRequest<SessionResponse<T>> gsonRequest = new GsonRequest(requestType(), API_BASE_URL + requestRoute(), jsonString, token, responseClass(), new Response.Listener<SessionResponse<T>>() {
            @Override
            public void onResponse(SessionResponse<T> response) {
                editor.putString(context.getString(R.string.session), response.sessionToken);
                editor.apply();
                callback.accept(response.request);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(Log.INFO, requestRoute(), "Request failed");
                callback.accept(null);
            }
        });

        requestQueue.add(gsonRequest);
    }

    public abstract String requestRoute();

    public abstract Class responseClass();

    public abstract int requestType();

}
