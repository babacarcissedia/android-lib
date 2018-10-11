package com.github.bcdbuddy;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.bcdbuddy.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bcdbuddy on 02/02/2017.
 */
public class AppApiResponse {
    private static final String RESPONSE_KEY = "response";
    private static final String TYPE_KEY = "type";
    private static final String MESSAGE_KEY = "message";
    private static final String ERRORS_KEY = "errors";
    private static final String SUCCESS_KEY = "success";
    public static final String INFO_KEY = "info";
    private static final String DATA_KEY = "data";
    private JSONObject jsonObjectResponse;
    private String type;
    private String message;
    private JSONObject dataObject = new JSONObject();
    private JSONArray dataArray = new JSONArray();
    private VolleyError volleyError;
    private JSONObject apiErrors;
    private boolean withVolleyError = false;


    public AppApiResponse (JSONObject response_json){
        this.jsonObjectResponse = response_json;
        this.handleJson(response_json);
    }


    public AppApiResponse (JSONArray jsonArray){
        // that mean that we were requesting for an array but got
        // json object describing the api response
        try {
            this.handleJson(jsonArray.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public AppApiResponse (String response_string){
        this.handleString(response_string);
    }


    public AppApiResponse (VolleyError error){
        withVolleyError = true;
        this.volleyError = error;
        if (error.networkResponse != null){
            String response_string = new String(error.networkResponse.data);
            this.handleString(response_string);
        }
    }


    private void handleJson(JSONObject response) {
        Log.i(AppUtils.APP_LOG, "reponse: "+response);
        try {
            JSONObject apiResponse = response.getJSONObject(RESPONSE_KEY);
            this.type = apiResponse.has(TYPE_KEY) ? apiResponse.getString(TYPE_KEY): "";
            this.message = apiResponse.has(MESSAGE_KEY) ? apiResponse.getString(MESSAGE_KEY): "";
            // one of them will trigger error
            // so commenting to be retrieving via methods like getDataObject() and getDataArray()
            // this.dataObject = response.has(DATA_KEY) ? response.getJSONObject(DATA_KEY) : new JSONObject();
            // this.dataArray = response.has(DATA_KEY) ? response.getJSONArray(DATA_KEY) : new JSONArray();

            if (!message.equals("")) this.apiErrors = new JSONObject(this.message);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(AppUtils.APP_LOG, "(handleJson) string for json: "+response.toString());
        }
    }


    private void handleString(String json_string) {
        JSONObject json;
        try {
            json = new JSONObject(json_string);
            this.handleJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(AppUtils.APP_LOG, "(handleString) string for json: "+json_string);
        }
    }


    public JSONObject getDataObject () {
        try {
            return this.jsonObjectResponse.has(DATA_KEY)
                    ? this.jsonObjectResponse.getJSONObject(DATA_KEY)
                    : new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    public JSONArray getDataArray () {
        try {
            return this.jsonObjectResponse.has(DATA_KEY)
                    ? this.jsonObjectResponse.getJSONArray(DATA_KEY)
                    : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }


    public String getType() {
        return type;
    }


    public String getMessage() {
        return message;
    }


    public boolean isSuccess (){
        return ((this.type != null) && this.type.equals(SUCCESS_KEY));
    }


    public boolean fails(){
        return (! this.isSuccess());
    }


    public String toString(){
        String dump = String.format("[API response] type: %s, message: %s, volley_error: %s",
                this.type, this.message, this.volleyError != null ? this.volleyError.toString() : null);
        if (this.volleyError != null && this.volleyError.networkResponse != null)
            dump += String.format("[-- Volley] error status: %s, response: %s",
                    this.volleyError.networkResponse.statusCode, new String(this.volleyError.networkResponse.data));
        return dump;
    }


    public boolean isNotFound(){
        return (this.volleyError == null || this.volleyError.networkResponse == null
                ||  this.volleyError.networkResponse.statusCode == 404);
    }


    public boolean isValidationError(){
        return (this.volleyError == null || this.volleyError.networkResponse == null
                ||  this.volleyError.networkResponse.statusCode == 422 && volleyError instanceof ServerError);
    }

    public JSONObject getErrors() {
        return apiErrors;
    }


    public boolean isServerError () {
        return withVolleyError && this.volleyError instanceof ServerError;
    }

    @Nullable
    public String getResponseErrorMessage () {
        JSONObject response;
        if (isServerError() && this.volleyError.networkResponse != null) {
            try {
                response = new JSONObject(new String(this.volleyError.networkResponse.data));
                JSONObject alert = response.getJSONObject(RESPONSE_KEY);
                return alert.getString(MESSAGE_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    public boolean setFieldErrorIfAny (String key, View view) {
        String error_message;
        if (apiErrors.has(key)) {
            try {
                JSONArray all_errors = apiErrors.getJSONArray(key);
                error_message = all_errors.getString(0);
                if (view instanceof TextView) {
                    ((TextView) view).setError(error_message);
                } else {
                    AppUtils.messageError(error_message);
                }
                view.requestFocus();
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
