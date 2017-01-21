package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 1/21/2017.
 */

public class BallStuckException extends Exception {

    private PointF collisionAxis;

    public BallStuckException(PointF collisionAxis) {
        this.collisionAxis = collisionAxis;
    }

    public PointF getCollisionAxis() {
        return collisionAxis;
    }
}
