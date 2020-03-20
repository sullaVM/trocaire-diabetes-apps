package com.example.diabetesapp.data.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.util.Consumer;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.diabetesapp.GsonRequest;
import com.example.diabetesapp.R;
import com.example.diabetesapp.login.User;
import com.google.gson.Gson;

public abstract class PatientRequest<T> {
    public static final String API_BASE_URL = "https://swe.sullamontes.com/api/";

    public void makeRequest(final Context context, final Consumer<T> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.session), Context.MODE_PRIVATE);

        String tokenID = sharedPreferences.getString(context.getString(R.string.session), User.DEFAULT_SESSION);
        setTokenID(tokenID);

        String jsonString = gson.toJson(this);

        GsonRequest<T> gsonRequest = new GsonRequest(requestType(), API_BASE_URL + requestRoute(), jsonString, responseClass(), new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                callback.accept(response);
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

    public abstract void setTokenID(String tokenID);
}
