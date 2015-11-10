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
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.data.mappers.ProjectMapper;
import com.teksystems.devicetracker.util.SortComparator;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.adapters.ProjectListAdapter;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;
import com.teksystems.devicetracker.view.views.activities.NewProjectActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by akokala on 6-10-15.
 */
public class ProjectsFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnItemTouchListener {

    private static final String LOG_TAG = "ProjectsFragment";
    private MenuActivity mMenuActivity;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private SearchView svSearchProject;
    private List<ProjectEntity> mProjectEntities;
    private ProjectListAdapter mProjectListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String searchQuery;

    @Override
    int getFragmentLayoutResourceId() {
        return R.layout.fragment_projects;
    }

    @Override
    void findViews() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_project_list);
        mFloatingActionButton = (FloatingActionButton) mRootView.findViewById(R.id.fab_add_new_project);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_project_list_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onCreateView = super.onCreateView(inflater, container, savedInstanceState);

        if (getActivity() == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
         /* Initialize MenuActivity*/
        mMenuActivity = (MenuActivity) getActivity();
        mContext = mMenuActivity.getApplicationContext();

        setHasOptionsMenu(true);
        mMenuActivity.getSupportActionBar().setTitle(R.string.navigation_item6);

        mLinearLayoutManager = new LinearLayoutManager(mMenuActivity, LinearLayoutManager.VERTICAL, false);
        addListener();

        return onCreateView;
    }

    @Override
    public void onResume() {
        fetchProjectList();
        super.onResume();
    }

    private void addListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFloatingActionButton.setOnClickListener(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mRecyclerView.canScrollVertically(-1)) {
                    mSwipeRefreshLayout.setEnabled(false);
                } else {
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_new_project:
                /*Start Add Project Screen Onclick of Fab Button*/
                Intent intent = new Intent(mMenuActivity, NewProjectActivity.class);
                startActivity(intent);
                break;
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
        if (!Utility.PROJECT.equalsIgnoreCase(mMenuActivity.getFragmentTitle())) {
            menu.clear();
        } else {
            menu.clear();
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            svSearchProject = (SearchView) searchItem.getActionView();
            showSearch();
        }
    }

    /*Method to FetchProjectList*/
    public void fetchProjectList() {
        mMenuActivity.showProgressDialog("");
        mMenuActivity.getPresenter().fetchAllRecords(Utility.PROJECT, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    mProjectEntities = new ArrayList<ProjectEntity>();
                    for (ParseObject parseObject : list) {
                        ProjectEntity projectEntity = ProjectMapper.getProjectEntityFromParseObject(parseObject);
                        mProjectEntities.add(projectEntity);
                    }
                    showProjectList();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else if (parseException.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(mMenuActivity);
                } else {
                    Utils.showUnexpectedError(mMenuActivity);
                }
            }
        });
    }

    /*Method to add Project Details to adapter*/
    private void showProjectList() {
        mRecyclerView.setHasFixedSize(true);
        Collections.sort(mProjectEntities, new SortComparator());
        mProjectListAdapter = new ProjectListAdapter(mProjectEntities, mContext, mMenuActivity, ProjectsFragment.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mProjectListAdapter);
        mMenuActivity.hideProgressDialog();
        mSwipeRefreshLayout.setRefreshing(false);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            svSearchProject.setQuery(searchQuery + " ", true);
            svSearchProject.setQuery(searchQuery.trim(), true);
        }
    }

    private void showSearch() {
        svSearchProject.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        svSearchProject.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                if (mProjectEntities != null && mProjectEntities.size() > 0) {
                    final List<ProjectEntity> filteredModelList = filter(mProjectEntities, newText);
                    mProjectListAdapter.animateTo(filteredModelList);
                    mRecyclerView.smoothScrollToPosition(0);
                }
                return true;
            }
        });
    }

    private List<ProjectEntity> filter(List<ProjectEntity> models, String query) {
        query = query.toLowerCase();

        final List<ProjectEntity> filteredModelList = new ArrayList<>();
            for (ProjectEntity model : models) {
                final String text = model.getProjectName().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        return filteredModelList;
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
        fetchProjectList();
    }
}
