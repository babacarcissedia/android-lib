package com.github.bcdbuddy;

import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AppQueryWatcher implements android.support.v7.widget.SearchView.OnQueryTextListener {
    private ListView listView;

    public AppQueryWatcher (ListView listView) {
        this.listView = listView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            this.listView.clearTextFilter();
        } else {
            this.listView.setFilterText(newText);
        }
        return true;
    }
}
