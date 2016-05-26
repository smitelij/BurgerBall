package com.example.eli.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.util.ArrayList;

/**
 * Created by Eli on 3/11/2016.
 */
public class GameState {

    private ArrayList<Interactable> allActiveObjects;
    private ArrayList<Ball> mBalls;
    private ArrayList<Polygon> mBorders;
    private float[] mVPMatrix = new float[16];
    private Context mActivityContext;

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

    //texture stuff
    private int mBallTexture;
    private int mWallTexture;
    private int mObstacleTexture;


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

    public GameState(){

        //Eventually this will be moved out of the constructor
        ///loadLevel();
    }

    public void loadLevel(Context activityContext){
        mActivityContext = activityContext;
        loadTextures();
        loadBalls();
        loadBoundaries();
        updateActiveObjects();
    }

    private void loadTextures(){
        //mBallTexture = loadGLTexture()
    }

    private void loadBalls(){
        mBalls = new ArrayList<>();

        //create balls and add them to collection
        float[] ballCoords1 = getBorderCoords(140f, 160f, 8f);
        float[] ballCoords2 = getBorderCoords(140f, 144f, 8f);
        float[] ballCoords3 = getBorderCoords(30f, 152f, 8f);
        Ball ball1 = new Ball(ballCoords1, new PointF(-3.0f, -3.0f), GameState.ballRadius, GameState.ballColor, mActivityContext);
        Ball ball2 = new Ball(ballCoords2, new PointF(3.0f, 3.0f), GameState.ballRadius, GameState.ballColor, mActivityContext);
        Ball ball3 = new Ball(ballCoords3, new PointF(6.0f, 0f), GameState.ballRadius, GameState.ballColor, mActivityContext);
        mBalls.add(ball1);
        mBalls.add(ball2);
        mBalls.add(ball3);
    }

    private void loadBoundaries(){

        //TODO eventually move specific code from Borders to here
        Borders borders = new Borders();
        mBorders = borders.getAllBorders();
    }

    private void updateActiveObjects(){
        allActiveObjects = new ArrayList<>();

        for (Ball currentBall : mBalls){
            allActiveObjects.add(currentBall);
        }

        for (Polygon currentBorder : mBorders){
            allActiveObjects.add(currentBorder);
        }
    }

    //Main method called by MyGLRenderer each frame
    public void advanceFrame(){

        float timeElapsed = 0f;

        //repeat until 1 whole 'frame' has been moved.
        //If there is a collision, we will only advance up until the collision point,
        //and then repeat until we reach the end of the frame.
        while (timeElapsed < 1) {

            float timeStep = 1 - timeElapsed;
            System.out.println("time step: " + timeStep);

            //initialize collision detection engine
            CollisionDetection CD = new CollisionDetection();

            //Do collision detection
            collisionDetection(CD, timeStep);

            //Handle collisions (update velocities / displacements as necessary)
            timeElapsed = collisionHandling(CD, timeStep, timeElapsed);

        }

        System.out.println("...");
        System.out.println("...");
    }

    private void collisionDetection(CollisionDetection CD, float timeStep) {

        /*try {
                Thread.sleep(500);
            } catch (Exception e) {
            }*/

        //go through all balls
        for (Ball currentBall : mBalls) {

            //temporarily advance the balls location by the time step
            currentBall.moveBallByFrame(timeStep);

            //go through all objects that could be hit
            for (Interactable curObject : allActiveObjects) {

                //do a first test on their bounding boxes
                //TODO should the bounding boxes be increased by 1 on all sides? should we slow down time step if a ball is moving too quickly?
                if (CD.testBoundingBoxes(currentBall, curObject)) {

                    //There may have been a collision, further testing is necessary.
                    CD.doCollisionDetection(currentBall, curObject, timeStep);

                }
            }

            //set the ball to 'moved' so it is included on future collision tests in this frame
            currentBall.setBallMoved();
        }

        //clear the moved status once we have gone through all balls
        cleanupBalls();
    }

    private void cleanupBalls(){
        for (Ball currentBall : mBalls){
            currentBall.clearMovedStatus();
        }
    }

