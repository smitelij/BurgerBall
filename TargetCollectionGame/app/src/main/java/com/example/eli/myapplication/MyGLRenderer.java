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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.graphics.PointF;

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

    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Ball mBall;

    private Borders mBorders;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mNewCoords = new float[12];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];


    private float mAngle;

    private double x = 40.0;
    private double y = 40.0;

    private float xVelocity = 5.0f;
    private float yVelocity = 3.0f;

    private int frames;

    //This should be moved to the ball class once it exists!
    private boolean exitingObstacle = false;
    private int framesExiting = 0;

    private boolean pauseNextTime = false;
    private PointF displacementVector;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        setMMVPMatrix();

        mTriangle = new Triangle();
        mBall = new Ball(GameState.initialBallCoords, GameState.initialBallVelocity, GameState.ballRadius); // top right});
        mBall.setColor(GameState.borderColor);
        mBorders = new Borders();
    }

    public void setMMVPMatrix() {
        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] oldSquarePos = new float[16];

        float timeElapsed = 0f;
        boolean collisionOccurred = false;
        PointF displacementVector = new PointF(0f,0f);

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        while (timeElapsed < 1) {

            float timeStep = 1 - timeElapsed;

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }

            //initialize collision detection engine
            CollisionDetection CD = new CollisionDetection();

            //go through all borders
            for (Polygon border : mBorders.allBorders) {

                //reset AABB to restore previous value
                mBall.resetAABB();
                //Move ball forward by time step amount
                mBall.moveBallByFrame(timeStep);

                //First, test the bounding boxes to see if there may have been a collision
                if (CD.testBoundingBoxes(mBall, border)) {

                    //There may have been a collision, further testing is necessary.
                    //For now, borders can only be polygons
                    if (border.getType() == GameState.OBSTACLE_POLYGON) {

                        CD.doPolygonCollisionDetection(mBall, border, timeStep);

                    }
                }
            }

            //If one collision occurred
            if (CD.getCollisions().size() == 1) {
                System.out.println("one collision occurred.");
                PointF newDisplacementVector = CD.calculateChangeInCoords(mBall, CD.getFirstCollision(), timeStep);
                displacementVector.set(displacementVector.x + newDisplacementVector.x, displacementVector.y + newDisplacementVector.y);
                timeElapsed = timeElapsed + CD.getCollisions().get(0).getTime();
                System.out.println("Time elapsed: " + timeElapsed);
                System.out.println("displacementVector: " + displacementVector.x + "." + displacementVector.y);

            //If multiple collisions occurred
            } else if (CD.getCollisions().size() > 1) {
                System.out.println("multiple collisions occurred");
                PointF newDisplacementVector = CD.calculateChangeInCoords(mBall, CD.getFirstCollision(), timeStep);
                displacementVector.set(displacementVector.x + newDisplacementVector.x, displacementVector.y + newDisplacementVector.y);
                timeElapsed = timeElapsed + CD.getCollisions().get(0).getTime();
                System.out.println("Time elapsed: " + timeElapsed);
                System.out.println("displacementVector: " + displacementVector.x + "." + displacementVector.y);

            //If no collisions occurred
            } else {
                timeElapsed = 1;
                //If no collision, move the ball forward by current velocity
                PointF additionalDisplacement = new PointF(mBall.getXVelocity() * timeStep, mBall.getYVelocity() * timeStep);
                //System.out.println("additional displacement: " + additionalDisplacement.x + "." + additionalDisplacement.y);
                displacementVector.set(displacementVector.x + additionalDisplacement.x, displacementVector.y + additionalDisplacement.y);
            }
        }

        //Move ball forward by displacement amount
        Matrix.translateM(mBall.mModelMatrix, 0, mBall.mModelMatrix, 0, displacementVector.x, displacementVector.y, 0);

        Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mBall.mModelMatrix, 0);

        //Draw Borders
        mBorders.drawAllBorders(mProjectionMatrix);

        // Draw square
        mBall.draw(scratch);


    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        int x, y, viewWidth, viewHeight;


        float STATIC_RATIO = GameState.FULL_WIDTH / GameState.FULL_HEIGHT;

        //System.out.println(STATIC_RATIO);

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

        GLES20.glViewport(x, y, viewWidth, viewHeight);


        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.orthoM(mProjectionMatrix, 0, 0, GameState.FULL_WIDTH, 0, GameState.FULL_HEIGHT, -1, 1);

        setMMVPMatrix();

        mBorders.drawAllBorders(mMVPMatrix);

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

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
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

    public void setCoords() {
        //mCoords = mCoords +1;
    }
}


