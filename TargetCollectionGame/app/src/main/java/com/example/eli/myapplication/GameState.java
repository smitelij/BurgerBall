package com.example.eli.myapplication;

import android.content.res.Resources;
import android.graphics.PointF;

/**
 * Created by Eli on 6/1/2016.
 */

//-------------------------------
// GameState class
//
// This class controls important game-wide variables, including mechanics, sizing, and frame rates.
// Also contains static functions that may be used by multiple other classes.
//-------------------------------
public class GameState {

    //========================
    //Game mechanics and feel
    //========================

    //How far objects will advance per frame. Should be set lower when FPS is higher, and higher when FPS is lower.
    static final float FRAME_SIZE = 0.5f;
    //this is a strange flag that will change the frame size automatically, based on how fast the game is running.
    // Seems to make it pretty choppy, but we'll keep it as an option for now.
    static final boolean VARIABLE_FRAME_SIZE = false;

    //This setting will force the frame rate to be capped at a certain rate. If this cap is set low enough,
    //it will make the frame rate more consistent.
    static final boolean FRAME_RATE_CAP = false;
    //Maximum 'frames' (calls to onDrawFrame in MyGLRenderer) that are allowed per second.
    //Does nothing if FRAME_RATE_CAP is set to false
    static final long FRAME_RATE_CAP_SIZE = 36;
    //This setting will determine an automatic frame rate cap based on past performance.
    //It will do nothing if FRAME_RATE_CAP is set to false.
    static final boolean AUTO_CAP_FRAME_RATE_SIZE = false;

    //num of collisions per frame size of 1 that will deactivate a ball.
    //For example, if frame size is 1, and a ball has 8 collisions in the span of 1 frame,
    //the ball will be deactivated. If frame size is 1/2, then the ball will need to have
    //4 collisions in the span of 1 frame.
    static final int DEACTIVATION_CONSTANT = 10;

    //Max possible firing velocity for balls in the X and Y components.
    static final float MAX_INITIAL_X_VELOCITY = 8f;
    static final float MAX_INITIAL_Y_VELOCITY = 8f;
    static final float MAX_INITIAL_VELOCITY = 10f;

    //The gravitational pull on the ball, specified in both X and Y components.
    //May eventually move to a level-controlled variable to allow changes.
    static final PointF GRAVITY_CONSTANT = new PointF(0f,-0.18f);
    //How velocity is affected after a collision (setting at 0.9 means 10% of energy is lost in a collision).
    static final float ELASTIC_CONSTANT = 0.9f;

    //Currently controls whether FPS stats are printed to console.
    //Eventually should be used to display an FPS rate within the game.
    static final boolean showFPS = true;

    //=====================
    //Dimensions and sizes
    //=====================

    //size of arena
    static final float FULL_WIDTH = 200.0f;
    static final float FULL_HEIGHT = 300.0f;
    //width of border
    static final float BORDER_WIDTH = 6.0f;

    /*
    static final float[] borderColor = { 0.8f, 0.8f, 0.8f, 1.0f };
    static final float[] ballColor = {0.9f, 0.2f, 0.9f, 1.0f};*/

    //Size of the ball. May eventually move it to a level-controlled variable to allow more flexibility
    static final float ballRadius = 10f;

    //Get dimensions of screen
    private static final int mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private static final int mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    //get ratio for android pixels to arena pixels
    private static float xRatioAndroidToArena = FULL_WIDTH / mWidth;
    private static float yRatioAndroidToArena = FULL_HEIGHT / mHeight;

    //The response radius is the radius of the circle that the player can fire the ball from.
    private static float mResponseRadius = mWidth * 0.4f;
    private static float mResponseRange = (float) Math.sqrt((mResponseRadius)*(mResponseRadius) + (mResponseRadius)*(mResponseRadius));
    //The center of the response radius circle
    private static PointF mResponseCenter = new PointF(mWidth / 2, mHeight - ((BORDER_WIDTH * 4) / yRatioAndroidToArena));



    //============
    // Constants
    //============

    //Reference ints for object types
    static final int OBSTACLE_POLYGON = 1000;
    static final int OBSTACLE_BALL = 1001;
    static final int OBSTACLE_TARGET = 1002;

    //Used as initializers when searching for larger or smaller numbers
    static final float LARGE_NUMBER = 99999f;
    static final float SMALL_NUMBER = -99999f;

    //Location of texture files
    static final int TEXTURE_WALL = R.drawable.brick;
    static final int TEXTURE_BALL = R.drawable.circle;
    static final int TEXTURE_TARGET = R.drawable.burger2;
    static final int TEXTURE_SELECTION_CIRCLE = R.drawable.selectioncircle;

    //texture number renderings
    static final int TEXTURE_DIGIT_0 = R.drawable.digit0;
    static final int TEXTURE_DIGIT_1 = R.drawable.digit1;
    static final int TEXTURE_DIGIT_2 = R.drawable.digit2;
    static final int TEXTURE_DIGIT_3 = R.drawable.digit3;
    static final int TEXTURE_DIGIT_4 = R.drawable.digit4;
    static final int TEXTURE_DIGIT_5 = R.drawable.digit5;
    static final int TEXTURE_DIGIT_6 = R.drawable.digit6;
    static final int TEXTURE_DIGIT_7 = R.drawable.digit7;
    static final int TEXTURE_DIGIT_8 = R.drawable.digit8;
    static final int TEXTURE_DIGIT_9 = R.drawable.digit9;


    //--------------------------
    //This function determines where balls are initialized when they are fired.
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    public static float[] getInitialBallCoords(){
        return createCircleCoords(GameState.FULL_WIDTH / 2, GameState.BORDER_WIDTH * 4, GameState.ballRadius);
    }

    public static float[] getSelectionCircleCoords(){

        PointF adjustedResponseCenter = new PointF(mResponseCenter.x * xRatioAndroidToArena, (GameState.BORDER_WIDTH * 4));

        //Radius is determined with the width, so use xRatio
        float adjustedRadius = mResponseRadius * xRatioAndroidToArena;

        return createCircleCoords(adjustedResponseCenter.x, adjustedResponseCenter.y, adjustedRadius);
    }

    //--------------------------
    //Helper function to create coordinates for a ball
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    protected static float[] createCircleCoords(float ballCenterX, float ballCenterY, float ballRadius){
        return new float[]{
                ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
                ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
                ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
                ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right
    }

    //---------------------------
    //Getter for the response range variable (controls the touch-sensitive firing radius of the user)
    public static float getResponseRange(){
        return mResponseRange;
    }

    //---------------------------
    //Getter for the response center variable (controls the center point of the user's firing circle)
    public static PointF getResponseCenter(){
        return mResponseCenter;
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

        float angle = (float) Math.atan(yChange / Math.abs(xChange));

        //Calculate the percent the user has pulled back, based on the response radius
        float pullBackLength = (float) Math.sqrt((xChange * xChange) + (yChange * yChange));
        float percentVelocity = pullBackLength / mResponseRadius;

        if (percentVelocity > 1){
            percentVelocity = 1;
        }
        if (percentVelocity < 0){
            percentVelocity = 0.01f;
        }

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


}

