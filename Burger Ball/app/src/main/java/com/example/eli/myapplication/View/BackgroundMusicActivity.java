package com.example.eli.myapplication.View;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.example.eli.myapplication.Logic.Ball.MusicPlayerServiceConnection;
import com.example.eli.myapplication.Logic.MediaPlayerService;
import com.example.eli.myapplication.Logic.OptionsController;

/**
 * Created by Eli on 6/25/2017.
 */

public class BackgroundMusicActivity extends AppCompatActivity {

    public final static String OPTIONS_STORAGE_LOCATION = "options.txt";

    MediaPlayerService mpService;
    int boundActivities = 0;


    @Override
    protected void onStart() {
        boolean muteMusic = shouldMuteMusic();
        bindMPservice(muteMusic);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindMP();
    }

    private void bindMPservice(boolean muteMusic) {
        // Bind to LocalService
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("muteMusic", muteMusic);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindMP() {
        // Unbind from the service
        if (boundActivities > 0) {
            unbindService(mConnection);
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mpService = binder.getService();
            boundActivities++;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundActivities--;

        }
    };

    public void stopMusic() {
        mpService.stopMusic();
    }

    public void restartMusic() {
        mpService.restartMusic();
    }

    private boolean shouldMuteMusic() {
        OptionsController optionsController = new OptionsController(this, OPTIONS_STORAGE_LOCATION);
        boolean shouldMuteMusic = optionsController.getOption("muteMusic");
        return shouldMuteMusic;
    }

}
