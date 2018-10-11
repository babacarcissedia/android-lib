package com.github.bcdbuddy;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AppArrayAdapter<T extends ApiModelInterface> extends ArrayAdapter<T> {
    protected HashMap<Integer, T> mIndexedDatas = new HashMap<>();
    protected ArrayList<T> mDatas;
    protected Activity mActivity;

    public AppArrayAdapter (Activity activity, int resource, @NonNull ArrayList<T> objects) {
        super(activity, resource, objects);
        mActivity = activity;

        mDatas = objects;
        this.fillIndexedDatas();
    }

    protected void fillIndexedDatas () {
        T current;
        for (int i = 0; i < mDatas.size(); i++) {
            current = mDatas.get(i);
            mIndexedDatas.put(current.getId(), current);
        }
    }


    @Override
    public T getItem (int position) {
        return mDatas.get(position);
    }

    public T getItemById (int id) {
        return mIndexedDatas.get(id);
    }

    @Override
    public void add (@Nullable T object) {
        super.add(object);
        mIndexedDatas.put(object.getId(), object);
    }

    @Override
    public void addAll (T... items) {
        super.addAll(items);
        for (int i = 0; i < items.length; i++) {
            mIndexedDatas.put(items[i].getId(), items[i]);
        }
    }
}