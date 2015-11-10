package com.teksystems.devicetracker.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.data.mappers.ProjectMapper;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.fragments.ProjectsFragment;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;
import com.teksystems.devicetracker.view.views.activities.NewProjectActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akokala on 7-10-15.
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectListHolder> {

    private static final String LOG_TAG = "ProjectListAdapter";
    private List<ProjectEntity> mProjectEntities;
    private Context mContext;
    private MenuActivity mMenuActivity;
    private ProjectsFragment mFragment;
    private String mProjectName;

    public ProjectListAdapter(List<ProjectEntity> mProjectEntities, Context mContext, MenuActivity mMenuActivity, ProjectsFragment mFragment) {
        this.mProjectEntities = new ArrayList<>(mProjectEntities);
        this.mContext = mContext;
        this.mMenuActivity = mMenuActivity;
        this.mFragment = (ProjectsFragment) mFragment;
    }

    @Override
    public ProjectListHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.layout_simple_project_list_item, viewGroup, false);

        return new ProjectListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProjectListHolder holder, final int position) {
        final ProjectEntity projectEntity = mProjectEntities.get(position);
        holder.mProjectName.setText(projectEntity.getProjectName());

        /*Long click for deleting Project*/
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteConfirmation(projectEntity);
                return true;
            }
        });

        /*Onclick for edit Project*/
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent projectListIntent = new Intent(mMenuActivity, NewProjectActivity.class);
                projectListIntent.putExtra(Utility.PROJECT, projectEntity);
                projectListIntent.putExtra(Utility.PROJECT_NAME, projectEntity.getProjectName());
                mMenuActivity.startActivity(projectListIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProjectEntities.size();
    }

    public void animateTo(List<ProjectEntity> mProjectEntities) {
        applyAndAnimateRemovals(mProjectEntities);
        applyAndAnimateAdditions(mProjectEntities);
        applyAndAnimateMovedItems(mProjectEntities);
    }

    private void applyAndAnimateRemovals(List<ProjectEntity> newModels) {
        for (int i = mProjectEntities.size() - 1; i >= 0; i--) {
            final ProjectEntity model = mProjectEntities.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ProjectEntity> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ProjectEntity model = newModels.get(i);
            if (!mProjectEntities.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ProjectEntity> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ProjectEntity model = newModels.get(toPosition);
            final int fromPosition = mProjectEntities.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ProjectEntity removeItem(int position) {
        final ProjectEntity model = mProjectEntities.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ProjectEntity model) {
        mProjectEntities.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ProjectEntity model = mProjectEntities.remove(fromPosition);
        mProjectEntities.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void deleteProject(final ProjectEntity projectEntity) {
     /*Delete the project if it contains in the table*/
        mMenuActivity.getPresenter().deleteObject(ProjectMapper.getParseObjectFromProjectEntity(projectEntity), new DeleteCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.showAlertDialog(null, mMenuActivity.getString(R.string.delete_successfull, projectEntity.getProjectName()), null, null,
                            mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
                                @Override
                                public void onPositiveButtonClicked() {

                                }

                                @Override
                                public void onNegativeButtonClicked() {
                                }

                                @Override
                                public void onMultiChoiceClicked(int position, boolean isChecked) {

                                }
                            });
                    mMenuActivity.hideProgressDialog();
                    mFragment.fetchProjectList();
                }
                else if (parseException != null && parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
                else {
                    projectNotFoundAlert(projectEntity);
                }
            }
        });
    }

    private void projectNotFoundAlert(ProjectEntity projectEntity) {
        mMenuActivity.showAlertDialog(null, mMenuActivity.getString(R.string.project_not_found, projectEntity.getProjectName()), null, null,
                mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {

                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
        mMenuActivity.hideProgressDialog();
        mFragment.fetchProjectList();
    }

    /*Method t show alertDialog before deleting the project Name*/
    private void deleteConfirmation(final ProjectEntity projectEntity) {
        mMenuActivity.showAlertDialog(null, mMenuActivity.getString(R.string.delete_project_confirmation, projectEntity.getProjectName()), null, null,
                mMenuActivity.getString(R.string.delete), mMenuActivity.getString(R.string.cancel_msg), new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        mMenuActivity.showProgressDialog("");
                        deleteProject(projectEntity);
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
    }

    public class ProjectListHolder extends RecyclerView.ViewHolder {
        protected TextView mProjectName;
        protected CardView mCardView;

        public ProjectListHolder(View itemView) {
            super(itemView);

            mProjectName = (TextView) itemView.findViewById(R.id.tv_project_name);
            mCardView = (CardView) itemView;
        }
    }
}
