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

import java.util.ArrayList;

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
    private ArrayList<Ball> mBalls = new ArrayList<>();

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

        //Create balls
        float[] ballCoords1 = GameState.getBorderCoords(170f, 270f, 8f);
        float[] ballCoords2 = GameState.getBorderCoords(120f, 240f, 8f);
        float[] ballCoords3 = GameState.getBorderCoords(80f, 10f, 8f);
        Ball ball1 = new Ball(ballCoords1, GameState.initialBallVelocity, GameState.ballRadius, GameState.ballColor);
        Ball ball2 = new Ball(ballCoords2, GameState.initialBallVelocity, GameState.ballRadius, GameState.ballColor);
        Ball ball3 = new Ball(ballCoords3, GameState.initialBallVelocity, GameState.ballRadius, GameState.ballColor);
        mBalls.add(ball1);
        mBalls.add(ball2);
        mBalls.add(ball3);

        mBorders = new Borders();
    }

    public void setMMVPMatrix() {
        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void collisionDetection(CollisionDetection CD, float timeStep){
        /*try {
                Thread.sleep(500);
            } catch (Exception e) {
            }*/

        //======================
        // collision detection
        //======================

        //go through all balls
        for (Ball currentBall : mBalls) {

            //go through all borders
            for (Polygon border : mBorders.allBorders) {

                //reset AABB to restore original value, and then move ball forward by time step amount.
                //This is better to keep here rather than in the WHILE loop, because it is easier to start each
                //collision detection testing with a clean slate.
                currentBall.resetAABB();
                currentBall.moveBallByFrame(timeStep);

                //First, test the bounding boxes to see if there may have been a collision
                if (CD.testBoundingBoxes(currentBall, border)) {

                    //There may have been a collision, further testing is necessary.
                    CD.doPolygonCollisionDetection(currentBall, border, timeStep);
                }
            }

            for (Ball otherBall : mBalls){
                //only need to test balls that have already moved

                if (otherBall.hasBallMoved()){

                    //System.out.println("testing balls inner: " + currentBall.getID() + ";" + otherBall.getID());

                    //test bounding boxes to see if there may have been a collision
                    if (CD.testBoundingBoxes(currentBall, otherBall)){
                        CD.doBallCollisionDetection(currentBall, otherBall, timeStep);
                    }
                }
            }

            currentBall.setBallMoved();
        }

        for (Ball currentBall : mBalls){
            currentBall.clearMovedStatus();
        }
    }

    //return timeElapsed
    public float collisionHandling(CollisionDetection CD, float timeStep, float timeElapsed){
        //==================
        //collision handling
        //==================

        //from this point on, referring to 'collisions' means the set of first collisions, by time.
        //this will usually be one collision, but it is possible for there to be multiple first collisions if:
        // 1- multiple balls collide with boundaries at the same time
        // 2- two or more balls collide with each other
        // 3- one ball collides with multiple boundaries at the same time
        ArrayList<CollisionHistory> firstCollisions = CD.getFirstCollision();

        //no collisions
        if (firstCollisions == null ) {
            //Move all balls forward by timestep
            handleNoCollisions(timeStep);
            timeElapsed = 1;

            //one or multiple collisions
        } else {
            float collisionTime = CD.getFirstCollisionTime();
            //move all balls forward by collision time, and update velocity for colliding balls
            handleCollisionsForBalls(CD, firstCollisions, collisionTime);
            timeElapsed = timeElapsed + collisionTime;
        }

        return timeElapsed;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        //System.out.println("START FRAME!");

        float[] mModelProjectionMatrix = new float[16];

        float timeElapsed = 0f;

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //repeat until 1 whole 'frame' has been moved.
        //If there is a collision, we will only advance up until the collision point,
        //and then repeat until we reach the end of the frame.
        while (timeElapsed < 1) {
            float timeStep = 1 - timeElapsed;

            //initialize collision detection engine
            CollisionDetection CD = new CollisionDetection();

            //Do collision detection
            collisionDetection(CD, timeStep);

            //Handle collisions
            timeElapsed = collisionHandling(CD, timeStep, timeElapsed);

        }


        //Draw all balls
        for (Ball currentBall : mBalls) {

            //Move ball forward by displacement amount
            Matrix.translateM(currentBall.mModelMatrix, 0, currentBall.mModelMatrix, 0, currentBall.getDisplacementVector().x, currentBall.getDisplacementVector().y, 0);
            //move into projection coordinates
            Matrix.multiplyMM(mModelProjectionMatrix, 0, mProjectionMatrix, 0, currentBall.mModelMatrix, 0);
            // Draw ball
            currentBall.draw(mModelProjectionMatrix);

            //clear displacement vector for next frame
            currentBall.clearDisplacementVector();
        }

        //Draw Borders
        mBorders.drawAllBorders(mProjectionMatrix);

    }

    public void handleCollisionsForBalls(CollisionDetection CD, ArrayList<CollisionHistory> firstCollisions, float collisionTime){

        ArrayList<CollisionHistory>[] collisionMapping;
        collisionMapping = CD.createBallCollisionArray(firstCollisions);

        for (Ball currentBall : mBalls){
            int currentBallID = currentBall.getID();

            PointF newDisplacementVector;

            //If there is collisionHistory in the mapping for the current ball, then a collision occurred for this ball.
            //This means we need to displace the ball to the moment of collision, and then calculate the new velocity based on the collision.
            if (collisionMapping[currentBallID] != null){
                //Don't actually need this?!
               // newDisplacementVector = CD.calculateChangeInCoords(mBall, CD.getFirstCollision(), timeStep);
                newDisplacementVector = new PointF(currentBall.getXVelocity() * collisionTime, currentBall.getYVelocity() * collisionTime);
                CD.calculateNewVelocity(currentBall, collisionMapping[currentBallID]);

            //If no collisionHistory in the mapping, then no collision occurred for this ball, so we only
            //need to move the ball forward to the same time as the other collision.
            } else {
                newDisplacementVector = new PointF(currentBall.getXVelocity() * collisionTime, currentBall.getYVelocity() * collisionTime);
            }

            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);
            cleanupAABB(currentBall, newDisplacementVector);
        }
    }

    public void cleanupAABB(Ball currentBall, PointF displacementVector){
        //clean up any lingering weird AABB stuff from collision detection
        currentBall.resetAABB();
        //move ball by however far it has so far been displaced (this will be 0 unless there are multiple collisions in 1 frame)
        currentBall.updateAABB(displacementVector.x, displacementVector.y);
        //Update saved value for AABB
        currentBall.updatePrevAABB();
    }

    public void handleNoCollisions(float timeStep){

        for (Ball currentBall : mBalls){
            PointF newDisplacementVector = new PointF(currentBall.getXVelocity() * timeStep, currentBall.getYVelocity() * timeStep);
            //Add the current displacement to the balls running tally of total displacement for this frame
            currentBall.addToDisplacementVector(newDisplacementVector);

            //no need to mess with AABB, it should be accurate so no need to cleanup,
            //and it will be updated within the Draw method of each ball.
        }
    }

    public void handleSingleCollision() {
    }

    public void handleMultipleCollisions() {
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


