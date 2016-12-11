package com.example.eli.myapplication.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.eli.myapplication.Model.GameState;
import com.example.eli.myapplication.Model.Interactable;
import com.example.eli.myapplication.Model.LevelData;
import com.example.eli.myapplication.Model.LevelInitialization;
import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.BallsRemainingIcon;
import com.example.eli.myapplication.Model.Circle;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Model.Drawable;
import com.example.eli.myapplication.Model.ScoreDigits;
import com.example.eli.myapplication.Model.Target;
import com.example.eli.myapplication.Model.MovingObstacle;

import java.util.ArrayList;

/**
 * This class contains code for the main game loop. First, loadLevel should be called from
 * MyGLRenderer.onSurfaceCreate, and after that advanceFrame and drawObjects are
 * called every GUI frame from MyGLRenderer.onDrawFrame.
 */
public class GameEngine {

    public final static String END_LEVEL_SCORE = "com.example.eli.myapplication.END_LEVEL_SCORE";

    //All active objects that need to be collision checked are added to this collection
    private ArrayList<Interactable> allInteractableObjects;

    //All active objects that only need to be drawn (not collision checked) are added to this collection
    private ArrayList<Drawable> allDrawableObjects;

    private ArrayList<MovingObstacle> allMovingObstacles;

    private ScoreDigits[] mScoreDigits = new ScoreDigits[5];

    private ArrayList<Ball> mAllBalls;     //Collection of all balls
    private int mNumActiveBalls = 0;
    private int mTargetsHit = 0;

    private float[] mVPMatrix = new float[16];  //View-Projection matrix to place objects into actual device coordinates
    private static Context mActivityContext;

    private int mTotalBalls;    //Number of total balls available this level
    private int mTotalTargets;  //Number of total targets this level

    private int mCurrentActiveBallID = 0;  //The current active ball index

    //can't add a new ball while in the middle of a frame, may cause a concurrent modification error.
    //So we'll setup flags to determine whether we are in the middle of a frame, and whether a ball is waiting.
    private boolean mBallWaiting;          //True if we are waiting to add a ball

    //Boolean that is true while the level is active (after the first ball has been fired, and before end conditions are met)
    private boolean mLevelActive = false;
    private boolean mInitialRender = false;

    //Frame size stuff
    private float mCurrentFrameSize;  //Current frame size (will be different every frame if GameState.VARIABLE_FRAME_SIZE is true)
    private float[] mFrameSizeHistory = new float[3];  //History of past frame sizes (used for taking the average)

    //All of these are used to determine the FPS the game is running at.
    private float mPrevTime = 0;  //The time of the previous frame
    private int mFrameCount = 0;  //Total frames that have run
    private float[] timesPerFrame = new float[10];  //Array to keep a running tally of the average FPS
    private float sumTotal = 0;  //Total time
    private float mLastTenFrameAvg = 0;  //Average length (in millisec) of the last ten frames

    //These are used for calculating avg discrepancy at the end of the level.
    //'Discrepancy' measures how large the variation is between frame lengths. It is calculated
    //every 10 frames, using the formula: (longest frame - shortest frame) / longest frame.
    // A high discrepancy is bad, because that means that the frame length isn't very consistent,
    // and may appear jittery to the user. The GameState.AUTO_CAP_FRAME_RATE_SIZE variable tries to automatically
    // keep discrepancy down by setting an ideal GameState.FRAME_RATE_CAP_SIZE, but it may be helpful
    // to manually set GameState.FRAME_RATE_CAP_SIZE for better results.
    private float discrepancySum = 0;
    private float discrepancyCounter = 0;

    private Circle mVelocityArrow;
    private boolean mIsVelocityArrowActive;

    private int currentScore = 100000;

    private int mChapter;
    private int mLevel;

    private Activity mParentActivity;

    private boolean freezeGame = false;


