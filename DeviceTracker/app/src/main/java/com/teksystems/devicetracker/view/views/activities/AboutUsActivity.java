package com.teksystems.devicetracker.view.views.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.presenter.presenters.AboutUsPresenter;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.view.views.AboutUsView;

import roboguice.inject.InjectView;

/**
 * Created by ajaiswal on 10/29/2015.
 */
public class AboutUsActivity extends BaseActivity<AboutUsView, AboutUsPresenter> {

    @InjectView(R.id.tv_about_us_text)
    private TextView tvAboutUs;
    @InjectView(R.id.tv_app_version)
    private TextView tvAppVersion;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setTitle(R.string.activity_about);

        justifyAboutUsText();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvAppVersion.setText("v " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void justifyAboutUsText() {
        tvAboutUs.setText(Html.fromHtml("<body style=\"text-align:justify;\">" + getString(R.string.about_us_text) + "</body>"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!Utility.ABOUT.equalsIgnoreCase(getSupportActionBar().getTitle().toString())) {
            menu.clear();
        }
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!Utility.ABOUT.equalsIgnoreCase(getSupportActionBar().getTitle().toString())) {
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
