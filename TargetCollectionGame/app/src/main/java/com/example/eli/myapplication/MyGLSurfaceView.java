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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private final int mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final int mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    private float mResponseRadius;
    private float mResponseRange;
    private PointF mResponseCenter;
    private boolean mFiringBall;
    private GameState mGame;

    public MyGLSurfaceView(Context context, GameState game) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(game, context);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(mRenderer);

        calculateResponseInfo();
        mGame = game;
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float xDistance = x - mResponseCenter.x;
                float yDistance = y - mResponseCenter.y;

                float distance = (float) Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));

                System.out.println("DISTANCE: " + distance);
                System.out.println("mResponseRange: " + mResponseRange);

                if (distance <= mResponseRange) {
                    //mRenderer.slowMoFlip();

                    mFiringBall = true;
                    mPreviousX = x;
                    mPreviousY = y;
                }

                return true;

            case MotionEvent.ACTION_UP:

                if (mFiringBall){

                    float xChange = x - mPreviousX;
                    float yChange = y - mPreviousY;

                    PointF initialVelocity = calculateInitialVelocity(xChange,yChange);
                    mGame.activateBall(initialVelocity);
                }

                return true;

        }

        return true;
    }

    private void calculateResponseInfo(){
        mResponseRadius = mWidth * 0.2f;
        mResponseRange = (float) Math.sqrt((mResponseRadius)*(mResponseRadius) + (mResponseRadius)*(mResponseRadius));

        float xCoord = mWidth / 2;
        float yCoord = mHeight;

        mResponseCenter = new PointF(xCoord,yCoord);
    }

    //TODO should be moved to GameEngine/State eventually
    private PointF calculateInitialVelocity(float xChange, float yChange){
        xChange = -xChange;  //flip so it goes in the correct x direction

        float xPercent = (xChange / mResponseRadius);
        float yPercent = (yChange / mResponseRadius);

        if (xPercent > 1){
            xPercent = 1;
        } else if (xPercent < -1){
            xPercent = -1;
        }

        if(yPercent > 1){
            yPercent = 1;
        } else if (yPercent < 0.1){
            yPercent = 0.1f;
        }


        float initialXVelocity = xPercent * GameState.MAX_INITIAL_X_VELOCITY;
        float initialYVelocity = yPercent * GameState.MAX_INITIAL_Y_VELOCITY;

        System.out.println("initial x velocity: " + initialXVelocity);
        System.out.println("intial y velocity: " + initialYVelocity);

        return new PointF(initialXVelocity,initialYVelocity);
    }


}
