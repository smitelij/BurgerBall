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

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Interactable extends Drawable {

    //All for AABB
    protected float mMinXCoord;
    protected float mMaxXCoord;
    protected float mMinYCoord;
    protected float mMaxYCoord;

    //Coordinates represented as an intuitive PointF array
    protected PointF[] m2dCoordArray;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Interactable(float[] borderCoords, int texturePointer) {

        super(borderCoords,texturePointer);

        set2dCoordArrayFromFullCoords();
        setupAABB();

    }

    private void setupAABB(){
        float currentXMax = 0.0f;
        float currentYMax = 0.0f;
        float currentXMin = GameState.FULL_HEIGHT;
        float currentYMin = GameState.FULL_HEIGHT;

        //get AABB for X & Y coordinates
        for(int i=0;i < m2dCoordArray.length; i++){

            //for x
            if (m2dCoordArray[i].x > currentXMax)
                currentXMax = m2dCoordArray[i].x;
            if (m2dCoordArray[i].x < currentXMin)
                currentXMin = m2dCoordArray[i].x;

            //for y
            if (m2dCoordArray[i].y > currentYMax)
                currentYMax = m2dCoordArray[i].y;
            if (m2dCoordArray[i].y < currentYMin)
                currentYMin = m2dCoordArray[i].y;
        }

        mMinXCoord = currentXMin;
        mMaxXCoord = currentXMax;

        mMinYCoord = currentYMin;
        mMaxYCoord = currentYMax;

    }

    public void updateAABB(float xChange, float yChange){
        mMinXCoord = mMinXCoord + xChange;
        mMaxXCoord = mMaxXCoord + xChange;
        mMinYCoord = mMinYCoord + yChange;
        mMaxYCoord = mMaxYCoord + yChange;
    }

    public PointF[] get2dCoordArray(){
        return m2dCoordArray;
    }

    private void set2dCoordArrayFromFullCoords(){
        int numberOfCoords = mBorderCoords.length / 3;
        PointF[] coords = new PointF[numberOfCoords];

        for (int i = 0; i < numberOfCoords; i++){
            PointF currentCoord = new PointF(mBorderCoords[i*3], mBorderCoords[(i*3) + 1]);
            coords[i] = currentCoord;
        }

        m2dCoordArray = coords;
    }

    public float getMinX(){
        return mMinXCoord;
    }

    public float getMaxX(){
        return mMaxXCoord;
    }

    public float getMinY(){
        return mMinYCoord;
    }

    public float getMaxY(){
        return mMaxYCoord;
    }

    //TODO Move this to just Ball class?
    public PointF getCenter(){

        PointF center;
        float xCenter = (mMaxXCoord + mMinXCoord) / 2;
        float yCenter = (mMaxYCoord + mMinYCoord) / 2;

        center = new PointF(xCenter, yCenter);

        return center;
    }


}