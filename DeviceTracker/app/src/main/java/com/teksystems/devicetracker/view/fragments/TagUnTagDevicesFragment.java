package com.teksystems.devicetracker.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.entities.ProjectEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.data.mappers.ProjectMapper;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.SortComparator;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akokala on 15-9-15.
 */
public class TagUnTagDevicesFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = "TagDevicesFragment";
    private Context mContext;
    private MenuActivity mMenuActivity;
    private Button mTagButton, mUnTagButton;
    private RelativeLayout mTagUserLayout, mUnTagUserLayout;
    private List<ProjectEntity> mProjectEntities;
    private List<UserEntity> mUserEntities;
    private List<DeviceEntity> mDeviceEntities;
    private List<DeviceEntity> mDeviceEntities1;

    /*Tag Related Views*/
    private Button mTagUser;
    private Spinner mTagDeviceOs, mTagUserName, mTagDevice, mTagProjects;
    private CheckBox mTagAdapter, mTagCharger, mTagHeadPhone, mTagBox;
    private EditText mTagNote;
    private String mSelectedOs, mSelectedDevice, mSelectedUserName, mSelectedProject, mSelectedUnTagDevices;
    private List<String> mDeviceEntitiesStringIOS;
    private List<String> mDeviceEntitiesStringAndroid;
    private DeviceEntity mDeviceEntity;
    private List<Object> mUnTagAccessories = new ArrayList<>();
    private UserEntity mUserEntity;

    /*UnTag Related View*/
    private Button mUnTagUser;
    private Spinner mUnTagDevices;
    private CheckBox mUnTagAdapter, mUnTagCharger, mUnTagHeadPhone, mUnTagBox;
    private EditText mUnTagNote;
    private TextView mUnTagUserName;
    private LinearLayout mLLUntagNotes;

    @Override
    int getFragmentLayoutResourceId() {
        return R.layout.fragment_tag_devices;
    }

    @Override
    void findViews() {
        mTagUserLayout = (RelativeLayout) mRootView.findViewById(R.id.rl_tag_layout);
        mUnTagUserLayout = (RelativeLayout) mRootView.findViewById(R.id.rl_untag_layout);

        mTagButton = (Button) mRootView.findViewById(R.id.btn_tag);
        mUnTagButton = (Button) mRootView.findViewById(R.id.bt_un_tag);
        mTagUser = (Button) mRootView.findViewById(R.id.btn_tag_user);
        mUnTagUser = (Button) mRootView.findViewById(R.id.btn_untag_user);

        mTagDeviceOs = (Spinner) mRootView.findViewById(R.id.spnr_select_device_os);
        mTagUserName = (Spinner) mRootView.findViewById(R.id.spnr_select_user_name);
        mTagDevice = (Spinner) mRootView.findViewById(R.id.spnr_select_devices);
        mTagProjects = (Spinner) mRootView.findViewById(R.id.spnr_select_projects);
        mUnTagDevices = (Spinner) mRootView.findViewById(R.id.spnr_select_untag_device);

        mTagAdapter = (CheckBox) mRootView.findViewById(R.id.cb_adapter);
        mTagCharger = (CheckBox) mRootView.findViewById(R.id.cb_charger);
        mTagHeadPhone = (CheckBox) mRootView.findViewById(R.id.cb_headphone);
        mTagBox = (CheckBox) mRootView.findViewById(R.id.cb_box);
        mUnTagAdapter = (CheckBox) mRootView.findViewById(R.id.cb_untag_adapter);
        mUnTagCharger = (CheckBox) mRootView.findViewById(R.id.cb_untag_charger);
        mUnTagHeadPhone = (CheckBox) mRootView.findViewById(R.id.cb_untag_headphone);
        mUnTagBox = (CheckBox) mRootView.findViewById(R.id.cb_untag_box);

        mTagNote = (EditText) mRootView.findViewById(R.id.et_edit_note);
        mUnTagNote = (EditText) mRootView.findViewById(R.id.et_untag_edit_note);
        mUnTagUserName = (TextView) mRootView.findViewById(R.id.tv_un_tag_username);
        mLLUntagNotes = (LinearLayout) mRootView.findViewById(R.id.ll_untag_notes);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View createView = super.onCreateView(inflater, container, savedInstanceState);

        if (getActivity() == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
         /* Initialize MenuActivity*/
        mMenuActivity = (MenuActivity) getActivity();
        mContext = mMenuActivity.getApplicationContext();

        mMenuActivity.getSupportActionBar().setTitle(R.string.navigation_item2);

        /*By Default Set the tag button is selected*/
        mTagButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));
        mTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mUnTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));

        mTagUserLayout.setVisibility(View.VISIBLE);

        addListener();

        addDeviceOS();
        addProjectValue();

        return createView;
    }

    private void addListener() {
        mTagButton.setOnClickListener(this);
        mUnTagButton.setOnClickListener(this);
        mTagUser.setOnClickListener(this);
        mUnTagUser.setOnClickListener(this);

        mTagDeviceOs.setOnItemSelectedListener(this);
        mTagDevice.setOnItemSelectedListener(this);
        mTagUserName.setOnItemSelectedListener(this);
        mTagProjects.setOnItemSelectedListener(this);
        mUnTagDevices.setOnItemSelectedListener(this);
        mUnTagDevices.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tag:

                resetTagDeviceValues();
//                addDeviceOS();
                addProjectValue();

                getActionBar().setTitle(getString(R.string.navigation_item2));

                mTagButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));
                mTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.white));

                mUnTagButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                mUnTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));

                mTagUserLayout.setVisibility(View.VISIBLE);
                mUnTagUserLayout.setVisibility(View.GONE);
                break;

            case R.id.bt_un_tag:
                mMenuActivity.hideKeyBoard();
                fetchUnTaggedDevices();

                getActionBar().setTitle(getString(R.string.untag_device_title));

                //Set Default userName as Username
                mUnTagUserName.setTextColor(ContextCompat.getColor(mContext, R.color.hint_color));

                mUnTagButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));
                mUnTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.white));

                mTagButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                mTagButton.setTextColor(ContextCompat.getColor(mContext, R.color.tag_untag_button));

                mTagUserLayout.setVisibility(View.GONE);
                mUnTagUserLayout.setVisibility(View.VISIBLE);
                resetUnTagDeviceValues();
                break;

            case R.id.btn_tag_user:
                mMenuActivity.hideKeyBoard();

                tagDevice();
                break;
            case R.id.btn_untag_user:

                updateOrUnTag();
                mMenuActivity.hideKeyBoard();
                break;
        }
    }

    /* Method to add List to Device OS Spinner*/
    private void addDeviceOS() {
        ArrayAdapter<String> selectDeviceOs = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                mMenuActivity.getResources().getStringArray(R.array.arr_device_os));
        selectDeviceOs.setDropDownViewResource(R.layout.spinner_dropdown);
        mTagDeviceOs.setAdapter(selectDeviceOs);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {

            //Related to Tag Device
            case R.id.spnr_select_device_os:
                addDevices();
                break;
            case R.id.spnr_select_user_name:
                mSelectedUserName = mTagUserName.getSelectedItem().toString();
                break;
            case R.id.spnr_select_devices:
                mSelectedDevice = mTagDevice.getSelectedItem().toString();
                break;
            case R.id.spnr_select_projects:
                mSelectedProject = mTagProjects.getSelectedItem().toString();
                break;

            //Related to unTag Devices
            case R.id.spnr_select_untag_device:
                mSelectedUnTagDevices = mUnTagDevices.getItemAtPosition(position).toString();

                if (position > 0) {
                    /*Prepopulate UnTag Values*/
                    //Prepopulate UserName
                    mUnTagUserName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    mDeviceEntity = mDeviceEntities1.get(position - 1);
                    mUnTagUserName.setText(mDeviceEntity.getUserEntity().getUsername());


                    //Prepopulate CheckBox Values
                    mUnTagAccessories = mDeviceEntity.getAccessories();
                    unTagGetAccessories();

                    //Prepopulate Previous Note
                    mUnTagNote.setText(mDeviceEntity.getNotes());
                } else {
                    mUnTagUserName.setText("Username");
                    mUnTagUserName.setTextColor(ContextCompat.getColor(mContext, R.color.hint_color));
                }
                break;
        }

        /*Condition for Enable or disable the Tag button*/
        if (mSelectedOs == null || mSelectedDevice == null || mSelectedUserName == null || mSelectedProject == null) {
            mTagUser.setEnabled(false);
        } else if (mSelectedOs.equalsIgnoreCase("Select Device OS") ||
                mSelectedDevice.equalsIgnoreCase(mMenuActivity.getString(R.string.select_device)) ||
                mSelectedUserName.equalsIgnoreCase(mMenuActivity.getString(R.string.select_user_name))) {
            mTagUser.setEnabled(false);
        } else {
            mTagUser.setEnabled(true);
        }

        /*Condition for Enable or disable the UnTag button*/
        if (mSelectedUnTagDevices == null) {
            mUnTagUser.setEnabled(false);
        } else if (mSelectedUnTagDevices.equals("Select Device")) {
            mUnTagUser.setEnabled(false);
        } else {
            mUnTagUser.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /* Method to Fetch Project Details and display in spinner*/
    private void addProjectValue() {
        mMenuActivity.showProgressDialog("");
        mMenuActivity.getPresenter().fetchAllRecords(Utility.PROJECT, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();
                    mProjectEntities = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        ProjectEntity projectEntity = ProjectMapper.getProjectEntityFromParseObject(parseObject);
                        mProjectEntities.add(projectEntity);
                    }
                    Collections.sort(mProjectEntities, new SortComparator());

                    List<String> projectEntities = new ArrayList<>();
                    for (ProjectEntity projectEntity : mProjectEntities) {
                        projectEntities.add(projectEntity.getProjectName());
                    }

                    projectEntities.add(0, mMenuActivity.getString(R.string.select_project));
                    ArrayAdapter<String> selectProject = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                            projectEntities);
                    selectProject.setDropDownViewResource(R.layout.spinner_dropdown);
                    mTagProjects.setAdapter(selectProject);
                    mTagProjects.setEnabled(true);

                    addUserNames();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        });
    }

    /*Method to fetch UserNames and display in the spinner*/
    private void addUserNames() {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.IS_ENABLED, true);
        mMenuActivity.getPresenter().fetchRecordForKeys(Utility.USER_TABLE, map, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();
                    mUserEntities = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        mUserEntity = UserMapper.getUserEntityFromParseObject(parseObject);
                        if (!mUserEntity.getUsername().equals(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity().getUsername())) {
                            mUserEntities.add(mUserEntity);
                        }
                    }

                    Collections.sort(mUserEntities, new SortComparator());

                    List<String> userEntities = new ArrayList<>();
                    for (UserEntity userEntity : mUserEntities) {
                        userEntities.add(userEntity.getUsername());
                    }

                    userEntities.add(0, mMenuActivity.getString(R.string.select_user_name));

                    ArrayAdapter<String> selectUserName = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                            userEntities);
                    selectUserName.setDropDownViewResource(R.layout.spinner_dropdown);
                    mTagUserName.setAdapter(selectUserName);
                    mTagUserName.setEnabled(true);

                    fetchDevicesWithTheAdminForAndroid();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        });
    }

    /*Method to show devices with the admin based on the selected OS*/
    private void addDevices() {
        mSelectedOs = mTagDeviceOs.getSelectedItem().toString();
        mDeviceEntities = new ArrayList<>();
        if (mSelectedOs != null && mSelectedOs.equalsIgnoreCase("Select Device OS")) {
            setSpinnerWithNoData(mTagDevice, new String[]{mMenuActivity.getString(R.string.select_device)});
            if (mUserEntities == null) {
                setSpinnerWithNoData(mTagUserName, new String[]{mMenuActivity.getString(R.string.select_user_name)});
            }
            if (mProjectEntities == null) {
                setSpinnerWithNoData(mTagProjects, new String[]{mMenuActivity.getString(R.string.select_project)});
            }
        } else if (mDeviceEntitiesStringAndroid != null && mSelectedOs != null && mSelectedOs.equalsIgnoreCase(Utility.ANDROID_OS)) {
            ArrayAdapter<String> devicesWithAdminAndroid = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                    mDeviceEntitiesStringAndroid);
            devicesWithAdminAndroid.setDropDownViewResource(R.layout.spinner_dropdown);
            mTagDevice.setAdapter(devicesWithAdminAndroid);
            mTagDevice.setEnabled(true);
        } else if (mDeviceEntitiesStringIOS != null && mSelectedOs != null && mSelectedOs.equalsIgnoreCase(Utility.IOS_OS)) {
            ArrayAdapter<String> devicesWithAdminIOS = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                    mDeviceEntitiesStringIOS);
            devicesWithAdminIOS.setDropDownViewResource(R.layout.spinner_dropdown);
            mTagDevice.setAdapter(devicesWithAdminIOS);
            mTagDevice.setEnabled(true);
        }
    }

    private void setSpinnerWithNoData(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
    }

    /* Method to fetch the free devices which are with Admin,and Android OS*/
    private void fetchDevicesWithTheAdminForAndroid() {

        mMenuActivity.getPresenter().fetchRecordsForKeysForAndroid(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    mDeviceEntities = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        DeviceEntity deviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                        if (deviceEntity.getUserEntity() != null && deviceEntity.getUserEntity().getUsername().equalsIgnoreCase(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity().getUsername())) {
                            mDeviceEntities.add(deviceEntity);
                        }
                    }

                    Collections.sort(mDeviceEntities, new SortComparator());
                    mDeviceEntitiesStringAndroid = new ArrayList<String>();
                    mDeviceEntitiesStringAndroid.add(mMenuActivity.getString(R.string.select_device));
                    for (DeviceEntity deviceEntity : mDeviceEntities) {
                        mDeviceEntitiesStringAndroid.add(deviceEntity.getDeviceName());
                    }

                    fetchDevicesWithTheAdminForIOS();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }

            }
        }, Utility.TAGGED_USER);
    }

    /*Method to fetch the free devices which are with Admin,and Android OS*/
    private void fetchDevicesWithTheAdminForIOS() {
        mMenuActivity.getPresenter().fetchRecordsForKeysForIOS(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    mDeviceEntities = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        DeviceEntity deviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                        if (deviceEntity.getUserEntity() == null || deviceEntity.getUserEntity().getUsername().equalsIgnoreCase(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity().getUsername())) {
                            mDeviceEntities.add(deviceEntity);
                        }
                    }

                    Collections.sort(mDeviceEntities, new SortComparator());

                    mDeviceEntitiesStringIOS = new ArrayList<String>();
                    mDeviceEntitiesStringIOS.add(mMenuActivity.getString(R.string.select_device));
                    for (DeviceEntity deviceEntity : mDeviceEntities) {
                        mDeviceEntitiesStringIOS.add(deviceEntity.getDeviceName());
                    }
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        }, Utility.TAGGED_USER);
    }


    /*Get the Checked checkbox value and pass to List */
    private List<Object> getCheckedCheckbox() {
        List<Object> selectedCheckboxes = new ArrayList<Object>();
        if (mTagAdapter.isChecked()) {
            selectedCheckboxes.add("Adaptor");
        }
        if (mTagCharger.isChecked()) {
            selectedCheckboxes.add("Charger");
        }
        if (mTagHeadPhone.isChecked()) {
            selectedCheckboxes.add("Headphone");
        }

        if (mTagBox.isChecked()) {
            selectedCheckboxes.add("Box");
        }
        return selectedCheckboxes;
    }

    /*Get the Checked checkbox value and pass to List For untag */
    private List<Object> getCheckedCheckboxForUnTag() {
        List<Object> selectedCheckboxes = new ArrayList<Object>();
        if (mUnTagAdapter.isChecked()) {
            selectedCheckboxes.add("Adaptor");
        }
        if (mUnTagCharger.isChecked()) {
            selectedCheckboxes.add("Charger");
        }
        if (mUnTagHeadPhone.isChecked()) {
            selectedCheckboxes.add("Headphone");
        }

        if (mUnTagBox.isChecked()) {
            selectedCheckboxes.add("Box");
        }
        return selectedCheckboxes;
    }

    /*Method to Tag Device*/
    private void tagDevice() {
        mSelectedDevice = mTagDevice.getSelectedItem().toString();
        Map<String, Object> map = new HashMap<>();
        mMenuActivity.showProgressDialog("");
        map.put(Utility.DEVICE_NAME, mSelectedDevice);
        mMenuActivity.getPresenter().fetchRecordForKeys(Utility.DEVICE, map, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();
                    mDeviceEntities = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        mDeviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                        if (mDeviceEntity.isEnabled()) {
                            if (mDeviceEntity.getUserEntity() == null || mDeviceEntity.getUserEntity().isAdmin()) {

                                 /*Proceed with next conditions*/
                                fetchUserName();
                            } else {
                                /*Device is already Tagged(Device is with some user)*/
                                mMenuActivity.showAlertDialog(null, mMenuActivity.getString((R.string.device_tagged_error), mSelectedDevice, mDeviceEntity.getUserEntity().getUsername()), null, null, mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
                                    @Override
                                    public void onPositiveButtonClicked() {
                                        mMenuActivity.hideProgressDialog();
                                      /*Fetch Devices */
                                        addDevices();
                                    }

                                    @Override
                                    public void onNegativeButtonClicked() {

                                    }

                                    @Override
                                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                                    }
                                });
                            }
                        } else {
                            /*Device is Not found*/
                            mMenuActivity.showAlertDialog(null, mMenuActivity.getString((R.string.device_not_found), mSelectedDevice), null, null, mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
                                @Override
                                public void onPositiveButtonClicked() {
                                    mMenuActivity.hideProgressDialog();
                                      /*Fetch Devices */
                                    addDevices();
                                }

                                @Override
                                public void onNegativeButtonClicked() {

                                }

                                @Override
                                public void onMultiChoiceClicked(int position, boolean isChecked) {

                                }
                            });
                        }

                    }
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        }, Utility.TAGGED_USER);
    }


    /* Method to Check weather the selected username is already exists or not!*/
    private void fetchUserName() {
        mSelectedUserName = mTagUserName.getSelectedItem().toString();
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.USERNAME, mSelectedUserName);
        map.put(Utility.IS_ENABLED, true);
        mMenuActivity.getPresenter().findObjectForKey(Utility.USER_TABLE, map, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    /*Check Weather the Selected project is exists or not*/

                    if (!mSelectedProject.equals("Select Project")) {
                        fetchProjectName(parseObject);
                    } else {
                        updateTagDetails(parseObject, null);
                    }
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                } else {
                    /*User Not found Alert*/
                    userNameNotExistsAlert();
                }
            }
        });
    }

    /*Method to check weather the selected project is exists or not*/

    private void fetchProjectName(final ParseObject userObject) {
        mSelectedProject = mTagProjects.getSelectedItem().toString();
        mMenuActivity.getPresenter().fetchRecordForKey(Utility.PROJECT, Utility.PROJECT_NAME, mSelectedProject, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    /*Tag The devices*/
                    updateTagDetails(userObject, parseObject);
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                } else {

                   /* Project Not Found Alert*/
                    ProjectNotExistsAlert();
                }
            }

        });
    }

    /* Project Not Found Alert*/
    private void ProjectNotExistsAlert() {
        mMenuActivity.showAlertDialog(null, mMenuActivity.getString((R.string.project_not_exists), mSelectedProject), null, null, mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                mMenuActivity.hideProgressDialog();

                /*Fetch ProjectNames*/
                addProjectValue();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    /*User Not found Alert*/
    private void userNameNotExistsAlert() {
        mMenuActivity.showAlertDialog(null, mMenuActivity.getString((R.string.user_not_exists), mSelectedUserName), null, null, mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                mMenuActivity.hideProgressDialog();

                /*Fetch UserNames*/
                addUserNames();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    /*Method for Update Tag Details*/
    private void updateTagDetails(ParseObject userObject, ParseObject projectObject) {
        mDeviceEntity.setUserEntity(UserMapper.getUserEntityFromParseObject(userObject));
        mDeviceEntity.setProjectEntity(ProjectMapper.getProjectEntityFromParseObject(projectObject));
        mDeviceEntity.setAccessories(getCheckedCheckbox());
        mDeviceEntity.setNotes(mTagNote.getText().toString().trim());
//        mMenuActivity.showProgressDialog("");
        mMenuActivity.getPresenter().saveTaggedUserValues(mDeviceEntity, new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    taggedSuccessfully();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        });
    }

    /*SuccessFully Tagged Alert*/
    private void taggedSuccessfully() {

       /*Successfully tagged devices*/
        mMenuActivity.showAlertDialog(null, getString((R.string.device_tagged_successfully), mSelectedDevice, mSelectedUserName), null, null, getString(R.string.ok_msg), null,
                new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        mMenuActivity.hideProgressDialog();

                         /*Fetch the Values*/
                        mDeviceEntitiesStringAndroid.clear();
                        mDeviceEntitiesStringIOS.clear();

                        resetTagDeviceValues();


                        ((ArrayAdapter<String>) mTagDevice.getAdapter()).notifyDataSetChanged();
                        addProjectValue();

                        mTagUser.setEnabled(false);

                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
    }


    /* Fetch UnTagged devices,it should not be with Loggged In user */
    private void fetchUnTaggedDevices() {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.IS_ENABLED, true);
        mMenuActivity.showProgressDialog("");
        mMenuActivity.getPresenter().fetchRecordForKeys(Utility.DEVICE, map, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();

                    mDeviceEntities1 = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        mDeviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                        if (mDeviceEntity.getUserEntity() != null && !mDeviceEntity.getUserEntity().getUsername().equalsIgnoreCase(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity().getUsername())) {
                            mDeviceEntities1.add(mDeviceEntity);
                        }
                    }

                    Collections.sort(mDeviceEntities1, new SortComparator());

                    List<String> deviceEntitiesForUnTagDevices = new ArrayList<>();
                    deviceEntitiesForUnTagDevices.add(mMenuActivity.getString(R.string.select_device));
                    for (DeviceEntity deviceEntity : mDeviceEntities1) {
                        deviceEntitiesForUnTagDevices.add(deviceEntity.getDeviceName());
                    }

                    addUnTagDevicesToSpinner(deviceEntitiesForUnTagDevices);

                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                    if (mDeviceEntities1 == null)
                        setSpinnerWithNoData(mUnTagDevices, new String[]{mMenuActivity.getString(R.string.select_device)});
                }
            }
        }, Utility.TAGGED_USER, Utility.LAST_ACTIVE_SESSION);
    }

    /*Add Untagged devices to spinner*/
    private void addUnTagDevicesToSpinner(List<String> deviceEntitiesForUnTagDevices) {
        ArrayAdapter<String> selectUnTagDevices = new ArrayAdapter<>(mMenuActivity, R.layout.spinner_list,
                deviceEntitiesForUnTagDevices);
        selectUnTagDevices.setDropDownViewResource(R.layout.spinner_dropdown);
        mUnTagDevices.setAdapter(selectUnTagDevices);
        mUnTagDevices.setEnabled(true);
    }

    /*Update Device Values/UnTagging*/
    private void updateOrUnTag() {
        mDeviceEntity.setAccessories(getCheckedCheckboxForUnTag());
        mDeviceEntity.setNotes(mUnTagNote.getText().toString().trim());
        mDeviceEntity.setUserEntity(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity());
        mDeviceEntity.setSessionsEntity(null);

        mMenuActivity.showProgressDialog("");
        mMenuActivity.getPresenter().saveTaggedUserValues(mDeviceEntity, new SaveCallback() {
            @Override
            public void done(ParseException parseException) {
                if (parseException == null) {
                    mMenuActivity.hideProgressDialog();
                    /*SuccessFully Untagged Alert*/
                    unTagSuccessfully();
                } else if (parseException.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        });
    }

    private List<Object> unTagGetAccessories() {
        //Prepopulate CheckBox Values
        if (mUnTagAccessories != null && mUnTagAccessories.contains("Adaptor")) {
            mUnTagAdapter.setChecked(true);
            mUnTagAdapter.setEnabled(true);
        } else {
            mUnTagAdapter.setChecked(false);
            mUnTagAdapter.setEnabled(false);
        }

        if (mUnTagAccessories != null && mUnTagAccessories.contains("Charger")) {
            mUnTagCharger.setChecked(true);
            mUnTagCharger.setEnabled(true);
        } else {
            mUnTagCharger.setChecked(false);
            mUnTagCharger.setEnabled(false);
        }

        if (mUnTagAccessories != null && mUnTagAccessories.contains("Headphone")) {
            mUnTagHeadPhone.setChecked(true);
            mUnTagHeadPhone.setEnabled(true);
        } else {
            mUnTagHeadPhone.setChecked(false);
            mUnTagHeadPhone.setEnabled(false);
        }

        if (mUnTagAccessories != null && mUnTagAccessories.contains("Box")) {
            mUnTagBox.setChecked(true);
            mUnTagBox.setEnabled(true);
        } else {
            mUnTagBox.setChecked(false);
            mUnTagBox.setEnabled(false);
        }
        return mUnTagAccessories;
    }

    private void unTagSuccessfully() {
        mMenuActivity.showAlertDialog(null, mMenuActivity.getString((R.string.device_untagged_successfully), mSelectedUnTagDevices, mUnTagUserName.getText().toString()), null, null,
                mMenuActivity.getString(R.string.ok_msg), null, new AlertDialogHandler() {
                    @Override
                    public void onPositiveButtonClicked() {
                        mMenuActivity.hideProgressDialog();
                        fetchUnTaggedDevices();

                        /*Reset the Spinner*/
                        resetUnTagDeviceValues();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }

                    @Override
                    public void onMultiChoiceClicked(int position, boolean isChecked) {

                    }
                });
    }

    private void resetTagDeviceValues() {
        mTagDeviceOs.setSelection(0);

        mTagAdapter.setChecked(false);
        mTagCharger.setChecked(false);
        mTagHeadPhone.setChecked(false);
        mTagBox.setChecked(false);

        mTagNote.setText("");
    }

    private void resetUnTagDeviceValues() {
        mUnTagDevices.setSelection(0);

        mUnTagUserName.setText("Username");
        mUnTagNote.setText("");

        mUnTagAdapter.setChecked(false);
        mUnTagCharger.setChecked(false);
        mUnTagHeadPhone.setChecked(false);
        mUnTagBox.setChecked(false);

        mUnTagNote.clearFocus();

        mLLUntagNotes.requestFocus();
    }

}
