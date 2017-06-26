package com.example.eli.myapplication.Logic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.eli.myapplication.R;

/**
 * Created by Eli on 6/17/2017.
 */

public class MediaPlayerService extends Service {

    private final IBinder binder = new LocalBinder();

    boolean started = false;
    MediaPlayer player;

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("destroy music service.");
        started = false;
        player.release();
    }


    @Override
    public IBinder onBind(Intent intent) {
        if (! started) {
            System.out.println("create music service");
            player = MediaPlayer.create(getBaseContext(), R.raw.fbc1final);
            player.setLooping(true);
            player.start();
            started = true;
        }

        System.out.println("on start command - music service");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        System.out.println("UNBIND!");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        restartMusic();
    }

    public void restartMusic() {
        player = MediaPlayer.create(getBaseContext(), R.raw.fbc1final);
        player.setLooping(true);
        player.start();
        started = true;
    }

}