    //--------------------
    //Initialize the GameEngine class
    //
    public GameEngine(String levelString, Activity parentActivity){

        System.out.println(levelString);
        String[] parts = levelString.split("\\.");
        String chapterPart = parts[0];
        String levelPart = parts[1];
        mChapter = Integer.parseInt(chapterPart);
        mLevel = Integer.parseInt(levelPart);
        mParentActivity = parentActivity;
    }

    //--------------------
    //Load a level
    //
    public void loadLevel(){

        //Grab the level data (number of balls, coordinates of objects, etc)
        LevelData currentLevelData = new LevelData(mChapter,mLevel);
        mTotalBalls = currentLevelData.getNumOfBalls();
        mTotalTargets = currentLevelData.getNumOfTargets();

        //Next we need to initialize all the objects that will be drawn in this level
        LevelInitialization levelInitialization = new LevelInitialization(currentLevelData, mActivityContext,mChapter);

        //Now grab the objects (or collections of objects) that we will need to access
        allInteractableObjects = levelInitialization.getAllInteractableObjects();
        allDrawableObjects = levelInitialization.getAllDrawableObjects();
        allMovingObstacles = levelInitialization.getAllMovingObstacles();
        mAllBalls = levelInitialization.getAllBalls();
        mScoreDigits = levelInitialization.getScoreDigits();
        mVelocityArrow = levelInitialization.getVelocityArrow();

    }

    //----------------------
    //This function activates a ball. It is called when a user
    //has dragged and released from within the response radius (specified in GameState).
    //if the ball collections are currently in use (e.g. collision detection is happening or balls are being drawn),
    //then we will set a flag to wait until the ball collection is free to activate the ball. Otherwise, we could
    //get a concurrent modification error from the ArrayList.
    //PARAMS:
    //  initialVelocity- The velocity that the ball will be activated with (determined by GameState.calculateInitialVelocity)
    //
    public void activateBall(PointF initialVelocity){

        //Add a new ball as long as there are still balls available to add
        if(mCurrentActiveBallID < mTotalBalls) {

            Ball ball = mAllBalls.get(mCurrentActiveBallID);

            //if initial velocity is null, that means we are activating after a wait,
            //which means that the initial velocity was already set.
            if (initialVelocity != null) {
                ball.setVelocity(initialVelocity);
            }

            //To activate a ball, all we need to do is add it to the collections.
            //check if we can activate the ball right now
            if (canActivateBall(ball)) {
                //'activating' the ball is as easy as adding to the collections
                //mActiveBalls.add(ball);
                ball.activateBall();
                mCurrentActiveBallID++;
                mNumActiveBalls++;
                mLevelActive = true;
                mBallWaiting = false; //clear the flag in case it got activated
                System.out.println("Ball activated." );
            //if we can't activate the ball, set the waiting flag.
            } else {
                mBallWaiting = true;
            }

        }

    }

    //-----------------
    //This function does checks to see if we can currently activate a ball.
    //PARAMS:
    //  newBall- Ball object, used to get the current ball radius
    //RETURNS:
    // True if we can activate the ball, false if we can't.
    //
    private boolean canActivateBall(Ball newBall){
        //Checks before we can activate a ball:
        //1: The firing zone must be clear
        if (!isFiringZoneClear(newBall)){
            return false;

        //Or else we can activate the ball.
        } else {
            return true;
        }
    }

    //-------------------------
    //This function checks if any other balls are currently in the firing zone.
    //By firing zone, we just mean the starting location of the new ball.
    //Used for a check in canActivateBall.
    private boolean isFiringZoneClear(Ball newBall){

        PointF newBallCenter = newBall.getCenter();

        for (Ball currentBall : mAllBalls){

            if (!currentBall.isBallActive()){
                continue;
            }

            PointF currentBallCenter = currentBall.getCenter();

            PointF distanceVector = new PointF(newBallCenter.x - currentBallCenter.x, newBallCenter.y - currentBallCenter.y);
            float distance = distanceVector.length();

            //If the distance between them is less than the radius's, then this ball is in the firing zone.
            if (distance < (newBall.getRadius() + currentBall.getRadius())){
                return false;
            }
        }

        //if we made it through all balls, and none collided with the new ball, then the firing zone is clear.
        return true;
    }

