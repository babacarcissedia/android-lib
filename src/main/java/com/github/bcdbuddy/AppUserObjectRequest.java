package com.github.bcdbuddy;

import bcdbuddy.edu.network.activity.MainActivity;
import bcdbuddy.edu.network.api.User;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bcdbuddy on 10/12/2016.
 */
public class AppUserObjectRequest extends JsonObjectRequest {
    private User mUser;

    public AppUserObjectRequest (int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        mUser = AppUtils.getAuthUser();
        Log.i(MainActivity.LOG_TAG, "making request to "+url);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        if (mUser != null){
            headers.put("X-CSRF-TOKEN", this.mUser.getCsrfToken());
            headers.put("Authorization", "Bearer " + this.mUser.getApiToken());
            Log.i(MainActivity.LOG_TAG, this.getClass().getName()+" added user api token: "+mUser.getApiToken()+" and csrf token: "+mUser.getCsrfToken());
        } else {
            Log.e(MainActivity.LOG_TAG, this.getClass().getName()+" user is null. Probably because user is not logged in to do this request");
        }
        return headers;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.e(AppUtils.APP_LOG, "json string : "+jsonString);
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
