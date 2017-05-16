package com.example.eli.myapplication.Resources;

import android.content.res.Resources;
import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.R;

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
    public static final float FRAME_SIZE = 0.5f;
    //this is a strange flag that will change the frame size automatically, based on how fast the game is running.
    // Seems to make it pretty choppy, but we'll keep it as an option for now.
    public static final boolean VARIABLE_FRAME_SIZE = false;

    //This setting will force the frame rate to be capped at a certain rate. If this cap is set low enough,
    //it will make the frame rate more consistent.
    public static final boolean FRAME_RATE_CAP = false;
    //Maximum 'frames' (calls to onDrawFrame in MyGLRenderer) that are allowed per second.
    //Does nothing if FRAME_RATE_CAP is set to false
    public static final long FRAME_RATE_CAP_SIZE = 36;
    //This setting will determine an automatic frame rate cap based on past performance.
    //It will do nothing if FRAME_RATE_CAP is set to false.
    public static final boolean AUTO_CAP_FRAME_RATE_SIZE = false;

    //num of collisions with an identical surface per frame size of 1 that will activate slowed ball handling.
    //If the ball is currently on a flat, non-moving obstacle, it will be deactivated.
    // If the surface isn't flat or the obstacle is moving, 'rolling' mode will be activated.
    public static final int SLOWED_BALL_CONSTANT = 20;
    //num of collisions per frame size that will 'bounce' a ball stuck on another ball.
    public static final int BALL_BOUNCE_CONSTANT = 16;
    //num of collisions with any obstacle surface within 1 frame that will 'bounce' a stuck ball.
    // Practically speaking, this should only happen when a ball gets stuck on a point of an obstacle.
    // Any other consecutive collisions should activate SLOWED_BALL_CONSTANT first. Points are different
    // because they never have the exact same collision boundary axis.
    public static final int STUCK_POINT_CONSTANT = 40;
    //num of collisions in 1 frame with any obstacle surface that will deactivate a stuck ball.
    public static final int DEACTIVATE_STUCK_BALL_CONSTANT = 70;

    //Speed at which a rolling ball will be deactivated.
    public static final float DEACTIVATE_BALL_VELOCITY = 0.02f;

    //Max possible firing velocity for balls in the X and Y components.
    public static final float MAX_INITIAL_VELOCITY = 10f;

    //The gravitational pull on the ball, specified in both X and Y components.
    //May eventually move to a level-controlled variable to allow changes.
    public static final PointF GRAVITY_CONSTANT = new PointF(0f,-0.18f);
    //Velocity loss per frame when ball is rolling on a flat surface
    public static final float ROLLING_DECELERATION_CONSTANT = 0.06f;
    public static final PointF PARTICLE_GRAVITY_CONSTANT = new PointF(GRAVITY_CONSTANT.x * 0.25f, GRAVITY_CONSTANT.y * 0.25f);
    //How velocity is affected after a collision (setting at 0.9 means 10% of energy is lost in a collision).
    public static final float ELASTIC_CONSTANT = 0.9f;
    //Number of times per frame elastic loss may be applied (preventing ridiculous losses when ball starts rolling)
    public static final int MAX_ELASTIC_COLLISIONS_PER_FRAME = 2;

    //Currently controls whether FPS stats are printed to console.
    //Eventually should be used to display an FPS rate within the game.
    public static final boolean showFPS = true;

    //=====================
    //Dimensions and sizes
    //=====================

    //size of arena
    public static final float FULL_WIDTH = 200.0f;
    public static final float FULL_HEIGHT = 300.0f;
    //width of border
    public static final float BORDER_WIDTH = 6.0f;

    /*
    static final float[] borderColor = { 0.8f, 0.8f, 0.8f, 1.0f };
    static final float[] ballColor = {0.9f, 0.2f, 0.9f, 1.0f};*/

    //Size of the ball. May eventually move it to a level-controlled variable to allow more flexibility
    public static final float ballRadius = 10f;
    public static final float GHOST_ALPHA = 0.3f;

    //Get dimensions of screen
    public static final int mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    public static final int mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    //get ratio for android pixels to arena pixels
    public static float xRatioAndroidToArena = FULL_WIDTH / mWidth;
    public static float yRatioAndroidToArena = FULL_HEIGHT / mHeight;

    //The response radius is the radius of the circle that the player can fire the ball from.
    public static float mResponseRadius = mWidth * 0.4f;
    public static float mResponseRange = (float) Math.sqrt((mResponseRadius)*(mResponseRadius) + (mResponseRadius)*(mResponseRadius));



    //============
    // Constants
    //============

    //Reference ints for object types
    public static final int INTERACTABLE_OBSTACLE = 1000;
    public static final int INTERACTABLE_BALL = 1001;
    public static final int INTERACTABLE_TARGET = 1002;
    public static final int INTERACTABLE_MOVING_OBSTACLE = 1003;
    public static final int DRAWABLE_SCOREDIGIT = 2000;
    public static final int DRAWABLE_GHOST_CIRCLES = 2001;
    public static final int DRAWABLE_VELOCITY_ARROW = 2002;
    public static final int DRAWABLE_CIRCLE = 2003;
    public static final int DRAWABLE_BALLS_REMAINING = 2004;
    public static final int DRAWABLE_POST_LEVEL_IMAGE = 2005;

    //Used as initializers when searching for larger or smaller numbers
    public static final float LARGE_NUMBER = 99999f;
    public static final float SMALL_NUMBER = -99999f;

    //Location of texture files
    public static final int TEXTURE_WALL = R.drawable.largebrick;
    public static final int TEXTURE_WALL2 = R.drawable.wall3small;
    public static final int TEXTURE_WALL3 = R.drawable.electric;

    public static final int TEXTURE_BALL1 = R.drawable.newball3;
    public static final int TEXTURE_BALL2 = R.drawable.glareball;

    public static final int TEXTURE_TARGET = R.drawable.burger2;
    public static final int TEXTURE_SELECTION_CIRCLE = R.drawable.selectioncircle;
    public static final int TEXTURE_SELECTION_ARROW = R.drawable.selectionarrow;

    //texture number renderings
    public static final int TEXTURE_DIGIT_0 = R.drawable.digit0;
    public static final int TEXTURE_DIGIT_1 = R.drawable.digit1;
    public static final int TEXTURE_DIGIT_2 = R.drawable.digit2;
    public static final int TEXTURE_DIGIT_3 = R.drawable.digit3;
    public static final int TEXTURE_DIGIT_4 = R.drawable.digit4;
    public static final int TEXTURE_DIGIT_5 = R.drawable.digit5;
    public static final int TEXTURE_DIGIT_6 = R.drawable.digit6;
    public static final int TEXTURE_DIGIT_7 = R.drawable.digit7;
    public static final int TEXTURE_DIGIT_8 = R.drawable.digit8;
    public static final int TEXTURE_DIGIT_9 = R.drawable.digit9;

    //end level image renderings
    public static final int TEXTURE_END_LEVEL_SUCCESS = R.drawable.anothersuccess;
    public static final int TEXTURE_END_LEVEL_FAIL = R.drawable.faillevel;
    public static final int TEXTURE_FINAL_SCORE_TEXTURE = R.drawable.finalscore;





    //State of the game
    public enum GameStatus {BEFORE_PLAY, ACTIVE, POST_PLAY}
}

