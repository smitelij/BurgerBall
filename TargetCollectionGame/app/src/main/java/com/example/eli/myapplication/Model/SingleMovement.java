package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 9/10/2016.
 */
public class SingleMovement {
    PointF velocity;
    int duration;

    public SingleMovement(PointF velocity, int duration){
        this.velocity = velocity;
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    public PointF getVelocity(){
        return velocity;
    }
}
