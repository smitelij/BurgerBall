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
public class GameEngine {

    private ArrayList<Interactable> allActiveObjects;

    private ArrayList<Ball> mAllBalls;
    private ArrayList<Ball> mActiveBalls;

    private ArrayList<Polygon> mBorders;
    private ArrayList<Polygon> mObstacles;

    private ArrayList<Target> mTargets;

    private float[] mVPMatrix = new float[16];
    private static Context mActivityContext;

    private int mTotalBalls;

    //texture stuff
    private int mBallTexture;
    private int mWallTexture;
    private int mObstacleTexture;

    private int mCurrentActiveBallID = 0;

    //can't add a new ball while in the middle of a frame, may cause a concurrent modification error.
    //So we'll setup flags to determine whether we are in the middle of a frame, and whether a ball is waiting.
    private boolean mBallCollectionInUse;
    private boolean mBallWaiting;

    public GameEngine(){

        //Eventually this will be moved out of the constructor
        ///loadLevel();
    }

    public void loadLevel(){
        //eventually this will depend on the level

        Level currentLevel = new Level(1);
        mTotalBalls=currentLevel.getNumOfBalls();

        initializeBalls();
        loadBoundaries();
        loadObstacles(currentLevel);
        loadTargets(currentLevel);
        initializeActiveObjects();
    }

    private void initializeBalls(){
        mAllBalls = new ArrayList<>();
        mActiveBalls = new ArrayList<>();

        float[] newBallCoords = GameState.getInitialBallCoords();

        //create balls and add them to collection
        for(int index=0; index < mTotalBalls; index++) {

            Ball ball = new Ball(newBallCoords, new PointF(0f, 0f), GameState.ballRadius, GameState.ballColor, loadGLTexture(GameState.TEXTURE_BALL));
            mAllBalls.add(ball);
        }

    }

    public void activateBall(PointF initialVelocity){

        //Add a new ball as long as there are still balls available to add
        if(mCurrentActiveBallID < mTotalBalls) {

            Ball ball = mAllBalls.get(mCurrentActiveBallID);

            //if initial velocity is null, that means we are activating after a wait,
            //which means that the initial velocity was already set.
            if (initialVelocity != null) {
                ball.setVelocity(initialVelocity);
            }

            //make sure ball collections aren't in use before adding to them.
            if (canActivateBall()) {
                mActiveBalls.add(ball);
                allActiveObjects.add(ball);
                mCurrentActiveBallID++;
                System.out.println("Ball activated." );
            }
        }
    }

    private boolean canActivateBall(){
        //wait to add balls if ball collection is currently in use
        if (mBallCollectionInUse){
            mBallWaiting = true;
            return false;

        //clear the flags if ball collection is not in use
        } else {
            mBallWaiting = false;
            return true;
        }
    }

    private void loadBoundaries(){

        //TODO eventually move specific code from Borders to here
        Borders borders = new Borders(loadGLTexture(GameState.TEXTURE_WALL));
        mBorders = borders.getAllBorders();

    }

    private void loadObstacles(Level level){
        mObstacles = level.getObstacles();
    }

    private void loadTargets(Level level){
        mTargets = level.getTargets();
    }

    private void initializeActiveObjects(){
        allActiveObjects = new ArrayList<>();

        //No active balls yet, so we only need to add borders
        for (Polygon currentBorder : mBorders){
            allActiveObjects.add(currentBorder);
        }

        for (Polygon obstacle : mObstacles){
            allActiveObjects.add(obstacle);
        }

        for (Target target : mTargets){
            allActiveObjects.add(target);
        }
    }

