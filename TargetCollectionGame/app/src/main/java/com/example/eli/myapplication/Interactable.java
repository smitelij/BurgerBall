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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Interactable {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_TexCoordinate;" + // Per-vertex texture coordinate information we will pass in.
                    "attribute vec4 vPosition;" +
                    "varying vec2 v_TexCoordinate;" +   // This will be passed into the fragment shader.
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    // Pass through the texture coordinate.
                    "  v_TexCoordinate = a_TexCoordinate;" +
                    "}";

    private final String fragmentShaderCode =
            "uniform sampler2D u_Texture;" +    // The input texture.
            "varying vec2 v_TexCoordinate;" + // Interpolated texture coordinate per fragment.
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;

    private Context context;


    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float mBorderCoords[] = {
            -0.1f,  0.1f, 0.0f,   // top left
            -0.1f, -0.1f, 0.0f,   // bottom left
            0.1f, -0.1f, 0.0f,   // bottom right
            0.1f,  0.1f, 0.0f }; // top right

    //All for AABB
    protected float mMinXCoord;
    protected float mMaxXCoord;
    protected float mMinYCoord;
    protected float mMaxYCoord;

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.6f, 0.75f, 0.6f, 0.5f };

    public float colorConstant = 0.01f;

    public float[] colorDirections = {colorConstant, colorConstant, colorConstant, colorConstant};

    //use one of the static ints declared in GameState.OBSTACLE_
    private int mType;
    private PointF[] m2dCoordArray;

    private FloatBuffer mTextureBuffer;  // buffer holding the texture coordinates
    private float texture[] = {
            // Mapping coordinates for the vertices
            0.0f, 0.0f,     // top left     (V2)
            1.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            0.0f, 1.0f      // bottom right (V3)
    };

    private float Originaltexture[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f,     // top left     (V2)
            0.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            1.0f, 0.0f      // bottom right (V3)
    };

    //texture pointer
    private int[] textures = new int[1];

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Interactable(float[] borderCoords, int type, int texturePointer) {

        mTextureDataHandle = texturePointer;
        /*
        if (activityContext != null) {
            loadGLTexture(activityContext);
            mTextureDataHandle = textures[0];
        }*/

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                borderCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(borderCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        //initialize texture buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer.asFloatBuffer();
        mTextureBuffer.put(texture);
        mTextureBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);


        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glBindAttribLocation(mProgram,0,"a_TexCoordinate");
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        String error = GLES20.glGetProgramInfoLog(mProgram);
        System.out.println("ERROR " + error);


        mType = type;
        mBorderCoords = borderCoords;
        //mTextureDataHandle = textureDataHandle;

        set2dCoordArray();
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

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // bind the previously generated texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        ////////////////////////////


        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        ////////////////////////////

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);



        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);


        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }

    public void setCoords(float[] newCoords){
        mBorderCoords = newCoords;
    }

    public PointF[] get2dCoordArray(){

        return m2dCoordArray;
    }

    public void set2dCoordArray(){
        int numberOfCoords = mBorderCoords.length / 3;
        PointF[] coords = new PointF[numberOfCoords];

        for (int i = 0; i < numberOfCoords; i++){
            PointF currentCoord = new PointF(mBorderCoords[i*3], mBorderCoords[(i*3) + 1]);
            coords[i] = currentCoord;
        }

        m2dCoordArray = coords;
    }

    /*public void translateSquare(){
        float[] oldCoords = getCoords();
        float[] newCoords = new float[16];
        float[] mModelMatrix = new float[16];
        float[] mModelMatrixTrans = new float[16];

        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.translateM(mModelMatrixTrans, 0, mModelMatrix, 0, 0.1f, 0.1f, 0);
        Matrix.multiplyMM(newCoords,0,mModelMatrixTrans,0,oldCoords,0);

        setCoords(newCoords);
    }*/

    public void setColor(float[] vertexColors){
        color = vertexColors;

        //System.out.println("Change:" + change);
        //System.out.println("Vertex:" + vertex);
        //System.out.println("Value: " + color[vertex]);

    }

    public float getColor(int vertex){
        return color[vertex];
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

    public int getType(){
        return mType;
    }

    public boolean hasObjectMoved(){

        //Since Polygon==Border currently, and borders never move, we always return true.
        if (mType==GameState.OBSTACLE_POLYGON){
            return true;

        //if not a polygon, we know it will be a ball, so we can call the sub class method
        } else {
            Ball tempBall = (Ball) this;
            return tempBall.hasBallMoved();
        }
    }

    public void loadGLTexture(Context context) {
        // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle);

        // generate one texture pointer
        GLES20.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();
    }



}