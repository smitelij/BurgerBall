package com.example.eli.myapplication.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.ArrayList;

/**
 * Created by Eli on 8/15/2016.
 */
public class LevelInitialization {

    private ArrayList<Ball> mAllBalls = new ArrayList<>();
    private ArrayList<Drawable> mAllDrawableObjects = new ArrayList<>();
    private ArrayList<Interactable> mAllInteractableObjects = new ArrayList<>();
    private ScoreDigits[] mScoreDigits = new ScoreDigits[5];
    private VelocityArrow mVelocityArrow;

    private static Context mActivityContext;

    private int mCurrentBallTexture;
    private int[] mDigitTextures = new int[10];

    private int mTotalBalls;


    public LevelInitialization(LevelData currentLevelData, Context activityContext){

        mActivityContext = activityContext;
        mTotalBalls = currentLevelData.getNumOfBalls();

        loadBoundaries();  //Load outer boundaries (don't need a reference to current level, because currently outer boundaries are always the same)
        loadObstacles(currentLevelData);  //Load level specific boundaries
        loadTargets(currentLevelData);  //Load level specific targets
        initializeBalls();  //Initialize the balls
        initializeDrawables();  //Add all objects that need to be drawn but are not interactable

    }

    //----------------
    //This function initializes all the balls that will be used for this level.
    // Although they are not drawn immediately (or activated), they need to be created
    // from the beginning so that OpenGL has a reference to them.
    //
    private void initializeBalls(){
        mAllBalls = new ArrayList<>();

        mCurrentBallTexture = loadGLTexture(GameState.TEXTURE_BALL);

        //Get initial ball coordinates
        float[] newBallCoords = GameState.getInitialBallCoords();

        //create balls and add them to collection
        for(int index=0; index < mTotalBalls; index++) {

            Ball ball = new Ball(newBallCoords, new PointF(0f, 0f), GameState.ballRadius, mCurrentBallTexture);
            mAllBalls.add(ball);
            mAllDrawableObjects.add(ball);
            mAllInteractableObjects.add(ball);
        }


    }

    private void loadBoundaries(){

        //TODO eventually move specific code from Borders to here
        Borders borders = new Borders(loadGLTexture(GameState.TEXTURE_WALL));
        ArrayList<Obstacle> boundaries = borders.getAllBorders();

        //Add borders as both interactable and drawable
        for (Obstacle boundary : boundaries){
            mAllInteractableObjects.add(boundary);
            mAllDrawableObjects.add(boundary);
        }

    }

    private void loadObstacles(LevelData levelData){
        ArrayList<float[]> obstacleCoords = levelData.getObstacleCoords();
        int obstacleTexture = LevelInitialization.loadGLTexture(GameState.TEXTURE_WALL);

        for (float[] currentObstacleCoords : obstacleCoords){
            Obstacle obstacle = new Obstacle(currentObstacleCoords, obstacleTexture);
            mAllInteractableObjects.add(obstacle);
            mAllDrawableObjects.add(obstacle);
        }
    }

    private void loadTargets(LevelData levelData){
        ArrayList<float[]> targetCoords = levelData.getTargetCoords();
        int targetTexture = LevelInitialization.loadGLTexture(GameState.TEXTURE_TARGET);

        for (float[] currentTargetCoords : targetCoords){
            Target target = new Target(currentTargetCoords,8f,targetTexture);
            mAllInteractableObjects.add(target);
            mAllDrawableObjects.add(target);
        }

    }

    private void initializeDrawables(){

        //Selection circle
        int selectionCircleTexture = loadGLTexture(GameState.TEXTURE_SELECTION_CIRCLE);
        SelectionCircle selectionCircle = new SelectionCircle(selectionCircleTexture);
        mAllDrawableObjects.add(selectionCircle);

        //Ghost ball
        GhostBall ghostBall = new GhostBall(mCurrentBallTexture);
        mAllDrawableObjects.add(ghostBall);

        //Velocity arrow
        int velocityArrowTexture = loadGLTexture(GameState.TEXTURE_SELECTION_ARROW);
        mVelocityArrow = new VelocityArrow(velocityArrowTexture);
        mAllDrawableObjects.add(mVelocityArrow);

        //Balls remaining counter
        initializeBallsRemainingCounter();

        //Score digits
        initializeScoreDigits();
    }

