package com.github.bcdbuddy;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import bcdbuddy.edu.network.R;
import bcdbuddy.edu.network.activity.MainActivity;
import bcdbuddy.edu.network.api.User;
import bcdbuddy.edu.network.receiver.MyNotificationReceiver;
import es.dmoral.toasty.Toasty;

public class AppUtils {
    public static final String APP_LOG = "781227";


    public static void scheduleNotification (Context context, NotificationCompat.Builder builder, long delay, int notificationId, Class target_activity) {//delay is after how much time(in millis) from current time you want to schedule the notification
        if (delay < 0) {
            return;
        }

        Intent intent = new Intent(context, target_activity);
        PendingIntent pIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        Notification notification = builder.build();
        Intent notificationIntent = new Intent(context, MyNotificationReceiver.class);
        notificationIntent.putExtra(MyNotificationReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


    public static boolean isNetworkAvailable (Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static void saveUser (User user) {
        AppApplication.getAppCacheManager().saveObject(AppCacheManager.AUTH_USER, user);
    }


    public static void deleteUserInfos () {
        Log.i(MainActivity.LOG_TAG, "auth user infos deleted");
        AppApplication.getAppCacheManager().delete(AppCacheManager.AUTH_USER);
    }


    public static boolean isLoggedIn () {
        return (getAuthUser() != null);
    }


    public static void logout () {
        final Context context = AppApplication.getContext();
        String logout_API_url = context.getString(R.string.API_url_user_logout);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse (JSONObject response) {
                AppApiResponse apiResponse = new AppApiResponse(response);
                if (apiResponse.isSuccess()) {
                    AppUtils.messageSuccess(apiResponse.getMessage());
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                handleVolleyError(error);
            }
        };
        AppUserObjectRequest request = new AppUserObjectRequest(Request.Method.GET, logout_API_url, null, responseListener, errorListener);
        AppUtils.addRequest(request);

        // clean up
        deleteUserInfos();
    }


    public static User getAuthUser () {
        return (User) AppApplication.getAppCacheManager()
                .getObject(AppCacheManager.AUTH_USER, User.class);
    }


    public static void addRequest (Context context, Request request) {
        if (AppUtils.isNetworkAvailable(context)) {
            Volley.newRequestQueue(context).add(request);
        } else {
            Log.e(MainActivity.LOG_TAG, "no internet connection");
            Toasty.warning(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public static void addRequest (Request request) {
        AppUtils.addRequest(AppApplication.getContext(), request);
    }


    public static boolean handleVolleyError (VolleyError error) {
        Log.e(MainActivity.LOG_TAG, "handling volley error " + error.toString());
        return (
                handleNoConnectionError(error) ||
                handleTimeoutError(error) ||
                handleAuthFailureError(error) ||
                handleServerError(error) ||
                handleParseError(error)
        );
    }


    public static boolean handleTimeoutError (VolleyError error) {
        if (error instanceof TimeoutError) {
            messageWarning(R.string.timeout_error);
            return true;
        }
        return false;
    }


    public static boolean handleAuthFailureError (VolleyError error) {
        if (error instanceof AuthFailureError) {
            messageError(R.string.auth_failure_error);
            return true;
        }
        return false;
    }


    public static boolean handleServerError (VolleyError error) {
        if (error instanceof ServerError) {
            int statusCode = error.networkResponse.statusCode;
            // do not bother if 401, 403, or 422.
            if (!String.valueOf(statusCode).matches("^4[0-9]{2}$"))
                AppUtils.messageError(R.string.server_error);
            return true;
        }
        return false;
    }


    private static boolean handleNoConnectionError (VolleyError error) {
        if (error instanceof NoConnectionError) {
            AppUtils.messageWarning(R.string.no_internet_connection);
            return true;
        }
        return false;
    }


    private static boolean handleParseError (VolleyError error) {
        if (error instanceof ParseError) {
            AppUtils.messageError(R.string.parse_error);
            return true;
        }
        return false;
    }

    public static void messageSuccess (String message) {
        Toasty.success(AppApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void messageInfo (String message) {
        Toasty.info(AppApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void messageWarning (String message) {
        Toasty.warning(AppApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void messageError (String message) {
        Toasty.error(AppApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void messageSuccess (int messageId) {
        messageSuccess(AppApplication.getContext().getString(messageId));
    }

    public static void messageInfo (int messageId) {
        messageInfo(AppApplication.getContext().getString(messageId));
    }

    public static void messageWarning (int messageId) {
        messageWarning(AppApplication.getContext().getString(messageId));
    }

    public static void messageError (int messageId) {
        messageError(AppApplication.getContext().getString(messageId));
    }




    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
}
