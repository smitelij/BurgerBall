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
package com.example.eli.myapplication.Controller;

import android.content.Context;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.eli.myapplication.R;
import com.example.eli.myapplication.Resources.CommonFunctions;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    private boolean mFiringBall;
    private boolean firstBall = true;
    private GameEngine mGame;

    public MyGLSurfaceView(Context context, GameEngine game) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(3);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(game, context);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(mRenderer);
        requestRender();

        mGame = game;
    }

    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        requestRender();

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (! mGame.areBallsAvailable()) {
                    return false;
                }

                mGame.playBallPullBack();

                PointF responseCenter = mGame.getInitialBallCenter();
                if (responseCenter == null) {
                    return false;
                }
                PointF androidCenter = CommonFunctions.calculateAndroidBallCenter(responseCenter);
                float responseRange = CommonFunctions.getResponseRange();

                float xDistance = x - androidCenter.x;
                float yDistance = y - androidCenter.y;

                float distance = (float) Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));

                if (distance <= responseRange) {
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

                    PointF initialVelocity = CommonFunctions.calculateInitialVelocity(xChange,yChange);

                    mGame.activateBall(initialVelocity);
                    if (firstBall) {
                        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                        firstBall = false;
                    }

                    mGame.disableVelocityArrow();

                }

                return true;

            case MotionEvent.ACTION_MOVE:

                if (mFiringBall){

                    float xChange = x - mPreviousX;
                    float yChange = y - mPreviousY;

                    mGame.redrawArrow(xChange,yChange);

                }

                return true;

        }

        return true;
    }



}
