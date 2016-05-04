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
package com.example.eli.myapplication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Ball extends Interactable{


    public float[] mModelMatrix = new float[16];
    private float mRadius;
   // private float[] mVelocity = new float[2];
    private PointF mVelocity;
    private float[] mPrevAABB = new float[4];
    private PointF mPrevVelocity;

    float color[] = { 0.6f, 0.75f, 0.6f, 0.5f };
    private int mID;
    private boolean isActive;
    private PointF mDisplacementVector = new PointF(0f, 0f);


    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Ball(float[] borderCoords, PointF velocity, float radius, float[] color) {

        //TODO AABB is initialized here, and using the same framework as for Polygons, which does a lot of unecessary computations for a ball object.
        super(borderCoords, GameState.OBSTACLE_BALL);
        setColor(color);
        updatePrevAABB();

        //shouldn't need this once ball is moved to Ball class
        Matrix.setIdentityM(mModelMatrix, 0);

        mVelocity = velocity;
        mRadius = radius;
        mID = GameState.getNextBallID();
        isActive = true;

    }

    public void moveBallByFrame(float percentOfFrame){
        updateAABB(mVelocity.x * percentOfFrame, mVelocity.y * percentOfFrame);
    }

    public void updateAABB(){
        mMinXCoord = mMinXCoord + mVelocity.x;
        mMaxXCoord = mMaxXCoord + mVelocity.x;

        mMinYCoord = mMinYCoord + mVelocity.y;
        mMaxYCoord = mMaxYCoord + mVelocity.y;
    }


    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        super.draw(mvpMatrix);
        updatePrevAABB();
    }



    public void setColor(float[] vertexColors){
        color = vertexColors;

    }

    public float getColor(int vertex){
        return color[vertex];
    }


    public PointF getVelocity(){
        return mVelocity;
    }

    public float getXVelocity(){
        return mVelocity.x;
    }

    public float getYVelocity(){
        return mVelocity.y;
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

    public int getID(){
        return mID;
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


}