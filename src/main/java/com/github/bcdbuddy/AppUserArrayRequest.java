package com.github.bcdbuddy;

import bcdbuddy.edu.network.activity.MainActivity;
import bcdbuddy.edu.network.api.User;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bcdbuddy on 09/12/2016.
 */
public class AppUserArrayRequest extends JsonArrayRequest {

    private User mUser;

    public AppUserArrayRequest (int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.mUser = AppUtils.getAuthUser();
        Log.i(MainActivity.LOG_TAG, "making request to "+url);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/json");
        if (this.mUser != null){
            headers.put("X-CSRF-TOKEN", this.mUser.getCsrfToken());
            headers.put("Authorization", "Bearer "+this.mUser.getApiToken());
        }
        return headers;
    }
}
