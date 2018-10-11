package com.github.bcdbuddy;

/**
 * Created by bcdbuddy on 04/12/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class AppCacheManager {
    private static final String APP_NAME = "Edu Network";
    public static final String STUDENTS = "users";
    public static final String STUDENT = "user_%d";
    public static final String LEVELS = "levels";
    public static final String LEVEL = "level_%d";
    public static final String SECTIONS = "sections";
    public static final String SECTION = "section_%d";
    public static final String UFRS = "ufrs";
    public static final String UFR = "ufr_%d";
    public static final String AUTH_USER = "auth_user";
    public static final String EVENTS = "events";
    public static final String EVENT = "event_%d";
    public static final String USER_EVENTS = "user_%d_events";
    public static final String LOCAL_EVENTS = "local_events";
    public static final String EVENT_TYPES = "event_types";
    public static final String EVENT_TYPE = "event_type_%d";
    public static final String WEEK_VIEW_EVENTS = "week_view_events";
    public static final String UFR_SECTIONS = "ufr_%d_sections";
    public static final String SECTION_LEVELS = "section_%d_levels";
    public static final String LEVEL_USERS = "level_%d_users";
    public static final String LEVEL_SUBJECTS = "level_%d_subjects";
    public static final String SUBJECTS = "subjects";
    public static final String SUBJECT = "subject_%d";
    public static final String SUBJECT_LEVELS = "subject_%d_levels";
    public static final String VERSION_CODE_KEY = "version_code";
    public static final String COMMUNITIES = "communities";
    public static final String COMMUNITY_USERS = "community_%d_users";
    private Context context;
    private SharedPreferences appCache;
    private Gson gson;

    public AppCacheManager (Context context)
    {
        this.context = context;
        this.gson = new Gson();
        this.appCache = this.context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }


    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param key - the pref key
     */
    public void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param value - the pref value
     */
    public boolean nullValue(String value){
        return (value == null);
    }


    public String getString(String name)
    {
        return this.appCache.getString(name, null);
    }


    public AppCacheManager saveString(String name, String value)
    {
        this.checkForNullKey(name);
        if (this.nullValue(name)) return null;
        SharedPreferences.Editor editor = this.appCache.edit();
        editor.putString(name, value);
        editor.apply();
        return this;
    }


    public Object getObject(String name, Class object_class)
    {
        String jsonString = this.getString(name);
        if (nullValue(jsonString)) return null;
        return gson.fromJson(jsonString, object_class);
    }

    public AppCacheManager saveObject(String name, Object value)
    {
        String jsonString = this.gson.toJson(value);
        this.saveString(name, jsonString);
        return this;
    }


    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(this.appCache.getString(key, ""), "‚‗‚")));
    }


    public AppCacheManager saveListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        this.saveString(key, TextUtils.join("‚‗‚", myStringList));
        return this;
    }


    public AppCacheManager saveListObject(String key, ArrayList objArray){
    	checkForNullKey(key);
    	ArrayList<String> objStrings = new ArrayList<String>();
    	for(Object obj : objArray){
    		objStrings.add(gson.toJson(obj));
    	}
    	this.saveListString(key, objStrings);
        return this;
    }


    public AppCacheManager saveListObject(String key, int param, ArrayList objArray){
        String newKey = String.format(key, param);
        return this.saveListObject(newKey, objArray);
    }


    public ArrayList<Object> getListObject(String key, Class<?> mClass){
        ArrayList<String> objStrings = getListString(key);
        ArrayList<Object> objects =  new ArrayList<Object>();
        Object value;
        for(String jObjString : objStrings){
            value = gson.fromJson(jObjString,  mClass);
            objects.add(value);
        }
        return objects;
    }


    public ArrayList<Object> getListObject(String key, int param, Class<?> mClass){
        String newKey = String.format(key, param);
        return this.getListObject(newKey, mClass);
    }


    public void delete(String key) {
        SharedPreferences.Editor editor = this.appCache.edit();
        editor.remove(key);
        editor.apply();
    }


    public void clearAll ()
    {
        SharedPreferences.Editor editor = this.appCache.edit();
        editor.clear();
        editor.apply();
    }
}