    //Main method called by MyGLRenderer each frame
    public void advanceFrame(){

        if (freezeGame){
            return;
        }

        //Handle things that should happen before the frame is advanced.
        //(Add waiting balls, keep track of FPS, etc)
        preAdvanceFrame();

        //This is the bulk of the advance frame logic.
        //Here we will advance through a frame, potentially step by step if collisions occur,
        //keeping track of all collisions and adjusting velocities accordingly.
        advanceFrameCollisionChecks();

        //Handle logic after the frame has been moved.
        //(Update score, move balls forward, check end conditions, etc)
        postAdvanceFrame();

    }

    private void preAdvanceFrame(){
        //This is only necessary if VARIABLE_FRAME_SIZE is activated in GameState.
        //Usually it's not very useful but it's an interesting setting regardless.
        mCurrentFrameSize = getFrameSize();

        //Determine how fast the game is running. Unless FRAME_RATE_CAP is specified in GameState,
        //this is just a piece of diagnostic information.
        updateFPSinfo();

        //Activate any balls that are ready to be fired.
        if (mBallWaiting){
            activateBall(null);
        }
    }

    private void advanceFrameCollisionChecks(){
        //start each frame at 0
        float timeElapsed = 0f;

        //repeat until 1 whole 'frame' has been moved.
        //If there is a collision, we will only advance up until the collision point,
        //and then repeat until we reach the end of the frame.
        while (timeElapsed < mCurrentFrameSize) {

            System.out.println("time elapsed (beginning) : " + timeElapsed);

            //At first, we try to advance the whole frame (or whatever is remaining), and see if any collisions happen.
            float timeStep = mCurrentFrameSize - timeElapsed;

            System.out.println("time step: " + timeStep);

            //Initialize and run collision detection
            CollisionDetection CD = new CollisionDetection();
            collisionDetection(CD, timeStep);

            //Handle collisions (update velocities / displacements as necessary)
            //Also handles NO collisions (assign normal velocity to the balls displacement tally)
            //If there is a collision, timeElapsed is updated to the collision time.
            timeElapsed = collisionHandling(CD, timeStep, timeElapsed);

            System.out.println("time elapsed (end): " + timeElapsed);

        }
    }

    private void postAdvanceFrame(){

        //based on each balls displacement tally, translate the balls model matrix by that amount
        moveBallsForward();

        //decrement a constant value from the score, update digits with correct texture
        updateScore();

        //check if a ball should be deactivated
        testForDeactivatingBalls();

        //check if all balls have been fired or all targets collected
        endLevelChecks();
    }

    private void updateScore(){

        if (currentScore == 0){
            return;
        }

        currentScore = currentScore - 171;

        if(currentScore < 0){
            currentScore = 0;
        }

        int scoreDigit1 = currentScore % 10;
        int scoreDigit10 = (currentScore % 100) / 10;
        int scoreDigit100 = (currentScore % 1000) / 100;
        int scoreDigit1000 = (currentScore % 10000) / 1000;
        int scoreDigit10000 = (currentScore % 100000) / 10000;

        mScoreDigits[0].updateTexture(scoreDigit1);
        mScoreDigits[1].updateTexture(scoreDigit10);
        mScoreDigits[2].updateTexture(scoreDigit100);
        mScoreDigits[3].updateTexture(scoreDigit1000);
        mScoreDigits[4].updateTexture(scoreDigit10000);

    }

