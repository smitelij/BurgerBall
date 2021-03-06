package com.example.eli.myapplication.Logic;

import android.graphics.PointF;
import android.util.Log;

import com.example.eli.myapplication.Logic.Ball.BallEngine;
import com.example.eli.myapplication.Model.InvalidBallPositionException;
import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;
import com.example.eli.myapplication.Model.Interactable;
import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Model.MovingObstacle;
import com.example.eli.myapplication.Model.Obstacle;
import com.example.eli.myapplication.Model.Target;

import java.util.ArrayList;


/**
 * This class contains the methods used to detect collisions between balls and obstacles.
 * It is intended to be initialized once per frame step within the GameEngine loop, because
 * a collection of Collisions is maintained between each iteration. This collection is
 * reviewed upon completion of collision detection by the CollisionHandling class.
 */
public class CollisionDetection {

    private BallEngine ballEngine;

    public CollisionDetection(BallEngine ballEngine) {
        this.ballEngine = ballEngine;
    }

    private class Penetration {
        PointF mNormalAxis;
        float mPenetrationDistance;
        PointF mVertex;
        boolean mNearestVertexAxis;

        Penetration(PointF normalAxis, float penetrationDistance, PointF vertex, boolean nearestVertexAxis){
            mNormalAxis = normalAxis;
            mPenetrationDistance = penetrationDistance;
            mVertex = vertex;
            mNearestVertexAxis = nearestVertexAxis;
        }
    }

    private ArrayList<Penetration> penetrationHistory = new ArrayList<Penetration>();
    private ArrayList<Collision> mCollisions = new ArrayList<>();

    public ArrayList<Collision> getCollisions(){
        return mCollisions;
    }

    public void addPenetrationToHistory(PointF axis, float penetrationDistance, PointF vertex, boolean nearestVertexAxis){
        penetrationHistory.add(new Penetration(axis, penetrationDistance, vertex, nearestVertexAxis));
    }

    public void clearPenetrationHistory(){
        penetrationHistory.clear();
    }

    //closest axis = minimum penetration
    public Penetration findClosestAxis(Ball ball, Obstacle obstacle){

        //sanity check
        if (penetrationHistory.size() == 0) {
            return null;
        }

        //set the defaults
        float minPenetration = GameState.SMALL_NUMBER;
        Penetration minHistoryItem = null;

        for (Penetration history : penetrationHistory) {

            //Since penetration distances are negative numbers, we want to find the largest value (i.e. closest to zero)
            //to find the minimum penetration.
            if (history.mPenetrationDistance > minPenetration) {
                minHistoryItem = history;
                minPenetration = history.mPenetrationDistance;

                //This should mean that we have two parallel axis's. We must determine which one is the intersecting one.
            } else if (history.mPenetrationDistance == minPenetration) {

                float currentDistance = calculateBallDistanceFromAxis(history, ball);
                float previousDistance = calculateBallDistanceFromAxis(minHistoryItem, ball);

                if (currentDistance < previousDistance) {
                    minHistoryItem = history;
                    minPenetration = history.mPenetrationDistance;
                }
            }
        }

        return minHistoryItem;
    }

    private float calculateBallDistanceFromAxis(Penetration penetration, Ball ball) {
        PointF surfaceVector = new PointF(-penetration.mNormalAxis.y, penetration.mNormalAxis.x);
        PointF nearestPoint = penetration.mVertex;
        PointF ballPos = ball.getCenter();
        PointF ballToPointVector = new PointF(ballPos.x - nearestPoint.x, ballPos.y - nearestPoint.y);

        float ballSurfaceProjDistance = CommonFunctions.dotProduct(ballToPointVector, surfaceVector);
        PointF ballSurfaceProj = new PointF(surfaceVector.x * ballSurfaceProjDistance, surfaceVector.y * ballSurfaceProjDistance);

        PointF ballSurfaceProjCoord = new PointF(nearestPoint.x + ballSurfaceProj.x, nearestPoint.y + ballSurfaceProj.y);
        return new PointF(ballPos.x - ballSurfaceProjCoord.x, ballPos.y - ballSurfaceProjCoord.y).length();
    }

