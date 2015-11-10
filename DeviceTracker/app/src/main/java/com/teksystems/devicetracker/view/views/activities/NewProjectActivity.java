package com.teksystems.devicetracker.view.views.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.data.mappers.ProjectMapper;
import com.teksystems.devicetracker.presenter.presenters.NewProjectPresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.NewProjectView;

import roboguice.inject.InjectView;

/**
 * Created by akokala on 6-10-15.
 */
public class NewProjectActivity extends BaseActivity<NewProjectView, NewProjectPresenter> implements NewProjectView,
        View.OnClickListener {

    private String mProjectNameValue, mProjectDescriptionValue;
    private ProjectEntity mProjectEntity;
    private String mode;
    private TextWatcher mTextWatcher1, mTextWatcher2;
    private String mProjectNameUpdate;

    @InjectView(R.id.et_project_name)
    private EditText mProjectName;

    @InjectView(R.id.et_edit_project_description)
    private EditText mProjectDescription;

    @InjectView(R.id.bt_cancel_button)
    private Button mCancelButton;

    @InjectView(R.id.bt_save_button)
    private Button mSaveButton;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        if (getIntent().hasExtra(Utility.PROJECT)) {
            setTitle(R.string.reset_project);
            mProjectEntity = (ProjectEntity) getIntent().getExtras().getSerializable(Utility.PROJECT);
            mProjectNameUpdate = getIntent().getStringExtra(Utility.PROJECT_NAME);
            mode = Utility.EDIT_MODE;
            prePopulateValues();
            onTextChangeForResetProject();
            mProjectName.addTextChangedListener(mTextWatcher2);
            mProjectDescription.addTextChangedListener(mTextWatcher2);
        }
        else {
            mode = Utility.SAVE_MODE;
            setTitle(R.string.new_project);
            mProjectEntity = new ProjectEntity();
            ProjectMapper.getParseObjectFromProjectEntity(mProjectEntity);
            onTextChangeForAddProject();
            mProjectName.addTextChangedListener(mTextWatcher1);
        }
        addListener();
    }

    private void addListener() {
        mSaveButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    private void prePopulateValues() {
        mSaveButton.setText(getString(R.string.update_button));
        mProjectName.setText(mProjectEntity.getProjectName());
        mProjectDescription.setText(mProjectEntity.getProjectDescription());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancel_button:
                onBackPressed();
                hideKeyBoard();
                break;

            case R.id.bt_save_button:
                showProgressDialog("");
                saveProjectDetails();
                hideKeyBoard();
                break;
        }
    }


    private void saveProjectDetails() {

        mProjectNameValue = mProjectName.getText().toString().trim();
        mProjectDescriptionValue = mProjectDescription.getText().toString().trim();

        mProjectEntity.setProjectName(mProjectNameValue);
        mProjectEntity.setProjectDescription(mProjectDescriptionValue);

        getPresenter().fetchRecordForKey(Utility.PROJECT, Utility.PROJECT_NAME, mProjectEntity.getProjectName(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException parseException) {
                 /*Project is not existing, So Add ProjectDetails*/
                if (parseObject == null && parseException.getCode() == Utility.OBJECT_NOT_FOUND) {
                    addProjectDetails();
                }
                /*No Internet Connection, Show Alert dialog*/
                else if (parseException != null && parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(NewProjectActivity.this);
                }
                else if (parseException != null && parseException.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(NewProjectActivity.this);
                }
                else {
                    if (parseObject.getObjectId().equals(mProjectEntity.getObjectId())) {
                        addProjectDetails();
                    }
                    else {
                    /*Project already exists, So Show exception AlertDialog*/
                        ParseException exception = new ParseException(202, "Project Name already exists");
                        projectNameExistsErrorDialog(exception);
                    }
                }
            }
        });
    }

    /*Method th show the alertDialog, if the project name is already exists*/
    private void projectNameExistsErrorDialog(ParseException exception) {
        hideProgressDialog();
        switch (exception.getCode()) {
            case Utility.USERNAME_ALREADY_EXISTS:
                showAlertDialog(null, String.format(getString(R.string.project_already_exists), mProjectNameValue), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {

                        if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
                            mProjectName.setText("");
                            mProjectDescription.setText("");
                        }
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
                break;
            default:
                showAlertDialog(null, getString(R.string.unexpected_error), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
                break;
        }
    }

    /*Method to add ProjectValues to Project table*/
    private void addProjectDetails() {
        getPresenter().addNewProject(ProjectMapper.getParseObjectFromProjectEntity(mProjectEntity), new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    projectAddedSuccessfully();
                    hideProgressDialog();
                }
                else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(NewProjectActivity.this);
                }
                else if (parseException.getCode() == Utility.OBJECT_NOT_FOUND) {
                    projectNotFoundAlert();
                }
                else if (parseException.getCode() == Utility.TIME_OUT) {
                    Utils.showRequestTimeOutError(NewProjectActivity.this);
                }
                else {
                    Utils.showUnexpectedError(NewProjectActivity.this);
                }
            }
        });
    }

    /*Method to add Project Values to Project Table*/
    private void projectAddedSuccessfully() {
        hideProgressDialog();
        String msg = "";
        if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
            msg = String.format(getString(R.string.project_added_successfully), mProjectEntity.getProjectName());
        }
        else if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
            msg = String.format(getString(R.string.project_updated_successfully), mProjectNameUpdate);
        }
        showAlertDialog(null, msg, null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {

                /*After SuccessFull Adding Project values to Table clear the EditTextFields*/
                if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
                    mProjectName.setText("");
                    mProjectDescription.setText("");
                }

                /*Update the projectName after successful update*/
                mProjectNameUpdate = mProjectName.getText().toString().trim();

                /*Disable the save button*/
                mSaveButton.setEnabled(false);
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    private void projectNotFoundAlert() {
        showAlertDialog(null, getString(R.string.project_not_found, mProjectEntity.getProjectName()), null, null,
                getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        onBackPressed();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
        hideProgressDialog();
    }

    private void onTextChangeForAddProject() {
        mTextWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String projectName = mProjectName.getText().toString().trim();

                if (projectName.isEmpty() || projectName == null) {
                    mSaveButton.setEnabled(false);
                }
                else {
                    mSaveButton.setEnabled(true);
                }
            }
        };
    }

    private void onTextChangeForResetProject() {
        mTextWatcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String projectName = mProjectName.getText().toString().trim();
                String projectDescription = mProjectDescription.getText().toString().trim();

                boolean isNotNullOrNotEmpty = projectName != null && !projectName.isEmpty();
                boolean isValueChanged = !projectName.equals(mProjectEntity.getProjectName()) || !projectDescription.equals(mProjectEntity.getProjectDescription());

                if (isNotNullOrNotEmpty) {
                    if (isValueChanged) {
                        mSaveButton.setEnabled(true);
                    }
                    else {
                        mSaveButton.setEnabled(false);
                    }
                }
                else {
                    mSaveButton.setEnabled(false);
                }
            }
        };
    }
}
