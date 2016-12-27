package com.example.eli.myapplication.Model;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import android.opengl.GLES30;

import com.example.eli.myapplication.Controller.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;


/**
 * Created by Eli on 12/17/2016.
 */

public class ParticleEngine {

    private final String vertexShaderCode2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            //"layout(location = 0) in vec4 vPosition2;" +
            "#version 300 es \n" +
            "uniform mat4 uMVPMatrix;" +
                    //"attribute vec2 a_TexCoordinate;" + // Per-vertex texture coordinate information we will pass in.
                    "in vec4 vPosition2;" +
                    //"in float vPosition[];" +
                    //"varying vec2 v_TexCoordinate;" +   // This will be passed into the fragment shader.
                    "flat out int v_InstanceId;" +
                    "float mult;" +
                    "vec4 testPosition;" +

                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  mult = float(gl_InstanceID);" +
                    //"  mult = float(gl_VertexID);" +
                    "  mult = 1.0 + (mult / 10.0);" +
                    "  testPosition = vec4(vPosition2.x * 1.0, vPosition2.y * mult, vPosition2.z, vPosition2.w);" +
                    //"  gl_Position = uMVPMatrix * testPosition;" +
                    "  gl_Position = uMVPMatrix * vPosition2;" +
                    // Pass through the texture coordinate.
                    //"  v_TexCoordinate = vec2(gl_InstanceId,gl_InstanceId);" +
                    //"  v_TexCoordinate = a_TexCoordinate;" +
                    "  v_InstanceId = gl_InstanceID;" +
                    "}";

    private final String fragmentShaderCode2 =
            "#version 300 es \n" +
            "uniform sampler2D u_Texture;" +    // The input texture.
                    "flat in int v_InstanceId;" +
                    //"varying vec2 v_TexCoordinate;" + // Interpolated texture coordinate per fragment.
                    //"precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "out vec4 flatColor;" +
                    "int test;" +
                    "float colorTest;" +
                    "void main() {" +
                    "  test = v_InstanceId;" +
                    "  colorTest = float(test);" +
                    "  colorTest = colorTest / 50.0;" +
                    //"  flatColor = vec4(colorTest,colorTest,colorTest,1.0);" +
                    "  flatColor = vec4(1.0,0.0,1.0,1.0);" +
                    "}";

    private final int mProgram;

    private int currentParticleIndex;

    private static IntBuffer vertexBufferReference;


