package com.teksystems.devicetracker.component;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.teksystems.devicetracker.R;
import com.teksystems.devicetracker.application.DeviceTrackerApplication;
import com.teksystems.devicetracker.data.entities.SessionsEntity;
import com.teksystems.devicetracker.data.mappers.SessionsMapper;
import com.teksystems.devicetracker.service.impl.ServiceBaseImpl;
import com.teksystems.devicetracker.util.DUCacheHelper;
import com.teksystems.devicetracker.util.Utility;
import com.teksystems.devicetracker.util.Utils;
import com.teksystems.devicetracker.view.views.activities.LoginActivity;
import com.teksystems.devicetracker.view.views.activities.SplashActivity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ajaiswal on 10/26/2015.
 */
public class LoggedInCheckService extends Service {

    public static long scheduledTime;
    private static Timer timer1;
    private static Timer timer2;
    private static AlertDialog alertDialog;

    public LoggedInCheckService() {

    }

    public static void removeCallbacks() {
        timer2.cancel();
    }

    public static void startSessionTimer(long newTimeout) {
        long sessionTimeout = newTimeout == 0 ? DeviceTrackerApplication.getContext().getSharedPreferences(Utility.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt(Utility.SESSION_TIMEOUT, 1) * 60 * 60 * 1000 : newTimeout;
        final long currentTime = System.currentTimeMillis();
        scheduledTime = currentTime + sessionTimeout;

        if (timer1 != null) {
            timer1.cancel();
        }

        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        LoggedInCheckService.scheduledTime = 0;
                        alertDialog.show();
                    }
                };
                handler.obtainMessage().sendToTarget();
            }
        }, sessionTimeout);
    }

    public static void stopSessionTimer() {
        timer1.cancel();
    }

    public void sessionTimeout() {
//        Toast.makeText(this, "SESSION_TIMEOUT scheduled", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeviceTrackerApplication.getContext());
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder
                .setMessage(R.string.session_expired)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        startSessionTimer(0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "STARTING", Toast.LENGTH_SHORT).show();
        int result = super.onStartCommand(intent, flags, startId);

        if (intent == null) {
            recurringAlert();
        } else if (!intent.hasExtra(Utility.ALERT_TYPE)) {
            stopSelf();
        } else {
            switch (intent.getExtras().getInt(Utility.ALERT_TYPE)) {
                case Utility.RECURRING_ALERT:
                    recurringAlert();
                    break;
                case Utility.SESSION_TIMEOUT_ALERT:
                    sessionTimeout();
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    protected void recurringAlert() {
//        Toast.makeText(this, "RECURRING_ALERT scheduled", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeviceTrackerApplication.getContext());
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder
                .setMessage(R.string.user_not_logged_in)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!Utils.isAppInForeground(LoggedInCheckService.this)) {
                            Intent intent = new Intent(LoggedInCheckService.this, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        if (timer2 != null) {
            timer2.cancel();
        }

        timer2 = new Timer();
        long alertInterval = getSharedPreferences(Utility.SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt(Utility.ALERT_INTERVAL, 2)  * 60 * 1000;
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        alertDialog.show();
                    }
                };
                Message message = handler.obtainMessage();
                message.sendToTarget();
            }
        }, alertInterval , alertInterval);

    }

    private void logout() {
        Intent intent = null;
        if (Utils.isAppInForeground(LoggedInCheckService.this)) {
            intent = new Intent(LoggedInCheckService.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            intent = new Intent(LoggedInCheckService.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        SessionsEntity sessionsEntity = DUCacheHelper.getDuCacheHelper().getSessionsEntity();
        if (sessionsEntity == null) {
            return;
        }
        sessionsEntity.setDeviceEntity(null);
        sessionsEntity.setLoginTime(null);
        sessionsEntity.setCreatedAt(null);
        sessionsEntity.setUserEntity(null);
        sessionsEntity.setUpdatedAt(null);
        sessionsEntity.setLogoutTime(new Date());
        sessionsEntity.setLoggedIn(false);
        new ServiceBaseImpl().addObject(SessionsMapper.getParseObjectFromSessionsEntity(sessionsEntity), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Utils.clearUserData();
                } else if (e.getCode() == Utility.NO_NETWORK) {
                    showAlert(getString(R.string.network_unavailable));
                } else if (e.getCode() == Utility.TIME_OUT) {
                    showAlert(getString(R.string.no_network));
                }
            }
        });
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeviceTrackerApplication.getContext());
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "STOPPED", Toast.LENGTH_SHORT).show();
    }
}
