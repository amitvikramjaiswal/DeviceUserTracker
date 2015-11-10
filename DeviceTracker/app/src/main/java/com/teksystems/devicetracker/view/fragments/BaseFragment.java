package com.teksystems.devicetracker.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getFragmentLayoutResourceId(), container, false);
            findViews();
        }
        return mRootView;
    }

    /**
     * Use this method to specify the main layout of the fragment that should be inflated in the method {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @return the layout id of the fragment's root view to be inflated in the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    abstract int getFragmentLayoutResourceId();

    /**
     * look up all the views inside the layout specified by {@link #getFragmentLayoutResourceId()} and assign them inside this method
     */
    abstract void findViews();

    /**
     * @return false if this fragment is no longer active, true otherwise
     */
    public boolean isAlive() {
        return (!isDetached() && isAdded() && getActivity() != null);
    }

    /**
     * @return the AppCompat SupportActionBar associated with the parent Activity. (actually the ToolBar)
     */
    public ActionBar getActionBar() {
        if (getActivity() == null)
            return null;
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
}
