package com.teksystems.devicetracker.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.util.SortComparator;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.adapters.UsersListAdapter;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;
import com.teksystems.devicetracker.view.views.activities.NewUserActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by akokala on 15-9-15.
 */
public class UsersFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnItemTouchListener, FindCallback<ParseObject> {


    private static final String LOG_TAG = "UsersFragment";
    private MenuActivity mMenuActivity;
    private Context mContext;

    private RecyclerView rvUsersList;
    private FloatingActionButton fabAddNewUser;
    private SearchView svSearchUser;
    private List<UserEntity> arlUsers;
    private UsersListAdapter usersAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String searchQuery;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mMenuActivity = (MenuActivity) getActivity();
        mContext = mMenuActivity.getApplicationContext();
        setHasOptionsMenu(true);
        mMenuActivity.getSupportActionBar().setTitle(R.string.navigation_item4);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(mMenuActivity, LinearLayoutManager.VERTICAL, false);
        addListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsersList();
    }

    private void showUserList() {
        rvUsersList.setHasFixedSize(true);
        Collections.sort(arlUsers, new SortComparator());
        usersAdapter = new UsersListAdapter(mContext, mMenuActivity, UsersFragment.this, arlUsers);
        rvUsersList.setLayoutManager(linearLayoutManager);
        rvUsersList.setAdapter(usersAdapter);
        mMenuActivity.hideProgressDialog();
        swipeRefreshLayout.setRefreshing(false);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            svSearchUser.setQuery(searchQuery + " ", true);
            svSearchUser.setQuery(searchQuery.trim(), true);
        }
    }

    private void addListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
        fabAddNewUser.setOnClickListener(this);
        rvUsersList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (rvUsersList.canScrollVertically(-1)) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });
    }

    public void fetchUsersList() {
        mMenuActivity.showProgressDialog(getString(R.string.progress_bar_message));
        mMenuActivity.getPresenter().fetchAllRecords(Utility.USER_TABLE, this);
    }

    private void showSearch() {
        svSearchUser.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        svSearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                if (arlUsers != null  && arlUsers.size() > 0) {
                    final List<UserEntity> filteredModelList = filter(arlUsers, newText);
                    usersAdapter.animateTo(filteredModelList);
                    rvUsersList.smoothScrollToPosition(0);
                }
                return true;
            }
        });
    }

    private List<UserEntity> filter(List<UserEntity> models, String query) {
        query = query.toLowerCase();

        final List<UserEntity> filteredModelList = new ArrayList<>();
        for (UserEntity model : models) {
            final String text = model.getUsername().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
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
        if (!Utility.SHOW_USERS.equalsIgnoreCase(mMenuActivity.getFragmentTitle())) {
            menu.clear();
        } else {
            menu.clear();
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            svSearchUser = (SearchView) searchItem.getActionView();
            showSearch();
        }
    }

    @Override
    int getFragmentLayoutResourceId() {
        return R.layout.fragment_users;
    }

    @Override
    void findViews() {
        rvUsersList = (RecyclerView) mRootView.findViewById(R.id.rv_users_list);
        fabAddNewUser = (FloatingActionButton) mRootView.findViewById(R.id.fab_add_new_user);
        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_new_user:
                fabAddNewUserClicked();
                break;
        }
    }

    private void fabAddNewUserClicked() {
        Intent intent = new Intent(mContext, NewUserActivity.class);
        startActivity(intent);
    }

    @Override
    public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
            arlUsers = new ArrayList<>();
            for (ParseObject parseObject : list) {
                UserEntity userEntity = UserMapper.getUserEntityFromParseObject(parseObject);
                arlUsers.add(userEntity);
            }
            showUserList();
        } else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(mMenuActivity);
            swipeRefreshLayout.setRefreshing(false);
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(mMenuActivity);
        } else {
            Utils.showUnexpectedError(mMenuActivity);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onRefresh() {
        fetchUsersList();
    }
}
