package com.example.eli.myapplication.Controller;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Model.GameState;
import com.example.eli.myapplication.Model.MovingObstacle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eli on 1/21/2017.
 */

public class BallPhysics {

    private HashMap<Ball, PointF> rollingAccelMap = new HashMap<>();
    private HashMap<Ball, Float> rollTimeMap = new HashMap<>();

    //Keeps track of how many objects this ball collided with in this frame
    //(only > 1 if multiple collisions happened at the exact same time)
    //(includes collisions where this ball is not the main ball)
    private HashMap<Ball, Integer> ballCollisionsThisStep = new HashMap<>();
    private HashMap<Ball, Integer> ballCollisionsThisFrame = new HashMap<>();
    private HashMap<Ball, Integer> boundaryCollisionsThisFrame = new HashMap<>();
    private HashMap<Ball, Integer> sameBoundaryCollisionsThisFrame = new HashMap<>();

    private HashMap<Ball, Collision> lastCollisionMap = new HashMap<>();

    public void moveByFrame(Ball currentBall, float percentOfFrame){
        PointF positionChange = calculatePositionChange(currentBall, percentOfFrame);
        currentBall.updateAABB(positionChange.x, positionChange.y);
    }

    public PointF calculatePositionChange(Ball currentBall, float percentOfFrame){

        if (currentBall.getBallState() == Ball.ballStatus.ACTIVE) {

            //This isn't precisely accurate- we would need much more complicated calculations
            //to perfectly account for gravities affect on displacement. However, taking the average
            //velocity (beginning / end of frame) should be more than accurate enough for our purposes.
            PointF avgVelocity = getAvgVelocity(currentBall, percentOfFrame);
            return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);

        //Otherwise, we have a rolling ball.
        } else {
            PointF avgVelocity = getAvgVelocity(currentBall, percentOfFrame);
            return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);
        }
    }

    public void addBallCollision(Ball currentBall, Collision collision){
        ballCollisionsThisStep = incrementCollisionMapEntry(ballCollisionsThisStep, currentBall);
        ballCollisionsThisFrame = incrementCollisionMapEntry(ballCollisionsThisFrame, currentBall);
        lastCollisionMap.put(currentBall,collision);
    }

    public void addObstacleCollision(Ball currentBall, Collision collision){

        boundaryCollisionsThisFrame = incrementCollisionMapEntry(boundaryCollisionsThisFrame, currentBall);

        if (lastCollisionMap.get(currentBall) == null) {
            sameBoundaryCollisionsThisFrame = incrementCollisionMapEntry(sameBoundaryCollisionsThisFrame, currentBall);
            lastCollisionMap.put(currentBall,collision);
        }

        if (sameLastCollision(currentBall, collision)) {
            System.out.println("same last collision");
            sameBoundaryCollisionsThisFrame = incrementCollisionMapEntry(sameBoundaryCollisionsThisFrame, currentBall);
        } else {
            System.out.println("different last collision");
            sameBoundaryCollisionsThisFrame.put(currentBall, 1);
            lastCollisionMap.put(currentBall, collision);
        }
    }

    private boolean sameLastCollision(Ball currentBall, Collision collision) {
        Collision lastCollision = lastCollisionMap.get(currentBall);
        if (collision.getObstacle().equals(lastCollision.getObstacle())) {
            if (collision.getBoundaryAxis().equals(lastCollision.getBoundaryAxis())) {
                return true;
            }
        }
        return false;
    }

    public int getBoundaryCollisionCountThisFrame(Ball currentBall) {
        if (boundaryCollisionsThisFrame.get(currentBall) == null) {
            System.out.println("bounday collisions this frame: " + 0);
            return 0;
        }
        System.out.println("bounday collisions this frame: " + boundaryCollisionsThisFrame.get(currentBall));
        return boundaryCollisionsThisFrame.get(currentBall);
    }

    public int getSameBoundaryCollisionCountThisFrame(Ball currentBall){
        if (sameBoundaryCollisionsThisFrame.get(currentBall) == null) {
            return 0;
        }
        return sameBoundaryCollisionsThisFrame.get(currentBall);
    }

    public int getBallCollisionsThisFrame(Ball currentBall){
        if (ballCollisionsThisFrame.get(currentBall) == null) {
            return 0;
        }
        return ballCollisionsThisFrame.get(currentBall);
    }

    public int getBallCollisionsThisStep(Ball currentBall) {
        if (ballCollisionsThisStep.get(currentBall) == null) {
            return 1;
        }
        return ballCollisionsThisStep.get(currentBall);
    }

    public PointF getRollingAccelForBall(Ball currentBall) {
        if (rollingAccelMap.get(currentBall) == null) {
            return new PointF(0f,0f);
        }
        return rollingAccelMap.get(currentBall);
    }

    public float getRollTimeForBall(Ball currentBall) {
        if (rollTimeMap.get(currentBall) == null) {
            return 0f;
        }
        return rollTimeMap.get(currentBall);
    }

    public void clearFrameCollisionCount(Ball currentBall){
        sameBoundaryCollisionsThisFrame.put(currentBall, 0);
        ballCollisionsThisFrame.put(currentBall,0);
        boundaryCollisionsThisFrame.put(currentBall, 0);
    }

    //Get a balls velocity after timeStep (takes into account gravity and everything else)
    public PointF getVelocity(Ball currentBall, float timeStep){

        PointF currentVelocity = currentBall.getVelocity();

        //we should either have an active ball,
        if (currentBall.getBallState() == Ball.ballStatus.ACTIVE) {
            return new PointF(currentVelocity.x + (GameState.GRAVITY_CONSTANT.x * timeStep), currentVelocity.y + (GameState.GRAVITY_CONSTANT.y * timeStep));

            //or else we have a rolling ball
        } else if (currentBall.getBallState() == Ball.ballStatus.ROLLING){
            PointF surfaceVelocity = getSurfaceVelocity(currentBall);
            PointF rollingAccel = getRollingAccelForBall(currentBall);

            //Add current velocity, rolling velocity, and moving obstacle velocity.
            // Both rolling velocity and surface velocity can be 0
            return new PointF(currentVelocity.x + (rollingAccel.x * timeStep) + surfaceVelocity.x,
                    currentVelocity.y + (rollingAccel.y * timeStep) + surfaceVelocity.y);

            //should never get here
        } else {
            return new PointF(0f,0f);
        }
    }

    /**
     * This should only be used for rolling balls.
     * @return
     */
    private PointF getSurfaceVelocity(Ball currentBall) {
        Collision lastCollision = lastCollisionMap.get(currentBall);
        if (lastCollision == null) {
            return new PointF(0f,0f);
        }
        if (lastCollision.getObstacle().getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
            // To calculate obstacle velocity, we need to calculate
            // how much of the frame we have moved
            MovingObstacle obstacle = (MovingObstacle) lastCollision.getObstacle();
            return obstacle.getVelocity();
        }
        return new PointF(0f,0f);
    }

    //update a balls velocity after time timeStep
    public void updateVelocityCollision(Ball currentBall, float timeStep){

        //if mNewVelocity exists, then we have updated the balls new velocity elsewhere
        if (currentBall.didBallCollide()) {
            currentBall.updateVelocityWithNewVelocity();

        //otherwise, just update based on gravity
        } else {
            updateVelocityNonCollision(currentBall, timeStep);
        }
    }

    //Update the velocity based on the current acceleration
    // (gravity if an active ball, or rolling accel for a rolling ball
    public void updateVelocityNonCollision(Ball currentBall, float timeStep) {
        PointF ballVelocity = getVelocity(currentBall, timeStep);
        //Subtract surface velocity here so it doesn't accumulate
        if (currentBall.getBallState() == Ball.ballStatus.ROLLING) {
            PointF surfaceVelocity = getSurfaceVelocity(currentBall);
            ballVelocity.set(ballVelocity.x - surfaceVelocity.x, ballVelocity.y - surfaceVelocity.y);
        }
        currentBall.setVelocity(new PointF(ballVelocity.x, ballVelocity.y));
    }

    //Get average velocity from current time until timeStep
    public PointF getAvgVelocity(Ball currentBall, float timeStep){
        PointF currentVelocity = getVelocity(currentBall, 0);
        PointF newVelocity = getVelocity(currentBall, timeStep);
        PointF finalVelocity = new PointF((currentVelocity.x + newVelocity.x) / 2, (currentVelocity.y + newVelocity.y) / 2);
        System.out.println("GET BALL VELOCITY= " + finalVelocity.x + " | " + finalVelocity.y);
        return new PointF((currentVelocity.x + newVelocity.x) / 2, (currentVelocity.y + newVelocity.y) / 2);
    }

    public Collision getLastCollision(Ball currentBall) {
        return lastCollisionMap.get(currentBall);
    }

    //This should probably be moved into game engine-
    // no reason for so much math to be in the ball class
    public void calculateRollingAccel(Ball currentBall) {

        PointF rollingVector;
        PointF collisionAxis = getLastCollision(currentBall).getBoundaryAxis();
        //warning - this code will break if gravity isn't solely in the negative Y-direction
        if (collisionAxis.x > 0 ) {
            rollingVector = new PointF(collisionAxis.y, -collisionAxis.x);
        } else {
            rollingVector = new PointF(-collisionAxis.y, collisionAxis.x);
        }
        double rollingAngle = Math.atan2(rollingVector.y, rollingVector.x);
        float rollingAcceleration = (float) (0.666 * GameState.GRAVITY_CONSTANT.y * Math.sin(rollingAngle));
        PointF rollingAccelVector = new PointF(rollingAcceleration * (float) Math.cos(rollingAngle), rollingAcceleration * (float) Math.sin(rollingAngle));

        rollingAccelMap.put(currentBall, rollingAccelVector);
    }

    public void clearRollingAccel(Ball currentBall) {
        rollingAccelMap.put(currentBall, new PointF(0f,0f));
    }

    public void setInitialRollingVelocity(Ball currentBall) {

        PointF rollingVector;
        PointF collisionAxis = getLastCollision(currentBall).getBoundaryAxis();
        //warning - this code will break if gravity isn't solely in the negative Y-direction
        if (collisionAxis.x > 0 ) {
            rollingVector = new PointF(collisionAxis.y, -collisionAxis.x);
        } else {
            rollingVector = new PointF(-collisionAxis.y, collisionAxis.x);
        }
        PointF rollingVectorNormal = new PointF(rollingVector.x / rollingVector.length(), rollingVector.y / rollingVector.length());
        PointF currentVelocity = getVelocity(currentBall, 0);
        float totalVelocity = currentVelocity.length();
        PointF directionalVelocity;
        if (currentVelocity.y > 0) {
            directionalVelocity = new PointF(-rollingVectorNormal.x * totalVelocity, -rollingVectorNormal.y * totalVelocity);
        } else {
            directionalVelocity = new PointF(rollingVectorNormal.x * totalVelocity, rollingVectorNormal.y * totalVelocity);
        }

        //Before a ball starts rolling, it undergoes a number of collisions each resulting in loss due to elasticity.
        //Here, we add that loss back in.
        //Add an extra 1/2 due to frame straddling possibility (some collisions occurred in the previous frame).
        int affectedFrames = GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME + (GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME / 2);
        float elasticLoss = (float) Math.pow(GameState.ELASTIC_CONSTANT, affectedFrames);
        PointF newVelocity = new PointF(directionalVelocity.x / elasticLoss, directionalVelocity.y / elasticLoss);
        currentBall.setVelocity(newVelocity);
    }

    public void calculateRollTime(Ball currentBall) {
        PointF boundaryAxis = getLastCollision(currentBall).getBoundaryAxis();
        PointF[] obstacleCoords = getLastCollision(currentBall).getObstacle().get2dCoordArray();

        PointF vertexA = new PointF();
        PointF vertexB = new PointF();
        for (int index = 0; index < obstacleCoords.length; index++) {
            //NOTE - THIS IS THE SAME CODE AS in collision detection so maybe should move it to a shared location
            //We need to make a line between two vertexes
            int vertexAIndex = index;
            int vertexBIndex = (index + 1) % obstacleCoords.length; //We need to wrap back to the first vertex at the end, so use modulus

            vertexA = obstacleCoords[vertexAIndex];
            vertexB = obstacleCoords[vertexBIndex];

            //formula to find the normal vector from a line is (-y, x)
            float xComponent = -(vertexB.y - vertexA.y);
            float yComponent = (vertexB.x - vertexA.x);

            //create vector and normalize
            PointF normalAxis = new PointF(xComponent, yComponent);
            float normalAxisLength = normalAxis.length();
            normalAxis.set(normalAxis.x / normalAxisLength, normalAxis.y / normalAxisLength);

            if (normalAxis.equals(boundaryAxis.x, boundaryAxis.y)) {
                break;
            }
        }

        //Make sure vertices are oriented correctly (A is above B)
        if (vertexA.y < vertexB.y) {
            PointF temp = new PointF(vertexA.x, vertexA.y);
            vertexA = vertexB;
            vertexB = temp;
        }

        PointF ballCenter = currentBall.getCenter();
        PointF ballPointOnLine = projectPointOntoLine(vertexA, vertexB, ballCenter);
        PointF remainingLength = new PointF(vertexB.x - ballPointOnLine.x, vertexB.y - ballPointOnLine.y);

        double quadA = getRollingAccelForBall(currentBall).length() / 2;
        double quadB = currentBall.getVelocity().length();
        double quadC = -remainingLength.length();

        double squareRoot = Math.sqrt((quadB * quadB) - (4*quadA*quadC));
        double result1 = (-quadB + squareRoot) / (2*quadA);
        double result2 = (-quadB - squareRoot) / (2*quadA);

        float rollTime;
        if ((result1 < 0) && (result2 < 0)) {
            rollTime = 0;
        } else if (result1 < 0) {
            rollTime = (float) result2;
        } else {
            rollTime = (float) result1;
        }
        rollTime = rollTime * 1.05f;
        rollTimeMap.put(currentBall, rollTime);
    }

    private PointF projectPointOntoLine(PointF vertexA, PointF vertexB, PointF pointToProject) {
        PointF pointVector = new PointF(pointToProject.x - vertexA.x, pointToProject.y - vertexA.y);
        PointF line = new PointF(vertexB.x - vertexA.x, vertexB.y - vertexA.y);
        PointF lineUnit = new PointF(line.x / line.length(), line.y / line.length());
        float scalar = GameState.dotProduct(pointVector, lineUnit);
        PointF pointOffset = new PointF(lineUnit.x * scalar, lineUnit.y * scalar);
        PointF finalPoint = new PointF(vertexA.x + pointOffset.x, vertexA.y + pointOffset.y);
        return finalPoint;
    }


    public void handleSlowedBall(Ball currentBall) {
        if (isBallOnFlatObstacle(currentBall)) {
            if (isBallOnMovingObstacle(currentBall)) {
                //Keep the ball 'rolling' if the surface is still moving
                currentBall.rollingBall();
                currentBall.setVelocity(new PointF(0f,0f));
            } else {
                currentBall.stopBall();
            }
        } else {
            currentBall.rollingBall();
            calculateRollingAccel(currentBall);
            setInitialRollingVelocity(currentBall);
            calculateRollTime(currentBall);
        }


    }

    /**
     * This function will break if gravity changes substantially.
     * @param currentBall
     * @return
     */
    private boolean isBallOnFlatObstacle(Ball currentBall) {
        if (getLastCollision(currentBall).getBoundaryAxis().equals(0, -1)) {
            return true;
        }
        return false;
    }

    private boolean isBallOnMovingObstacle(Ball currentBall) {
        if (getLastCollision(currentBall).getObstacle().getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
            return true;
        }
        return false;
    }

    public void handleBallOnTopOfBall(Ball stuckBall) {

        Ball otherBall = (Ball) getLastCollision(stuckBall).getObstacle();

        //Determine which is on top
        Ball topBall;
        Ball bottomBall;
        if (stuckBall.getCenter().y > otherBall.getCenter().y){
            topBall = stuckBall;
            bottomBall = otherBall;
        } else {
            topBall = otherBall;
            bottomBall = stuckBall;
        }

        PointF topBallCenter = topBall.getCenter();
        PointF bottomBallCenter = bottomBall.getCenter();
        PointF distanceVector = new PointF(topBallCenter.x - bottomBallCenter.x, topBallCenter.y - bottomBallCenter.y);
        PointF distanceVectorNormal = new PointF(distanceVector.x / distanceVector.length(), distanceVector.y / distanceVector.length());
        topBall.setVelocity(distanceVectorNormal);
    }

    public void handleStuckBall(Ball stuckBall) {
        PointF collisionAxis = getLastCollision(stuckBall).getBoundaryAxis();
        //displace ball away from the collision axis
        stuckBall.setVelocity(new PointF(-collisionAxis.x, -collisionAxis.y));
    }

    public void clearCollisionHistories() {
        ballCollisionsThisFrame.clear();
        ballCollisionsThisStep.clear();
        boundaryCollisionsThisFrame.clear();
        sameBoundaryCollisionsThisFrame.clear();
    }

    private HashMap<Ball,Integer> incrementCollisionMapEntry(HashMap<Ball, Integer> map, Ball currentKey) {
        if (map.get(currentKey) == null) {
            map.put(currentKey, 1);
            return map;
        }
        int currentValue = map.get(currentKey);
        map.put(currentKey, currentValue+1);
        return map;
    }

    /**
     *
     * @param currentBall
     */
    public void moveRollingBall(Ball currentBall, float timeStep) {

        if (currentBall.isBallRolling()) {
            if (getRollTimeForBall(currentBall) < 0) {
                activateBall(currentBall);
                //We need to update the balls current velocity, because if we
                // are on a moving object, then we will forget that velocity
                // when we reactivate the ball.
                PointF currentVelocity = getVelocity(currentBall, 0); //Get balls instantaneous velocity
                currentBall.setVelocity(currentVelocity);
            } else {
                moveByFrame(currentBall, timeStep);
            }
        }
    }

    public boolean isBallSlowedOnConsecutiveCollisionAxis(Ball currentBall) {
        return (getSameBoundaryCollisionCountThisFrame(currentBall) > (GameState.FRAME_SIZE * GameState.SLOWED_BALL_CONSTANT));
    }

    public boolean isBallSlowedOnAnyAxis(Ball currentBall) {
        return (getBoundaryCollisionCountThisFrame(currentBall) > (GameState.FRAME_SIZE * GameState.STUCK_POINT_CONSTANT));
    }

    public boolean isBallSlowedOnAnotherBall(Ball currentBall) {
        System.out.println("get ball collisions this frame: " + getBallCollisionsThisFrame(currentBall));
        return (getBallCollisionsThisFrame(currentBall) > GameState.BALL_BOUNCE_CONSTANT * GameState.FRAME_SIZE);
    }

    public boolean isBallReallyStuck(Ball currentBall) {
        return (getBoundaryCollisionCountThisFrame(currentBall) > (GameState.FRAME_SIZE * GameState.DEACTIVATE_STUCK_BALL_CONSTANT));
    }

    public boolean shouldElasticLossBeAppliedForCollision(Ball currentBall) {
        return (getBoundaryCollisionCountThisFrame(currentBall) < GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME);
    }

    public void updateBallVelocity(Ball currentBall, boolean collisionOccurred, float timeStep) {

        if (currentBall.isBallInactive()) {
            return;
        }

        if (!collisionOccurred) {

            if (currentBall.isBallStopped()) {
                return;
            }

            // just factor in the current acceleration and return a new velocity.
            updateVelocityNonCollision(currentBall, timeStep);
            ballCollisionsThisStep.put(currentBall, 0);
            return;
        }

        //At this point we know a collision occurred.

        //Handle stopped balls
        if (currentBall.isBallStopped()) {
            if (currentBall.didBallCollide()) {
                updateVelocityCollision(currentBall, timeStep);
                activateBall(currentBall); //In case ball was previously stopped
                ballCollisionsThisStep.put(currentBall, 0);
                return;
            }
        }

        //Handle rolling balls
        if (currentBall.isBallRolling()) {
            if (currentBall.didBallCollide()) {
                updateVelocityCollision(currentBall, timeStep);
                reactivateRollingBall(currentBall); //In case ball was previously stopped
                ballCollisionsThisStep.put(currentBall, 0);
                return;
            } else {
                updateVelocityNonCollision(currentBall, timeStep);
                return;
            }
        }

        //Handle active balls
        if (currentBall.isBallActive()) {
            if (currentBall.didBallCollide()) {
                updateVelocityCollision(currentBall, timeStep);
            } else {
                updateVelocityNonCollision(currentBall, timeStep);
            }
            ballCollisionsThisStep.put(currentBall, 0);
        }

        return;
    }

    public void decreaseRollTime(Ball currentBall, float timeStep) {
        float currentRollTime = getRollTimeForBall(currentBall);
        rollTimeMap.put(currentBall, currentRollTime - timeStep);
    }

    private void reactivateRollingBall(Ball currentBall) {
        activateBall(currentBall);
        clearRollingAccel(currentBall);
    }

    protected void activateBall(Ball currentBall) {
        currentBall.activateBall();
        clearFrameCollisionCount(currentBall);
    }

    //Get the available velocity (amount that is free to be transferred) at timeStep
    public PointF getAvailableVelocity(Ball currentBall, float timeStep){
        PointF newVelocity = getVelocity(currentBall, timeStep);
        int ballCollisions = getBallCollisionsThisStep(currentBall);
        return new PointF(newVelocity.x / ballCollisions, newVelocity.y / ballCollisions);
    }

}
