package com.github.bcdbuddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import bcdbuddy.edu.network.BuildConfig;
import bcdbuddy.edu.network.R;
import bcdbuddy.edu.network.activity.MainActivity;


public class AppApplication extends MultiDexApplication {
    private static AppCacheManager appCacheManager = null;
    private static Context mContext;
    private static ImageLoader imageLoader;
    private static RequestQueue requestQueue;


    @Override
    public void onCreate () {
        super.onCreate();
        MultiDex.install(this);
        mContext = getApplicationContext();

        this.purgeCacheOnUpdate();
    }


    public static Context getContext () {return mContext;}

    /**
     * Singleton for a common shared preference over all activities
     * @return AppCacheManager
     */
    public static AppCacheManager getAppCacheManager () {
        if (appCacheManager == null) {
            appCacheManager = new AppCacheManager(mContext);
        }
        return appCacheManager;
    }



    private void purgeCacheOnUpdate () {
        int currentVersionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.i(MainActivity.LOG_TAG, String.format("version code: %d, version name: %s", currentVersionCode, versionName));

        String oldVersionCode = getVersionCode();
        if (oldVersionCode == null || !oldVersionCode.equals(String.valueOf(currentVersionCode))) {
            Log.i(MainActivity.LOG_TAG, "update detected. purging application cache");
            purgeCache();
            saveVersionCode(currentVersionCode);
        }
    }


    private String getVersionCode () {
        return AppApplication.getAppCacheManager().getString(AppCacheManager.VERSION_CODE_KEY);
    }


    private void saveVersionCode (int versionCode) {
        Log.i(MainActivity.LOG_TAG, "saving new application version code and name");
        AppApplication.getAppCacheManager().saveString(AppCacheManager.VERSION_CODE_KEY, String.valueOf(versionCode));
    }


    public void purgeCache () {
        AppApplication.getAppCacheManager().clearAll();
    }



    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getContext());
        }
        return requestQueue;
    }


    public static void cancelPendingRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll("Event Request Tag");
        }
    }

    public static ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(), new AppImageCache());
        }
        return imageLoader;
    }

    public static void createNotificationChannel (Context context, String channelId, int channelNameId, int channelDescriptionId) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // The user-visible name of the channel.
        CharSequence name = context.getString(channelNameId);
        // The user-visible description of the channel.
        String description = context.getString(channelDescriptionId);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }
}