    /**
     * Exclude testing certain objects (inactive balls, balls that haven't been moved
     * yet this frame, etc.), based on the state that the ball is in.
     * @param otherObject
     * @return TRUE if we should test this object, FALSE if we don't need to test
     */
    public boolean ballCollisionPreChecks(Ball currentBall, Interactable otherObject) {

        //For stopped balls, the only thing we need to test are moving obstacles.
        if (currentBall.isBallStopped()) {
            if (otherObject.getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
                return true;
            }
            return false;

        //Defer to moving ball collision prechecks
        } else if (currentBall.isBallActive()) {

            return movingBallCollisionPrechecks(currentBall, otherObject);

        //For rolling balls, we need to exclude the current rolling surface.
        //After that, follow the same logic as active balls.
        } else if (currentBall.isBallRolling()) {

            Interactable obstacleRollingDown = ballEngine.getLastCollision(currentBall).getObstacle();
            if (obstacleRollingDown.equals(otherObject)) {
                return false;
            }
            return movingBallCollisionPrechecks(currentBall, otherObject);

        }

        return false;
    }

    /**
     * For moving balls, check all obstacles, and check all moving balls that have been advanced.
     * @param currentBall
     * @param otherObject
     * @return whether or not this Object is eligible for further collision testing
     */
    private boolean movingBallCollisionPrechecks(Ball currentBall, Interactable otherObject) {

        //Do pre-checks if the other obstacle is a ball
        if (otherObject.getType() == GameState.INTERACTABLE_BALL) {
            Ball tempBall = (Ball) otherObject;

            //Don't need to check inactive balls
            if (tempBall.isBallInactive()) {
                return false;
            }

            //If the other ball is moving, make sure it has been advanced this frame.
            //The purpose of this is to insure we only check each ball-pair once.
            // For example, the first ball to be advanced each frame would not be collision checked
            // with any balls, but the last ball would be checked with every ball.
            if (tempBall.isBallMoving() && !tempBall.hasBallBeenAdvanced()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This helper method reduces complexity by calling one of three collision detection
     * methods, based on the obstacle type. One other approach I considered was having
     * each object type (Ball / Target / Obstacle) override an abstract collision detection method
     * in Interactable. However, I rejected this due to the increased complexity this would add
     * to each object class, preferring to keep all collision detection code isolated in one class.
     */
    public void detailedCollisionTesting(Ball ball, Interactable object, float timeStep) throws InvalidBallPositionException {

        if (object.getType()== GameState.INTERACTABLE_BALL){
            doBallCollisionDetection(ball, (Ball) object, timeStep);
            return;
        }

        if ((object.getType()== GameState.INTERACTABLE_OBSTACLE) || (object.getType()== GameState.INTERACTABLE_MOVING_OBSTACLE)){
            doPolygonCollisionDetection(ball, (Obstacle) object, timeStep);
            return;
        }

        if (object.getType()== GameState.INTERACTABLE_TARGET){
            doTargetCollisionDetection(ball, (Target) object, timeStep);
            return;
        }

    }

    private void doBallCollisionDetection(Ball ball1, Ball ball2, float timeStep) throws InvalidBallPositionException {
        PointF ball1center;
        PointF ball2center;

        PointF distanceVector;
        float distance;

        ball1center = ball1.getCenter();
        ball2center = ball2.getCenter();

        distanceVector = new PointF(ball1center.x - ball2center.x, ball1center.y - ball2center.y);
        distance = distanceVector.length();

        if (distance >= (ball1.getRadius() + ball2.getRadius())){
            return;
        }

        //We now can be sure that there was a collision.
        //Calculate more collision info, such as timing, and save the collision event
        calculateBallBallCollisionInfo(ball1, ball2, timeStep);
    }

    //This is almost exactly the same as doBallCollisionDetection.
    //However, I think it is more clear to have a separate method, so we can name variables more
    // accurately, and avoid checking the type for any different code that is needed.
    private void doTargetCollisionDetection(Ball ball, Target target, float timeStep){
        PointF ballCenter;
        PointF targetCenter;

        PointF distanceVector;
        float distance;

        ballCenter = ball.getCenter();
        targetCenter = target.getCenter();

        distanceVector = new PointF(ballCenter.x - targetCenter.x, ballCenter.y - targetCenter.y);
        distance = distanceVector.length();

        if (distance >= (ball.getRadius() + target.getRadius())){
            return;
        }

        //We now can be sure that there was a collision.
        //Calculate more collision info, such as timing, and save the collision event
        calculateBallPointCollisionInfo(ball, targetCenter, target, timeStep, target.getRadius());
    }

    /**
     * This method calculates the precise collision time between a ball and an obstacle.
     * It makes use of a penetration object, which contains data about how far into an obstacle
     * the ball penetrated, as well as the ball velocity and the current time step.
     *
     * PARAMS:
     *   ball - The current ball
     *   penetration - Object containing information about the penetration distance and axis
     *   obstacle - The obstacle that has been struck
     *   timeStep - The step (length) of the current iteration
     */
    private void calculateBallBoundaryCollisionInfo(Ball ball, Penetration penetration, Interactable obstacle, float timeStep){

        //grab information from the penetration object
        PointF boundaryAxis = penetration.mNormalAxis;
        float penetrationDistance = penetration.mPenetrationDistance;

        //get general info about the ball and frame
        PointF ballVelocity = ballEngine.getAvgVelocity(ball, timeStep);
        PointF prevVelocityStep;
        PointF totalVelocity;

        prevVelocityStep = new PointF(ballVelocity.x * timeStep, ballVelocity.y * timeStep);
        totalVelocity = ballVelocity;

        //Calculate the angle of the ball's velocity against the boundary axis
        double prevAngle = Math.acos(CommonFunctions.dotProduct(boundaryAxis, totalVelocity) / (boundaryAxis.length() * totalVelocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetrationDistance) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        //once a ball gets really close, this can happen... not sure why
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //add this collision to the collection of collisions this frame
        Collision curCollision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(curCollision);
    }

    private void calculateBallMovingBoundaryCollisionInfo(Ball ball, Penetration penetration, Interactable obstacle, float timeStep) throws InvalidBallPositionException {

        //grab information from the penetration object
        PointF boundaryAxis = penetration.mNormalAxis;
        float penetrationDistance = penetration.mPenetrationDistance;

        //get general info about the ball and frame
        PointF ballVelocity = ballEngine.getAvgVelocity(ball, timeStep);
        PointF prevVelocityStep;
        PointF totalVelocity;

        MovingObstacle tempObstacle = (MovingObstacle) obstacle;
        PointF obstacleVelocity = tempObstacle.getVelocity();
        prevVelocityStep = new PointF((ballVelocity.x - obstacleVelocity.x) * timeStep, (ballVelocity.y - obstacleVelocity.y) * timeStep);

        totalVelocity = new PointF(ballVelocity.x - obstacleVelocity.x, ballVelocity.y - obstacleVelocity.y);

        //Calculate the angle of the ball's velocity against the boundary axis
        if (totalVelocity.length() == 0) {
            throw new InvalidBallPositionException(boundaryAxis);
        }

        double prevAngle = Math.acos(CommonFunctions.dotProduct(boundaryAxis, totalVelocity) / (boundaryAxis.length() * totalVelocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetrationDistance) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        //once a ball gets really close, this can happen... not sure why
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //add this collision to the collection of collisions this frame
        Collision curCollision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(curCollision);
    }


    private void calculateBallBallCollisionInfo(Ball ball1, Ball ball2, float timeStep) throws InvalidBallPositionException {

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball1.getPrevCenter();
        PointF ball2PrevCenter = ball2.getPrevCenter();
        PointF oldDistanceVector = new PointF(ball1PrevCenter.x - ball2PrevCenter.x, ball1PrevCenter.y - ball2PrevCenter.y);

        //technically, we would need to do a cubic equation if we wanted to factor gravity in perfectly.
        //However, it will be close enough to use the average velocity from the current timestep
        PointF ball1AvgVel = ballEngine.getAvgVelocity(ball1, timeStep);
        PointF ball2AvgVel = ballEngine.getAvgVelocity(ball2, timeStep);
        PointF velocityDifference = new PointF(ball1AvgVel.x - ball2AvgVel.x, ball1AvgVel.y - ball2AvgVel.y);
        float distanceBetweenBalls = ball1.getRadius() + ball2.getRadius();

        float collisionTime = 0.01f;
        try {
            //calculate the collision time using quadratic formula
            collisionTime = (float) quadraticCollisionTime(velocityDifference, oldDistanceVector, distanceBetweenBalls, timeStep);
        } catch (Exception e) {}

        if (collisionTime < 0) {
            collisionTime = 0.01f;
        }

        //calculate the boundary axis based on the collision-point location of the two balls
        PointF ball1Displacement = ballEngine.calculatePositionChange(ball1, collisionTime);
        PointF ball2Displacement = ballEngine.calculatePositionChange(ball2, collisionTime);
        PointF ball1NewPos = new PointF(ball1PrevCenter.x + ball1Displacement.x, ball1PrevCenter.y + ball1Displacement.y);
        PointF ball2NewPos = new PointF(ball2PrevCenter.x + ball2Displacement.x, ball2PrevCenter.y + ball2Displacement.y);
        PointF[] vertexArray = new PointF[]{ball1NewPos, ball2NewPos};
        PointF boundaryAxis = makeNormalVectorBetweenPoints(vertexArray, 0);

        //add this collision to the collection
        mCollisions.add(new Collision(collisionTime, boundaryAxis, ball2, ball1));
    }

    private PointF makeNormalVectorBetweenPoints(PointF[] obstacleCoords, int index){
        //We need to make a line between two vertexes
        int vertexA = index;
        int vertexB = (index + 1) % obstacleCoords.length; //We need to wrap back to the first vertex at the end, so use modulus

        //formula to find the normal vector from a line is (-y, x)
        float xComponent = -(obstacleCoords[vertexB].y - obstacleCoords[vertexA].y);
        float yComponent = (obstacleCoords[vertexB].x - obstacleCoords[vertexA].x);

        //create vector and normalize
        PointF normalAxis = new PointF(xComponent, yComponent);
        float normalAxisLength = normalAxis.length();
        normalAxis.set(normalAxis.x / normalAxisLength, normalAxis.y / normalAxisLength);

        return normalAxis;
    }

    private void calculateBallPointCollisionInfo(Ball ball, PointF vertex, Interactable obstacle,
                                                 float timeStep, float radius){

        //get necessary information to calculate quadratic
        PointF boundaryAxis = new PointF(0f,0f);
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF velocityDifference = ballEngine.getAvgVelocity(ball, timeStep);
        float distanceThreshold = ball.getRadius() + radius;

        float collisionTime = 0.01f;
        try {
            //calculate the collision time using the quadratic formula
            collisionTime = (float) quadraticCollisionTime(velocityDifference, distanceFromVertex, distanceThreshold, timeStep);
        } catch (Exception e) {}

        //sanity check
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //If we are handling a nearest-vertex collision (and not a target collision), then...
        if (radius == 0) {
            //update the boundary axis based on the collision-point location of the ball (this will make velocity calculation more accurate)
            boundaryAxis = updateBoundaryAxis(ball, collisionTime, vertex);
        }

        //add this collision to the collection
        Collision collision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(collision);
    }

    private void calculateBallMovingPointCollisionInfo(Ball ball, Penetration penetration,
                                                       MovingObstacle obstacle, float timeStep) throws InvalidBallPositionException {

        //Get vertex from penetration
        PointF vertex = penetration.mVertex;

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF ballVelocity = ballEngine.getAvgVelocity(ball, timeStep);
        PointF obstacleVelocity = obstacle.getVelocity();

        //get current distance (instead of prev distance)
        PointF ballCenter = ball.getCenter();
        PointF currentVertexPos = new PointF(vertex.x + (obstacleVelocity.x * timeStep), vertex.y + (obstacleVelocity.y * timeStep));
        PointF distanceFromVertexAfterTimestep = new PointF(ballCenter.x - currentVertexPos.x, ballCenter.y - currentVertexPos.y);

        PointF velocityDifference = new PointF(ballVelocity.x - obstacleVelocity.x, ballVelocity.y - obstacleVelocity.y);
        if (velocityDifference.length() == 0) {
            throw new InvalidBallPositionException(penetration.mNormalAxis);
        }
        float distanceThreshold = ball.getRadius();

        //calculate the collision time using the quadratic formula
        float collisionTime = 0.01f;
        try {
            collisionTime = (float) quadraticCollisionTime(velocityDifference, distanceFromVertex, distanceThreshold, timeStep);
        } catch (Exception e) {}

        if (collisionTime > timeStep) {
            throw new InvalidBallPositionException(penetration.mNormalAxis);
        }

        //For now, we are only handling moving nearest vertex collisions.
        // if we want to handle moving target collisions, this if-statement will need to change.
        PointF boundaryAxis = new PointF(0f,0f);
        if (true) {
            //update the boundary axis based on the collision-point location of the ball (this will make velocity calculation more accurate)
            boundaryAxis = updateBoundaryAxisMovingVertex(ball, collisionTime, obstacleVelocity, vertex);
        }

        if (collisionTime < 0) {
            throw new InvalidBallPositionException(penetration.mNormalAxis);
        }

        //add this collision to the collection
        Collision collision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(collision);
    }


    //To calculate the collision time of two objects, we need three pieces of information:
    //  velocityDifference: vector representing how the distance between the objects is changing
    //  distanceDifference: vector representing the current distance between the objects
    //  distanceThreshold: How close the objects need to be before they are considered collided
    private double quadraticCollisionTime(PointF velocityDifference, PointF distanceDifference, float distanceThreshold, float timeStep) throws Exception{
        double quadA = (velocityDifference.x * velocityDifference.x) + (velocityDifference.y * velocityDifference.y);
        double quadB =  (2 * (distanceDifference.x * velocityDifference.x)) + (2 * distanceDifference.y * velocityDifference.y);
        double quadC = (distanceDifference.x * distanceDifference.x) + (distanceDifference.y * distanceDifference.y) - (distanceThreshold * distanceThreshold);

        //-b +/- sqrt(b^2 - 4ac) / 2a

        double squareRoot = Math.sqrt((quadB * quadB) - (4*quadA*quadC));
        double result1 = (-quadB + squareRoot) / (2*quadA);
        double result2 = (-quadB - squareRoot) / (2*quadA);

        if (isResultWithinBounds(result1, timeStep) && isResultWithinBounds(result2, timeStep)) {
            return Math.min(result1, result2);
        }
        if (isResultWithinBounds(result1, timeStep)) {
            return result1;
        }
        if (isResultWithinBounds(result2, timeStep)) {
            return result2;
        }

        //At this point, since neither quadratics were within the range, something
        // likely went wrong. Since we know a collision did occur, it's most likely it actually occurred
        // before the frame started, so return the lower of the results.
        return Math.min(result1, result2);
    }

    private boolean isResultWithinBounds(double result, float timeStep) {
        return ((result > 0) && (result <= timeStep));
    }

    private void doPolygonCollisionDetection(Ball ball, Obstacle obstacle, float timeStep) throws InvalidBallPositionException {

        //reset projection history
        clearPenetrationHistory();

        //gather info
        PointF ballCenter = ball.getCenter();

        //Grab the current coords of the obstacle (get temp coords if it's moving)
        PointF[] obstacleCoords;
        if (obstacle.getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
            MovingObstacle temp = (MovingObstacle) obstacle;
            obstacleCoords = temp.getTempCoords();
        } else {
            obstacleCoords = obstacle.get2dCoordArray();
        }


        float radius = ball.getRadius();

        //First, we will check the nearest vertex axis.
        //find distance to closest vertex
        int nearestVertexIndex = getNearestVertexIndex(obstacleCoords, ballCenter);
        PointF nearestVertex = obstacleCoords[nearestVertexIndex];
        PointF nearestVertexToBall = new PointF(nearestVertex.x - ballCenter.x,nearestVertex.y - ballCenter.y);

        //normalize vector
        float nearestVertexLength = nearestVertexToBall.length();
        PointF normalAxis = new PointF(nearestVertexToBall.x / nearestVertexLength, nearestVertexToBall.y / nearestVertexLength);

        //Determine if a gap exists in the projection
        PointF nearestVertexPrevPos = obstacle.get2dCoordArray()[nearestVertexIndex];
        boolean gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, nearestVertex, true, nearestVertexPrevPos);

        if (gapDetected) {
            //definitely no collision, exit
            return;
        }

        //or else we need to keep on going.
        //Now, do a projection test on every vertex pair.
        //calculate normal axis for each vertex pair, and project all the points onto each normal axis
        for (int index = 0; index < obstacleCoords.length; index++) {
            normalAxis = obstacle.getBoundaryAxis(index);

            //project each vertex / circle onto the current normal axis, check for gap
            gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, obstacleCoords[index], false, null);

            if (gapDetected) {
                //definitely no collision, exit
                return;
            }
        }

        //Collision has occurred.
        //Need to calculate timing and save collision info
        getBoundaryCollisionInfo(ball, obstacle, timeStep);
    }

    /**
     * Calculate precise collision information so we can handle it appropriately.
     * Use one of four methods, depending on if the obstacle is moving, and if the
     * collision occurred on a flat part of the obstacle or a point.
     * @param ball
     * @param obstacle
     * @param timeStep
     * @throws Exception
     */
    private void getBoundaryCollisionInfo(Ball ball, Obstacle obstacle, float timeStep) throws InvalidBallPositionException {

        //Find the most likely axis of penetration, based on depth of penetration.
        Penetration pHistory = findClosestAxis(ball, obstacle);

        //Calculate info based on whether the ball collided with a point or a flat wall.
        //If mNearestVertexAxis, then we know ball collided with the corner of an obstacle
        if (pHistory.mNearestVertexAxis){

            //Moving obstacle or stationary obstacle.
            if (obstacle.getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
                MovingObstacle movingObstacle = (MovingObstacle) obstacle;
                calculateBallMovingPointCollisionInfo(ball, pHistory, movingObstacle, timeStep);
            } else {
                calculateBallPointCollisionInfo(ball, pHistory.mVertex, obstacle, timeStep, 0);
            }

        //Otherwise, we know the ball collided on a normal boundary of the obstacle
        } else {

            //Moving obstacle or stationary obstacle.
            if (obstacle.getType() == GameState.INTERACTABLE_MOVING_OBSTACLE) {
                calculateBallMovingBoundaryCollisionInfo(ball, pHistory, obstacle, timeStep);
            } else {
                calculateBallBoundaryCollisionInfo(ball, pHistory, obstacle, timeStep);
            }
        }

    }

    private PointF updateBoundaryAxis(Ball ball, float collisionTime, PointF nearestVertex){
        //move ball to new pos
        PointF newBallPos = calculateNewBallPos(ball, collisionTime);

        //calculate new collision axis
        PointF collisionAxis = new PointF(nearestVertex.x - newBallPos.x, nearestVertex.y - newBallPos.y);
        float collisionAxisLength = collisionAxis.length();
        collisionAxis.set(collisionAxis.x / collisionAxisLength, collisionAxis.y / collisionAxisLength);
        return collisionAxis;
    }

    private PointF updateBoundaryAxisMovingVertex(Ball ball, float collisionTime, PointF obstacleVelocity, PointF nearestVertex) {

        //move ball to new pos
        PointF newBallPos = calculateNewBallPos(ball, collisionTime);

        //Move vertex to new pos
        PointF newVertexPos = calculateNewVertexPos(nearestVertex, collisionTime, obstacleVelocity);

        //calculate new collision axis
        PointF collisionAxis = new PointF(newVertexPos.x - newBallPos.x, newVertexPos.y - newBallPos.y);
        float collisionAxisLength = collisionAxis.length();
        collisionAxis.set(collisionAxis.x / collisionAxisLength, collisionAxis.y / collisionAxisLength);
        return collisionAxis;
    }

    private PointF calculateNewBallPos(Ball ball, float collisionTime) {
        PointF prevBallPos = ball.getPrevCenter();
        PointF ballDisplacement = ballEngine.calculatePositionChange(ball, collisionTime);
        //new ball position
        return new PointF(prevBallPos.x + ballDisplacement.x, prevBallPos.y + ballDisplacement.y);
    }

    private PointF calculateNewVertexPos(PointF vertex, float collisionTime, PointF vertexVelocity) {
        PointF vertexDisplacement = new PointF(vertexVelocity.x * collisionTime, vertexVelocity.y * collisionTime);
        return new PointF(vertex.x + vertexDisplacement.x, vertex.y + vertexDisplacement.y);
    }

    private int getNearestVertexIndex(PointF[] obstacleCoords, PointF ballCenter){
        float smallestLength = GameState.LARGE_NUMBER;
        float currentLength;
        int nearestVertexIndex = -1;
        PointF distanceVector;

        for (int i = 0; i < obstacleCoords.length; i++){
            distanceVector = new PointF(obstacleCoords[i].x - ballCenter.x, obstacleCoords[i].y - ballCenter.y);
            currentLength = distanceVector.length();
            if (currentLength < smallestLength){
                smallestLength = currentLength;
                nearestVertexIndex = i;
            }
        }

        return nearestVertexIndex;
    }

    private boolean projectPointsAndTestForGap(PointF normalAxis, PointF[] obstacleCoords, PointF ballCenter,
                                               float radius, PointF vertex, boolean nearestVertexAxis, PointF oldVertex){
        //Project points onto normal axis and find min / max
        float vertexMin = GameState.LARGE_NUMBER;
        float vertexMax = GameState.SMALL_NUMBER;

        for (int i = 0; i < obstacleCoords.length; i++) {
            float vertexProjection = CommonFunctions.dotProduct(normalAxis, obstacleCoords[i]);

            if (vertexProjection > vertexMax)
                vertexMax = vertexProjection;
            if (vertexProjection < vertexMin)
                vertexMin = vertexProjection;
        }

        //project circle points onto normal axis (max is radius, min is negative radius)
        float circleProjection = CommonFunctions.dotProduct(normalAxis, ballCenter);
        float circleMin = circleProjection - radius;
        float circleMax = circleProjection + radius;

        //calculate results comparing vertex / circle
        float result1 = vertexMin - circleMax;
        float result2 = circleMin - vertexMax;

        //check if the gap is greater than 0 (min - max > 0), which means definitely no collision!
        if ((result1 > 0) || (result2 > 0 )){
            return true;
        } else {

            if(nearestVertexAxis) {
                vertex = new PointF(oldVertex.x, oldVertex.y);
            }

            //Penetration will be a negative number; because a positive number indicates a gap
            addPenetrationToHistory(normalAxis, Math.max(result1, result2), vertex, nearestVertexAxis);
            return false;
        }

    }

    /**
     * This method performs pre-checks to determine if two objects may reasonably be colliding.
     * This consists of:
     *  1) Making sure that if the obstacle is a ball, it has already been moved this frame
     *     (If a ball hasn't moved yet, we must wait until later to test it)
     *  2) Checking the bounding boxes, to see if they intersect on both the y and x axis.
     *
     * PARAMS
     *   ball - The current ball being tested
     *   obstacle - The current Interactable object being tested
     *
     * RETURNS
     *   A boolean, true meaning they may be colliding, and detailed collision testing is needed.
     *   False meaning they are definitely not colliding.
     */
    public boolean coarseCollisionTesting(Ball ball, Interactable obstacle){

        //Do pre-checks if the other obstacle is a ball
        if (obstacle.getType() == GameState.INTERACTABLE_BALL){
            Ball tempBall = (Ball) obstacle;

            //Don't need to check inactive balls
            if (tempBall.isBallInactive()) {
                return false;
            }

            //If the other ball is active (moving), make sure it has been moved this frame.
            if (tempBall.isBallActive() && !tempBall.hasBallBeenAdvanced()) {
                return false;
            }

        }

        //Test Bounding boxes
        //x axis
        if (((ball.getMaxX()) + 1 >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX() - 1)){

            //y axis
            if (((ball.getMaxY()) + 1 >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY() - 1)) {

                return true;
            }
        }

        return false;
    }

}
