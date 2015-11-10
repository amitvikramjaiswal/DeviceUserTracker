package com.teksystems.devicetracker.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.entities.UserEntity;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.data.mappers.UserMapper;
import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.fragments.UsersFragment;
import com.teksystems.devicetracker.view.views.activities.LoginActivity;
import com.teksystems.devicetracker.view.views.activities.MenuActivity;
import com.teksystems.devicetracker.view.views.activities.NewUserActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ajaiswal on 9/24/2015.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.CardViewHolder> implements DeleteCallback, SaveCallback {

    private final List<UserEntity> mArlUsers;
    private Context mContext;
    private MenuActivity mMenuActivity;
    private UsersFragment mFragment;
    private String username;
    private boolean isFromUserTable;
    private boolean isLoggedInUser;

    public UsersListAdapter(Context context, MenuActivity menuActivity, Fragment fragment, List<UserEntity> arlUsers) {
        this.mContext = context;
        this.mMenuActivity = menuActivity;
        this.mFragment = (UsersFragment) fragment;
        this.mArlUsers = new ArrayList<>(arlUsers);
    }

    private void deleteSuccess() {
        mMenuActivity.hideProgressDialog();
        mMenuActivity.showAlertDialog(mContext.getString(R.string.app_name), String.format(mContext.getString(R.string.delete_successfull), username), null, null, mContext.getString(R.string.ok_msg), null, new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                if (isLoggedInUser) {
                    logoutFromTheApp();
                }
                else {
                    mFragment.fetchUsersList();
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

    private void logoutFromTheApp() {
        mMenuActivity.showProgressDialog("");
        SessionsEntity sessionsEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity();
        if (sessionsEntity == null) {
            return;
        }
        sessionsEntity.setLogoutTime(new Date());
        sessionsEntity.setLoggedIn(false);
        mMenuActivity.getPresenter().addObject(SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mMenuActivity.hideProgressDialog();
                if (e == null) {
                    Utils.clearUserData();
                    Intent intent = new Intent(mMenuActivity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mMenuActivity.startActivity(intent);
                }
                else if (e.getCode() == Utility.NO_NETWORK) {
                    Utils.showNetworkError(mMenuActivity);
                }
            }
        });
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_simple_list_item, viewGroup, false);

        return new CardViewHolder(mContext, itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final UserEntity userEntity = mArlUsers.get(position);

        holder.tvUsername.setText(userEntity.getUsername());
        if (userEntity.isEnabled()) {
            holder.tvUsername.setTextColor(ContextCompat.getColor(mContext, R.color.active_color));
            holder.cvUserRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    username = userEntity.getUsername();
                    deleteUser(userEntity);
                    return true;
                }
            });
        }
        else {
            holder.tvUsername.setTextColor(Color.RED);
        }
        holder.cvUserRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewUserActivity.class);
                intent.putExtra(Utility.USER_TABLE, userEntity);
                intent.putExtra(Utility.USERNAME, userEntity.getUsername());
                mMenuActivity.startActivity(intent);
            }
        });
    }

    private void deleteUser(final UserEntity userEntity) {
        UserEntity tempUserEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity();
        String msg = null;
        if (tempUserEntity.getUsername().equalsIgnoreCase(username)) {
            isLoggedInUser = true;
            msg = mContext.getString(R.string.delete_loggedin_user_confirmation);
        }
        else {
            isFromUserTable = false;
            msg = mContext.getString(R.string.delete_user_confirmation);
        }

        mMenuActivity.showAlertDialog(mContext.getString(R.string.app_name), String.format(msg, userEntity.getUsername()), null, null, mContext.getString(R.string.delete), mContext.getString(R.string.cancel_msg), new AlertDialogHandler() {
            @Override
            public void onPositiveButtonClicked() {
                isFromUserTable = true;
                mMenuActivity.showProgressDialog(mContext.getString(R.string.progress_bar_message));

//                Uncomment for hard delete
//                mMenuActivity.getPresenter().deleteObject(UserMapper.getParseObjectFromUserEntity(userEntity), UsersListAdapter.this);

                UserEntity tempUserEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity().getUserEntity();
                if (tempUserEntity.getObjectId().equals(userEntity.getObjectId())) {
                    tempUserEntity.setIsEnabled(false);
                }

                userEntity.setIsEnabled(false);
//                soft delete
                mMenuActivity.getPresenter().addObject(UserMapper.getParseObjectFromUserEntity(userEntity), UsersListAdapter.this);
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
        return mArlUsers.size();
    }

    public void animateTo(List<UserEntity> arlUsers) {
        applyAndAnimateRemovals(arlUsers);
        applyAndAnimateAdditions(arlUsers);
        applyAndAnimateMovedItems(arlUsers);
    }

    private void applyAndAnimateRemovals(List<UserEntity> newModels) {
        for (int i = mArlUsers.size() - 1; i >= 0; i--) {
            final UserEntity model = mArlUsers.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<UserEntity> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final UserEntity model = newModels.get(i);
            if (!mArlUsers.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<UserEntity> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final UserEntity model = newModels.get(toPosition);
            final int fromPosition = mArlUsers.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public UserEntity removeItem(int position) {
        final UserEntity model = mArlUsers.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, UserEntity model) {
        mArlUsers.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final UserEntity model = mArlUsers.remove(fromPosition);
        mArlUsers.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void done(ParseException e) {
        if (e == null) {
            if (isFromUserTable) {
                deleteSuccess();
            }
        }
        else if (e.getCode() == Utility.NO_NETWORK) {
            Utils.showNetworkError(mMenuActivity);
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvUsername;
        protected CardView cvUserRow;
        protected RelativeLayout rlUserRow;
        protected Context mContext;

        public CardViewHolder(Context context, View view) {
            super(view);
            mContext = context;
            tvUsername = (TextView) view.findViewById(R.id.tv_username);
            cvUserRow = (CardView) view;
            rlUserRow = (RelativeLayout) view.findViewById(R.id.rl_user_row);
        }

    }

}
