package com.github.bcdbuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import bcdbuddy.edu.network.R;

public class AppGuiUtils {
    private static ProgressDialog progressDialog;

    public static void animateToolbar (Toolbar toolbar) {
        hideToolBar(toolbar);
        showToolBar(toolbar);
    }

    private static void hideToolBar (Toolbar toolbar) {
        toolbar.animate()
                .alpha(0) //la rendre invisible
                .translationY(-toolbar.getHeight()) //la déplacer vers le haut
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    private static void showToolBar (Toolbar toolbar) {
        toolbar.animate()
                .alpha(1) //la rendre visible
                .translationY(0) //retour à la position d'origine
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
    }


    public static void hideKeyboard (Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void showProgressBar (Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public static void showProgressBar () {
        showProgressBar(AppApplication.getContext());
    }


    public static void hideProgressBar () {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public static boolean isXLargeTablet (Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    public static boolean isNormalTablet (Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }
}