package com.example.eli.myapplication.Logic;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.eli.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Eli on 6/11/2017.
 */

public class SoundEngine {

    boolean active = false;
    boolean muteSound = false;

    private Context context;
    public enum SoundType {BALL_FIRE, BALL_WALL_COLLIDE, BALL_BALL_COLLIDE}

    private ArrayList<MediaPlayer> ballFirePlayers = new ArrayList<>();
    private ArrayList<MediaPlayer> ballWallCollidePlayers = new ArrayList<>();
    private ArrayList<MediaPlayer> ballBallCollidePlayers = new ArrayList<>();

    private SoundPool soundPool;

    private int ballPullHandle;
    private int ballFireHandle;
    private int ballBallCollideHandle;
    private int ballWallCollideHandle;

    private MediaPlayer musicPlayer;

    @TargetApi(21)
    public SoundEngine(Context context, boolean muteSound) {
        this.context = context;

        SoundPool.Builder builder = new SoundPool.Builder();
        soundPool = builder.build();

        ballPullHandle = soundPool.load(context, R.raw.ballpullbackfinal, 1);
        ballFireHandle = soundPool.load(context, R.raw.ballfirefinal, 1);
        ballBallCollideHandle = soundPool.load(context, R.raw.ballballcollidefinal, 1);
        ballWallCollideHandle = soundPool.load(context, R.raw.ballwallcollidefinal, 1);
        musicPlayer = MediaPlayer.create(context,R.raw.fbcfull);
        active = true;
        this.muteSound = muteSound;
    }

    public void playBallFire(float volume, float freq) {
        if (muteSound) {
            return;
        }
        soundPool.play(ballFireHandle,volume, volume, 1, 0, freq);
    }

    public void playBallBallCollide(float volume, float freq) {
        if (muteSound) {
            return;
        }
        soundPool.play(ballBallCollideHandle,volume, volume, 1, 0, freq);
    }

    public void playBallWallCollide(float volume, float freq) {
        if (muteSound) {
            return;
        }
        soundPool.play(ballWallCollideHandle,volume,volume,1,0,freq);
    }

    public void playBallPullBack(float volume, float freq) {
        if (muteSound) {
            return;
        }
        soundPool.play(ballPullHandle, volume, volume, 1,0,freq);
    }

    public void start() {
        //musicPlayer.start();
    }

    public void release() {
        if (active) {
            soundPool.release();
            //musicPlayer.release();
            active = false;
        }
    }

}
