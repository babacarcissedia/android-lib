package com.github.bcdbuddy;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

/**
 * Created by bcdbuddy on 06/12/2016.
 */
public class AppTextWatcher implements TextWatcher {
    private ArrayAdapter adapter;
    public AppTextWatcher (ArrayAdapter adapter)
    {
        this.adapter = adapter;
    }

    @Override
    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
        // When user changed the Text
        this.adapter.getFilter().filter(cs);
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
    int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
    }
}