    private void testForDeactivatingBalls(){

        ArrayList<Ball> deactivationList = new ArrayList<>();

        for (Ball currentBall : mAllBalls){

            if (!currentBall.isBallActive()){
                continue;
            }

            if (currentBall.getPerFrameCollisionCount() > (mCurrentFrameSize * GameState.DEACTIVATION_CONSTANT)){
                deactivationList.add(currentBall);
                currentBall.deactivateBall();
                mNumActiveBalls--;
            } else {
                currentBall.clearFrameCollisionCount();
            }
        }

        for (Ball deadBall : deactivationList){
            //mActiveBalls.remove(deadBall);
            allInteractableObjects.remove(deadBall);
        }

    }

    private void endLevelChecks(){
        if (! mLevelActive){
            return;
        }

        //If all available balls have been fired, and none are still active, then we are done.
        if ((mCurrentActiveBallID == (mTotalBalls)) && (mNumActiveBalls == 0)){
            endLevelFail();
        }

        //Or if all the targets have been hit
        if ((mTargetsHit == mTotalTargets)){
            endLevelSuccess();
        }
    }

    private void endLevelSuccess(){
        //eventually put some sort of graphic or message here

        //give them at least 1 point for 1 star
        if (currentScore == 0){
            currentScore = 1;
        }
        endLevel();
    }

    private void endLevelFail(){
        //eventually put some sort of graphic or message here
        currentScore = 0;
        endLevel();
    }

    private void endLevel(){
        mLevelActive = false;
        showFinalAvgFPS();

        System.out.println("FINAL SCORE: " + currentScore);
        Intent data = new Intent();
        data.putExtra(END_LEVEL_SCORE, currentScore);
        mParentActivity.setResult(mParentActivity.RESULT_OK, data);

        mParentActivity.finish();

    }

    /**
     * This method starts the collision detection process for each active ball.
     * To do this, we advance the ball to its next location (calculated using time step and velocity),
     * and then test this new location against all currently active objects.
     */
    private void collisionDetection(CollisionDetection CD, float timeStep) {
/*
        try {
                Thread.sleep(200);
            } catch (Exception e) {
            } */

        System.out.println("Begin collision detection frame. Size: " + mCurrentFrameSize);

        for(MovingObstacle currentObstacle : allMovingObstacles){
            currentObstacle.moveTempCoordsByFrame(timeStep);
        }

        //go through all active balls
        for (Ball currentBall : mAllBalls) {

            System.out.println("..Collision detection for ball.");

            if (!currentBall.isBallActive()){
                continue;
            }

            //temporarily advance the balls location by the time step
            currentBall.moveByFrame(timeStep);

            //do collision testing between the current ball and each active object
            collisionTestAllActiveObjects(CD, currentBall, timeStep);

            //set the ball to 'moved' so it is included on future collision tests in this frame
            currentBall.setBallMoved();
        }

        //clear the moved status once we have gone through all balls
        cleanupBalls();

    }

    /**
     * This method coordinates the actual collision detection between a ball and all active objects.
     * To be efficient, we first compare the bounding boxes of each object. If they intersect on both
     * the x and y axis, it is possible they are colliding. This means we need detailed collision testing,
     * which is implemented using the separating axis theorem on the objects. More details about the
     * separating axis test is in the CollisionDetection class.
     *
     * If we detect a collision, no further action is needed- it is handled within detailedCollisionTesting.
     *
     * PARAMS:
     *   CD - CollisionDetection object used to call the necessary methods
     *   currentBall - The current ball being tested
     *   timeStep - The length of the current iteration
     */
    private void collisionTestAllActiveObjects(CollisionDetection CD, Ball currentBall, float timeStep){

        //Go through all objects that could be hit
        for (Interactable curObject : allInteractableObjects) {

            //System.out.println("###### collision testing - ball: " + currentBall.hashCode() + " _ obstacle: " + curObject.hashCode());

            //Do a first test on their bounding boxes
            //TODO should the bounding boxes be increased by 1 on all sides? should we slow down time step if a ball is moving too quickly?
            if (CD.coarseCollisionTesting(currentBall, curObject)) {

                //There may have been a collision, further testing is necessary.
                //Any collisions detected will be added to a collection in CD (mCollisions).
                try {
                    //System.out.println("do detailed collision testing.");
                    CD.detailedCollisionTesting(currentBall, curObject, timeStep);
                } catch (Exception e) {
                    freezeGame();
                }

            }
        }
    }

