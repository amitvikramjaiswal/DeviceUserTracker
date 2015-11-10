package com.teksystems.devicetracker.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.data.mappers.DeviceMapper;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.fragments.DevicesFragment;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;
import com.teksystems.devicetracker.view.views.activities.NewDeviceActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajaiswal on 10/6/2015.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.CardViewHolder> implements DeleteCallback, SaveCallback {

    private final List<DeviceEntity> mArlDevices;
    private Context mContext;
    private MenuActivity mMenuActivity;
    private DevicesFragment mFragment;
    private String username;

    public DeviceListAdapter(Context context, MenuActivity menuActivity, DevicesFragment fragment, List<DeviceEntity> arlDevices) {
        this.mContext = context;
        this.mMenuActivity = menuActivity;
        this.mFragment = fragment;
        this.mArlDevices = new ArrayList<>(arlDevices);
    }

    private void deleteSuccess() {
        mMenuActivity.showAlertDialog(mContext.getString(R.string.app_name), String.format(mContext.getString(R.string.delete_successfull), username), null, null, mContext.getString(R.string.ok_msg), null, new AlertDialogHandler() {
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
        mFragment.fetchDeviceList();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_device_list_item, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final DeviceEntity deviceEntity = mArlDevices.get(position);
        holder.tvDeviceName.setText(deviceEntity.getDeviceName());
        holder.tvMacAddress.setText(deviceEntity.getMacAddress());
        if (deviceEntity.getDeviceOS() != null && deviceEntity.getDeviceOS().equalsIgnoreCase("android")) {
            holder.ivOS.setImageResource(R.drawable.android);
        }
        else {
            holder.ivOS.setImageResource(R.drawable.apple);
        }

        if (deviceEntity.isEnabled()) {
            holder.tvDeviceName.setTextColor(ContextCompat.getColor(mContext, R.color.active_color));
        }
        else {
            holder.tvDeviceName.setTextColor(Color.RED);
        }
        holder.cvUserRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                username = deviceEntity.getDeviceName();
                if (deviceEntity.isEnabled()) {
                    deleteDevice(deviceEntity);
                }
                return true;
            }
        });

        holder.cvUserRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewDeviceActivity.class);
                intent.putExtra(Utility.DEVICE, deviceEntity);
                intent.putExtra(Utility.DEVICE_NAME, deviceEntity.getDeviceName());
                mMenuActivity.startActivity(intent);
            }
        });
    }

    private void deleteDevice(final DeviceEntity deviceEntity) {
        mMenuActivity.showAlertDialog(mContext.getString(R.string.app_name), String.format(mContext.getString(R.string.delete_device_confirmation), deviceEntity.getDeviceName()), null, null, mContext.getString(R.string.delete), mContext.getString(R.string.cancel_msg), new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                mMenuActivity.showProgressDialog(mContext.getString(R.string.progress_bar_message));
//                mMenuActivity.getPresenter().deleteObject(DeviceMapper.getParseObjectFromDeviceEntity(deviceEntity), DeviceListAdapter.this);

                /*After Deleting User,Disable the device in table*/
                deviceEntity.setIsEnabled(false);

                mMenuActivity.getPresenter().addObject(DeviceMapper.getParseObjectFromDeviceEntity(deviceEntity), DeviceListAdapter.this);

                DeviceEntity temp = DUCacheHelper.getDuCacheHelper().getSessionsEntity().getDeviceEntity();
                if (deviceEntity.getObjectId().equals(temp.getObjectId())) {
                    temp.setIsEnabled(false);
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }

            @Override
            public void onMultiChoiceClicked(int position, boolean isChecked) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mArlDevices.size();
    }

    public void animateTo(List<DeviceEntity> arlDevices) {
        applyAndAnimateRemovals(arlDevices);
        applyAndAnimateAdditions(arlDevices);
        applyAndAnimateMovedItems(arlDevices);
    }

    private void applyAndAnimateRemovals(List<DeviceEntity> newModels) {
        for (int i = mArlDevices.size() - 1; i >= 0; i--) {
            final DeviceEntity model = mArlDevices.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<DeviceEntity> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final DeviceEntity model = newModels.get(i);
            if (!mArlDevices.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<DeviceEntity> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final DeviceEntity model = newModels.get(toPosition);
            final int fromPosition = mArlDevices.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public DeviceEntity removeItem(int position) {
        final DeviceEntity model = mArlDevices.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, DeviceEntity model) {
        mArlDevices.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final DeviceEntity model = mArlDevices.remove(fromPosition);
        mArlDevices.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            deleteSuccess();
        }
        else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(mMenuActivity);
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDeviceName;
        protected TextView tvMacAddress;
        protected ImageView ivOS;
        protected CardView cvUserRow;
        protected RelativeLayout rlUserRow;

        public CardViewHolder(View view) {
            super(view);
            tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
            tvMacAddress = (TextView) view.findViewById(R.id.tv_mac_address);
            ivOS = (ImageView) view.findViewById(R.id.iv_os);
            cvUserRow = (CardView) view;
            rlUserRow = (RelativeLayout) view.findViewById(R.id.rl_user_row);
        }

    }
}
