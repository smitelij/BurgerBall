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


    //All of these are just used to show the FPS the game is running at.
    private float mPrevTime = 0;
    private int frameCounter = 0;
    private float[] timesPerFrame = new float[10];
    private float sumTotal = 0;

    private ArrayList<Interactable> allActiveObjects;

    private ArrayList<Ball> mAllBalls;
    private ArrayList<Ball> mActiveBalls;

    private ArrayList<Polygon> mBorders;
    private ArrayList<Polygon> mObstacles;

    private ArrayList<Target> mTargets;

    private float[] mVPMatrix = new float[16];
    private static Context mActivityContext;

    private int mTotalBalls;

    private int mCurrentActiveBallID = 0;
    private int deactivatedBallCount = 0;

    //can't add a new ball while in the middle of a frame, may cause a concurrent modification error.
    //So we'll setup flags to determine whether we are in the middle of a frame, and whether a ball is waiting.
    private boolean mBallCollectionInUse;
    private boolean mBallWaiting;

    private boolean mLevelOver = false;

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
                //To activate a ball, all we need to do is add it to the collections.
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

        updateFPSinfo();

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
            //Also handles NO collisions
            timeElapsed = collisionHandling(CD, timeStep, timeElapsed);

        }

        testForDeactivatingBalls();

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
            deactivatedBallCount++;
            mActiveBalls.remove(deadBall);
            allActiveObjects.remove(deadBall);
        }

        if (deactivatedBallCount == mTotalBalls){
            mLevelOver = true;
            showFinalAvgFPS();
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

            //do collision testing between the current ball and each active object
            collisionTestAllActiveObjects(CD, currentBall, timeStep);

            //set the ball to 'moved' so it is included on future collision tests in this frame
            currentBall.setBallMoved();
        }

        //clear the moved status once we have gone through all balls
        cleanupBalls();
        unlockBalls();
    }

    private void collisionTestAllActiveObjects(CollisionDetection CD, Ball currentBall, float timeStep){
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
    }

    private void cleanupBalls(){
        for (Ball currentBall : mActiveBalls){
            currentBall.clearMovedStatus();
        }
    }

    private float collisionHandling(CollisionDetection CD, float timeStep, float timeElapsed){
        //==================
        //collision handling
        //==================

        //initialize collision handling object
        CollisionHandling CH = new CollisionHandling (CD.getCollisions());

        //...All that matters is the first collision...
        //Target collisions don't count here because they don't affect trajectories

        //from this point on, referring to 'collisions' means the set of first collisions, by time.
        //this will usually be one collision, but it is possible for there to be multiple first collisions if:
        // 1- multiple balls collide with objects at the same time
        // 2- one ball collides with multiple objects at the same time
        // (two balls colliding with each other will not cause multiple first collisions, because each pair is checked only once.)
        ArrayList<CollisionHistory> firstCollisions = CH.getFirstCollision();

        //no trajectory-affecting collisions
        if (firstCollisions == null) {
            //Move all balls forward by timestep
            handleNoCollisionsForBalls(timeStep);

            //Any target collisions will be valid, since there were no trajectory affecting collisions
            handleTargetCollisions(CH, timeStep);

            timeElapsed = GameState.FRAME_SIZE;

        //one or multiple collisions
        } else {
            //we can just grab the first member here since they all have the same time
            float collisionTime = firstCollisions.get(0).getTime();
            //move all balls forward by collision time, and update velocity for colliding balls
            handleCollisionsForBalls(CH, firstCollisions, collisionTime);

            //Only target collisions before collision time will be valid
            handleTargetCollisions(CH, collisionTime);

            timeElapsed = timeElapsed + collisionTime;
        }

        return timeElapsed;
    }

    private void handleCollisionsForBalls(CollisionHandling CH, ArrayList<CollisionHistory> firstCollisions, float collisionTime){

        //Move all balls to the point of collision
        moveBallsToCollisionInstant(collisionTime, true);

        //Keep track of ball collisions vs boundary collisions
        CH.updateCollisionCollections(firstCollisions);

        //Calculate new velocity for balls that have collided
        CH.handleBoundaryCollisions();  //Do boundary collisions first... more info in method header
        CH.handleBallCollisions();

        //Update velocities (not done above, for cases that one ball collides with multiple things...
        //then we don't want to update the calculated velocity until after going through all collisions)
        updateVelocities(collisionTime);

    }

    private void handleNoCollisionsForBalls(float timeStep){

        //first, move all the balls to the 'collision point'
        // (in this case, just means the end of the frame)
        moveBallsToCollisionInstant(timeStep, false);

        //update the velocities of all the balls (because gravity is affecting it)
        updateVelocities(timeStep);
    }

    private void moveBallsToCollisionInstant(float collisionTime, boolean cleanupAABB){

        lockBalls();

        //Go through all active balls
        for (Ball currentBall : mActiveBalls){
            //calculate displacement that will result in new position
            PointF newDisplacementVector = currentBall.calculatePositionChange(collisionTime);

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);

            //Only cleanup AABB if necessary (only when there are collisions in the frame)
            if (cleanupAABB) {
                cleanupAABB(currentBall, newDisplacementVector);
            }

        }

        unlockBalls();
    }

    private void cleanupAABB(Ball currentBall, PointF displacementVector){
        //clean up any lingering weird AABB stuff from collision detection
        currentBall.resetAABB();
        //move ball by however far it has so far been displaced (this will be 0 unless there are multiple collisions in 1 frame)
        currentBall.updateAABB(displacementVector.x, displacementVector.y);
        //Update saved value for AABB
        currentBall.updatePrevAABB();
    }


    private void updateVelocities(float timeStep){

        lockBalls();

        for (Ball currentBall : mActiveBalls){

            //will either use any new velocity that was calculated in the event of a collision,
            // or else just factor in the current gravity and return a new velocity.
            currentBall.updateVelocity(timeStep);
            currentBall.clearCollisionHistory();
        }

        unlockBalls();
    }

    private void handleTargetCollisions(CollisionHandling CH, float firstCollisionTime){

        ArrayList<Target> hitTargets = CH.getTargetCollisions(firstCollisionTime);

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

    private void updateFPSinfo(){

        if (GameState.showFPS == false){
            return;
        }

        if (mPrevTime != 0) {
            float currentTime = System.nanoTime();
            float timeLastFrame = currentTime - mPrevTime;
            timesPerFrame[frameCounter % 10] = timeLastFrame;
            sumTotal = sumTotal + timeLastFrame;
            frameCounter++;
            mPrevTime = currentTime;
            if (frameCounter % 10 == 0){
                System.out.println("Frames per second: " + calculateFPS());
            }
        } else {
            mPrevTime = System.nanoTime();
        }
    }

    private float calculateFPS(){
        float sumTime = 0;

        for (int index = 0; index < 10; index++){
            sumTime = sumTime + timesPerFrame[index];
        }

        float avgTimeMillis = sumTime / 10;
        float avgTimeSecs = avgTimeMillis / 1000000000;

        return (1/avgTimeSecs);
    }

    private void showFinalAvgFPS(){
        if (GameState.showFPS == false){
            return;
        }

        float avgFPS = 1 / ((sumTotal / frameCounter) / 1000000000);
        System.out.println("!!!FINAL AVG FPS: " + avgFPS);
    }

    public boolean isLevelActive(){
        if (mLevelOver){
            return false;
        } else {
            return true;
        }
    }



}
