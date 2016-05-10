package com.example.eli.myapplication;

import android.graphics.PointF;

/**
 * Created by Eli on 5/2/2016.
 */
public class CollisionHistory{
    private float mTime; //percent time into the frame that collision occurred
    private PointF mBoundaryAxis; //normalized boundary axis of the obstacle where collision occurred
    private Interactable mObstacle; //the obstacle that was struck
    private Ball mBall;

    public CollisionHistory(float time, PointF boundaryAxis, Interactable obstacle, Ball ball){
        mTime = time;
        mBoundaryAxis = boundaryAxis;
        mObstacle = obstacle;
        mBall = ball;
    }

    public float getTime(){
        return mTime;
    }

    public Ball getBall() { return mBall; }

    public PointF getBoundaryAxis() { return mBoundaryAxis;}

    public Interactable getObstacle(){
        return mObstacle;
    }
}
