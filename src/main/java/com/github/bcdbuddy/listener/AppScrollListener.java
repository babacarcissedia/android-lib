package com.github.bcdbuddy.listener;

import android.widget.AbsListView;

public abstract class AppScrollListener implements AbsListView.OnScrollListener {
    private int mLastFirstVisibleItem;

    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mLastFirstVisibleItem < firstVisibleItem) {
            onScrollingDown();
        }
        if (mLastFirstVisibleItem > firstVisibleItem) {
            onScrollingUp();
        }
        mLastFirstVisibleItem = firstVisibleItem;
    }

    protected abstract void onScrollingUp ();
    protected abstract void onScrollingDown ();


}