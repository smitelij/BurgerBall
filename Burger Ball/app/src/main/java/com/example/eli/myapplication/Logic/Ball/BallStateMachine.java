package com.example.eli.myapplication.Logic.Ball;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 2/4/2017.
 */

public class BallStateMachine {

    boolean allBallsFired = false;
    float[] initialBallCoords;

    public BallStateMachine (float[] initialBallCoords) {
        this.initialBallCoords = initialBallCoords;
    }

    public void updateBallState(Ball currentBall, BallEngine ballEngine) {

        if (currentBall.isBallActive()) {
            updateActiveBall(currentBall, ballEngine);

        } else if (currentBall.isBallRolling()) {
            updateRollingBall(currentBall, ballEngine);

        } else if (currentBall.isBallStopped()) {
            updateStoppedBall(currentBall, ballEngine);
        }

    }

    private void updateActiveBall(Ball currentBall, BallEngine ballEngine) {
        checkIsBallSlowedOnBoundary(currentBall, ballEngine);
        checkIsBallSlowedOnCorner(currentBall, ballEngine);
        checkIsBallSlowedOnAnotherBall(currentBall, ballEngine);
        checkIsBallReallyStuck(currentBall, ballEngine);
    }

    private void updateRollingBall(Ball currentBall, BallEngine ballEngine) {
        checkHasBallRolledOffEdge(currentBall, ballEngine);
        checkHasBallStopped(currentBall, ballEngine);
        checkIsBallReallyStuck(currentBall, ballEngine);
        //checkHasBallCollided(currentBall, ballEngine);
    }

    private void updateStoppedBall(Ball currentBall, BallEngine ballEngine) {
        checkIsBallReallyStuck(currentBall, ballEngine);
    }

    protected void setAllBallsFired() {
        allBallsFired = true;
    }

    private void checkIsBallSlowedOnBoundary(Ball currentBall, BallEngine ballEngine) {
        //Check if a ball has collided with an identical axis enough times to activate 'slowed ball logic'
        if (ballEngine.isBallSlowedOnConsecutiveCollisionAxis(currentBall)) {
            ballEngine.handleSlowedBall(currentBall);
        }
    }

    private void checkIsBallSlowedOnCorner(Ball currentBall, BallEngine ballEngine) {
        //Check if a ball has collided with any obstacle enough that we can say it is 'stuck'.
        // This would usually mean it is stuck on the corner of an object
        if (ballEngine.isBallSlowedOnCorner(currentBall)) {
            ballEngine.handleStuckBall(currentBall);
        }
    }

    private void checkIsBallSlowedOnAnotherBall(Ball currentBall, BallEngine ballEngine) {
        if (ballEngine.isBallSlowedOnAnotherBall(currentBall)) {
            ballEngine.handleBallOnTopOfBall(currentBall);
        }
    }

    private void checkIsBallReallyStuck(Ball currentBall, BallEngine ballEngine) {
        //If ball has been stuck for a while, we will simply deactivate.
        if (ballEngine.isBallReallyStuck(currentBall)) {
            currentBall.deactivateBall();
        }
    }

    private void checkHasBallRolledOffEdge(Ball currentBall, BallEngine ballEngine) {
        if (ballEngine.getRollTimeForBall(currentBall) < 0) {
            ballEngine.activateBall(currentBall);
            //We need to update the balls current velocity, because if we
            // are on a moving object, then we will forget that velocity
            // when we reactivate the ball.
            PointF currentVelocity = ballEngine.getVelocity(currentBall, 0); //Get balls instantaneous velocity
            currentBall.setVelocity(currentVelocity);
        }
    }

    private void checkHasBallStopped(Ball currentBall, BallEngine ballEngine) {

        if (! (ballEngine.isBallOnFlatObstacle(currentBall))) {
            return;
        }

        if (ballEngine.getVelocity(currentBall,0).length() < GameState.DEACTIVATE_BALL_VELOCITY ) {
            if (ActivateBallLogic.isBallInFiringZone(null, currentBall, initialBallCoords) && !allBallsFired) {
                currentBall.deactivateBall();
            } else {
                ballEngine.stopBall(currentBall);
            }
        }
    }

    private void checkHasBallCollided(Ball currentBall, BallEngine ballEngine) {

        if (ballEngine.getBallCollisionsThisFrame(currentBall) > 0) {
            currentBall.activateBall();
            //We need to update the balls current velocity, because if we
            // are on a moving object, then we will forget that velocity
            // when we reactivate the ball.
            PointF currentVelocity = ballEngine.getVelocity(currentBall, 0); //Get balls instantaneous velocity
            currentBall.setVelocity(currentVelocity);
        }
    }
}
