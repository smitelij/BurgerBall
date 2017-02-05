package com.example.eli.myapplication.Resources;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;

/**
 * Created by Eli on 2/4/2017.
 */

public class CommonFunctions {

    //--------------------------
    //This function determines where balls are initialized when they are fired.
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    public static float[] getInitialBallCoords(){
        return createCircleCoords(GameState.FULL_WIDTH / 2, GameState.BORDER_WIDTH * 4, GameState.ballRadius);
    }

    public static PointF getFiringZoneCenter() {
        Ball newBall = new Ball(getInitialBallCoords(), new PointF(0f,0f), 10f, GameState.TEXTURE_BALL);
        return newBall.getCenter();
    }

    public static float[] getSelectionCircleCoords(){

        PointF adjustedResponseCenter = new PointF(GameState.mResponseCenter.x * GameState.xRatioAndroidToArena, (GameState.BORDER_WIDTH * 4));

        //Radius is determined with the width, so use xRatio
        float adjustedRadius = GameState.mResponseRadius * GameState.xRatioAndroidToArena;

        return createCircleCoords(adjustedResponseCenter.x, adjustedResponseCenter.y, adjustedRadius);
    }

    //Initialize as empty so we don't display anything until the user begins dragging
    public static float[] getVelocityArrowCoords(){
        return new float[]{
                0f,  1f, 0.0f,   // top left
                0f, 1f, 0.0f,   // bottom left
                0f, 1f, 0.0f,   // bottom right
                0f, 1f, 0.0f }; //top right
    }

    //--------------------------
    //Helper function to create coordinates for a ball
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    public static float[] createCircleCoords(float ballCenterX, float ballCenterY, float ballRadius){
        return new float[]{
                ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
                ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
                ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
                ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right
    }

    //---------------------------
    //Getter for the response range variable (controls the touch-sensitive firing radius of the user)
    public static float getResponseRange(){
        return GameState.mResponseRange;
    }

    //---------------------------
    //Getter for the response center variable (controls the center point of the user's firing circle)
    public static PointF getResponseCenter(){
        return GameState.mResponseCenter;
    }

    //---------------------------
    //This function calculates the initial velocity of a fired ball,
    //based on how far they have 'pulled back' the ball.
    //PARAMS:
    //  xChange- Pixel value representing how far the user has slid their finger within the
    //         - response radius, in the x direction.
    //  yChange- Pixel value representing how far the user has slid their finger within the
    //         - response radius, in the y direction.
    //RETURNS:
    //  A PointF the represents velocity.
    public static PointF calculateInitialVelocity(float xChange, float yChange){

        float angle = calculateFiringAngle(xChange,yChange);

        float percentVelocity = calculateFiringVelocity(xChange,yChange);

        float xVelocityPercent;
        float yVelocityPercent;

        yVelocityPercent = (float) Math.sin(angle) * percentVelocity;

        if (xChange >= 0){
            xVelocityPercent = (float) -(Math.cos(angle) * percentVelocity);
        } else {
            xVelocityPercent = (float) (Math.cos(angle) * percentVelocity);
        }

        //Calculate the velocity based on MAX_INITIAL_VELOCITY variables
        float initialXVelocity = xVelocityPercent * GameState.MAX_INITIAL_VELOCITY;
        float initialYVelocity = yVelocityPercent * GameState.MAX_INITIAL_VELOCITY;

        return new PointF(initialXVelocity,initialYVelocity);
    }

    public static float calculateFiringAngle(float xChange, float yChange){
        return (float) Math.atan(yChange / Math.abs(xChange));
    }

    public static float calculateFiringVelocity(float xChange, float yChange){

        //Calculate the percent the user has pulled back, based on the response radius
        float pullBackLength = (float) Math.sqrt((xChange * xChange) + (yChange * yChange));
        float percentVelocity = pullBackLength / GameState.mResponseRadius;

        if (percentVelocity > 1){
            percentVelocity = 1;
        }
        if (percentVelocity < 0){
            percentVelocity = 0.01f;
        }

        return percentVelocity;
    }

    //------------------------
    //Useful function to quickly print both components of a vector to the console
    public static void vectorPrint(PointF vector, String msg){
        System.out.println(msg + ": " + vector.x + ";" + vector.y);
    }

    //------------------------
    //Calculate the dot product of two vectors
    public static float dotProduct(PointF vector1, PointF vector2){
        return ((vector1.x * vector2.x) + (vector1.y * vector2.y));
    }

    public static float[] updateVelocityArrow(float angle, float height){

        float adjustedRadius = GameState.mResponseRadius * GameState.xRatioAndroidToArena;
        PointF adjustedResponseCenter = new PointF(GameState.mResponseCenter.x * GameState.xRatioAndroidToArena, (GameState.BORDER_WIDTH * 4));

        float[] baseCoords = { - 5, (adjustedRadius * height), 0.0f,           //top left
                                -5, GameState.ballRadius, 0.0f,   //bottom left
                                5, GameState.ballRadius, 0.0f,   //bottom right
                                5, (adjustedRadius * height), 0.0f          //top right
        };

        double cosAngle = Math.cos((double) angle);
        double sinAngle = Math.sin((double) angle);

        PointF baseTopLeft = new PointF(-5, (adjustedRadius*height));
        PointF baseBottomLeft = new PointF(-5, GameState.ballRadius);
        PointF baseBottomRight = new PointF(5, GameState.ballRadius);
        PointF baseTopRight = new PointF(5, (adjustedRadius*height));

        PointF rotatedTopLeft = new PointF( (float) ((baseTopLeft.x * cosAngle) - (baseTopLeft.y * sinAngle)), (float) ((baseTopLeft.y * cosAngle) + (baseTopLeft.x * sinAngle)));
        PointF rotatedBottomLeft = new PointF( (float) ((baseBottomLeft.x * cosAngle) - (baseBottomLeft.y * sinAngle)), (float) ((baseBottomLeft.y * cosAngle) + (baseBottomLeft.x * sinAngle)));
        PointF rotatedBottomRight = new PointF( (float) ((baseBottomRight.x * cosAngle) - (baseBottomRight.y * sinAngle)), (float) ((baseBottomRight.y * cosAngle) + (baseBottomRight.x * sinAngle)));
        PointF rotatedTopRight = new PointF( (float) ((baseTopRight.x * cosAngle) - (baseTopRight.y * sinAngle)), (float) ((baseTopRight.y * cosAngle) + (baseTopRight.x * sinAngle)));

        float[] finalCoords = { rotatedTopLeft.x + adjustedResponseCenter.x, rotatedTopLeft.y + adjustedResponseCenter.y, 0.0f,
                                rotatedBottomLeft.x + adjustedResponseCenter.x, rotatedBottomLeft.y + adjustedResponseCenter.y, 0.0f,
                                rotatedBottomRight.x + adjustedResponseCenter.x, rotatedBottomRight.y + adjustedResponseCenter.y, 0.0f,
                                rotatedTopRight.x + adjustedResponseCenter.x, rotatedTopRight.y + adjustedResponseCenter.y, 0.0f,
        };

        return finalCoords;

    }
}