    private void freezeGame() {
        freezeGame = true;
    }

    private void cleanupBalls(){
        for (Ball currentBall : mAllBalls){

            if (! currentBall.isBallActive()){
                continue;
            }

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
        ArrayList<Collision> firstCollisions = CH.getFirstCollision();

        //no trajectory-affecting collisions
        if (firstCollisions == null) {
            //Move all balls forward by timestep
            handleNoCollisionsForBalls(timeStep);

            //Any target collisions will be valid, since there were no trajectory affecting collisions
            handleTargetCollisions(CH, timeStep);

            //Need to update coords & AABB for moving obstacles
            handleMovingObstacles(timeStep);

            timeElapsed = mCurrentFrameSize;

        //one or multiple collisions
        } else {

            //we can just grab the first member here since they all have the same time
            float collisionTime = firstCollisions.get(0).getTime();

            System.out.println("COLLISION DETECTED. Collision time: " + collisionTime);

            //move all balls forward by collision time, and update velocity for colliding balls
            handleCollisionsForBalls(CH, firstCollisions, collisionTime);

            //Only target collisions before collision time will be valid
            handleTargetCollisions(CH, collisionTime);

            //Need to update coords & AABB for moving obstacles
            handleMovingObstacles(collisionTime);

            timeElapsed = timeElapsed + collisionTime;

        }

        return timeElapsed;
    }

    private void handleCollisionsForBalls(CollisionHandling CH, ArrayList<Collision> firstCollisions, float collisionTime){

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

        //Go through all active balls
        for (Ball currentBall : mAllBalls){

            if(!currentBall.isBallActive()){
                continue;
            }

            //calculate displacement that will result in new position
            PointF newDisplacementVector = currentBall.calculatePositionChange(collisionTime);

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);

            //Only cleanup AABB if necessary (only when there are collisions in the frame)
            if (cleanupAABB) {
                cleanupBallAABB(currentBall, newDisplacementVector);
            }

        }

    }

    private void cleanupBallAABB(Ball currentBall, PointF displacementVector){
        //clean up any lingering weird AABB stuff from collision detection
        currentBall.resetAABB();
        //move ball by however far it has so far been displaced (this will be 0 unless there are multiple collisions in 1 frame)
        currentBall.updateAABB(displacementVector.x, displacementVector.y);
        //Update saved value for AABB
        currentBall.updatePrevAABB();
    }


    private void handleMovingObstacles(float collisionTime){
        for (MovingObstacle currentObstacle : allMovingObstacles){
            //Cleanup moving obstacle AABB
            currentObstacle.resetAABB(); //reset because they should have been moved 1 full frame
            System.out.println("Move MovingObstacle by frame step: " + collisionTime);
            System.out.println("previous vertex location: " + currentObstacle.get2dCoordArray()[0]);
            currentObstacle.moveByFrame(collisionTime); //move forward to the collision time
            System.out.println("new vertex location: " + currentObstacle.get2dCoordArray()[0]);
            currentObstacle.updatePrevAABB();  //update saved value
        }
    }


    private void updateVelocities(float timeStep){

        for (Ball currentBall : mAllBalls){

            if (!currentBall.isBallActive()){
                continue;
            }

            //will either use any new velocity that was calculated in the event of a collision,
            // or else just factor in the current gravity and return a new velocity.
            currentBall.updateVelocity(timeStep);
            currentBall.clearCollisionHistory();
        }
    }

