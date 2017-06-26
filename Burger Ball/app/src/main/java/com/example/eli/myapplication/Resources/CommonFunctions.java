package com.example.eli.myapplication.Resources;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;

/**
 * Created by Eli on 2/4/2017.
 */

public class CommonFunctions {

    //--------------------------
    //This function determines where balls are initialized when they are fired.
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    public static float[] getDefaultBallCoords(){
        return createCircleCoords(GameState.FULL_WIDTH / 2, GameState.BORDER_WIDTH * 4, GameState.ballRadius);
    }

    public static PointF getFiringZoneCenter(float[] startingCoords) {
        return calculateInitialBallCenter(startingCoords);
    }

    public static float[] getSelectionCircleCoords(float[] startingCoords){
        PointF responseCenter = calculateInitialBallCenter(startingCoords);

        //Radius is determined with the width, so use xRatio
        float adjustedRadius = GameState.mResponseRadius * GameState.xRatioAndroidToArena;

        return createCircleCoords(responseCenter.x, responseCenter.y, adjustedRadius);
    }

    public static float[] getEndLevelSuccessImageCoords(int stage, float[] multipliers) {

        if (multipliers == null) {
            multipliers = new float[]{0f,0f,0f,0f};
        }

        float adjustedHeight = GameState.mHeight * GameState.yRatioAndroidToArena;
        float adjustedWidth = GameState.mWidth * GameState.xRatioAndroidToArena;

        return new float[]{
                0, adjustedHeight * (0.7f + (multipliers[0] * stage)), 0f,
                0, adjustedHeight * (0.4f + (multipliers[1] * stage)), 0f,
                adjustedWidth, adjustedHeight * (0.4f + (multipliers[2] * stage)), 0f,
                adjustedWidth, adjustedHeight * (0.7f + (multipliers[3] * stage)), 0f};
    }

    public static float[] getEndLevelFailImageCoords() {

        float adjustedHeight = GameState.mHeight * GameState.yRatioAndroidToArena;
        float adjustedWidth = GameState.mWidth * GameState.xRatioAndroidToArena;

        return new float[]{
                0, adjustedHeight * 0.7f, 0f,
                0, adjustedHeight * 0.034f, 0f,
                adjustedWidth, adjustedHeight * 0.034f, 0f,
                adjustedWidth, adjustedHeight * 0.7f, 0f};
    }

    public static float[] getFinalScoreTextCoords(int stage, float[] multipliers) {

        if (multipliers == null) {
            multipliers = new float[]{0f,0f,0f,0f};
        }

        float adjustedHeight = GameState.mHeight * GameState.yRatioAndroidToArena;
        float adjustedWidth = GameState.mWidth * GameState.xRatioAndroidToArena;

        return new float[]{
                0, adjustedHeight * (0.4f + (multipliers[0] * stage)), 0f,
                0, adjustedHeight * (0.2f + (multipliers[1] * stage)), 0f,
                adjustedWidth * 0.3f, adjustedHeight * (0.2f + (multipliers[2] * stage)), 0f,
                adjustedWidth *  0.3f, adjustedHeight * (0.4f + (multipliers[3] * stage)), 0f};
    }

    public static float[] getFinalScoreDigitCoords(int digitIndex, int stage, float[] multipliers) {

        if (multipliers == null) {
            multipliers = new float[]{0f,0f,0f,0f};
        }

        float adjustedHeight = GameState.mHeight * GameState.yRatioAndroidToArena;
        float adjustedWidth = GameState.mWidth * GameState.xRatioAndroidToArena;
        float boxWidth = adjustedWidth / 14f;
        float boxHeightRatio = boxWidth / adjustedHeight;

        //Some crazy weirdness to make the score digits waver as one whole unit instead of per-digit
        float[] currentDigitMultiplier = new float[4];
        currentDigitMultiplier[0] = (((5 - digitIndex) / 5f) * multipliers[0]) + ((digitIndex / 5f) * multipliers[3]);
        currentDigitMultiplier[1] = ((5 - digitIndex) / 5f) * multipliers[1] + ((digitIndex / 5f) * multipliers[2]);
        currentDigitMultiplier[2] = ((digitIndex + 1) / 5f) * multipliers[2] + (((5 - (digitIndex + 1)) / 5f) * multipliers[1]);
        currentDigitMultiplier[3] = ((digitIndex + 1) / 5f) * multipliers[3] + (((5 - (digitIndex + 1)) / 5f) * multipliers[0]);


        return new float[]{
                (adjustedWidth * 0.71f) - (boxWidth * (digitIndex+1)), (adjustedHeight * (0.4f + (currentDigitMultiplier[0] * stage))), 0f,                      //top left
                (adjustedWidth * 0.71f) - (boxWidth * (digitIndex+1)), (adjustedHeight * (0.4f - boxHeightRatio + (currentDigitMultiplier[1] * stage))), 0f,         //bottom left
                (adjustedWidth * 0.71f) - (boxWidth * digitIndex), (adjustedHeight * (0.4f - boxHeightRatio + (currentDigitMultiplier[2] * stage))), 0f,             //bottom right
                (adjustedWidth * 0.71f) - (boxWidth * digitIndex), (adjustedHeight * (0.4f + (currentDigitMultiplier[3] * stage))), 0f                           //top right
        };
    }

