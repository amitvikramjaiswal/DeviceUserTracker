package com.teksystems.devicetracker.view.views.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.inject.Inject;
import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

public class RoboAppCompatActivity extends AppCompatActivity implements RoboContext {
    protected EventManager eventManager;
    protected HashMap<Key<?>, Object> scopedObjects = new HashMap();
    @Inject
    ContentViewListener ignored;

    public RoboAppCompatActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        RoboInjector injector = RoboGuice.getInjector(this);
        this.eventManager = (EventManager) injector.getInstance(EventManager.class);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        this.eventManager.fire(new OnCreateEvent(savedInstanceState));
    }

    protected void onRestart() {
        super.onRestart();
        this.eventManager.fire(new OnRestartEvent());
    }

    protected void onStart() {
        super.onStart();
        this.eventManager.fire(new OnStartEvent());
    }

    protected void onResume() {
        super.onResume();
        this.eventManager.fire(new OnResumeEvent());
    }

    protected void onPause() {
        super.onPause();
        this.eventManager.fire(new OnPauseEvent());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.eventManager.fire(new OnNewIntentEvent());
    }

    protected void onStop() {
        try {
            this.eventManager.fire(new OnStopEvent());
        } finally {
            super.onStop();
        }

    }

    protected void onDestroy() {
        try {
            this.eventManager.fire(new OnDestroyEvent());
        } finally {
            try {
                RoboGuice.destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
        Configuration currentConfig = this.getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        this.eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    public void onContentChanged() {
        super.onContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
        this.eventManager.fire(new OnContentChangedEvent());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
    }

    public Map<Key<?>, Object> getScopedObjectMap() {
        return this.scopedObjects;
    }
}