    private void handleTargetCollisions(CollisionHandling CH, float firstCollisionTime){

        ArrayList<Target> hitTargets = CH.getTargetCollisions(firstCollisionTime);

        for (Target target : hitTargets){
            mTargetsHit++;
            allInteractableObjects.remove(target);
            allDrawableObjects.remove(target);
        }
    }

    private void moveBallsForward(){
        //Go through all balls
        for (Ball currentBall : mAllBalls) {

            //skip inactive balls
            if (!currentBall.isBallActive()){
                continue;
            }

            //get current model matrix
            float[] modelMatrix = currentBall.getModelMatrix();
            float[] modelProjectionMatrix = new float[16];

            //Move ball forward by displacement amount
            Matrix.translateM(modelMatrix, 0, modelMatrix, 0, currentBall.getDisplacementVector().x, currentBall.getDisplacementVector().y, 0);
            //move into projection coordinates
            Matrix.multiplyMM(modelProjectionMatrix, 0, mVPMatrix, 0, modelMatrix, 0);

            //save model matrix and model projection matrix
            // (model matrix will be used next frame, and model projection matrix will be used to draw this frame)
            currentBall.setModelMatrix(modelMatrix);
            currentBall.setModelProjectionMatrix(modelProjectionMatrix);

            //clear displacement vector for next frame
            currentBall.clearDisplacementVector();
        }
    }

    public void drawObjects(){

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //Draw all drawable objects
        for (Drawable object : allDrawableObjects) {
            if (shouldObjectBeDrawn(object)) {
                object.draw(mVPMatrix);
            }
        }

    }

    private boolean shouldObjectBeDrawn(Drawable object){

        //certain objects have cases where they should not be drawn.
        //here we go through these specific cases
        switch (object.getType()) {

            //----BALL----
            // Balls should not be drawn if they are not active.
            case GameState.INTERACTABLE_BALL:
                Ball currentBall = (Ball) object;
                if (!currentBall.isBallActive()){
                    return false;
                }
                break;

            //----FIRING AIDS
            // This is the ghost ball and the outer selection ring that appears
            // while you still have balls remaining. They should not be drawn
            // if the current active ball is the last ball.
            case GameState.DRAWABLE_GHOST_CIRCLES:
                if (mCurrentActiveBallID == mAllBalls.size()) {
                    return false;
                }
                break;


            //----VELOCITY ARROW
            // This is another firing aid. More specifically than the above ones though,
            // it should only be drawn when the flag mIsVelocityArrowActive is true.
            case GameState.DRAWABLE_VELOCITY_ARROW:
                if ((!mIsVelocityArrowActive) || (mCurrentActiveBallID == mAllBalls.size())){
                    return false;
                }
                break;


            //-----BALLS REMAINING ICON
            // This is an icon/counter displaying the number of balls remaining. Whether each
            // icon should be drawn is determined based on the index of that icon and comparing
            // it to the current active ball ID.
            case GameState.DRAWABLE_BALLS_REMAINING:
                BallsRemainingIcon currentIcon = (BallsRemainingIcon) object;
                if ( (mAllBalls.size() - mCurrentActiveBallID)  <= currentIcon.getIndex()) {
                    return false;
                }
                break;

        }

        //If we passed all the checks, it is ok to draw this object.
        return true;
    }


    public void updateVPMatrix(float[] VPMatrix){
        mVPMatrix = VPMatrix;
    }

    public void setContext(Context context){
        mActivityContext = context;
    }

    private void updateFPSinfo(){

        if (GameState.showFPS == false){
            return;
        }

        if (mPrevTime != 0) {
            float currentTime = System.nanoTime();
            float timeLastFrame = currentTime - mPrevTime;
            System.out.println("Length of last frame: In GameEngine: " + timeLastFrame / 1000000);
            timesPerFrame[mFrameCount % 10] = timeLastFrame;
            sumTotal = sumTotal + timeLastFrame;
            mFrameCount++;
            mPrevTime = currentTime;
            if (mFrameCount % 10 == 0){
                System.out.println("Frames per second: " + calculateFPS());
            }
        } else {
            mPrevTime = System.nanoTime();
        }
    }