    //Main method called by MyGLRenderer each frame
    public void advanceFrame(){

        float timeElapsed = 0f;

        //repeat until 1 whole 'frame' has been moved.
        //If there is a collision, we will only advance up until the collision point,
        //and then repeat until we reach the end of the frame.
        while (timeElapsed < GameState.FRAME_SIZE) {

            float timeStep = GameState.FRAME_SIZE - timeElapsed;
            //System.out.println("time step: " + timeStep);

            //initialize collision detection engine
            CollisionDetection CD = new CollisionDetection();

            //Do collision detection
            collisionDetection(CD, timeStep);

            //Handle collisions (update velocities / displacements as necessary)
            if (CD.didCollisionOccur()) {
                timeElapsed = collisionHandling(CD, timeStep, timeElapsed);
            }

        }

        testForDeactivatingBalls();

        //System.out.println("...");
        //System.out.println("...");
    }

    private void testForDeactivatingBalls(){

        ArrayList<Ball> deactivationList = new ArrayList<>();

        lockBalls();

        for (Ball currentBall : mActiveBalls){

            if (currentBall.getPerFrameCollisionCount() > (GameState.FRAME_SIZE * GameState.DEACTIVATION_CONSTANT)){
                deactivationList.add(currentBall);
            } else {
                currentBall.clearFrameCollisionCount();
            }
        }

        unlockBalls();

        for (Ball deadBall : deactivationList){
            mActiveBalls.remove(deadBall);
            allActiveObjects.remove(deadBall);
        }
    }

    private void collisionDetection(CollisionDetection CD, float timeStep) {

        /*try {
                Thread.sleep(500);
            } catch (Exception e) {
            }*/

        lockBalls();
        //go through all balls
        for (Ball currentBall : mActiveBalls) {

            //temporarily advance the balls location by the time step
            currentBall.moveBallByFrame(timeStep);

            //go through all objects that could be hit
            for (Interactable curObject : allActiveObjects) {

                //do a first test on their bounding boxes
                //TODO should the bounding boxes be increased by 1 on all sides? should we slow down time step if a ball is moving too quickly?
                if (CD.coarseCollisionTesting(currentBall, curObject)) {

                    //There may have been a collision, further testing is necessary.
                    //Any collisions detected will be saved in CD within a CollisionHistory arraylist.
                    CD.detailedCollisionTesting(currentBall, curObject, timeStep);

                }
            }

            //set the ball to 'moved' so it is included on future collision tests in this frame
            currentBall.setBallMoved();
        }

        //clear the moved status once we have gone through all balls
        cleanupBalls();
        unlockBalls();
    }

    private void cleanupBalls(){
        for (Ball currentBall : mActiveBalls){
            currentBall.clearMovedStatus();
        }
    }

    //return timeElapsed
    private float collisionHandling(CollisionDetection CD, float timeStep, float timeElapsed){
        //==================
        //collision handling
        //==================

        //...All that matters is the first collision...
        //Target collisions don't matter here because they don't affect trajectories

        //from this point on, referring to 'collisions' means the set of first collisions, by time.
        //this will usually be one collision, but it is possible for there to be multiple first collisions if:
        // 1- multiple balls collide with objects at the same time
        // 2- one ball collides with multiple objects at the same time
        // (two balls colliding with each other will not cause multiple first collisions, because each pair is checked only once.)
        ArrayList<CollisionHistory> firstCollisions = CD.getFirstCollision();

        lockBalls();

        //no collisions
        if (firstCollisions == null) {
            //Move all balls forward by timestep
            handleNoCollisions(timeStep);
            //Any target collisions will be valid, since there were no trajectory affecting collisions
            handleTargetCollisions(CD, GameState.FRAME_SIZE);
            timeElapsed = GameState.FRAME_SIZE;

            //one or multiple collisions
        } else {
            float collisionTime = CD.getFirstCollisionTime();
            //move all balls forward by collision time, and update velocity for colliding balls
            handleCollisionsForBalls(CD, firstCollisions, collisionTime);
            //Only target collisions before collision time will be valid
            handleTargetCollisions(CD, collisionTime);
            timeElapsed = timeElapsed + collisionTime;
        }

        unlockBalls();

        return timeElapsed;
    }

