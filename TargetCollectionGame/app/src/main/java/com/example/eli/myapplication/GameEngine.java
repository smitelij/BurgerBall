package com.example.eli.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.util.ArrayList;

/**
 * This class contains code for the main game loop. First, loadLevel should be called from
 * MyGLRenderer.onSurfaceCreate, and after that advanceFrame and drawObjects are
 * called every GUI frame from MyGLRenderer.onDrawFrame.
 */
public class GameEngine {

    //All active objects that need to be collision checked are added to this collection
    private ArrayList<Interactable> allInteractableObjects;

    //All active objects that only need to be drawn (not collision checked) are added to this collection
    private ArrayList<Drawable> allDrawableObjects;

    private ScoreDigits[] scoreDigits = new ScoreDigits[5];

    private ArrayList<Ball> mAllBalls;     //Collection of all balls
    private ArrayList<Ball> mActiveBalls;  //Collection of only active balls

    private ArrayList<Polygon> mBorders;    //Collection of outer borders
    private ArrayList<Polygon> mObstacles;  //Collection of other (inner) boundaries

    private ArrayList<Target> mTargets;  //Collection of targets

    private float[] mVPMatrix = new float[16];  //View-Projection matrix to place objects into actual device coordinates
    private static Context mActivityContext;

    private int mTotalBalls;  //Number of total balls available this level

    private int mCurrentActiveBallID = 0;  //The current active ball index

    //can't add a new ball while in the middle of a frame, may cause a concurrent modification error.
    //So we'll setup flags to determine whether we are in the middle of a frame, and whether a ball is waiting.
    private boolean mBallCollectionInUse;  //True while the ball collection is in use
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

    private Circle selectionCircle;
    private Circle ghostBall;
    private Circle velocityArrow;
    private boolean mIsVelocityArrowActive;

    private int[] digitTextures = new int[10];
    private int currentScore = 100000;

    private int mChapter;
    private int mLevel;

    private Activity mParentActivity;


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
    // (eventually this will probably take a parameter to specify which level)
    public void loadLevel(){

        //Initialize the level
        Level currentLevel = new Level(mLevel);
        mTotalBalls=currentLevel.getNumOfBalls(); //grab the number of total balls

        initializeBalls();  //Initialize the balls
        loadBoundaries();  //Load outer boundaries (don't need a reference to current level, because currently outer boundaries are always the same)
        loadObstacles(currentLevel);  //Load level specific boundaries
        loadTargets(currentLevel);  //Load level specific targets
        initializeDrawables();
        initializeActiveObjects();  //Add all active objects to the active-objects collection
    }

