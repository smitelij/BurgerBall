package com.example.eli.myapplication.Logic;

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.opengl.GLES30;

import com.example.eli.myapplication.Controller.MyGLRenderer;
import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Particle;
import com.example.eli.myapplication.Resources.GameState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.example.eli.myapplication.Logic.ParticleEngine.ParticleSpeed.*;


/**
 * Created by Eli on 12/17/2016.
 */

public class ParticleEngine {

    private boolean active = false;

    public enum ParticleSpeed {SPEED_SLOW, SPEED_MEDIUM, SPEED_FAST}

    private static final float speedClassSlow = 4.5f;
    private static final float speedClassMedium = 7f;
    private static final float speedClassFast = 10f;

    //1 is full particle generation, 0 is none.
    // greater than 1 multiplies the quantity
    private float particleGenerationConstant = 1f;

    private float[] particleBaseColor;
    private float[] particleSlowColor;
    private float[] particleMediumColor;
    private float[] particleFastColor;
    private float[] particleDispersionColor;

    private final String vertexShaderCode2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            //"layout(location = 0) in vec4 vPosition2;" +
            "#version 300 es \n" +
            "uniform mat4 uMVPMatrix;" +
                    "in vec4 vPosition2;" +
                    "in vec4 vColor;" +
                    "out vec4 aColor;" +   // This will be passed into the fragment shader.
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition2;" +
                    // Pass through the color coordinate.
                    "  aColor = vColor;" +
                    "}";

    private final String fragmentShaderCode2 =
            "#version 300 es \n" +
                    "in vec4 aColor;" +
                    "out vec4 flatColor;" +
                    "void main() {" +
                    "  flatColor = aColor;" +
                    "}";

    private final int mProgram;

    final int buffers[] = new int[2];

    private ArrayList<Particle> allParticles = new ArrayList<>();

    private final static int standardBufferSize = 36;
    private final static int standardCoordArraySize = 9;

    private FloatBuffer floatPositionBuffer;
    private FloatBuffer floatColorBuffer;

    private final static int positionHandle = 0;
    private final static int colorHandle = 1;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private final static int floatSize = 4;
    private final static int colorCoordArraySize = 4;

    public ParticleEngine(int chapter) {

        active = true;
        setParticleColors(chapter);

        // First, generate 3 buffers- vertex (constant), positions, and colors
        // This will give us the OpenGL handles for these buffers.
        GLES30.glGenBuffers(2, buffers, 0);

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
    public void drawAllParticles(float[] mvpMatrix) {

        updateBuffers();

        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition2");
        MyGLRenderer.checkGlError("glVposition");

        // get handle to vertex shaders vColor member
        mColorHandle = GLES30.glGetAttribLocation(mProgram, "vColor");

        // Bind to the position buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[positionHandle]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, standardCoordArraySize * allParticles.size() * 4, floatPositionBuffer, GLES30.GL_STREAM_DRAW); //buffer orphaning performance improvement?

        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0 , 0);
        MyGLRenderer.checkGlError("gl after pushing to buffer");

        // Bind to the color
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[colorHandle]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, allParticles.size() * 4 * 3 * 4, floatColorBuffer, GLES30.GL_STREAM_DRAW); //buffer orphaning performance improvement?

