package com.example.eli.myapplication;

import android.graphics.PointF;

/**
 * Created by Eli on 3/11/2016.
 */
public class GameState {

    //started at -1 because always incremented before it is returned. the first value will be 0.
    static int currentBallID = -1;
    static final float FULL_WIDTH = 200.0f;
    static final float FULL_HEIGHT = 300.0f;
    static final float BORDER_WIDTH = 6.0f;

    static final float[] backgroundColor = {0.05f, 0.05f, 0.05f, 1.0f};
    static final float[] borderColor = { 0.8f, 0.8f, 0.8f, 1.0f };
    static final float[] ballColor = {0.9f, 0.2f, 0.9f, 1.0f};

    static final float ARENA_HEIGHT = FULL_HEIGHT * 0.8f;

    static final float ballCenterX = 169.5f;
    static final float ballCenterY = 270f;
    static final float ballRadius = 8f;

    static int currentBalls = 3;

    static final float[] initialBallCoords = {
            ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
            ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
            ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
            ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right



    static final PointF initialBallVelocity = new PointF(3.0f, 3.0f);

    static final int OBSTACLE_POLYGON = 1000;
    static final int OBSTACLE_BALL = 1001;

    //currently only used for collision detection, move into that class if never used again
    static final float LARGE_NUMBER = 99999f;
    static final float SMALL_NUMBER = -99999f;

    public static float[] getBorderCoords(float ballCenterX, float ballCenterY, float ballRadius){
        return new float[]{
                ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
                ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
                ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
                ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right
    }

    public static int getNextBallID(){
        currentBallID++;
        return currentBallID;
    }

}