    private float calculateFPS(){
        float sumTime = 0;
        float maxNum = timesPerFrame[0];
        float minNum = timesPerFrame[0];

        for (int index = 0; index < 10; index++){
            sumTime = sumTime + timesPerFrame[index];
            if (timesPerFrame[index] > maxNum){
                maxNum = timesPerFrame[index];
            }
            if (timesPerFrame[index] < minNum){
                minNum = timesPerFrame[index];
            }
        }

        float discrepancy = ((maxNum - minNum) / maxNum);
        System.out.println("Discrepancy between frames: " + discrepancy);
        discrepancySum = discrepancySum + discrepancy;
        discrepancyCounter++;

        float avgTimeNanos = sumTime / 10;
        float avgTimeSecs = avgTimeNanos / 1000000000;

        mLastTenFrameAvg = avgTimeNanos / 1000000;

        return (1/avgTimeSecs);
    }

    private void showFinalAvgFPS(){
        if (GameState.showFPS == false){
            return;
        }

        float avgFPS = 1 / ((sumTotal / mFrameCount) / 1000000000);
        System.out.println("!!!FINAL AVG FPS: " + avgFPS);
        System.out.println("!!!FINAL AVG DISCREPANCY: " + (discrepancySum / discrepancyCounter));
    }

    public boolean isLevelActive(){
        if (mLevelActive){
            return true;
        } else {
            return false;
        }
    }

    public float getFrameSize(){

        if (!GameState.VARIABLE_FRAME_SIZE) {
            return GameState.FRAME_SIZE;

        } else {

            float frameSize;

            if (mFrameCount == 0) {
                frameSize = 0.5f;
                mFrameSizeHistory[mFrameCount % 3] = frameSize;
                return frameSize;

            } else if (mFrameCount < 3) {
                frameSize = (System.nanoTime() - mPrevTime) / 100000000;
                mFrameSizeHistory[mFrameCount % 3] = frameSize;
                return frameSize;
            } else {
                frameSize = (System.nanoTime() - mPrevTime) / 100000000;
                mFrameSizeHistory[mFrameCount % 3] = frameSize;
                return ((mFrameSizeHistory[0] + mFrameSizeHistory[1] + mFrameSizeHistory[2]) / 3);
            }
        }

    }

    public float getAvgFrameLength(){
        return mLastTenFrameAvg;
    }

    public boolean hasLevelBeenRendered(){
        return mInitialRender;
    }

    public void disableVelocityArrow(){
        mIsVelocityArrowActive = false;
    }

    public void redrawArrow(float xChange, float yChange){

        if (mVelocityArrow==null){
            return;
        }

        mIsVelocityArrowActive = true;

        float angle = GameState.calculateFiringAngle(xChange, yChange);
        float height = GameState.calculateFiringVelocity(xChange,yChange);

        if (xChange > 0){
            angle = (float) (3.1415 / 2) - angle;
        } else {
            angle = (float) -(3.1415 / 2) + angle;
        }

        float[] newCoords = GameState.updateVelocityArrow(angle, height);

        mVelocityArrow.setCoords(newCoords);
    }

    public int getScore(){
        return currentScore;
    }

    // This function is used before the level is activated (when the ball is fired)
    // so the player can see the obstacles moving.
    public void advanceMovingObstacles() {
        for (MovingObstacle currentObstacle : allMovingObstacles){
            currentObstacle.resetAABB(); //reset AABB
            currentObstacle.moveByFrame(GameState.FRAME_SIZE); //move forward to the collision time
            currentObstacle.updatePrevAABB();  //update saved value
        }
    }



}
