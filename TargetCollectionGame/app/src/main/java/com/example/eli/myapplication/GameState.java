package com.example.eli.myapplication;

import android.content.res.Resources;
import android.graphics.PointF;

/**
 * Created by Eli on 6/1/2016.
 */
public class GameState {

    static final float FRAME_SIZE = 1f;

    //num of collisions per frame size of 1 that will deactivate a ball.
    //For example, if frame size is 1, and a ball has 8 collisions in the span of 1 frame,
    //the ball will be deactivated. If frame size is 1/2, then the ball will need to have
    //4 collisions in the span of 1 frame.
    static final int DEACTIVATION_CONSTANT = 8;

    static final float FULL_WIDTH = 200.0f;
    static final float FULL_HEIGHT = 300.0f;
    static final float BORDER_WIDTH = 6.0f;

    static final float MAX_INITIAL_X_VELOCITY = 6f;
    static final float MAX_INITIAL_Y_VELOCITY = 6f;

    static final float[] backgroundColor = {0.05f, 0.05f, 0.05f, 1.0f};
    static final float[] borderColor = { 0.8f, 0.8f, 0.8f, 1.0f };
    static final float[] ballColor = {0.9f, 0.2f, 0.9f, 1.0f};

    static final float ballRadius = 8f;

    static final int OBSTACLE_POLYGON = 1000;
    static final int OBSTACLE_BALL = 1001;
    static final int OBSTACLE_TARGET = 1002;

    //currently only used for collision detection, move into that class if never used again
    static final float LARGE_NUMBER = 99999f;
    static final float SMALL_NUMBER = -99999f;

    private static final int mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private static final int mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    private static float mResponseRadius = mWidth * 0.4f;
    private static float mResponseRange = (float) Math.sqrt((mResponseRadius)*(mResponseRadius) + (mResponseRadius)*(mResponseRadius));;
    private static PointF mResponseCenter = new PointF(mWidth / 2, mHeight);

    static final PointF GRAVITY_CONSTANT = new PointF(0f,-0.1f);
    static final float ELASTIC_CONSTANT = 0.9f;

    static final int TEXTURE_WALL = R.drawable.brick;
    static final int TEXTURE_BALL = R.drawable.circle;
    static final int TEXTURE_TARGET = R.drawable.burger2;


    public static float[] getInitialBallCoords(){
        return createCircleCoords(GameState.FULL_WIDTH / 2, GameState.ballRadius * 4, GameState.ballRadius);
    }

    public static float[] createCircleCoords(float ballCenterX, float ballCenterY, float ballRadius){
        return new float[]{
                ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
                ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
                ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
                ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right
    }


    public static float getResponseRange(){
        return mResponseRange;
    }

    public static PointF getResponseCenter(){
        return mResponseCenter;
    }

    public static PointF calculateInitialVelocity(float xChange, float yChange){
        xChange = -xChange;  //flip so it goes in the correct x direction

        float xPercent = (xChange / mResponseRadius);
        float yPercent = (yChange / mResponseRadius);

        if (xPercent > 1){
            xPercent = 1;
        } else if (xPercent < -1){
            xPercent = -1;
        }

        if(yPercent > 1){
            yPercent = 1;
        } else if (yPercent < 0.1){
            yPercent = 0.1f;
        }

        float initialXVelocity = xPercent * GameState.MAX_INITIAL_X_VELOCITY;
        float initialYVelocity = yPercent * GameState.MAX_INITIAL_Y_VELOCITY;

        return new PointF(initialXVelocity,initialYVelocity);
    }






}