    private void initializeDrawables(){

        //Selection circle
        selectionCircle = new Circle(GameState.getSelectionCircleCoords(), loadGLTexture(GameState.TEXTURE_SELECTION_CIRCLE));
        selectionCircle.setAlpha(0.3f);

        //Ghost ball
        ghostBall = new Circle(GameState.getInitialBallCoords(), loadGLTexture(GameState.TEXTURE_BALL));
        ghostBall.setAlpha(0.3f);

        //Velocity arrow
        velocityArrow = new Circle(GameState.getInitialBallCoords(), loadGLTexture(GameState.TEXTURE_SELECTION_ARROW));
        velocityArrow.setAlpha(0.3f);

        initializeScoreDigits();
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
            scoreDigits[index] = new ScoreDigits(coords, digitTextures[9]);
        }

    }

    private void loadDigitTextures(){
        digitTextures[0] = loadGLTexture(GameState.TEXTURE_DIGIT_0);
        digitTextures[1] = loadGLTexture(GameState.TEXTURE_DIGIT_1);
        digitTextures[2] = loadGLTexture(GameState.TEXTURE_DIGIT_2);
        digitTextures[3] = loadGLTexture(GameState.TEXTURE_DIGIT_3);
        digitTextures[4] = loadGLTexture(GameState.TEXTURE_DIGIT_4);
        digitTextures[5] = loadGLTexture(GameState.TEXTURE_DIGIT_5);
        digitTextures[6] = loadGLTexture(GameState.TEXTURE_DIGIT_6);
        digitTextures[7] = loadGLTexture(GameState.TEXTURE_DIGIT_7);
        digitTextures[8] = loadGLTexture(GameState.TEXTURE_DIGIT_8);
        digitTextures[9] = loadGLTexture(GameState.TEXTURE_DIGIT_9);
    }

    //----------------
    //This function initializes all the balls that will be used for this level.
    // Although they are not drawn immediately (or activated), they need to be created
    // from the beginning so that OpenGL has a reference to them.
    //
    private void initializeBalls(){
        mAllBalls = new ArrayList<>();
        mActiveBalls = new ArrayList<>();

        //Get initial ball coordinates
        float[] newBallCoords = GameState.getInitialBallCoords();

        //create balls and add them to collection
        for(int index=0; index < mTotalBalls; index++) {

            Ball ball = new Ball(newBallCoords, new PointF(0f, 0f), GameState.ballRadius, loadGLTexture(GameState.TEXTURE_BALL));
            mAllBalls.add(ball);
        }

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
                mActiveBalls.add(ball);
                allInteractableObjects.add(ball);
                mCurrentActiveBallID++;
                mLevelActive = true;
                mBallWaiting = false; //clear the flag in case it got actiated
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
        //1: The ball collection must not be in use
        //2: The firing zone must be clear
        if ((mBallCollectionInUse)||(!isFiringZoneClear(newBall))){
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

        lockBalls();

        for (Ball currentBall : mActiveBalls){
            PointF currentBallCenter = currentBall.getCenter();

            PointF distanceVector = new PointF(newBallCenter.x - currentBallCenter.x, newBallCenter.y - currentBallCenter.y);
            float distance = distanceVector.length();

            //If the distance between them is less than the radius's, then this ball is in the firing zone.
            if (distance < (newBall.getRadius() + currentBall.getRadius())){
                return false;
            }
        }

        unlockBallsAlreadyWaiting();

        //if we made it through all balls, and none collided with the new ball, then the firing zone is clear.
        return true;
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
        allInteractableObjects = new ArrayList<>();

        //No active balls yet, so we only need to add borders
        for (Polygon currentBorder : mBorders){
            allInteractableObjects.add(currentBorder);
        }

        for (Polygon obstacle : mObstacles){
            allInteractableObjects.add(obstacle);
        }

        for (Target target : mTargets){
            allInteractableObjects.add(target);
        }
    }

    //Main method called by MyGLRenderer each frame
    public void advanceFrame(){


        mCurrentFrameSize = getFrameSize();

        updateFPSinfo();

        float timeElapsed = 0f;

        //repeat until 1 whole 'frame' has been moved.
        //If there is a collision, we will only advance up until the collision point,
        //and then repeat until we reach the end of the frame.
        while (timeElapsed < mCurrentFrameSize) {

            float timeStep = mCurrentFrameSize - timeElapsed;
            //System.out.println("time step: " + timeStep);

            //initialize collision detection engine
            CollisionDetection CD = new CollisionDetection();

            //Do collision detection
            collisionDetection(CD, timeStep);

            //Handle collisions (update velocities / displacements as necessary)
            //Also handles NO collisions
            timeElapsed = collisionHandling(CD, timeStep, timeElapsed);

        }

        updateScore();
        testForDeactivatingBalls();
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

        System.out.println("current Score: " + currentScore);

        int scoreDigit1 = currentScore % 10;
        int scoreDigit10 = (currentScore % 100) / 10;
        int scoreDigit100 = (currentScore % 1000) / 100;
        int scoreDigit1000 = (currentScore % 10000) / 1000;
        int scoreDigit10000 = (currentScore % 100000) / 10000;

        scoreDigits[0].updateTexture(digitTextures[scoreDigit1]);
        scoreDigits[1].updateTexture(digitTextures[scoreDigit10]);
        scoreDigits[2].updateTexture(digitTextures[scoreDigit100]);
        scoreDigits[3].updateTexture(digitTextures[scoreDigit1000]);
        scoreDigits[4].updateTexture(digitTextures[scoreDigit10000]);

    }

    private void testForDeactivatingBalls(){

        ArrayList<Ball> deactivationList = new ArrayList<>();

        lockBalls();

        for (Ball currentBall : mActiveBalls){

            if (currentBall.getPerFrameCollisionCount() > (mCurrentFrameSize * GameState.DEACTIVATION_CONSTANT)){
                deactivationList.add(currentBall);
            } else {
                currentBall.clearFrameCollisionCount();
            }
        }

        unlockBalls();

        for (Ball deadBall : deactivationList){
            mActiveBalls.remove(deadBall);
            allInteractableObjects.remove(deadBall);
        }

    }

    private void endLevelChecks(){
        if (! mLevelActive){
            return;
        }

        if ((mActiveBalls.size() == 0) || (mTargets.size() == 0)){
            endLevel();
        }
    }

    private void endLevel(){
        mLevelActive = false;
        showFinalAvgFPS();

        mParentActivity.finish();

    }

    /**
     * This method starts the collision detection process for each active ball.
     * To do this, we advance the ball to its next location (calculated using time step and velocity),
     * and then test this new location against all currently active objects.
     */
    private void collisionDetection(CollisionDetection CD, float timeStep) {

        /*try {
                Thread.sleep(500);
            } catch (Exception e) {
            }*/

        //Need to lock ball collection to prevent new balls from being
        //added while we are accessing the collection
        lockBalls();

        //go through all active balls
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

            //Do a first test on their bounding boxes
            //TODO should the bounding boxes be increased by 1 on all sides? should we slow down time step if a ball is moving too quickly?
            if (CD.coarseCollisionTesting(currentBall, curObject)) {

                //There may have been a collision, further testing is necessary.
                //Any collisions detected will be added to a collection in CD (mCollisions).
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
        ArrayList<Collision> firstCollisions = CH.getFirstCollision();

        //no trajectory-affecting collisions
        if (firstCollisions == null) {
            //Move all balls forward by timestep
            handleNoCollisionsForBalls(timeStep);

            //Any target collisions will be valid, since there were no trajectory affecting collisions
            handleTargetCollisions(CH, timeStep);

            timeElapsed = mCurrentFrameSize;

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
            allInteractableObjects.remove(target);
        }
    }

    public void drawObjects(){

        //System.out.println("objects being drawn.");

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        drawInteractables();

        drawNonInteractables();

    }

    private void drawInteractables(){

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

    private void drawNonInteractables(){

        if (mCurrentActiveBallID != mAllBalls.size()){
            selectionCircle.draw(mVPMatrix);
            ghostBall.draw(mVPMatrix);

            if (mIsVelocityArrowActive) {
                velocityArrow.draw(mVPMatrix);
            }
        }

        for (ScoreDigits currentDigit : scoreDigits){
            currentDigit.draw(mVPMatrix);
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
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

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

    private void unlockBallsAlreadyWaiting(){
        mBallCollectionInUse = false;
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

        mIsVelocityArrowActive = true;

        float angle = GameState.calculateFiringAngle(xChange, yChange);
        float height = GameState.calculateFiringVelocity(xChange,yChange);

        if (xChange > 0){
            angle = (float) (3.1415 / 2) - angle;
        } else {
            angle = (float) -(3.1415 / 2) + angle;
        }

        float[] newCoords = GameState.updateVelocityArrow(angle, height);

        velocityArrow.setCoords(newCoords);
    }

    public int getScore(){
        return currentScore;
    }



}
