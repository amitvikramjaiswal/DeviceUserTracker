package com.teksystems.devicetracker.view.views.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.inject.Inject;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.presenter.BasePresenter;
import com.teksystems.devicetracker.presenter.ListPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.log.Logger;
import com.teksystems.devicetracker.view.views.BaseView;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;

/**
 * Base for all the activities in the application.
 *
 * @param <V> The view class to use.
 * @param <P> The presenter class to use.
 */
public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends RoboAppCompatActivity implements BaseView,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener, AdapterView.OnItemLongClickListener {

    private static final String LOG_TAG = "BaseActivity";
    private P mPresenter;
    @InjectView(android.R.id.list)
    @Nullable
    private ListView mListView;
    private ProgressDialog mProgressDialog;

    /**
     * Gets the presenter.
     */
    public final P getPresenter() {
        return mPresenter;
    }

    @SuppressWarnings("unchecked")
    @Inject
    final void setPresenter(P presenter) {
        presenter.setView((V) this);
        mPresenter = presenter;
    }

    /**
     * Gets the list view.
     */
    protected ListView getListView() {
        return mListView;
    }

    /**
     * Gets the list view as an expandable list.
     */
    protected ExpandableListView getExpandableListView() {
        return (ExpandableListView) mListView;
    }

    /**
     * Gets the list adapter.
     */
    protected ListAdapter getListAdapter() {
        if (mListView != null) {
            return mListView.getAdapter();
        }

        return null;
    }

    /**
     * Sets the list adapter for an expandable list.
     */
    protected void setListAdapter(ExpandableListAdapter adapter) {
        if (mListView != null) {
            ((ExpandableListView) mListView).setAdapter(adapter);
        }
    }

    /**
     * Gets the list adapter from an expandable list.
     */
    protected ExpandableListAdapter getExpandableListAdapter() {
        if (mListView != null) {
            return ((ExpandableListView) mListView).getExpandableListAdapter();
        }

        return null;
    }

    /**
     * Sets the list adapter.
     */
    protected void setListAdapter(ListAdapter adapter) {
        if (mListView != null) {
            mListView.setAdapter(adapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onCreate activityClass=" + getClass().getName() + " savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.app_name)));
        }

       /* Change the statusBar color if the version is above Lollipop,Because this feature implemented in API 21*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.app_name));
        }

        onBaseActivityCreate(savedInstanceState);
        setProgressBarIndeterminateVisibility(false);
        mPresenter.onCreate();

        // ListView click listeners
        if (mListView != null && !(mListView instanceof ExpandableListView)) {
            mListView.setOnItemClickListener(this);
            mListView.setOnScrollListener(this);
            mListView.setOnItemLongClickListener(this);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        ;
    }

    public void setHeaderTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onDestroy() {
        Logger.d(LOG_TAG, "onDestroy activityClass=" + getClass().getName());
        mPresenter.onDestroy();
        onBaseActivityDestroy();
        super.onDestroy();
    }

    /**
     * Use this function instead of onCreate to implement functionality.
     *
     * @param savedInstanceState The saved instance state.
     */
    protected void onBaseActivityCreate(Bundle savedInstanceState) {

    }

    /**
     * Use this function instead of onDestroy to implement functionality.
     */
    protected void onBaseActivityDestroy() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getStringArray(int arrayResourceId) {
        return getResources().getStringArray(arrayResourceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startActivityForResult(Intent intent, int resultCode) {
        super.startActivityForResult(intent, resultCode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
//        if (taskList.get(0).numActivities > 0 ||
//                taskList.get(0).baseActivity.getClassName().equals(MainActivity.class.getName())) {
//            Logger.i(LOG_TAG, "This is the last activity in the stack");
//            super.onBackPressed();
//        }
//        else {
//            Logger.i(LOG_TAG, "This is not the last activity in the stack***");
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("hideSplashScreen", true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
//            startActivity(intent);
//            finish();
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        ((ListPresenter<?>) getPresenter()).onListItemClicked(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ((ListPresenter<?>) getPresenter()).onListScrolled(firstVisibleItem, visibleItemCount, totalItemCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return ((ListPresenter<?>) getPresenter()).onItemLongClick(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkGooglePlayServices() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (errorCode == ConnectionResult.SUCCESS) {
            return true;
        }
        else {
            Dialog d = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0);
            if (d != null) {
                d.show();
            }
            else {
                showToast("Could not initialize Google Play Services.");
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgressDialog(String status) {
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_bar_message));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showAlertDialog(String title, String message, Spanned[] checkItemLabels, boolean[] checkItemLabelsChecked,
                                String positiveButton, String negativeButton, final AlertDialogHandler alertDialogHandler) {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);


        myAlertDialog.setTitle(getResources().getString(R.string.app_name));

        if (message != null) {
            myAlertDialog.setMessage(message);
        }

        if (checkItemLabels != null && checkItemLabelsChecked != null) {
            myAlertDialog.setMultiChoiceItems(checkItemLabels, checkItemLabelsChecked, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (alertDialogHandler != null) {
                        alertDialogHandler.onMultiChoiceClicked(which, isChecked);
                    }
                }
            });
        }

        if (positiveButton != null) {
            myAlertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialogHandler != null) {
                        alertDialogHandler.onPositiveButtonClicked();
                    }
                }
            });
        }

        if (negativeButton != null) {
            myAlertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialogHandler != null) {
                        alertDialogHandler.onNegativeButtonClicked();
                    }
                }
            });
            myAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialogHandler.onNegativeButtonClicked();
                }
            });
        }
        else {
            myAlertDialog.setCancelable(false);
        }

        myAlertDialog.show();
    }

    @Override
    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            IBinder windowToken = this.getCurrentFocus().getWindowToken();
            if (windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }
}