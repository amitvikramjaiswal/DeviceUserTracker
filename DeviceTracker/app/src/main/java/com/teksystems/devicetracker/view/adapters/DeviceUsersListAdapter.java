package com.teksystems.devicetracker.view.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.DeviceEntity;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.fragments.DeviceUsersFragment;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajaiswal on 10/15/2015.
 */
public class DeviceUsersListAdapter extends RecyclerView.Adapter<DeviceUsersListAdapter.CardViewHolder> {

    private final List<DeviceEntity> mArlDevices;
    private Context mContext;
    private MenuActivity mMenuActivity;
    private DeviceUsersFragment mFragment;

    public DeviceUsersListAdapter(Context mContext, MenuActivity mMenuActivity, DeviceUsersFragment mFragment, List<DeviceEntity> mArlDevices) {
        this.mContext = mContext;
        this.mMenuActivity = mMenuActivity;
        this.mFragment = mFragment;
        this.mArlDevices = new ArrayList<>(mArlDevices);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_device_user_list_item, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        DeviceEntity deviceEntity = mArlDevices.get(position);

        holder.tvDeviceName.setText(deviceEntity.getDeviceName());

        if (deviceEntity.getUserEntity() != null)
            holder.tvUserName.setText(deviceEntity.getUserEntity().getUsername());
        else
            holder.tvUserName.setText("");

        if (deviceEntity.getSessionsEntity() != null)
            holder.tvLastAccessed.setText(Utils.getLocalizedLastAccessTime(deviceEntity.getSessionsEntity()));
        else
            holder.tvLastAccessed.setText("");

        String deviceVersion = deviceEntity.getDeviceVersion() != null ? deviceEntity.getDeviceOS() + " : " + deviceEntity.getDeviceVersion() : deviceEntity.getDeviceOS();
        holder.tvDeviceVersion.setText(deviceVersion);

        if (deviceEntity.getSessionsEntity() == null && deviceEntity.getUserEntity() == null) {
            holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        } else if (deviceEntity.getUserEntity() != null && deviceEntity.getUserEntity().isAdmin()) {
            holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.with_admin));
        } else if (deviceEntity.getUserEntity() != null && !deviceEntity.getUserEntity().isAdmin()) {
            if (deviceEntity.getSessionsEntity() == null) {
                holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
            } else if (deviceEntity.getSessionsEntity() != null && deviceEntity.getSessionsEntity().getLogoutTime() == null) {
                holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.logged_in));
            } else {
                holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.logged_out));
            }
        } else {
            holder.viewColorIndicator.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        }
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

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvDeviceName;
        protected TextView tvLastAccessed;
        protected TextView tvUserName;
        protected TextView tvDeviceVersion;
        protected View viewColorIndicator;
        protected CardView cvUserRow;

        public CardViewHolder(View view) {
            super(view);
            tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
            tvLastAccessed = (TextView) view.findViewById(R.id.tv_last_accessed);
            tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
            tvDeviceVersion = (TextView) view.findViewById(R.id.tv_device_version);
            viewColorIndicator = view.findViewById(R.id.view_color_indicator);
            cvUserRow = (CardView) view;
        }

    }
}