    private void handleCollisionsForBalls(CollisionDetection CD, ArrayList<CollisionHistory> firstCollisions, float collisionTime){

        //Keep track of ball collisions vs boundary collisions
        CD.updateCollisionCollections(firstCollisions);

        //Move all balls to the point of collision
        for (Ball currentBall : mActiveBalls){
            PointF newDisplacementVector;

            //newDisplacementVector = new PointF(currentBall.getXVelocity() * collisionTime, currentBall.getYVelocity() * collisionTime);
            newDisplacementVector = currentBall.calculatePositionChange(collisionTime);

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);
            cleanupAABB(currentBall, newDisplacementVector);
        }

        //Calculate new velocity for balls that have collided
        CD.handleBoundaryCollisions();  //Do boundary collisions first... more info in method header
        CD.handleBallCollisions();

        //Update velocities
        for (Ball currentBall : mActiveBalls){
            currentBall.updateVelocity(collisionTime);
            currentBall.clearCollisionHistory();
        }

        //System.out.println(".");

    }

    private void cleanupAABB(Ball currentBall, PointF displacementVector){
        //clean up any lingering weird AABB stuff from collision detection
        currentBall.resetAABB();
        //move ball by however far it has so far been displaced (this will be 0 unless there are multiple collisions in 1 frame)
        currentBall.updateAABB(displacementVector.x, displacementVector.y);
        //Update saved value for AABB
        currentBall.updatePrevAABB();
    }

    private void handleNoCollisions(float timeStep){

        for (Ball currentBall : mActiveBalls){
            //PointF newDisplacementVector = new PointF(currentBall.getXVelocity() * timeStep, currentBall.getYVelocity() * timeStep);
            PointF newDisplacementVector = currentBall.calculatePositionChange(timeStep);

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);

            //no need to mess with AABB, it should be accurate so no need to cleanup,
            //and it will be updated within the Draw method of each ball.
        }

        //Update velocities
        for (Ball currentBall : mActiveBalls){
            currentBall.updateVelocity(timeStep);
            currentBall.clearCollisionHistory();
        }
    }

    private void handleTargetCollisions(CollisionDetection CD, float firstCollisionTime){
        ArrayList<Target> hitTargets = new ArrayList<>();

        hitTargets = CD.getTargetCollisions(firstCollisionTime);

        for (Target target : hitTargets){
            mTargets.remove(target);
        }
    }

    public void drawObjects(){

        float[] mModelProjectionMatrix = new float[16];

        lockBalls();

        //Draw all balls
        for (Ball currentBall : mActiveBalls) {

            //Move ball forward by displacement amount
            Matrix.translateM(currentBall.mModelMatrix, 0, currentBall.mModelMatrix, 0, currentBall.getDisplacementVector().x, currentBall.getDisplacementVector().y, 0);
            //move into projection coordinates
            Matrix.multiplyMM(mModelProjectionMatrix, 0, mVPMatrix, 0, currentBall.mModelMatrix, 0);
            // Draw ball
            currentBall.draw(mModelProjectionMatrix);

            //clear displacement vector for next frame
            currentBall.clearDisplacementVector();
        }

        unlockBalls();


        //Draw Borders
        for (Polygon currentBorder : mBorders){
            currentBorder.draw(mVPMatrix);
        }

        //Draw obstacles
        for (Polygon currentObstacle : mObstacles){
            currentObstacle.draw(mVPMatrix);
        }

        //Draw targets
        for (Target currentTarget : mTargets){
            currentTarget.draw(mVPMatrix);
        }

    }

    public void updateVPMatrix(float[] VPMatrix){
        mVPMatrix = VPMatrix;
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
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();

        return temp[0];
    }

    public void setContext(Context context){
        mActivityContext = context;
    }

    private void lockBalls(){
        mBallCollectionInUse = true;
    }

    private void unlockBalls(){
        mBallCollectionInUse = false;

        //if a ball is waiting (because it couldn't be added while the frame is in progress), then activate it now
        if (mBallWaiting){
            activateBall(null);
        }
    }



}
