package com.teksystems.devicetracker.view.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.presenter.presenters.MenuPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.fragments.DeviceUsersFragment;
import com.teksystems.devicetracker.view.fragments.DevicesFragment;
import com.teksystems.devicetracker.view.fragments.NavigationDrawerFragment;
import com.teksystems.devicetracker.view.fragments.ProjectsFragment;
import com.teksystems.devicetracker.view.fragments.SettingsFragment;
import com.teksystems.devicetracker.view.fragments.TagUnTagDevicesFragment;
import com.teksystems.devicetracker.view.fragments.UsersFragment;
import com.teksystems.devicetracker.view.views.MenuView;

import java.util.Date;

public class MenuActivity extends BaseActivity<MenuView, MenuPresenter>
        implements MenuView, NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {

            case 0:
                mTitle = getString(R.string.navigation_item1);
                fragment = new DeviceUsersFragment();
                break;
            case 1:
                mTitle = getString(R.string.navigation_item2);
                fragment = new TagUnTagDevicesFragment();
                break;
            case 2:
                mTitle = getString(R.string.navigation_item3);
                fragment = new DevicesFragment();
                break;
            case 3:
                mTitle = getString(R.string.navigation_item4);
                fragment = new UsersFragment();
                break;
            case 4:
                mTitle = getString(R.string.navigation_item6);
                fragment = new ProjectsFragment();
                break;
            case 5:
                mTitle = getString(R.string.navigation_item5);
                fragment = new SettingsFragment();
                break;
            case 6:
                showLogoutAlert();
                hideProgressDialog();
                break;
            default:
                break;
        }
        if (position != 6) {
            replaceFragment(fragment);
        }
    }


    //Method to replace the Fragment
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        SessionsEntity sessionsEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity();
        if (sessionsEntity == null) {
            return;
        }
        sessionsEntity.setDeviceEntity(null);
        sessionsEntity.setLoginTime(null);
        sessionsEntity.setCreatedAt(null);
        sessionsEntity.setUserEntity(null);
        sessionsEntity.setUpdatedAt(null);
        sessionsEntity.setLogoutTime(new Date());
        sessionsEntity.setLoggedIn(false);
        getPresenter().addObject(SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressDialog();
                if (e == null) {
                    Utils.clearUserData();
                    Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(MenuActivity.this);
                } else if (e.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(MenuActivity.this);
                }
            }
        });

    }

    /* Method to highlight the selected menu from the navigation drawer, when user cancle the logout*/
    private void selectItemBasedOnTitle() {
        if (mTitle.equals(getResources().getString(R.string.navigation_item1))) {
            mNavigationDrawerFragment.selectItem(0);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item2))) {
            mNavigationDrawerFragment.selectItem(1);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item3))) {
            mNavigationDrawerFragment.selectItem(2);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item4))) {
            mNavigationDrawerFragment.selectItem(3);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item5))) {
            mNavigationDrawerFragment.selectItem(5);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item6))) {
            mNavigationDrawerFragment.selectItem(4);
        }
        else if (mTitle.equals(getResources().getString(R.string.navigation_item7))) {
            mNavigationDrawerFragment.selectItem(6);
        }
    }

    public void showLogoutAlert() {
        showAlertDialog(null, getString(R.string.logout_confirmation_msg), null, null, getString(R.string.log_out), getString(R.string.cancel_msg), new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                showProgressDialog("");
                logout();
            }

            @Override
            public void onNegativeButtonClicked() {
                selectItemBasedOnTitle();
            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }

        });

    }

    public String getFragmentTitle() {
        return mTitle.toString();
    }

    public void onBackPressed() {

        if (mNavigationDrawerFragment.getmCurrentSelectedPosition() != 0) {
            mNavigationDrawerFragment.selectItem(0);
        }
        else {
//            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}