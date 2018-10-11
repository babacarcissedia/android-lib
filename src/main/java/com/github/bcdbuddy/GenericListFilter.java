package com.github.bcdbuddy;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import bcdbuddy.edu.network.activity.MainActivity;

/**
 * Created by bcdbuddy on 07/12/2016.
 */
public class GenericListFilter<T> extends Filter {

    private ArrayAdapter<T> mAdapterUsed;
    private List<T> mInternalList;
    private Method mCompairMethod;
    private ArrayList<Method> mCompairMethods;
    private boolean one_method;

    public GenericListFilter (List<T> list, String reflectMethodName, ArrayAdapter<T> adapter) {
        super();
        this.one_method = true;
        mInternalList = new ArrayList<>(list);
        mAdapterUsed = adapter;

        try {
            ParameterizedType stringListType = (ParameterizedType)
                    getClass().getField("mInternalList").getGenericType();
            mCompairMethod = stringListType.getActualTypeArguments()[0].getClass().getMethod(reflectMethodName);
        } catch (Exception ex) {
            Log.w("GenericListFilter", ex.getMessage(), ex);
            try {
                if (mInternalList.size() > 0) {
                    T type = mInternalList.get(0);
                    mCompairMethod = type.getClass().getMethod(reflectMethodName);
                }
            } catch (Exception e) {
                Log.e("GenericListFilter", e.getMessage(), e);
            }
        }
    }


    // produced by bcdbuddy
    public GenericListFilter (List<T> list, String reflectMethodNames[], ArrayAdapter<T> adapter) {
        super();
        this.one_method = false;
        mInternalList = new ArrayList<>(list);
        mAdapterUsed = adapter;
        mCompairMethods = new ArrayList<Method>();

        try {
            ParameterizedType stringListType = (ParameterizedType)
                    getClass().getField("mInternalList").getGenericType();
            for (String reflectMethodName : reflectMethodNames) {
                mCompairMethods.add(stringListType.getActualTypeArguments()[0].getClass().getMethod(reflectMethodName));
            }
        } catch (Exception ex) {
            Log.w("GenericListFilter", ex.getMessage(), ex);
            try {
                if (mInternalList.size() > 0) {
                    T type = mInternalList.get(0);
                    for (String reflectMethodName : reflectMethodNames) {
                        mCompairMethods.add(type.getClass().getMethod(reflectMethodName));
                    }
                }
            } catch (Exception e) {
                Log.e("GenericListFilter", e.getMessage(), e);
            }
        }
    }

    /**
     * Let's filter the data with the given constraint
     *
     * @param constraint
     * @return
     */
    @Override
    protected FilterResults performFiltering (CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<T> filteredContents = new ArrayList<>();

        if (constraint != null && TextUtils.isEmpty(constraint)) {
            try {
                String result;
                for (T obj : mInternalList) {
                    if (this.one_method) {
                        result = (String) mCompairMethod.invoke(obj);
                        if (result.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredContents.add(obj);
                        }
                    } else {
                        for (Method method : mCompairMethods) {
                            result = (String) method.invoke(obj);
                            if (result.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                filteredContents.add(obj);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(MainActivity.LOG_TAG, "GenericListFilter" + ex.getMessage(), ex);
            }
        } else {
            filteredContents.addAll(mInternalList);
        }

        results.values = filteredContents;
        results.count = filteredContents.size();
        return results;
    }

    /**
     * Publish the filtering adapter list
     *
     * @param constraint
     * @param results
     */
    @Override
    protected void publishResults (CharSequence constraint, FilterResults results) {
        mAdapterUsed.clear();
        mAdapterUsed.addAll((List<T>) results.values);

        if (results.count == 0) {
            mAdapterUsed.notifyDataSetInvalidated();
        } else {
            mAdapterUsed.notifyDataSetChanged();
        }
    }
}
