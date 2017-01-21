/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.eli.myapplication.Model;

import android.graphics.PointF;
import android.opengl.Matrix;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Ball extends Interactable implements Movable {

    private enum ballStatus {INACTIVE, ACTIVE, STOPPED, ROLLING};

    private float[] mModelMatrix = new float[16];
    private float[] mModelProjectionMatrix = new float[16];
    private float mRadius;
   // private float[] mVelocity = new float[2];
    private PointF mVelocity;
    private PointF mNewVelocity = new PointF(0f,0f);
    private float[] mPrevAABB = new float[4];
    private PointF mPrevVelocity;
    private PointF mSurfaceVelocity = new PointF(0f,0f);
    private PointF mRollingAccel = new PointF(0f,0f);
    private Collision mLastCollision;
    float mRollTime = 0;

    float color[] = { 0.6f, 0.75f, 0.6f, 0.5f };
    private PointF mDisplacementVector = new PointF(0f, 0f);
    private ballStatus ballState;

    //used for collision detection, tracks whether we have advanced this ball yet for the current frame
    boolean mHasMovedForward;

    //Keeps track of how many objects this ball collided with in this frame
    //(only > 1 if multiple collisions happened at the exact same time)
    //(includes collisions where this ball is not the main ball)
    private int mNumOfBallCollisionsThisStep;
    private int mNumOfBallCollisionsThisFrame;
    private int mNumOfBoundaryCollisionsThisFrame;
    private int mNumOfSameBoundaryCollisionsThisFrame = 0;


    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Ball(float[] borderCoords, PointF velocity, float radius, int texturePointer) {

        //TODO AABB is initialized here, and using the same framework as for Polygons, which does a lot of unecessary computations for a ball object.
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_BALL);
        //setColor(color);
        updatePrevAABB();

        Matrix.setIdentityM(mModelMatrix, 0);

        mVelocity = velocity;
        mRadius = radius;
        ballState = ballStatus.INACTIVE;

    }

    public void moveByFrame(float percentOfFrame){
        PointF positionChange = calculatePositionChange(percentOfFrame);
        updateAABB(positionChange.x, positionChange.y);
    }

    public PointF calculatePositionChange(float percentOfFrame){

        if (ballState == ballStatus.ACTIVE) {

            //This isn't precisely accurate- we would need much more complicated calculations
            //to perfectly account for gravities affect on displacement. However, taking the average
            //velocity (beginning / end of frame) should be more than accurate enough for our purposes.
            PointF avgVelocity = getAvgVelocity(percentOfFrame);
            return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);

        //Otherwise, we have a rolling ball.
        } else {
            PointF avgVelocity = getAvgVelocity(percentOfFrame);
            return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);
        }
    }


    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     * PARAMS : mvpMatrix - not used
     */
    public void draw(float[] mvpMatrix) {
        //Each ball should keep its own copy of the model projection matrix to be drawn with
        super.draw(mModelProjectionMatrix);
        updatePrevAABB();
    }


    public void setColor(float[] vertexColors){
        color = vertexColors;

    }

    public float getColor(int vertex){
        return color[vertex];
    }

    public void setVelocity(PointF newVelocity){
        mVelocity = newVelocity;
    }

    public float getRadius() { return mRadius; }

    public void resetAABB(){
        mMinXCoord = mPrevAABB[0];
        mMaxXCoord = mPrevAABB[1];

        mMinYCoord = mPrevAABB[2];
        mMaxYCoord = mPrevAABB[3];
    }

    private void resetVeleocity(){
        mVelocity = mPrevVelocity;
    }

    public void updatePrevAABB(){
        mPrevAABB[0] = mMinXCoord;
        mPrevAABB[1] = mMaxXCoord;
        mPrevAABB[2] = mMinYCoord;
        mPrevAABB[3] = mMaxYCoord;
    }

    public PointF getPrevCenter(){
        PointF center;
        float xCenter = (mPrevAABB[0] + mPrevAABB[1]) / 2;
        float yCenter = (mPrevAABB[2] + mPrevAABB[3]) / 2;

        center = new PointF(xCenter, yCenter);

        return center;
    }

    public PointF getDisplacementVector(){
        return mDisplacementVector;
    }

    public void clearDisplacementVector(){
        mDisplacementVector.set(0f,0f);
    }

    public void addToDisplacementVector(PointF additionalDisplacement){
        mDisplacementVector.set(mDisplacementVector.x + additionalDisplacement.x, mDisplacementVector.y + additionalDisplacement.y);
    }

    public void setBallMoved(){
        mHasMovedForward = true;
    }

    public boolean hasBallMoved(){
        return mHasMovedForward;
    }

    public void clearMovedStatus(){
        mHasMovedForward = false;
    }

    public void clearStepCollisionHistory(){
        mNumOfBallCollisionsThisStep = 0;
    }

    public void increaseBallCollisionCounts(){
        mNumOfBallCollisionsThisStep++;
        mNumOfBallCollisionsThisFrame++;

        //mNumOfSameBoundaryCollisionsThisFrame++;
    }

    public void addObstacleCollision(Collision collision){

        mNumOfBoundaryCollisionsThisFrame++;

        if (mLastCollision == null) {
            mNumOfSameBoundaryCollisionsThisFrame++;
            mLastCollision = collision;
        }

        if (sameLastCollision(collision)) {
            mNumOfSameBoundaryCollisionsThisFrame++;
        } else {
            mNumOfSameBoundaryCollisionsThisFrame = 1;
            mLastCollision = collision;
        }
        System.out.println("number of same boundary collisions: " + mNumOfSameBoundaryCollisionsThisFrame);
    }

    private boolean sameLastCollision(Collision collision) {
        if (collision.getObstacle().equals(mLastCollision.getObstacle())) {
            if (collision.getBoundaryAxis().equals(mLastCollision.getBoundaryAxis())) {
                return true;
            }
        }
        return false;
    }

    public int getBoundaryCollisionCountThisFrame() {
        return mNumOfBoundaryCollisionsThisFrame;
    }


    public int getSameBoundaryCollisionCountThisFrame(){
        return mNumOfSameBoundaryCollisionsThisFrame;
    }

    public int getBallCollisionCountThisFrame(){
        return mNumOfBallCollisionsThisFrame;
    }

    public void clearFrameCollisionCount(){
        mNumOfSameBoundaryCollisionsThisFrame = 0;
        mNumOfBallCollisionsThisFrame = 0;
        mNumOfBoundaryCollisionsThisFrame = 0;
    }

    //get a balls current velocity
    public PointF getVelocity(){
        return mVelocity;
    }

    //Get a balls velocity after timeStep (takes into account gravity and everything else)
    public PointF getVelocity(float timeStep){

        //we should either have an active ball,
        if (ballState == ballStatus.ACTIVE) {
            return new PointF(mVelocity.x + (GameState.GRAVITY_CONSTANT.x * timeStep), mVelocity.y + (GameState.GRAVITY_CONSTANT.y * timeStep));

        //or else we have a rolling ball
        } else if (ballState == ballStatus.ROLLING){
            PointF surfaceVelocity = getSurfaceVelocity();

            //Add current velocity, rolling velocity, and moving obstacle velocity.
            // Both rolling velocity and surface velocity can be 0
            return new PointF(mVelocity.x + (mRollingAccel.x * timeStep) + surfaceVelocity.x,
                    mVelocity.y + (mRollingAccel.y * timeStep) + surfaceVelocity.y);

        //should never get here
        } else {
            return new PointF(0f,0f);
        }
    }

    /**
     * This should only be used for rolling balls.
     * @return
     */
    private PointF getSurfaceVelocity() {
        if (getLastCollision().getObstacle().getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
            // To calculate obstacle velocity, we need to calculate
            // how much of the frame we have moved
            MovingObstacle obstacle = (MovingObstacle) getLastCollision().getObstacle();
            return obstacle.getVelocity();
        }
        return new PointF(0f,0f);
    }

    public PointF getVelocityNoGravity() {
        return new PointF(mVelocity.x, mVelocity.y);
    }

    //Get the available velocity (amount that is free to be transferred) at timeStep
    public PointF getAvailableVelocity(float timeStep){
        PointF newVelocity = getVelocity(timeStep);

        return new PointF(newVelocity.x / mNumOfBallCollisionsThisStep, newVelocity.y / mNumOfBallCollisionsThisStep);
    }

    //Set a new velocity for a ball (will be updated in update velocity)
    public void addNewVelocity(PointF newVelocity){
        //System.out.println("addedd new velocity: " + newVelocity.x + ";" + newVelocity.y);
        mNewVelocity.set(mNewVelocity.x + newVelocity.x, mNewVelocity.y + newVelocity.y);
    }

    //update a balls velocity after time timeStep
    public void updateVelocityCollision(float timeStep){

        //if mNewVelocity exists, then we have updated the balls new velocity elsewhere
        if (mNewVelocity.length() > 0) {
            mVelocity.set(mNewVelocity.x, mNewVelocity.y);
            mNewVelocity.set(0f, 0f);

        //otherwise, just update based on gravity
        } else {
            updateVelocityNonCollision(timeStep);
        }
    }

    //Update the velocity based on the current acceleration
    // (gravity if an active ball, or rolling accel for a rolling ball
    public void updateVelocityNonCollision(float timeStep) {
        PointF ballVelocity = getVelocity(timeStep);
        //Subtract surface velocity here so it doesn't accumulate
        if (ballState == ballStatus.ROLLING) {
            PointF surfaceVelocity = getSurfaceVelocity();
            ballVelocity.set(ballVelocity.x - surfaceVelocity.x, ballVelocity.y - surfaceVelocity.y);
        }
        mVelocity.set(ballVelocity.x, ballVelocity.y);
    }

    //Get average velocity from current time until timeStep
    public PointF getAvgVelocity(float timeStep){
        PointF currentVelocity = getVelocity(0);
        PointF newVelocity = getVelocity(timeStep);
        PointF finalVelocity = new PointF((currentVelocity.x + newVelocity.x) / 2, (currentVelocity.y + newVelocity.y) / 2);
        System.out.println("GET BALL VELOCITY= " + finalVelocity.x + " | " + finalVelocity.y);
        return new PointF((currentVelocity.x + newVelocity.x) / 2, (currentVelocity.y + newVelocity.y) / 2);
    }

    public boolean isBallActive() {
        return (ballState == ballStatus.ACTIVE);
    }

    public boolean isBallStopped() {
        return (ballState == ballStatus.STOPPED);
    }

    public boolean isBallInactive() {
        return (ballState == ballStatus.INACTIVE);
    }

    public boolean isBallRolling() { return (ballState == ballStatus.ROLLING); }

    public ballStatus getBallState() {
        return ballState;
    }

    public void activateBall(){
        ballState = ballStatus.ACTIVE;
        clearFrameCollisionCount();

    }

    public void stopBall(){
        ballState = ballStatus.STOPPED;
    }

    public void rollingBall() {
        ballState = ballStatus.ROLLING;
    }

    public void deactivateBall() {
        ballState = ballStatus.INACTIVE;
    }

    public float[] getModelMatrix(){
        return mModelMatrix;
    }

    public void setModelProjectionMatrix(float[] modelProjectionMatrix){
        mModelProjectionMatrix = modelProjectionMatrix;
    }

    public float[] getModelProjectionMatrix() {
        return mModelProjectionMatrix;
    }

    public void setModelMatrix(float[] modelMatrix){
        mModelMatrix = modelMatrix;
    }

    public boolean didBallCollide() {
        return (mNewVelocity.length() > 0);
    }

    public Collision getLastCollision() {
        return mLastCollision;
    }

    public void setLastCollision(Collision collision) {
        mLastCollision = collision;
    }

    //This should probably be moved into game engine-
    // no reason for so much math to be in the ball class
    public void calculateRollingAccel() {

        PointF rollingVector;
        PointF collisionAxis = getLastCollision().getBoundaryAxis();
        //warning - this code will break if gravity isn't solely in the negative Y-direction
        if (collisionAxis.x > 0 ) {
            rollingVector = new PointF(collisionAxis.y, -collisionAxis.x);
        } else {
            rollingVector = new PointF(-collisionAxis.y, collisionAxis.x);
        }
        double rollingAngle = Math.atan2(rollingVector.y, rollingVector.x);
        float rollingAcceleration = (float) (0.666 * GameState.GRAVITY_CONSTANT.y * Math.sin(rollingAngle));
        PointF rollingAccelVector = new PointF(rollingAcceleration * (float) Math.cos(rollingAngle), rollingAcceleration * (float) Math.sin(rollingAngle));

        mRollingAccel = rollingAccelVector;
    }

    public void clearRollingAccel() {
        mRollingAccel = new PointF(0f,0f);
    }

    public void setInitialRollingVelocity() {

        PointF rollingVector;
        PointF collisionAxis = getLastCollision().getBoundaryAxis();
        //warning - this code will break if gravity isn't solely in the negative Y-direction
        if (collisionAxis.x > 0 ) {
            rollingVector = new PointF(collisionAxis.y, -collisionAxis.x);
        } else {
            rollingVector = new PointF(-collisionAxis.y, collisionAxis.x);
        }
        PointF rollingVectorNormal = new PointF(rollingVector.x / rollingVector.length(), rollingVector.y / rollingVector.length());
        PointF currentVelocity = getVelocity(0);
        float totalVelocity = getVelocity().length();
        PointF directionalVelocity;
        if (currentVelocity.y > 0) {
            directionalVelocity = new PointF(-rollingVectorNormal.x * totalVelocity, -rollingVectorNormal.y * totalVelocity);
        } else {
            directionalVelocity = new PointF(rollingVectorNormal.x * totalVelocity, rollingVectorNormal.y * totalVelocity);
        }

        //Before a ball starts rolling, it undergoes a number of collisions each resulting in loss due to elasticity.
        //Here, we add that loss back in.
        //Add an extra 1/2 due to frame straddling possibility (some collisions occurred in the previous frame).
        int affectedFrames = GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME + (GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME / 2);
        float elasticLoss = (float) Math.pow(GameState.ELASTIC_CONSTANT, affectedFrames);
        PointF newVelocity = new PointF(directionalVelocity.x / elasticLoss, directionalVelocity.y / elasticLoss);
        setVelocity(newVelocity);
    }

    public void calculateRollTime() {
        PointF boundaryAxis = getLastCollision().getBoundaryAxis();
        PointF[] obstacleCoords = getLastCollision().getObstacle().get2dCoordArray();

        PointF vertexA = new PointF();
        PointF vertexB = new PointF();
        for (int index = 0; index < obstacleCoords.length; index++) {
            //NOTE - THIS IS THE SAME CODE AS in collision detection so maybe should move it to a shared location
            //We need to make a line between two vertexes
            int vertexAIndex = index;
            int vertexBIndex = (index + 1) % obstacleCoords.length; //We need to wrap back to the first vertex at the end, so use modulus

            vertexA = obstacleCoords[vertexAIndex];
            vertexB = obstacleCoords[vertexBIndex];

            //formula to find the normal vector from a line is (-y, x)
            float xComponent = -(vertexB.y - vertexA.y);
            float yComponent = (vertexB.x - vertexA.x);

            //create vector and normalize
            PointF normalAxis = new PointF(xComponent, yComponent);
            float normalAxisLength = normalAxis.length();
            normalAxis.set(normalAxis.x / normalAxisLength, normalAxis.y / normalAxisLength);

            if (normalAxis.equals(boundaryAxis.x, boundaryAxis.y)) {
                break;
            }
        }

        //Make sure vertices are oriented correctly (A is above B)
        if (vertexA.y < vertexB.y) {
            PointF temp = new PointF(vertexA.x, vertexA.y);
            vertexA = vertexB;
            vertexB = temp;
        }

        PointF ballCenter = getCenter();
        PointF ballPointOnLine = projectPointOntoLine(vertexA, vertexB, ballCenter);
        PointF remainingLength = new PointF(vertexB.x - ballPointOnLine.x, vertexB.y - ballPointOnLine.y);

        double quadA = mRollingAccel.length() / 2;
        double quadB = getVelocity().length();
        double quadC = -remainingLength.length();

        double squareRoot = Math.sqrt((quadB * quadB) - (4*quadA*quadC));
        double result1 = (-quadB + squareRoot) / (2*quadA);
        double result2 = (-quadB - squareRoot) / (2*quadA);

        float rollTime;
        if ((result1 < 0) && (result2 < 0)) {
            rollTime = 0;
        } else if (result1 < 0) {
            rollTime = (float) result2;
        } else {
            rollTime = (float) result1;
        }
        rollTime = rollTime * 1.05f;
        mRollTime = rollTime;
    }

    private PointF projectPointOntoLine(PointF vertexA, PointF vertexB, PointF pointToProject) {
        PointF pointVector = new PointF(pointToProject.x - vertexA.x, pointToProject.y - vertexA.y);
        PointF line = new PointF(vertexB.x - vertexA.x, vertexB.y - vertexA.y);
        PointF lineUnit = new PointF(line.x / line.length(), line.y / line.length());
        float scalar = GameState.dotProduct(pointVector, lineUnit);
        PointF pointOffset = new PointF(lineUnit.x * scalar, lineUnit.y * scalar);
        PointF finalPoint = new PointF(vertexA.x + pointOffset.x, vertexA.y + pointOffset.y);
        return finalPoint;
    }

    public void decreaseRollTime(float frameLength) {
        mRollTime = mRollTime - frameLength;
    }

    public float getRemainingRollTime() {
        return mRollTime;
    }


}