    //return timeElapsed
    private float collisionHandling(CollisionDetection CD, float timeStep, float timeElapsed){
        //==================
        //collision handling
        //==================

        //...All that matters is the first collision...

        //from this point on, referring to 'collisions' means the set of first collisions, by time.
        //this will usually be one collision, but it is possible for there to be multiple first collisions if:
        // 1- multiple balls collide with boundaries at the same time
        // 2- one ball collides with multiple boundaries at the same time
        // (two balls colliding with each other will not cause multiple first collisions, because each pair is checked only once.)
        ArrayList<CollisionHistory> firstCollisions = CD.getFirstCollision();

        //no collisions
        if (firstCollisions == null ) {
            //Move all balls forward by timestep
            handleNoCollisions(timeStep);
            timeElapsed = 1;

            //one or multiple collisions
        } else {
            System.out.println("collisions detected.");
            float collisionTime = CD.getFirstCollisionTime();
            //move all balls forward by collision time, and update velocity for colliding balls
            handleCollisionsForBalls(CD, firstCollisions, collisionTime);
            timeElapsed = timeElapsed + collisionTime;
        }

        return timeElapsed;
    }

    private void handleCollisionsForBalls(CollisionDetection CD, ArrayList<CollisionHistory> firstCollisions, float collisionTime){

        //Keep track of ball collisions vs boundary collisions
        CD.updateCollisionCollections(firstCollisions);

        //Move all balls to the point of collision
        for (Ball currentBall : mBalls){
            PointF newDisplacementVector;

            newDisplacementVector = new PointF(currentBall.getXVelocity() * collisionTime, currentBall.getYVelocity() * collisionTime);

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);
            cleanupAABB(currentBall, newDisplacementVector);
        }

        //Calculate new velocity for balls that have collided
        CD.handleBoundaryCollisions();
        CD.handleBallCollisions();

        //Update velocities
        for (Ball currentBall : mBalls){
            currentBall.updateVelocity();
            currentBall.clearCollisionHistory();
        }

        System.out.println(".");

    }

    private void cleanupAABB(Ball currentBall, PointF displacementVector){
        //clean up any lingering weird AABB stuff from collision detection
        currentBall.resetAABB();
        //move ball by however far it has so far been displaced (this will be 0 unless there are multiple collisions in 1 frame)
        currentBall.updateAABB(displacementVector.x, displacementVector.y);
        //Update saved value for AABB
        currentBall.updatePrevAABB();
    }

    public void handleNoCollisions(float timeStep){

        for (Ball currentBall : mBalls){
            PointF newDisplacementVector = new PointF(currentBall.getXVelocity() * timeStep, currentBall.getYVelocity() * timeStep);
            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);

            //no need to mess with AABB, it should be accurate so no need to cleanup,
            //and it will be updated within the Draw method of each ball.
        }
    }

    public void drawObjects(){

        float[] mModelProjectionMatrix = new float[16];

        //Draw all balls
        for (Ball currentBall : mBalls) {

            //Move ball forward by displacement amount
            Matrix.translateM(currentBall.mModelMatrix, 0, currentBall.mModelMatrix, 0, currentBall.getDisplacementVector().x, currentBall.getDisplacementVector().y, 0);
            //move into projection coordinates
            Matrix.multiplyMM(mModelProjectionMatrix, 0, mVPMatrix, 0, currentBall.mModelMatrix, 0);
            // Draw ball
            currentBall.draw(mModelProjectionMatrix);

            //clear displacement vector for next frame
            currentBall.clearDisplacementVector();
        }

        //Draw Borders
        for (Polygon currentBorder : mBorders){
            currentBorder.draw(mVPMatrix);
        }

    }

    public void updateVPMatrix(float[] VPMatrix){
        mVPMatrix = VPMatrix;
    }

    public int loadGLTexture(Context context) {

        int[] temp = new int[1];
        // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle);

        // generate one texture pointer
        GLES20.glGenTextures(1, temp, 0);
        // ...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, temp[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();

        return temp[0];
    }


}