    //--------------------------
    //Helper function to create coordinates for a ball
    //PARAMS:
    //  ballCenterX- xCoordinate where ball will be initialized
    //  ballCenterY- yCoordinate where ball will be initialized
    //  ballRadius- The radius of the ball
    public static float[] createCircleCoords(float ballCenterX, float ballCenterY, float ballRadius){
        return new float[]{
                ballCenterX - ballRadius,  ballCenterY + ballRadius, 0.0f,   // top left
                ballCenterX - ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom left
                ballCenterX + ballRadius, ballCenterY - ballRadius, 0.0f,   // bottom right
                ballCenterX + ballRadius,  ballCenterY + ballRadius, 0.0f }; //top right
    }

    //---------------------------
    //Getter for the response range variable (controls the touch-sensitive firing radius of the user)
    public static float getResponseRange(){
        return GameState.mResponseRange;
    }

    //---------------------------
    //This function calculates the initial velocity of a fired ball,
    //based on how far they have 'pulled back' the ball.
    //PARAMS:
    //  xChange- Pixel value representing how far the user has slid their finger within the
    //         - response radius, in the x direction.
    //  yChange- Pixel value representing how far the user has slid their finger within the
    //         - response radius, in the y direction.
    //RETURNS:
    //  A PointF the represents velocity.
    public static PointF calculateInitialVelocity(float xChange, float yChange){

        float angle = calculateFiringAngle(xChange,yChange);

        float percentVelocity = calculateFiringVelocity(xChange,yChange);

        float xVelocityPercent;
        float yVelocityPercent;

        yVelocityPercent = (float) Math.sin(angle) * percentVelocity;

        if (xChange >= 0){
            xVelocityPercent = (float) -(Math.cos(angle) * percentVelocity);
        } else {
            xVelocityPercent = (float) (Math.cos(angle) * percentVelocity);
        }

        //Calculate the velocity based on MAX_INITIAL_VELOCITY variables
        float initialXVelocity = xVelocityPercent * GameState.MAX_INITIAL_VELOCITY;
        float initialYVelocity = yVelocityPercent * GameState.MAX_INITIAL_VELOCITY;

        return new PointF(initialXVelocity,initialYVelocity);
    }

    public static float calculateFiringAngle(float xChange, float yChange){
        return (float) Math.atan(yChange / Math.abs(xChange));
    }

    public static float calculateFiringVelocity(float xChange, float yChange){

        //Calculate the percent the user has pulled back, based on the response radius
        float pullBackLength = (float) Math.sqrt((xChange * xChange) + (yChange * yChange));
        float percentVelocity = pullBackLength / GameState.mResponseRadius;

        if (percentVelocity > 1){
            percentVelocity = 1;
        }
        if (percentVelocity < 0){
            percentVelocity = 0.01f;
        }

        return percentVelocity;
    }

    //------------------------
    //Useful function to quickly print both components of a vector to the console
    public static void vectorPrint(PointF vector, String msg){
        System.out.println(msg + ": " + vector.x + ";" + vector.y);
    }

    //------------------------
    //Calculate the dot product of two vectors
    public static float dotProduct(PointF vector1, PointF vector2){
        return ((vector1.x * vector2.x) + (vector1.y * vector2.y));
    }

