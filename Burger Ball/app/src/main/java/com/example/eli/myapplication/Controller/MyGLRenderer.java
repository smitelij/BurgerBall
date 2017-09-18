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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.eli.myapplication.Resources.GameState;
import com.example.eli.myapplication.Resources.GameState.GameStatus;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private GameEngine mGame;
    private static final String TAG = "MyGLRenderer";

    // mVPMatrix is an abbreviation for "View Projection Matrix"
    private final float[] mVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private long mEndTime;
    private long mStartTime = 0;
    private long[] sleepTimes = new long[10];
    private int frameCount = 0;
    private long mFrameRateCap = (1000 / GameState.FRAME_RATE_CAP_SIZE);


    private float mAngle;
    private int mSlowMo = 0;


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Disable depth testing -- we're 2D only.
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        //Disable backface culling
        GLES30.glDisable(GLES30.GL_CULL_FACE);

        // Enable transparency
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        //REALLY IMPORTANT to keep here!
        //it seems that the drawable objects must be initialized no earlier than this point
        //or else openGL has no reference to them.
        mGame.loadLevel();
        //mGame.drawObjects();

        setVPMatrix();

        mGame.drawObjects();

    }

    public void setVPMatrix() {
        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 10, 10, -3, 10f, 10f, 0f, 0f, 1.0f, 0.0f);

        //TODO currently this only uses the projection matrix. is this bad?
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        mGame.updateVPMatrix(mProjectionMatrix);
    }


    @Override
    public void onDrawFrame(GL10 unused) {

        GameStatus levelStatus = mGame.getCurrentLevelStatus();

        switch (levelStatus) {
            case BEFORE_PLAY:
                mGame.advanceMovingObstacles();
                mGame.drawObjects();
                break;

            case ACTIVE:
                mGame.advanceFrame();
                mGame.drawObjects();

                if (GameState.FRAME_RATE_CAP){
                    capFrameRate();
                }
                break;

            case POST_PLAY:
                mGame.drawObjects();
                mGame.postPlaySequence();
                break;
        }

    }


    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        int x, y, viewWidth, viewHeight;

        float STATIC_RATIO = GameState.FULL_WIDTH / GameState.FULL_HEIGHT;

        if (width > (int) height * STATIC_RATIO) {
            //too wide, restrict width
            viewWidth = (int) (height * STATIC_RATIO);
            viewHeight = height;
        } else {
            //too tall, restrict height
            viewHeight = (int) (width / STATIC_RATIO);
            viewWidth = width;
        }

        x = (width - viewWidth) / 2;
        y = (height - viewHeight) / 2;

        GLES30.glViewport(x, y, viewWidth, viewHeight);


        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.orthoM(mProjectionMatrix, 0, 0, GameState.FULL_WIDTH, 0, GameState.FULL_HEIGHT, -1, 1);

        setVPMatrix();

    }

    /**
     * Utility method for compiling a OpenGL shader.
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }


    public MyGLRenderer(GameEngine game, final Context activityContext){

        mGame = game;
        mGame.setContext(activityContext);
    }

    public void slowMoFlip(){
        mSlowMo = (mSlowMo+1) % 2;
    }

    private void capFrameRate(){

        if (mStartTime == 0){
            mStartTime = System.currentTimeMillis();
            return;
        }

        mEndTime = System.currentTimeMillis();

        if (GameState.AUTO_CAP_FRAME_RATE_SIZE){
            if ((frameCount % 10) == 0) {
                long avgFrameSizeLastTen = (long) (mGame.getAvgFrameLength() * .94);
                long avgSleepTimeLastTen = getAvgSleepTime();
                long desiredSleepTime = (long) (mFrameRateCap * 0.25);
                long diffFromDesiredSleep = avgSleepTimeLastTen - desiredSleepTime;
                mFrameRateCap = avgFrameSizeLastTen - (diffFromDesiredSleep/2);
            }

            if (mFrameRateCap <= 0){
                mFrameRateCap = (1000 / GameState.FRAME_RATE_CAP_SIZE);
            }
        }

        long sleepTime = mFrameRateCap - (mEndTime - mStartTime);
        sleepTimes[frameCount % 10] = sleepTime;
        frameCount++;
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
            }
        }
        mStartTime = System.currentTimeMillis();

    }

    private long getAvgSleepTime(){
        long sum = 0;
        for (int index = 0; index < 10; index++){
            sum = sum + sleepTimes[index];
        }
        return sum/10;
    }
}