        GLES30.glEnableVertexAttribArray(mColorHandle);
        GLES30.glVertexAttribPointer(mColorHandle, 4, GLES30.GL_FLOAT, false, 0 , 0);
        MyGLRenderer.checkGlError("gl after pushing to buffer");

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        //Draw all particles
        //GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, allParticles.size() * 3, allParticles.size());
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, allParticles.size() * 3);
        MyGLRenderer.checkGlError("draw arrays");

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        // Disable attrib arrays
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mColorHandle);

    }

    private void updateBuffers() {
        ByteBuffer positionByteBuffer = ByteBuffer.allocateDirect(standardBufferSize * allParticles.size());
        positionByteBuffer.order(ByteOrder.nativeOrder());
        floatPositionBuffer = positionByteBuffer.asFloatBuffer();

        //4 floats per color, 3 vertices, floats = 4 bytes
        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(4 * 3 * 4 * allParticles.size());
        colorByteBuffer.order(ByteOrder.nativeOrder());
        floatColorBuffer = colorByteBuffer.asFloatBuffer();

        for (Particle particle : allParticles) {

            float[] particleColor = particle.getColor();

            floatColorBuffer.put(particleColor);
            floatColorBuffer.put(particleColor);
            floatColorBuffer.put(particleColor);

            floatPositionBuffer.put(particle.getPosition());
        }

        floatColorBuffer.position(0);
        floatPositionBuffer.position(0);
    }

    public void updateParticles() {

        ArrayList<Particle> deactivationList = new ArrayList<>();

        for (Particle currentParticle : allParticles) {

            //Decrease the life
            currentParticle.decreaseLife();
            if (currentParticle.getRemainingLife() < 0 ){
                deactivationList.add(currentParticle);
                continue;
            }
            //Update alpha value of particle based on remaining life
            float percentLife = currentParticle.getPercentLife();
            currentParticle.setColorAlpha(percentLife * 2);

            //Update velocity
            PointF oldVelocity = currentParticle.getVelocity();
            PointF newVelocity = new PointF(oldVelocity.x + GameState.PARTICLE_GRAVITY_CONSTANT.x, oldVelocity.y + GameState.PARTICLE_GRAVITY_CONSTANT.y);
            currentParticle.setVelocity(newVelocity);

            //Update Position
            updateParticlePosition(currentParticle);
        }

        for (Particle deadParticle : deactivationList) {
            allParticles.remove(deadParticle);
        }

    }

    private void updateParticlePosition(Particle currentParticle) {
        float[] newPosition = new float[9];
        float[] currentPosition = currentParticle.getPosition();
        PointF velocity = currentParticle.getVelocity();
        newPosition[0] = currentPosition[0] + velocity.x;
        newPosition[1] = currentPosition[1] + velocity.y;
        newPosition[3] = currentPosition[3] + velocity.x;
        newPosition[4] = currentPosition[4] + velocity.y;
        newPosition[6] = currentPosition[6] + velocity.x;
        newPosition[7] = currentPosition[7] + velocity.y;
        currentParticle.setPosition(newPosition);
    }

    public void createNewParticles(ArrayList<Ball> mAllBalls) {
        for (Ball currentBall : mAllBalls) {

            //skip inactive and stopped balls
            if (!currentBall.isBallActive()) {
                continue;
            }

            createParticlesForBall(currentBall);
        }
    }

    private void createParticlesForBall (Ball ball) {
        PointF ballVelocity = ball.getVelocity();
        float ballVelocityLength = ballVelocity.length();

        PointF particleAxisOpposite = calculateParticleAxisOpposite(ball);
        PointF particleAxisChangeA = calculateParticleAxisChangeA(ball,particleAxisOpposite);
        PointF particleAxisChangeB = calculateParticleAxisChangeB(ball,particleAxisOpposite);

        PointF velocityAxisOpposite = calculateVelocityOpposite(ball);
        PointF velocityAxisChangeA = calculateVelocityChangeA(ball, velocityAxisOpposite);
        PointF velocityAxisChangeB = calculateVelocityChangeB(ball, velocityAxisOpposite);

        int numParticles = determineNumParticlesToGenerate(ballVelocityLength);
        ParticleSpeed speedClass = getParticleSpeedClass(ballVelocityLength);
        float narrowingPercent = getParticleWideningPercent(speedClass);

        for (int i = 0; i < numParticles; i++) {
            long time = System.nanoTime();
            float randomPercent = ((time % 40) / (float) 20) - 1;
            float randomPercentNarrowed = randomPercent * narrowingPercent;

            float[] particleCoords = calculateParticleCoords(particleAxisChangeA, particleAxisChangeB, particleAxisOpposite, randomPercentNarrowed);
            PointF particleVelocity = calculateParticleVelocity(velocityAxisChangeA, velocityAxisChangeB,
                                                                velocityAxisOpposite, randomPercentNarrowed,ballVelocityLength);
            float[] color = determineParticleColor(speedClass, ballVelocityLength, randomPercent);
            int particleLife = determineParticleLife(particleCoords[0], ballVelocityLength);

            allParticles.add(new Particle(particleCoords, particleVelocity, color, particleLife));
        }
    }

    private int determineNumParticlesToGenerate(float ballVelocityLength) {
        int numParticles = (int) (((ballVelocityLength) / 2) * particleGenerationConstant);
        if (numParticles == 0) {
            float randomPercent = ((System.nanoTime() % 40) / (float) 20) - particleGenerationConstant;
            if (randomPercent < 0) {
                numParticles = 1;
            }
        }
        return numParticles;
    }

    private ParticleSpeed getParticleSpeedClass(float ballVelocity) {
        ParticleSpeed speedClass;
        if (ballVelocity < speedClassSlow) {
            speedClass = SPEED_SLOW;
        } else if (ballVelocity  >= speedClassSlow && ballVelocity < speedClassMedium) {
            speedClass = SPEED_MEDIUM;
        } else {
            speedClass = SPEED_FAST;
        }
        return speedClass;
    }

    private float getParticleWideningPercent(ParticleSpeed speed) {
        switch (speed) {
            case SPEED_SLOW:
                return 1f;

            case SPEED_MEDIUM:
                return 0.8f;

            case SPEED_FAST:
                return 0.6f;
        }
        //should never get here
        return 1f;
    }

    private float[] calculateParticleCoords(PointF particleAxisChangeA, PointF particleAxisChangeB,
                                           PointF particleAxisOpposite,float randomPercentNarrowed) {
        PointF particleCoords;
        //Opposite -> Normal A
        if (randomPercentNarrowed < 0) {
            PointF axisDisplacement = new PointF(particleAxisChangeA.x * randomPercentNarrowed, particleAxisChangeA.y * randomPercentNarrowed);
            particleCoords = new PointF(particleAxisOpposite.x + axisDisplacement.x, particleAxisOpposite.y + axisDisplacement.y);

        //Opposite -> Normal B
        } else {
            PointF axisDisplacement = new PointF(particleAxisChangeB.x * randomPercentNarrowed, particleAxisChangeB.y * randomPercentNarrowed);
            particleCoords = new PointF(particleAxisOpposite.x + axisDisplacement.x, particleAxisOpposite.y + axisDisplacement.y);
        }

        //convert to float[]
        float[] finalCoords = {
                particleCoords.x, particleCoords.y, 0f,
                particleCoords.x + 1, particleCoords.y + 1, 0f,
                particleCoords.x + 1, particleCoords.y, 0f
        };

        return finalCoords;
    }

    private PointF calculateParticleVelocity(PointF velocityAxisChangeA, PointF velocityAxisChangeB,
                                             PointF velocityOpposite, float randomPercentNarrowed, float ballVelocity) {
        PointF particleVelocity;
        //Opposite -> Normal A
        if (randomPercentNarrowed < 0) {
            PointF velocityDisplacement = new PointF(velocityAxisChangeA.x * randomPercentNarrowed, velocityAxisChangeA.y * randomPercentNarrowed);
            particleVelocity = new PointF(velocityOpposite.x + velocityDisplacement.x, velocityOpposite.y + velocityDisplacement.y);

        //Opposite -> Normal B
        } else {
            PointF velocityDisplacement = new PointF(velocityAxisChangeB.x * randomPercentNarrowed, velocityAxisChangeB.y * randomPercentNarrowed);
            particleVelocity = new PointF(velocityOpposite.x + velocityDisplacement.x, velocityOpposite.y + velocityDisplacement.y);
        }

        //reduce velocity based on ball speed
        float percentOfMaxVelocity = ballVelocity / GameState.MAX_INITIAL_VELOCITY;
        return new PointF(particleVelocity.x * percentOfMaxVelocity, particleVelocity.y * percentOfMaxVelocity);
    }

    private float[] determineParticleColor(ParticleSpeed speed, float ballVelocity, float randomPercent) {
        float[] color = new float[4];
        float percentOfSpeedClass;
        float[] dispersionColor = new float[]{particleDispersionColor[0] * 0.2f, particleDispersionColor[1] * 0.2f,
                particleDispersionColor[2] * 0.2f, particleDispersionColor[3] * 0.2f};

        switch (speed) {
            case SPEED_SLOW:
                percentOfSpeedClass = ballVelocity / speedClassSlow;
                color = calculateColorGradient(particleBaseColor, particleSlowColor, percentOfSpeedClass,dispersionColor);
                break;

            case SPEED_MEDIUM:
                percentOfSpeedClass = ballVelocity / speedClassMedium;
                color = calculateColorGradient(particleSlowColor, particleMediumColor, percentOfSpeedClass,dispersionColor);
                break;

            case SPEED_FAST:
                percentOfSpeedClass = ballVelocity / speedClassFast;
                color = calculateColorGradient(particleMediumColor, particleFastColor, percentOfSpeedClass,dispersionColor);
                break;
        }
        return color;
    }

    private float[] calculateColorGradient(float[] firstColor, float[] secondColor, float percentBetween, float[] dispersionColor) {
        float[] changeCoefficients = new float[]{secondColor[0] - firstColor[0],
                secondColor[1] - firstColor[1], secondColor[2] - firstColor[2], secondColor[3] - firstColor[3]};
        float[] colorChange = new float[]{changeCoefficients[0] * percentBetween, changeCoefficients[1] * percentBetween,
                changeCoefficients[2] * percentBetween, changeCoefficients[3] * percentBetween};

        return new float[]{firstColor[0] + colorChange[0] + dispersionColor[0], firstColor[1] + colorChange[1] + dispersionColor[1],
                firstColor[2] + colorChange[2] + dispersionColor[2], firstColor[3] + colorChange[3] + dispersionColor[3]};
    }

    private int determineParticleLife(float randomCoord, float ballVelocity) {
        //Determine life
        float randomFraction = randomCoord - (int) randomCoord;
        int randomInt = (int) (randomFraction * 20) - 10;

        int life = 30 + randomInt + (int) (ballVelocity * 2);
        return life;
    }


    private void setParticleColors(int chapter) {
        switch (chapter) {
            case 1:
                particleBaseColor = new float[]{1f,0.2f,0.2f,1f};
                particleSlowColor = new float[]{1f,0.635f,0.157f,1f};
                particleMediumColor = new float[]{0.96f,0.96f,0.235f,1f};
                particleFastColor = new float[]{0.98f,1f,0.83f,1f};
                particleDispersionColor = new float[]{0f,0f,1f,1f};
                break;

            case 2:
                particleBaseColor = new float[]{0.44f,0.13f,0.52f,1f}; //purple
                particleSlowColor = new float[]{0.13f,0.13f,0.647f,1f}; //blue
                particleMediumColor = new float[]{0.09f,0.70f,0.37f,1f}; //green (with hint of blue)
                particleFastColor = new float[]{0.82f,1f,0.90f,1f}; //white green-blue
                particleDispersionColor = new float[]{1f,0.33f,0.71f,1f}; //pink-red
                break;
        }
    }

    public float getParticleGenerationConstant() {
        return particleGenerationConstant;
    }

    public void decreaseParticleGeneration(float amount) {
        if (particleGenerationConstant > 0) {
            particleGenerationConstant = particleGenerationConstant - amount;
        }
    }

    public void increaseParticleGeneration(float amount) {
        if (particleGenerationConstant < 1) {
            particleGenerationConstant = particleGenerationConstant + amount;
        }
    }







    private PointF calculateParticleAxisOpposite(Ball ball) {
        PointF ballCenter = ball.getCenter();
        PointF ballVelocity = ball.getVelocity();
        float ballRadius = ball.getRadius();
        float ballVelocityLength = ballVelocity.length();
        PointF ballOpposite = new PointF((-ballVelocity.x / ballVelocityLength) * ballRadius, (-ballVelocity.y / ballVelocityLength) * ballRadius);
        return new PointF(ballCenter.x + ballOpposite.x, ballCenter.y + ballOpposite.y);
    }

    private PointF calculateParticleAxisChangeA(Ball ball, PointF particleAxisOpposite) {
        PointF ballCenter = ball.getCenter();
        PointF ballVelocity = ball.getVelocity();
        float ballRadius = ball.getRadius();
        float ballVelocityLength = ballVelocity.length();
        PointF ballNormalA = new PointF((-ballVelocity.y / ballVelocityLength) * ballRadius, (ballVelocity.x / ballVelocityLength) * ballRadius);
        PointF particleAxisA = new PointF(ballCenter.x + ballNormalA.x, ballCenter.y + ballNormalA.y);
        return new PointF(particleAxisOpposite.x - particleAxisA.x, particleAxisOpposite.y - particleAxisA.y);
    }

    private PointF calculateParticleAxisChangeB(Ball ball, PointF particleAxisOpposite) {
        PointF ballCenter = ball.getCenter();
        PointF ballVelocity = ball.getVelocity();
        float ballRadius = ball.getRadius();
        float ballVelocityLength = ballVelocity.length();
        PointF ballNormalB = new PointF((ballVelocity.y / ballVelocityLength) * ballRadius, (-ballVelocity.x / ballVelocityLength) * ballRadius);
        PointF particleAxisB = new PointF(ballCenter.x + ballNormalB.x, ballCenter.y + ballNormalB.y);
        return new PointF(particleAxisB.x - particleAxisOpposite.x, particleAxisB.y - particleAxisOpposite.y);
    }

    private PointF calculateVelocityOpposite(Ball ball) {
        PointF ballVelocity = ball.getVelocity();
        float ballVelocityLength = ballVelocity.length();
        return new PointF((-ballVelocity.x / ballVelocityLength), (-ballVelocity.y / ballVelocityLength));
    }

    private PointF calculateVelocityChangeA(Ball ball, PointF velocityOpposite) {
        PointF ballVelocity = ball.getVelocity();
        float ballVelocityLength = ballVelocity.length();
        PointF velocityNormalA = new PointF((-ballVelocity.y / ballVelocityLength), (ballVelocity.x / ballVelocityLength));
        return new PointF(velocityOpposite.x - velocityNormalA.x, velocityOpposite.y - velocityNormalA.y);
    }

    private PointF calculateVelocityChangeB(Ball ball, PointF velocityOpposite) {
        PointF ballVelocity = ball.getVelocity();
        float ballVelocityLength = ballVelocity.length();
        PointF velocityNormalB = new PointF((ballVelocity.y / ballVelocityLength), (-ballVelocity.x / ballVelocityLength));
        return new PointF(velocityNormalB.x - velocityOpposite.x, velocityNormalB.y - velocityOpposite.y);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

}