    public static float[] updateVelocityArrow(float angle, float height, PointF responseCenter){

        float adjustedRadius = GameState.mResponseRadius * GameState.xRatioAndroidToArena;
        //PointF adjustedResponseCenter = new PointF(GameState.mResponseCenter.x * GameState.xRatioAndroidToArena, (GameState.BORDER_WIDTH * 4));
        //PointF adjustedResponseCenter = new PointF(GameState.mResponseCenter.x * GameState.xRatioAndroidToArena, (GameState.BORDER_WIDTH * 4));
        PointF adjustedResponseCenter = new PointF(responseCenter.x * GameState.xRatioAndroidToArena, responseCenter.y * GameState.yRatioAndroidToArena);

        double cosAngle = Math.cos((double) angle);
        double sinAngle = Math.sin((double) angle);

        PointF baseTopLeft = new PointF(-5, (adjustedRadius*height));
        PointF baseBottomLeft = new PointF(-5, GameState.ballRadius);
        PointF baseBottomRight = new PointF(5, GameState.ballRadius);
        PointF baseTopRight = new PointF(5, (adjustedRadius*height));

        PointF rotatedTopLeft = new PointF( (float) ((baseTopLeft.x * cosAngle) - (baseTopLeft.y * sinAngle)), (float) ((baseTopLeft.y * cosAngle) + (baseTopLeft.x * sinAngle)));
        PointF rotatedBottomLeft = new PointF( (float) ((baseBottomLeft.x * cosAngle) - (baseBottomLeft.y * sinAngle)), (float) ((baseBottomLeft.y * cosAngle) + (baseBottomLeft.x * sinAngle)));
        PointF rotatedBottomRight = new PointF( (float) ((baseBottomRight.x * cosAngle) - (baseBottomRight.y * sinAngle)), (float) ((baseBottomRight.y * cosAngle) + (baseBottomRight.x * sinAngle)));
        PointF rotatedTopRight = new PointF( (float) ((baseTopRight.x * cosAngle) - (baseTopRight.y * sinAngle)), (float) ((baseTopRight.y * cosAngle) + (baseTopRight.x * sinAngle)));

        float[] finalCoords = { rotatedTopLeft.x + responseCenter.x, rotatedTopLeft.y + responseCenter.y, 0.0f,
                                rotatedBottomLeft.x + responseCenter.x, rotatedBottomLeft.y + responseCenter.y, 0.0f,
                                rotatedBottomRight.x + responseCenter.x, rotatedBottomRight.y + responseCenter.y, 0.0f,
                                rotatedTopRight.x + responseCenter.x, rotatedTopRight.y + responseCenter.y, 0.0f,
        };

        return finalCoords;

    }

    public static float[] rotateBallCoords(float angle, float[] initialBallCoords) {

        float cosAngle = (float) Math.cos((double) angle);
        float sinAngle = (float) Math.sin((double) angle);

        float cosBallRadius = GameState.ballRadius * cosAngle;
        float sinBallRadius = GameState.ballRadius * sinAngle;

        PointF rotatedTopLeft = new PointF( -cosBallRadius - sinBallRadius, cosBallRadius - sinBallRadius);
        PointF rotatedBottomLeft = new PointF( -cosBallRadius + sinBallRadius, -cosBallRadius - sinBallRadius);
        PointF rotatedBottomRight = new PointF( cosBallRadius + sinBallRadius,  -cosBallRadius + sinBallRadius);
        PointF rotatedTopRight = new PointF( cosBallRadius - sinBallRadius, cosBallRadius + sinBallRadius);

        PointF ballCenter = calculateInitialBallCenter(initialBallCoords);

        float[] finalCoords = { rotatedTopLeft.x + ballCenter.x, rotatedTopLeft.y + ballCenter.y, 0.0f,
                rotatedBottomLeft.x + ballCenter.x, rotatedBottomLeft.y + ballCenter.y, 0.0f,
                rotatedBottomRight.x + ballCenter.x, rotatedBottomRight.y + ballCenter.y, 0.0f,
                rotatedTopRight.x + ballCenter.x, rotatedTopRight.y + ballCenter.y, 0.0f,
        };

        return finalCoords;
    }

    public static PointF calculateInitialBallCenter(float[] initialBallCoords) {
        return new PointF((initialBallCoords[0] + initialBallCoords[6]) / 2, (initialBallCoords[1] + initialBallCoords[4]) / 2);
    }

    public static PointF calculateAndroidBallCenter(PointF arenaBallCenter) {
        return new PointF(arenaBallCenter.x / GameState.xRatioAndroidToArena, GameState.mHeight - (arenaBallCenter.y / GameState.yRatioAndroidToArena));
    }

    public static float getFreqOfBoundary(float area) {
        float min = 20f;
        float max = 10000f;

        float freqMax = 2.0f;
        float freqMin = 0.5f;

        if (area < min) {
            return 2.0f;
        }
        if (area > max) {
            return 0.5f;
        }

        float freqAdjustRate = (freqMax - freqMin);
        float maxAdjusted = max - min;
        float rate = (max- area) / maxAdjusted;
        float areaToFreq = freqAdjustRate / maxAdjusted;
        float areaRate = rate * maxAdjusted;

        return (areaRate * areaToFreq) + (freqMin);
    }

    public static float getIntensityOfWallSound(float surfaceVelocity) {
        float min = 0.3f;
        float max = 20f;

        if (surfaceVelocity < min) {
            return 0f;
        }
        if (surfaceVelocity > max) {
            return 1f;
        }
        return surfaceVelocity / (max - min);
    }

    public static float getIntensityOfBallSound(float ballVelocityChange) {
        float min = 0.3f;
        float max = 6f;

        if (ballVelocityChange < min) {
            return 0f;
        }
        if (ballVelocityChange > max) {
            return 1f;
        }
        return ballVelocityChange / (max - min);
    }
}
