package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 10/1/2016.
 */
public interface Movable {

    void moveByFrame(float percentOfFrame);

    void resetAABB();

    void updatePrevAABB();

    PointF getVelocity();

    PointF getVelocity(float timeStep);
}
