package com.teksystems.devicetracker.view.views.activities;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.presenter.presenters.HelpPresenter;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.view.views.HelpView;

/**
 * Created by ajaiswal on 10/29/2015.
 */
public class HelpActivity extends BaseActivity<HelpView, HelpPresenter> {

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setTitle(R.string.activity_help);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!Utility.HELP.equalsIgnoreCase(getSupportActionBar().getTitle().toString())) {
            menu.clear();
        }
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!Utility.HELP.equalsIgnoreCase(getSupportActionBar().getTitle().toString())) {
            menu.clear();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
