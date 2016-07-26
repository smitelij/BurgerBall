package com.example.eli.myapplication;

/**
 * Created by Eli on 3/4/2016.
 */
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

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Drawable {

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

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.6f, 0.75f, 0.6f, 0.5f };

    public float colorConstant = 0.01f;

    private FloatBuffer mTextureBuffer;  // buffer holding the texture coordinates

    //For stretching textures properly
    private float mXCoefficient;
    private float mYCoefficient;

    private float texture[];/* = {
            // Mapping coordinates for the vertices (y,x)
            0.0f, 0.0f,     // bottom left     (V2)
            1.0f, 0.0f,     // top left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            0.0f, 1.0f      // bottom right (V3)
    };*/


    //texture pointer
    private int[] textures = new int[1];

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Drawable(float[] borderCoords, int texturePointer) {


        calculateTextureCoefficients(borderCoords);
        setTextureMappingCoords();

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

        mBorderCoords = borderCoords;
        mTextureDataHandle = texturePointer;

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



    public void setColor(float[] vertexColors){
        color = vertexColors;

        //System.out.println("Change:" + change);
        //System.out.println("Vertex:" + vertex);
        //System.out.println("Value: " + color[vertex]);

    }

    public float getColor(int vertex){
        return color[vertex];
    }

    private void calculateTextureCoefficients(float[] borderCoords){
        float minX = GameState.LARGE_NUMBER;
        float maxX = GameState.SMALL_NUMBER;
        float minY = GameState.LARGE_NUMBER;
        float maxY = GameState.SMALL_NUMBER;

        for (int index=0; index<borderCoords.length;index++){
            //x coordinates will always be in the first location
            if (index % 3 == 0){
                if (borderCoords[index] < minX)
                    minX = borderCoords[index];
                if (borderCoords[index] > maxX)
                    maxX = borderCoords[index];
                //y coordinates are in the second location
            } else if (index % 3 == 1){
                if (borderCoords[index] < minY)
                    minY = borderCoords[index];
                if (borderCoords[index] > maxY)
                    maxY = borderCoords[index];
            }
            //and the third location (z) is unused

        }

        float xDifference = maxX - minX;
        float yDifference = maxY - minY;

        if (xDifference > yDifference){
            mXCoefficient = xDifference / yDifference;
            mYCoefficient = 1;
        } else {
            mYCoefficient = yDifference / xDifference;
            mXCoefficient = 1;
        }
    }

    private void setTextureMappingCoords(){
        texture = new float[]{
                // Mapping coordinates for the vertices (y,x)
                0.0f, 0.0f,     // bottom left     (V2)
                1.0f * mYCoefficient, 0.0f,     // top left  (V1)
                1.0f * mYCoefficient, 1.0f * mXCoefficient,     // top right    (V4)
                0.0f, 1.0f * mXCoefficient      // bottom right (V3)
        };


    }

    protected void updateTexture(int newTexture){
        mTextureDataHandle = newTexture;
    }



}