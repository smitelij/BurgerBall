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

import com.example.eli.myapplication.Resources.GameState;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Ball extends Interactable implements Movable {

    public enum ballStatus {INACTIVE, ACTIVE, STOPPED, ROLLING}

    private float[] mModelMatrix = new float[16];
    private float[] mModelProjectionMatrix = new float[16];
    private float mRadius;
   // private float[] mVelocity = new float[2];
    private PointF mVelocity;
    private PointF mNewVelocity = new PointF(0f,0f);
    private float[] mPrevAABB = new float[4];
    private float currentRotation = 0f;
    private float currentSpin = 0f;

    private PointF mDisplacementVector = new PointF(0f, 0f);
    private ballStatus ballState;

    //used for collision detection, tracks whether we have advanced this ball yet for the current frame
    boolean mHasMovedForward;


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

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     * PARAMS : mvpMatrix - not used
     */
    public void draw(float[] mvpMatrix) {
        //Each ball should keep its own copy of the model projection matrix to be drawn with
        super.draw(mModelProjectionMatrix);
        updatePrevAABB();
        currentRotation = (currentRotation + currentSpin) % 360;
    }

    //Fake method to appease Movable interface. The real 'moveByFrame' became too complex
    // and so was moved to the BallEngine class. Call that one if you want to moveByFrame.
    public void moveByFrame(float timeStep) {
        //Call BallEngine.moveByFrame!
    }

    public void setVelocity(PointF newVelocity){

        System.out.println("setting velocity: " + newVelocity);
        mVelocity = newVelocity;
    }

    public float getRadius() { return mRadius; }

    public void resetAABB(){
        mMinXCoord = mPrevAABB[0];
        mMaxXCoord = mPrevAABB[1];

        mMinYCoord = mPrevAABB[2];
        mMaxYCoord = mPrevAABB[3];
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

    public boolean hasBallBeenAdvanced(){
        return mHasMovedForward;
    }

    public void clearMovedStatus(){
        mHasMovedForward = false;
    }



    //get a balls current velocity
    public PointF getVelocity(){
        return mVelocity;
    }

    //Another fake method to appease the interface.
    // I wonder if there's a better way to do things...
    public PointF getVelocity(float timeStep) {
        //Call BallEngine.getVelocity for the actual method
        return null;
    }


    //Set a new velocity for a ball (will be updated in update velocity)
    public void addNewVelocity(PointF newVelocity){
        //System.out.println("addedd new velocity: " + newVelocity.x + ";" + newVelocity.y);
        mNewVelocity.set(mNewVelocity.x + newVelocity.x, mNewVelocity.y + newVelocity.y);
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

    public boolean isBallMoving() {
        return ((ballState == ballStatus.ACTIVE) || (ballState == ballStatus.ROLLING));
    }

    public ballStatus getBallState() {
        return ballState;
    }

    public void activateBall(){
        ballState = ballStatus.ACTIVE;
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

    public void setModelMatrix(float[] modelMatrix){
        mModelMatrix = modelMatrix;
    }

    public boolean didBallCollide() {
        return (mNewVelocity.length() > 0);
    }

    public void updateVelocityWithNewVelocity() {
        setVelocity(mNewVelocity);
        mNewVelocity = new PointF(0f,0f);
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    public void addToSpin(float spinAmt) {
        currentSpin = currentSpin + spinAmt;
    }

    public void reverseSpin() {
        currentSpin = -currentSpin * 0.6f;
    }

    public void normalizeSpin() {
        currentSpin = currentSpin * 0.9f;
    }

    public void setSpin(float spin) { currentSpin = spin;}


}