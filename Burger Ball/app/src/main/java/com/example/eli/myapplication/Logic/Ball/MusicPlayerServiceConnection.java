package com.example.eli.myapplication.Logic.Ball;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * shell class to appease the service connection interface
 * Created by Eli on 6/20/2017.
 */

public class MusicPlayerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {}
        @Override
        public void onServiceDisconnected(ComponentName name) {}
}
