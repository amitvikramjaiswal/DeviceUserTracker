package com.teksystems.devicetracker.view.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.util.SortComparator;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.adapters.DeviceUsersListAdapter;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by akokala on 15-9-15.
 */
public class DeviceUsersFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FindCallback<ParseObject> {

    private static final String LOG_TAG = "DeviceUsersFragment";
    private MenuActivity mMenuActivity;
    private Context mContext;

    private RecyclerView rvDeviceList;
    private SearchView svSearch;
    private List<DeviceEntity> arlDevices;
    private DeviceUsersListAdapter deviceUsersListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String searchQuery;
    private String query;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mMenuActivity = (MenuActivity) getActivity();
        mContext = mMenuActivity.getApplicationContext();
        setHasOptionsMenu(true);
        mMenuActivity.getSupportActionBar().setTitle(R.string.navigation_item1);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(mMenuActivity, LinearLayoutManager.VERTICAL, false);
        addListeners();
    }

    private void addListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
        rvDeviceList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (rvDeviceList.canScrollVertically(-1)) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDeviceList();
    }

    private void fetchDeviceList() {
        mMenuActivity.showProgressDialog(getString(R.string.progress_bar_message));
        mMenuActivity.getPresenter().fetchAllRecords(Utility.DEVICE, this, Utility.TAGGED_USER, Utility.TAGGED_PROJECT, Utility.LAST_ACTIVE_SESSION);
    }

    @Override
    public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
            arlDevices = new ArrayList<>();
            for (ParseObject parseObject : list) {
                DeviceEntity deviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                arlDevices.add(deviceEntity);
            }
            showDeviceList();
        } else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(mMenuActivity);
            swipeRefreshLayout.setRefreshing(false);
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(mMenuActivity);
        } else {
            Utils.showUnexpectedError(mMenuActivity);
        }
    }

    private void showDeviceList() {
        rvDeviceList.setHasFixedSize(true);
        Collections.sort(arlDevices, new SortComparator());
        deviceUsersListAdapter = new DeviceUsersListAdapter(mContext, mMenuActivity, DeviceUsersFragment.this, arlDevices);
        rvDeviceList.setLayoutManager(linearLayoutManager);
        rvDeviceList.setAdapter(deviceUsersListAdapter);
        mMenuActivity.hideProgressDialog();
        swipeRefreshLayout.setRefreshing(false);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            svSearch.setQuery(searchQuery + " ", true);
            svSearch.setQuery(searchQuery.trim(), true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!Utility.DEVICE_USERS.equalsIgnoreCase(mMenuActivity.getFragmentTitle())) {
            menu.clear();
        } else {
            menu.clear();
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            svSearch = (SearchView) searchItem.getActionView();
            showSearch();
        }
    }

    private void showSearch() {
        svSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                if (arlDevices != null && arlDevices.size() > 0) {
                    final List<DeviceEntity> filteredModelList = filter(arlDevices, newText);
                    deviceUsersListAdapter.animateTo(filteredModelList);
                    rvDeviceList.smoothScrollToPosition(0);
                }
                return true;
            }
        });
    }

    private List<DeviceEntity> filter(List<DeviceEntity> models, String query) {
        query = query.toLowerCase();

        final List<DeviceEntity> filteredModelList = new ArrayList<>();
        for (DeviceEntity model : models) {
            final String deviceName = model.getDeviceName().toLowerCase();
            final String username = model.getUserEntity() != null ? model.getUserEntity().getUsername().toLowerCase() : "";
            if (deviceName.contains(query) || username.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    int getFragmentLayoutResourceId() {
        return R.layout.fragment_device_users;
    }

    @Override
    void findViews() {
        rvDeviceList = (RecyclerView) mRootView.findViewById(R.id.rv_device_user_list);
        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN);
    }

    @Override
    public void onRefresh() {
        fetchDeviceList();
    }


}
