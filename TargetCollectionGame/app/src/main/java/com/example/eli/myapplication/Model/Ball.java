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

    private enum ballStatus {INACTIVE, ACTIVE, STOPPED};

    private float[] mModelMatrix = new float[16];
    private float[] mModelProjectionMatrix = new float[16];
    private float mRadius;
   // private float[] mVelocity = new float[2];
    private PointF mVelocity;
    private PointF mNewVelocity = new PointF(0f,0f);
    private float[] mPrevAABB = new float[4];
    private PointF mPrevVelocity;
    private PointF mLastCollisionAxis = new PointF(0f,0f);

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
    private int mNumOfBoundaryCollisionsThisFrame = 0;


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

        //This isn't precisely accurate- we would need much more complicated calculations
        //to perfectly account for gravities affect on displacement. However, taking the average
        //velocity (beginning / end of frame) should be more than accurate enough for our purposes.
        PointF avgVelocity = getAvgVelocity(percentOfFrame);
        return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);
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

        //mNumOfBoundaryCollisionsThisFrame++;
    }

    public void addBoundaryCollision(Collision collision){

        if (mLastCollisionAxis.equals(collision.getBoundaryAxis())) {
            mNumOfBoundaryCollisionsThisFrame++;
        }
        mLastCollisionAxis = collision.getBoundaryAxis();
    }


    public int getBoundaryCollisionCountThisFrame(){
        return mNumOfBoundaryCollisionsThisFrame;
    }

    public int getBallCollisionCountThisFrame(){
        return mNumOfBallCollisionsThisFrame;
    }

    public void clearFrameCollisionCount(){
        mNumOfBoundaryCollisionsThisFrame = 0;
        mNumOfBallCollisionsThisFrame = 0;
    }

    //get a balls current velocity
    public PointF getVelocity(){
        return mVelocity;
    }

    //Get a balls velocity after timeStep (calculates gravity)
    public PointF getVelocity(float timeStep){
        return new PointF(mVelocity.x + (GameState.GRAVITY_CONSTANT.x * timeStep), mVelocity.y + (GameState.GRAVITY_CONSTANT.y * timeStep));
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
            PointF newVelocity = getVelocity(timeStep);
            mVelocity.set(newVelocity.x, newVelocity.y);
        }
    }

    public void updateVelocityGravity(float timeStep) {
        PointF newVelocity = getVelocity(timeStep);
        mVelocity.set(newVelocity.x, newVelocity.y);
    }

    //Get average velocity from current time until timeStep
    public PointF getAvgVelocity(float timeStep){
        PointF newVelocity = getVelocity(timeStep);
        return new PointF((newVelocity.x + mVelocity.x) / 2, (newVelocity.y + mVelocity.y)/2);
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

    public void activateBall(){
        ballState = ballStatus.ACTIVE;
        clearFrameCollisionCount();

    }

    public void stopBall(){
        ballState = ballStatus.STOPPED;
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

    public void setModelMatrix(float[] modelMatrix){
        mModelMatrix = modelMatrix;
    }

    public boolean didBallCollide() {
        return (mNewVelocity.length() > 0);
    }


}