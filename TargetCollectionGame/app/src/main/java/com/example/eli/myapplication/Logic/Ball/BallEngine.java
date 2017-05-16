package com.example.eli.myapplication.Logic.Ball;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;
import com.example.eli.myapplication.Model.MovingObstacle;
import com.example.eli.myapplication.Model.Obstacle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eli on 1/21/2017.
 */

public class BallEngine {

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

    private BallStateMachine ballStateMachine;

    public BallEngine(float[] initialBallCoords) {
        ballStateMachine = new BallStateMachine(initialBallCoords);
    }

    public void updateBallState(Ball currentBall) {
        ballStateMachine.updateBallState(currentBall, this);
    }

    public void moveByFrame(Ball currentBall, float percentOfFrame){
        PointF positionChange = calculatePositionChange(currentBall, percentOfFrame);
        currentBall.updateAABB(positionChange.x, positionChange.y);
    }

    /**
     * Calculate the position change of a ball after percentOfFrame, by computing the average
     * velocity starting at current time and and ending at percentOfFrame. This is a slight
     * simplification of how acceleration, velocity, and displacement are related, but for
     * our purposes and over small frame steps, it is close enough.
     * @param currentBall
     * @param percentOfFrame
     * @return
     */
    public PointF calculatePositionChange(Ball currentBall, float percentOfFrame){
        PointF avgVelocity = getAvgVelocity(currentBall, percentOfFrame);
        return new PointF(avgVelocity.x * percentOfFrame, avgVelocity.y * percentOfFrame);
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

    private PointF getRollingAccelForBall(Ball currentBall) {
        if (isBallOnFlatObstacle(currentBall)) {
            return getCurrentFlatRollDeceleration(currentBall);
        }
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

    /**
     * Get a balls velocity at the current moment + timeStep
     * There are two different velocity calculations, one for active balls (bouncing around),
     * and one for rolling balls (moving linearly across a single surface.
     * @param currentBall
     * @param timeStep
     * @return
     */
    public PointF getVelocity(Ball currentBall, float timeStep){

        PointF currentVelocity = currentBall.getVelocity();

        //we should either have an active ball,
        if (currentBall.isBallActive()) {
            return new PointF(currentVelocity.x + (GameState.GRAVITY_CONSTANT.x * timeStep), currentVelocity.y + (GameState.GRAVITY_CONSTANT.y * timeStep));

            //or else we have a rolling ball
        } else if (currentBall.isBallRolling()){

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

    private PointF getCurrentFlatRollDeceleration(Ball currentBall) {
        System.out.println("get current flat roll deceleration");
        PointF currentVelocity = currentBall.getVelocity();
        System.out.println("current ball velocity: " + currentVelocity);
        System.out.println("flat roll deceleration: " + new PointF(-currentVelocity.x * GameState.ROLLING_DECELERATION_CONSTANT,0f));
        return new PointF(-currentVelocity.x * GameState.ROLLING_DECELERATION_CONSTANT,0f);

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
            System.out.println("ball collided?");
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
        if (currentBall.isBallRolling()) {
            PointF surfaceVelocity = getSurfaceVelocity(currentBall);
            ballVelocity.set(ballVelocity.x - surfaceVelocity.x, ballVelocity.y - surfaceVelocity.y);
        }
        System.out.println("setting non collision velocity: " + new PointF(ballVelocity.x, ballVelocity.y));
        currentBall.setVelocity(new PointF(ballVelocity.x, ballVelocity.y));
    }

    //Get average velocity between current time and timeStep
    public PointF getAvgVelocity(Ball currentBall, float timeStep){
        PointF currentVelocity = getVelocity(currentBall, 0);
        PointF finalVelocity = getVelocity(currentBall, timeStep);
        return new PointF((currentVelocity.x + finalVelocity.x) / 2, (currentVelocity.y + finalVelocity.y) / 2);
    }

    public Collision getLastCollision(Ball currentBall) {
        return lastCollisionMap.get(currentBall);
    }

    private void setRollingAcceleration(Ball currentBall, boolean flatObstacle) {
        if (flatObstacle) {
            return;
        }

        setRollingAccelIncline(currentBall);

    }

    private void setRollingAccelIncline(Ball currentBall) {

        PointF rollingVector = calculateRollingVectorIncline(currentBall);
        double rollingAngle = Math.atan2(rollingVector.y, rollingVector.x);
        float rollingAcceleration = (float) (0.666 * GameState.GRAVITY_CONSTANT.y * Math.sin(rollingAngle));
        PointF rollingAccelVector = new PointF(rollingAcceleration * (float) Math.cos(rollingAngle), rollingAcceleration * (float) Math.sin(rollingAngle));

        rollingAccelMap.put(currentBall, rollingAccelVector);
    }

    private void clearRollingAccel(Ball currentBall) {
        rollingAccelMap.put(currentBall, new PointF(0f,0f));
    }

    private void setInitialRollingVelocity(Ball currentBall, boolean flatSurface) {
        System.out.println("set initial rolling velocity.");
        PointF rollingVector = calculateRollingVector(currentBall, flatSurface);
        PointF directionalVelocity = calculateDirectionalVelocity(currentBall, rollingVector, flatSurface);
        PointF finalVelocity = increaseVelocityForElasticLoss(directionalVelocity);
        currentBall.setVelocity(finalVelocity);
    }

    /**
     * Before a ball starts rolling, it undergoes a number of collisions each resulting in loss
     * due to elasticity. Here, we add that loss back in.
     * @param directionalVelocity
     * @return
     */
    private PointF increaseVelocityForElasticLoss(PointF directionalVelocity) {
        int affectedFrames = GameState.MAX_ELASTIC_COLLISIONS_PER_FRAME;
        float elasticLoss = (float) Math.pow(GameState.ELASTIC_CONSTANT, affectedFrames);
        return new PointF(directionalVelocity.x / elasticLoss, directionalVelocity.y / elasticLoss);
    }

    private PointF calculateRollingVector(Ball currentBall, boolean flatSurface) {
        if (flatSurface) {
            return calculateRollingVectorFlat(currentBall);
        } else {
            return calculateRollingVectorIncline(currentBall);
        }
    }

    /**
     * Calculate a vector representation of the surface the ball will be rolling down
     * @param currentBall
     * @return
     */
    private PointF calculateRollingVectorIncline(Ball currentBall) {
        PointF collisionAxis = getLastCollision(currentBall).getBoundaryAxis();
        //warning - this code will break if gravity isn't solely in the negative Y-direction
        if (collisionAxis.x > 0 ) {
            return new PointF(collisionAxis.y, -collisionAxis.x);
        } else {
            return new PointF(-collisionAxis.y, collisionAxis.x);
        }
    }

    private PointF calculateRollingVectorFlat(Ball currentBall) {
        PointF currentVelocity = getVelocity(currentBall, 0);
        if (currentVelocity.x > 0) {
            return new PointF(1f,0f);
        } else {
            return new PointF(-1f,0f);
        }
    }

    private PointF calculateDirectionalVelocity(Ball currentBall, PointF rollingVector, boolean flatSurface) {
        PointF currentVelocity = getVelocity(currentBall, 0);
        float totalVelocity = currentVelocity.length();

        if (flatSurface) {
            return new PointF(rollingVector.x * totalVelocity, 0f);
        }
        if (currentVelocity.y > 0) {
            return new PointF(-rollingVector.x * totalVelocity, -rollingVector.y * totalVelocity);
        } else {
            return new PointF(rollingVector.x * totalVelocity, rollingVector.y * totalVelocity);
        }
    }

    private void setRollTime(Ball currentBall, boolean bottomBoundary) {

        if (bottomBoundary) {
            setInfiniteRollTime(currentBall);
            return;
        }

        PointF remainingLength = calculateRemainingDistanceToBeRolled(currentBall);
        System.out.println("remaining length to be rolleD: " + remainingLength);
        float rollTime = calculateQuadraticRollTime(currentBall, remainingLength);
        System.out.println("remaining time to be rolled: " + rollTime);
        rollTime = rollTime * 1.05f; //Add a bit extra so we don't get stuck on a corner after rolling.
        rollTimeMap.put(currentBall, rollTime);
    }

    /**
     * If a ball is rolling across the bottom, then we set the roll time to be infinite.
     * @param currentBall
     */
    private void setInfiniteRollTime(Ball currentBall) {
        rollTimeMap.put(currentBall, GameState.LARGE_NUMBER);
    }

    private PointF calculateRemainingDistanceToBeRolled(Ball currentBall) {
        ArrayList<PointF> surfaceVertices = getSurfaceVertices(currentBall);

        PointF vertexA = surfaceVertices.get(0);
        PointF vertexB = surfaceVertices.get(1);

        PointF ballCenter = currentBall.getCenter();
        PointF ballPointOnLine = projectPointOntoLine(vertexA, vertexB, ballCenter);
        return new PointF(vertexB.x - ballPointOnLine.x, vertexB.y - ballPointOnLine.y);
    }

    /**
     * Helper function for setRollTime. Performs a quadratic calculation, and returns
     * the length of time that the ball will be rolling.
     * @param currentBall
     * @param remainingDistance - Distance vector remaining on the surface that ball is rolling down
     * @return
     */
    private float calculateQuadraticRollTime(Ball currentBall, PointF remainingDistance) {
        double quadA = getRollingAccelForBall(currentBall).length() / 2;
        double quadB = currentBall.getVelocity().length();
        double quadC = -remainingDistance.length();

        //If ball is rolling on flat obstacle, accel will always be negative (slowing down)
        if (isBallOnFlatObstacle(currentBall)) {
            quadA = -quadA;
        } else {
            //If ball is rolling UP a slanted surface, set velocity to be negative.
            if (currentBall.getVelocity().y > 0) {
                quadB = -quadB;
            }
        }

        double squareRoot = Math.sqrt((quadB * quadB) - (4*quadA*quadC));
        double result1 = (-quadB + squareRoot) / (2*quadA);
        double result2 = (-quadB - squareRoot) / (2*quadA);

        if ((result1 < 0) && (result2 < 0)) {
            return 0;
        } else if (result1 < 0) {
            return (float) result2;
        } else {
            return (float) result1;
        }
    }

    private ArrayList<PointF> getSurfaceVertices(Ball currentBall) {
        Obstacle obstacle = (Obstacle) getLastCollision(currentBall).getObstacle();

        PointF collisionAxis = getLastCollision(currentBall).getBoundaryAxis();

        int boundaryIndex = 0;
        for (int index = 0; index < obstacle.get2dCoordArray().length; index++) {
            PointF currentBoundaryAxis = obstacle.getBoundaryAxis(index);
            if (currentBoundaryAxis.equals(collisionAxis.x, collisionAxis.y)) {
                boundaryIndex = index;
                break;
            }
        }

        ArrayList<PointF> vertices = obstacle.getSurfaceVertices(boundaryIndex);

        //Make sure vertices are oriented correctly (first is above second)
        if (vertices.get(0).y < vertices.get(1).y) {
            PointF first = vertices.get(0);
            vertices.remove(0);
            vertices.add(first);
        }
        return vertices;
    }

    private PointF projectPointOntoLine(PointF vertexA, PointF vertexB, PointF pointToProject) {
        PointF pointVector = new PointF(pointToProject.x - vertexA.x, pointToProject.y - vertexA.y);
        PointF line = new PointF(vertexB.x - vertexA.x, vertexB.y - vertexA.y);
        PointF lineUnit = new PointF(line.x / line.length(), line.y / line.length());
        float scalar = CommonFunctions.dotProduct(pointVector, lineUnit);
        PointF pointOffset = new PointF(lineUnit.x * scalar, lineUnit.y * scalar);
        PointF finalPoint = new PointF(vertexA.x + pointOffset.x, vertexA.y + pointOffset.y);
        return finalPoint;
    }


    public void handleSlowedBall(Ball currentBall) {
        boolean onFlatSurface = isBallOnFlatObstacle(currentBall);
        boolean onBottomObstacle = isBallOnBottomObstacle(currentBall);

        //Order is important here- initial rolling velocity must be calculated before ball status
        // has been updated to rolling, but roll time must be calculated after.
        setRollingAcceleration(currentBall, onFlatSurface);
        setInitialRollingVelocity(currentBall, onFlatSurface);
        currentBall.rollingBall();
        setRollTime(currentBall, onBottomObstacle);
    }

    /**
     * This function will break if gravity changes substantially.
     * @param currentBall
     * @return
     */
    public boolean isBallOnFlatObstacle(Ball currentBall) {
        if (getLastCollision(currentBall).getBoundaryAxis().equals(0, -1)) {
            return true;
        }
        return false;
    }

    private boolean isBallOnBottomObstacle(Ball currentBall) {
        Obstacle obstacle = (Obstacle) getLastCollision(currentBall).getObstacle();
        return (obstacle.isBottomBoundary());
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
        System.out.println("HANDLING STUCK BALL");
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


    public boolean isBallSlowedOnConsecutiveCollisionAxis(Ball currentBall) {
        return (getSameBoundaryCollisionCountThisFrame(currentBall) > (GameState.FRAME_SIZE * GameState.SLOWED_BALL_CONSTANT));
    }

    public boolean isBallSlowedOnCorner(Ball currentBall) {
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

    public void activateBall(Ball currentBall) {
        currentBall.activateBall();
        clearFrameCollisionCount(currentBall);
    }

    public void stopBall(Ball currentBall) {
        currentBall.stopBall();
        clearFrameCollisionCount(currentBall);
    }

    protected void deactivateBall(Ball currentBall) {
        currentBall.deactivateBall();
    }

    //Get the available velocity (amount that is free to be transferred) at timeStep
    public PointF getAvailableVelocity(Ball currentBall, float timeStep){
        PointF newVelocity = getVelocity(currentBall, timeStep);
        int ballCollisions = getBallCollisionsThisStep(currentBall);
        return new PointF(newVelocity.x / ballCollisions, newVelocity.y / ballCollisions);
    }

    public void setAllBallsFired() {
        ballStateMachine.setAllBallsFired();
    }

    public void updateSpinRollingBall (Ball currentBall){

        PointF currentVelocity = getVelocity(currentBall, 0);

        Collision lastCollision = lastCollisionMap.get(currentBall);
        PointF lastBoundaryAxis = lastCollision.getBoundaryAxis();

        PointF surfaceVector = new PointF(-lastBoundaryAxis.y, lastBoundaryAxis.x);

        float surfaceVelocity = CommonFunctions.dotProduct(currentVelocity, surfaceVector);

        System.out.println("surface velocity: " + surfaceVelocity);
        currentBall.setSpin(surfaceVelocity / -8);
    }

}