    private void initializeBallsRemainingCounter(){

        float boxWidth = GameState.BORDER_WIDTH;
        BallsRemainingIcon[] ballsRemainingCounter = new BallsRemainingIcon[mTotalBalls];

        for (int index = 0; index < mTotalBalls; index++){

            float separationAmount = (float) (index * 1.5);

            float[] coords = new float[]{
                    GameState.FULL_WIDTH - (boxWidth * (separationAmount)) - boxWidth, boxWidth, 0f,                      //top left
                    GameState.FULL_WIDTH - (boxWidth * (separationAmount)) - boxWidth, 0, 0f,         //bottom left
                    GameState.FULL_WIDTH - (boxWidth * separationAmount), 0, 0f,             //bottom right
                    GameState.FULL_WIDTH - (boxWidth * separationAmount), boxWidth, 0f                           //top right
            };

            ballsRemainingCounter[index] = new BallsRemainingIcon(coords, mCurrentBallTexture, index);
            mAllDrawableObjects.add(ballsRemainingCounter[index]);
        }

    }

    private void initializeScoreDigits(){

        loadDigitTextures();

        float boxWidth = GameState.BORDER_WIDTH * 2;

        for (int index = 0; index < 5; index++){
            float[] coords = new float[]{
                    GameState.FULL_WIDTH - (boxWidth * (index+1)), GameState.FULL_HEIGHT, 0f,                      //top left
                    GameState.FULL_WIDTH - (boxWidth * (index+1)), GameState.FULL_HEIGHT - (boxWidth), 0f,         //bottom left
                    GameState.FULL_WIDTH - (boxWidth * index), GameState.FULL_HEIGHT - (boxWidth), 0f,             //bottom right
                    GameState.FULL_WIDTH - (boxWidth * index), GameState.FULL_HEIGHT, 0f                           //top right
            };
            mScoreDigits[index] = new ScoreDigits(coords, mDigitTextures[9], mDigitTextures);
            mAllDrawableObjects.add(mScoreDigits[index]);
        }

    }

    private void loadDigitTextures(){
        mDigitTextures[0] = loadGLTexture(GameState.TEXTURE_DIGIT_0);
        mDigitTextures[1] = loadGLTexture(GameState.TEXTURE_DIGIT_1);
        mDigitTextures[2] = loadGLTexture(GameState.TEXTURE_DIGIT_2);
        mDigitTextures[3] = loadGLTexture(GameState.TEXTURE_DIGIT_3);
        mDigitTextures[4] = loadGLTexture(GameState.TEXTURE_DIGIT_4);
        mDigitTextures[5] = loadGLTexture(GameState.TEXTURE_DIGIT_5);
        mDigitTextures[6] = loadGLTexture(GameState.TEXTURE_DIGIT_6);
        mDigitTextures[7] = loadGLTexture(GameState.TEXTURE_DIGIT_7);
        mDigitTextures[8] = loadGLTexture(GameState.TEXTURE_DIGIT_8);
        mDigitTextures[9] = loadGLTexture(GameState.TEXTURE_DIGIT_9);
    }

    public static int loadGLTexture(int imagePointer) {

        int[] temp = new int[1];
        // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource(mActivityContext.getResources(), imagePointer);

        // generate one texture pointer
        GLES20.glGenTextures(1, temp, 0);
        // ...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, temp[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();

        return temp[0];
    }

    public ArrayList<Drawable> getAllDrawableObjects(){
        return mAllDrawableObjects;
    }

    public ArrayList<Interactable> getAllInteractableObjects(){
        return mAllInteractableObjects;
    }

    public ArrayList<Ball> getAllBalls(){
        return mAllBalls;
    }

    public ScoreDigits[] getScoreDigits(){
        return mScoreDigits;
    }

    public VelocityArrow getVelocityArrow(){
        return mVelocityArrow;
    }
}