    private final static float vertexCoords[] = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
    };

    private float positionBuffer[] = {
            1f, 1f, 0f,
            1f, 1f, 0f,
            1f, 1f, 0f,
            1f, 1f, 0f,
    };

    private float colorBuffer[] = {
            1f, 1f, 1f, 1f
    };

    final int buffers[] = new int[3];

    //final static int maxParticles = 1000;

    private ArrayList<Particle> allParticles = new ArrayList<>();

    private final static int standardBufferSize = 36;
    private final static int standardCoordArraySize = 9;

    private final static int vertexHandle = 0;
    private final static int positionHandle = 1;
    private final static int colorHandle = 2;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mVertexHandle;

    private final static int floatSize = 4;
    private final static int colorCoordArraySize = 4;


    //****

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    private final int vertexStride = 12; // 4 bytes per vertex



    public ParticleEngine() {

        // First, generate 3 buffers- vertex (constant), positions, and colors
        // This will give us the OpenGL handles for these buffers.
        GLES30.glGenBuffers(3, buffers, 0);

        //***********
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER,
                vertexShaderCode2);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER,
                fragmentShaderCode2);


        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        //GLES30.glBindAttribLocation(mProgram,0,"vPosition");
        GLES30.glLinkProgram(mProgram);                  // create OpenGL program executables
        String error = GLES30.glGetProgramInfoLog(mProgram);
        String error2 = GLES30.glGetShaderInfoLog(vertexShader);
        String error3 = GLES30.glGetShaderInfoLog(fragmentShader);
    }


    @TargetApi(18)
    public void drawAllParticles2(float[] mvpMatrix) {

        updateParticles();

        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition2");
        MyGLRenderer.checkGlError("glVposition");

        FloatBuffer positionBuffer = getHugeFloatBuffer();

        // Bind to the position buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[positionHandle]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, standardCoordArraySize * allParticles.size() * 4, positionBuffer, GLES30.GL_STREAM_DRAW); //buffer orphaning performance improvement?

        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0 , 0);
        MyGLRenderer.checkGlError("gl after pushing to buffer");

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        float[] extendedColor = new float[40];
        for (int i = 0; i < extendedColor.length; i++) {
            extendedColor[i] = 1f;
        }

        FloatBuffer colorBuffer = getHugeColorFloatBuffer(new float[]{1f,1f,1f,1f});

        // Set color for drawing the triangle
        GLES30.glUniform4fv(mColorHandle, 1, new float[]{1f,1f,1f,1f}, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        float[] extendedMatrix = new float[320];
        //for (int i = 0; i < extendedMatrix.length; i++) {
            //extendedMatrix[i] = mvpMatrix[i % 16];
        //}

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        //Draw all particles
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, allParticles.size() * 3, allParticles.size());
        MyGLRenderer.checkGlError("draw arrays instanced");

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        GLES30.glVertexAttribDivisor(mPositionHandle, 0);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);

    }

    private void updateBuffers() {
    }

    private FloatBuffer getFloatBuffer(float[] coords) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(standardCoordArraySize * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(coords).position(0);

        return floatBuffer;
    }

    private FloatBuffer getParticlePositionBuffer(Particle currentParticle) {

        float[] position = currentParticle.getPosition();
        System.out.println("------current particle position: " + position[0] + "|" + position[1] + "|" + position[2] + "|" +
                position[3] + "|" + position[4] + "|" + position[5] + "|" + position[6] + "|" + position[7] +
                position[8] + "|" + position[9] + "|" + position[10] + "|" + position[11]);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(standardCoordArraySize * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(position).position(0);

        return floatBuffer;

    }

    private FloatBuffer getHugeFloatBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(standardBufferSize * allParticles.size());
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

        float[] bigFloat = new float[9 * allParticles.size()];
        int k = 0;

        for (Particle particle : allParticles) {
            floatBuffer.put(particle.getPosition());
        }

        floatBuffer.position(0);
        return floatBuffer;
    }

    private FloatBuffer getHugeColorFloatBuffer(float[] color) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 4 * allParticles.size());
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

        for (Particle particle : allParticles) {
            floatBuffer.put(color);
        }

        floatBuffer.position(0);
        return floatBuffer;
    }

    private FloatBuffer getParticleColorBuffer(Particle currentParticle) {

        float[] color = currentParticle.getColor();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(color).position(0);

        return getFloatBuffer(color);

    }

    public void updateParticles() {

        ArrayList<Particle> deactivationList = new ArrayList<>();

        for (Particle currentParticle : allParticles) {
            //return new PointF(mVelocity.x + (GameState.GRAVITY_CONSTANT.x * timeStep), mVelocity.y + (GameState.GRAVITY_CONSTANT.y * timeStep));
            float xChange = GameState.GRAVITY_CONSTANT.x;
            float yChange = GameState.GRAVITY_CONSTANT.y;
            float[] currentPosition = currentParticle.getPosition();
            currentPosition[0] = currentPosition[0] + xChange;
            currentPosition[1] = currentPosition[1] + yChange;
            currentPosition[3] = currentPosition[3] + xChange;
            currentPosition[4] = currentPosition[4] + yChange;
            currentPosition[6] = currentPosition[6] + xChange;
            currentPosition[7] = currentPosition[7] + yChange;

            currentParticle.setPosition(currentPosition);

            currentParticle.decreaseLife();

            if (currentParticle.getRemainingLife() < 0 ){
                deactivationList.add(currentParticle);
            }
        }

        for (Particle deadParticle : deactivationList) {
            allParticles.remove(deadParticle);
        }

    }

    public void addParticle(float[] position) {
/*
        System.out.println("updating particle: " + currentParticleIndex);
        System.out.println("------current particle position: " + position[0] + "|" + position[1] + "|" + position[2] + "|" +
                position[3] + "|" + position[4] + "|" + position[5] + "|" + position[6] + "|" + position[7] + position[8]);*/

        allParticles.add(new Particle(position));

        currentParticleIndex = (currentParticleIndex + 1) % allParticles.size();
    }

}
