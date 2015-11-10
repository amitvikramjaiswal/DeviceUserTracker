package com.teksystems.devicetracker.view.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.presenter.presenters.NewDevicePresenter;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.NewDeviceView;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.InjectView;

public class NewDeviceActivity extends BaseActivity<NewDeviceView, NewDevicePresenter>
        implements NewDeviceView, View.OnClickListener, TextWatcher, SaveCallback, View.OnFocusChangeListener, AdapterView.OnItemSelectedListener, GetCallback<ParseObject>, CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.sv_new_device)
    private ScrollView svNewDevice;
    @InjectView(R.id.et_device_name)
    private EditText etDeviceName;
    @InjectView(R.id.et_mac_address)
    private EditText etMacAddress;
    @InjectView(R.id.spnr_device_os)
    private Spinner spnrDeviceOS;
    @InjectView(R.id.et_os_version)
    private EditText etDeviceVersion;
    @InjectView(R.id.btn_save)
    private Button btnSave;
    @InjectView(R.id.btn_cancel)
    private Button btnCancel;
    @InjectView(R.id.cb_is_enabled)
    private CheckBox cbIsEnabled;
    private Context mContext;
    private DeviceEntity deviceEntity;
    private String mode, mMacAddress, mDeviceOs;
    private UserEntity mUserEntity;
    private Bundle mBundle;
    private String mDeviceNameUpdate;

    @Override
    protected void onBaseActivityCreate(Bundle savedInstanceState) {
        super.onBaseActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        mContext = this;

        showSpinner();

        if (getIntent().hasExtra(Utility.DEVICE)) {
            deviceEntity = (DeviceEntity) getIntent().getExtras().getSerializable(Utility.DEVICE);
            mDeviceNameUpdate = getIntent().getStringExtra(Utility.DEVICE_NAME);
            mode = Utility.EDIT_MODE;
            getSupportActionBar().setTitle(R.string.reset_device);
            populateValues();
        } else if (getIntent().hasExtra(Utility.MAC_ADDRESS) && getIntent().hasExtra(Utility.DEVICE_OS)) {
            mode = Utility.ADMIN_MODE;
            deviceEntity = new DeviceEntity();
            btnCancel.setEnabled(false);
            prePopulateValues();
            getSupportActionBar().setTitle(R.string.new_device);

        } else {
            mode = Utility.SAVE_MODE;
            cbIsEnabled.setChecked(true);
            deviceEntity = new DeviceEntity();
            getSupportActionBar().setTitle(R.string.new_device);
        }

        addListeners();

    }

    private void prePopulateValues() {
        mBundle = getIntent().getExtras();

        mUserEntity = (UserEntity) mBundle.getSerializable(Utility.USER_TABLE);
        mMacAddress = mBundle.getString(Utility.MAC_ADDRESS);
        mDeviceOs = mBundle.getString(Utility.DEVICE_OS);
        etMacAddress.setText(mMacAddress);
        etDeviceVersion.setText(Build.VERSION.RELEASE);
        cbIsEnabled.setChecked(true);
        spnrDeviceOS.setSelection(1);
    }

    private void populateValues() {
        btnSave.setText(getString(R.string.update_button));
        etDeviceName.setText(deviceEntity.getDeviceName());
        etMacAddress.setText(deviceEntity.getMacAddress());
        spnrDeviceOS.setSelection(deviceEntity.getDeviceOS().equalsIgnoreCase("android") ? 1 : 2);
        etDeviceVersion.setText(deviceEntity.getDeviceVersion());
        cbIsEnabled.setChecked(deviceEntity.isEnabled());
    }

    private void addListeners() {
        etDeviceName.addTextChangedListener(this);
        etMacAddress.addTextChangedListener(this);
        etDeviceVersion.addTextChangedListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        cbIsEnabled.setOnCheckedChangeListener(this);
        spnrDeviceOS.setOnItemSelectedListener(this);
    }

    public void showSpinner() {
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list, getResources().getStringArray(R.array.arr_device_os));
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spnrDeviceOS.setAdapter(sortAdapter);
        spnrDeviceOS.setSelection(1);
    }

    public void addNewDevice() {
        if (mode.equalsIgnoreCase(Utility.ADMIN_MODE)) {
            deviceEntity.setUserEntity(mUserEntity);
        } else if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
            deviceEntity.setUserEntity(DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity());
        }
        getPresenter().addObject(DeviceMapper.getParseObjectFromDeviceEntity(deviceEntity), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                onSaveButtonClick();
                break;
            case R.id.btn_cancel:
                onCancelButtonClick();
                break;
        }
    }

    private void onSaveButtonClick() {
        String strDeviceName = etDeviceName.getText().toString().toLowerCase().trim();
        String strMacAddress = etMacAddress.getText().toString().trim();
        String strDeviceOS = (String) spnrDeviceOS.getSelectedItem();
        String strDeviceVersion = etDeviceVersion.getText().toString().toLowerCase().trim();
        boolean blnIsEnabled = cbIsEnabled.isChecked();

        deviceEntity.setDeviceName(strDeviceName);
        deviceEntity.setDeviceOS(strDeviceOS);
        deviceEntity.setDeviceVersion(strDeviceVersion);
        deviceEntity.setMacAddress(strMacAddress);
        deviceEntity.setIsEnabled(blnIsEnabled);

        if (deviceEntity.getUserEntity() != null) {
            deviceEntity.setWithAdmin(deviceEntity.getUserEntity().isAdmin());
        } else {
            deviceEntity.setWithAdmin(true);
        }

        if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
            DeviceEntity temp = DUCacheHelper.getDuCacheHelper().getSessionsEntity().getDeviceEntity();
            if (deviceEntity.getObjectId().equals(temp.getObjectId())) {
                temp.setIsEnabled(blnIsEnabled);
            }
        }

        hideKeyBoard();
        initiateAddingDevice();
    }

    private void initiateAddingDevice() {
        showProgressDialog("");
        checkIfDeviceRegistered();
    }

    public void checkIfDeviceRegistered() {
        Map<String, Object> map = new HashMap<>();
        map.put(Utility.MAC_ADDRESS, deviceEntity.getMacAddress());
        getPresenter().fetchRecordForKeys(Utility.DEVICE, map, this);
    }

    private void onCancelButtonClick() {
        hideKeyBoard();
        onBackPressed();
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            deviceAddedSuccessfully();
        } else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(NewDeviceActivity.this);
        } else if (e.getCode() == Utility.OBJECT_NOT_FOUND) {
            deviceNotFoundAlert();
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(NewDeviceActivity.this);
        } else {
            Utils.showUnexpectedError(NewDeviceActivity.this);
        }
    }

    private void deviceNotFoundAlert() {
        showAlertDialog(null, getString(R.string.device_not_found, deviceEntity.getDeviceName()), null, null,
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

    private void showDeviceAlreadyRegistered(DeviceEntity duplicateDeviceEntity) {
        hideProgressDialog();
        showAlertDialog(getString(R.string.app_name), String.format(getString(R.string.device_already_exists), duplicateDeviceEntity.getDeviceName(), duplicateDeviceEntity.getMacAddress()), null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                etMacAddress.requestFocus();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    private void deviceAddedSuccessfully() {
        hideProgressDialog();
        String msg = "";
        if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
            msg = String.format(getString(R.string.device_updated_successfully), mDeviceNameUpdate);
        } else if (mode.equalsIgnoreCase(Utility.SAVE_MODE) || mode.equalsIgnoreCase(Utility.ADMIN_MODE)) {
            msg = String.format(getString(R.string.device_added_successfully), deviceEntity.getDeviceName(), deviceEntity.getMacAddress());
        }
        btnSave.setEnabled(false);
        showAlertDialog(getString(R.string.app_name), msg, null, null, getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                if (mode.equalsIgnoreCase(Utility.ADMIN_MODE)) {
                    Intent loginIntent = new Intent(NewDeviceActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
                    clearFields();
                }

                /*Update the deviceName after successful update*/
                mDeviceNameUpdate = etDeviceName.getText().toString().trim();
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    private void clearFields() {
        etDeviceName.setText("");
        etMacAddress.setText("");
        etDeviceVersion.setText("");
        spnrDeviceOS.setSelection(1);
        cbIsEnabled.setChecked(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkFieldsForEmptyValues();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void checkFieldsForEmptyValues() {
        String strDeviceName = etDeviceName.getText().toString().trim();
        String strMacAddress = etMacAddress.getText().toString().trim();
        String strDeviceOS = (String) spnrDeviceOS.getSelectedItem();
        String strDeviceVersion = etDeviceVersion.getText().toString().trim();
        boolean blnIsEnabled = cbIsEnabled.isChecked();

        enableDisableSaveButton(strDeviceName, strMacAddress, strDeviceOS, blnIsEnabled, strDeviceVersion);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.et_mac_address:
                    svNewDevice.fullScroll(View.FOCUS_DOWN);
                    break;
            }
        }
    }

    @Override
    public void done(ParseObject parseObject, ParseException e) {
        if (e == null && parseObject != null) {
            if (parseObject.getObjectId().equals(deviceEntity.getObjectId())) {
                addNewDevice();
            } else {
                DeviceEntity duplicateDeviceEntity = DeviceMapper.getDeviceEntityFromParseObject(parseObject);
                showDeviceAlreadyRegistered(duplicateDeviceEntity);
            }
        } else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(NewDeviceActivity.this);
        } else if (e.getCode() == Utility.TIME_OUT) {
            Utils.showRequestTimeOutError(NewDeviceActivity.this);
        } else {
            addNewDevice();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (deviceEntity == null) {
            return;
        }
        String strDeviceName = etDeviceName.getText().toString().trim();
        String strMacAddress = etMacAddress.getText().toString().trim();
        String strDeviceVersion = etDeviceVersion.getText().toString().trim();
        String strDeviceOS = (String) spnrDeviceOS.getSelectedItem();
        boolean blnIsEnabled = cbIsEnabled.isChecked();

        enableDisableSaveButton(strDeviceName, strMacAddress, strDeviceOS, blnIsEnabled, strDeviceVersion);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_is_enabled:
                String strDeviceName = etDeviceName.getText().toString().trim();
                String strMacAddress = etMacAddress.getText().toString().trim();
                String strDeviceVersion = etDeviceVersion.getText().toString().trim();
                String strDeviceOS = (String) spnrDeviceOS.getSelectedItem();
                boolean blnIsEnabled = cbIsEnabled.isChecked();

                enableDisableSaveButton(strDeviceName, strMacAddress, strDeviceOS, blnIsEnabled, strDeviceVersion);
                break;
        }
    }

    private void enableDisableSaveButton(String strDeviceName, String strMacAddress, String strDeviceOS, boolean blnIsEnabled, String strDeviceVersion) {
        if (strDeviceName == null || strMacAddress == null || strDeviceOS == null) {
            btnSave.setEnabled(false);
        } else if (strDeviceName.isEmpty() || strMacAddress.isEmpty() || strDeviceOS.equalsIgnoreCase(getString(R.string.select_device_os))) {
            btnSave.setEnabled(false);
        } else {
            if (mode.equalsIgnoreCase(Utility.EDIT_MODE)) {
                if (strDeviceName.equalsIgnoreCase(deviceEntity.getDeviceName()) && strMacAddress.equalsIgnoreCase(deviceEntity.getMacAddress()) && strDeviceOS.equalsIgnoreCase(deviceEntity.getDeviceOS()) && blnIsEnabled == deviceEntity.isEnabled() && strDeviceVersion.equalsIgnoreCase(deviceEntity.getDeviceVersion())) {
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                }
            } else if (mode.equalsIgnoreCase(Utility.SAVE_MODE)) {
                btnSave.setEnabled(true);
            } else if (mode.equalsIgnoreCase(Utility.ADMIN_MODE)) {
                btnSave.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!(mode.equalsIgnoreCase(Utility.ADMIN_MODE))) {
            super.onBackPressed();
        }
    }
}
