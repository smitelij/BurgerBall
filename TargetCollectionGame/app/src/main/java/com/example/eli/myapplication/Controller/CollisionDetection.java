package com.example.eli.myapplication.Controller;

import android.graphics.PointF;
import android.util.Log;

import com.example.eli.myapplication.Model.BallStuckException;
import com.example.eli.myapplication.Model.GameState;
import com.example.eli.myapplication.Model.Interactable;
import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Model.MovingObstacle;
import com.example.eli.myapplication.Model.Obstacle;
import com.example.eli.myapplication.Model.Target;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This class contains the methods used to detect collisions between balls and obstacles.
 * It is intended to be initialized once per frame step within the GameEngine loop, because
 * a collection of Collisions is maintained between each iteration. This collection is
 * reviewed upon completion of collision detection by the CollisionHandling class.
 */
public class CollisionDetection {

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
        Penetration minHistoryItem = penetrationHistory.get(0);
        float minPenetration = penetrationHistory.get(0).mPenetrationDistance;

        for (Penetration history : penetrationHistory){

            //Since penetration distances are negative numbers, we want to find the largest value (i.e. closest to zero)
            //to find the minimum penetration.
            if (history.mPenetrationDistance > minPenetration){
                minHistoryItem = history;
                minPenetration = history.mPenetrationDistance;

            //This should mean that we have two parallel axis's. We must determine which one is the intersecting one.
            } else if (history.mPenetrationDistance == minPenetration){
                //TODO improve handling for polygon obstacles
                //when a rectangle:
                PointF ballCenter = ball.getCenter();
                float xComponent = history.mNormalAxis.x;
                //If normal is on the x-axis:
                if ((xComponent == 1) || (xComponent == -1)){
                    float diff1 = (Math.abs(ballCenter.x - history.mVertex.x));
                    float diff2 = (Math.abs(ballCenter.x - minHistoryItem.mVertex.x));

                    if (diff1 < diff2) {
                        minHistoryItem = history;
                    } else {
                        //nothing needs to change, minHistoryItem is correct.
                    }
                //Otherwise, normal is on the y-axis (we are only handling rectangles here):
                } else {
                    float diff1 = (Math.abs(ballCenter.y - history.mVertex.y));
                    float diff2 = (Math.abs(ballCenter.y - minHistoryItem.mVertex.y));

                    if (diff1 < diff2) {
                        minHistoryItem = history;
                    } else {
                        //nothing needs to change, minHistoryItem is correct.
                    }
                }
            }
        }

        return minHistoryItem;
    }

    /**
     * This helper method reduces complexity by calling one of three collision detection
     * methods, based on the obstacle type. One other approach I considered was having
     * each object type (Ball / Target / Obstacle) override an abstract collision detection method
     * in Interactable. However, I rejected this due to the increased complexity this would add
     * to each object class, preferring to keep all collision detection code isolated in one class.
     */
    public void detailedCollisionTesting(Ball ball, Interactable object, float timeStep) throws BallStuckException{

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

    private void doBallCollisionDetection(Ball ball1, Ball ball2, float timeStep) throws BallStuckException {
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
        calculateBallPointCollisionInfo(ball, targetCenter, target, timeStep, target.getRadius(), 0f);
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
        PointF ballVelocity = ball.getAvgVelocity(timeStep);
        PointF prevVelocityStep;
        PointF totalVelocity;

        Log.d("my app","standard obstacle.");
        prevVelocityStep = new PointF(ballVelocity.x * timeStep, ballVelocity.y * timeStep);
        totalVelocity = ballVelocity;

        //Calculate the angle of the ball's velocity against the boundary axis
        double prevAngle = Math.acos(GameState.dotProduct(boundaryAxis, totalVelocity) / (boundaryAxis.length() * totalVelocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetrationDistance) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        Log.d("my app","***collision time: " + collisionTime);

        //once a ball gets really close, this can happen... not sure why
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //add this collision to the collection of collisions this frame
        Collision curCollision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(curCollision);
    }

    private void calculateBallMovingBoundaryCollisionInfo(Ball ball, Penetration penetration, Interactable obstacle, float timeStep) throws BallStuckException {

        //grab information from the penetration object
        PointF boundaryAxis = penetration.mNormalAxis;
        float penetrationDistance = penetration.mPenetrationDistance;

        //get general info about the ball and frame
        PointF ballVelocity = ball.getAvgVelocity(timeStep);
        PointF prevVelocityStep;
        PointF totalVelocity;

        Log.d("my app","Moving obstacle.");

        MovingObstacle tempObstacle = (MovingObstacle) obstacle;
        PointF obstacleVelocity = tempObstacle.getVelocity();
        prevVelocityStep = new PointF((ballVelocity.x - obstacleVelocity.x) * timeStep, (ballVelocity.y - obstacleVelocity.y) * timeStep);

        totalVelocity = new PointF(ballVelocity.x - obstacleVelocity.x, ballVelocity.y - obstacleVelocity.y);

        //Calculate the angle of the ball's velocity against the boundary axis
        if (totalVelocity.length() == 0) {
            System.out.println("total velocity 0?");
            throw new BallStuckException(boundaryAxis);
        }

        double prevAngle = Math.acos(GameState.dotProduct(boundaryAxis, totalVelocity) / (boundaryAxis.length() * totalVelocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetrationDistance) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        Log.d("my app","***collision time: " + collisionTime);

        //once a ball gets really close, this can happen... not sure why
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //add this collision to the collection of collisions this frame
        Collision curCollision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        mCollisions.add(curCollision);
    }


    private void calculateBallBallCollisionInfo(Ball ball1, Ball ball2, float timeStep) throws BallStuckException {

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball1.getPrevCenter();
        PointF ball2PrevCenter = ball2.getPrevCenter();
        PointF oldDistanceVector = new PointF(ball1PrevCenter.x - ball2PrevCenter.x, ball1PrevCenter.y - ball2PrevCenter.y);

        //technically, we would need to do a cubic equation if we wanted to factor gravity in perfectly.
        //However, it will be close enough to use the average velocity from the current timestep
        PointF ball1AvgVel = ball1.getAvgVelocity(timeStep);
        PointF ball2AvgVel = ball2.getAvgVelocity(timeStep);
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
        PointF ball1Displacement = ball1.calculatePositionChange(collisionTime);
        PointF ball2Displacement = ball2.calculatePositionChange(collisionTime);
        PointF ball1NewPos = new PointF(ball1PrevCenter.x + ball1Displacement.x, ball1PrevCenter.y + ball1Displacement.y);
        PointF ball2NewPos = new PointF(ball2PrevCenter.x + ball2Displacement.x, ball2PrevCenter.y + ball2Displacement.y);
        PointF[] vertexArray = new PointF[]{ball1NewPos, ball2NewPos};
        PointF boundaryAxis = makeNormalVectorBetweenPoints(vertexArray, 0);

        //add this collision to the collection
        mCollisions.add(new Collision(collisionTime, boundaryAxis, ball2, ball1));
    }

    private void calculateBallPointCollisionInfo(Ball ball, PointF vertex, Interactable obstacle,
                                                 float timeStep, float radius, float penetrationDistance){

        //get necessary information to calculate quadratic
        PointF boundaryAxis = new PointF(0f,0f);
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF velocityDifference = ball.getAvgVelocity(timeStep);
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
        collision.setPenetrationDepth(penetrationDistance);
        mCollisions.add(collision);
    }

    private void calculateBallMovingPointCollisionInfo(Ball ball, Penetration penetration, MovingObstacle obstacle, float timeStep) throws BallStuckException {

        //Get vertex from penetration
        PointF vertex = penetration.mVertex;
        Log.d("my app","***ball location: " + ball.getPrevCenter());
        Log.d("my app","***point location: " + vertex);

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF ballVelocity = ball.getAvgVelocity(timeStep);
        PointF obstacleVelocity = obstacle.getVelocity();

        //get current distance (instead of prev distance)
        PointF ballCenter = ball.getCenter();
        PointF currentVertexPos = new PointF(vertex.x + (obstacleVelocity.x * timeStep), vertex.y + (obstacleVelocity.y * timeStep));
        PointF distanceFromVertexAfterTimestep = new PointF(ballCenter.x - currentVertexPos.x, ballCenter.y - currentVertexPos.y);
        Log.d("my app","_____________________");
        Log.d("my app","Current ball center: " + ballCenter);
        Log.d("my app","current vertex pos: " + currentVertexPos);
        Log.d("my app","distance from vertex after timestep: " + distanceFromVertexAfterTimestep);
        Log.d("my app","distance value: " + distanceFromVertexAfterTimestep.length());
        Log.d("my app","timestep: " + timeStep);
        Log.d("my app","_______________");

        PointF velocityDifference = new PointF(ballVelocity.x - obstacleVelocity.x, ballVelocity.y - obstacleVelocity.y);
        if (velocityDifference.length() == 0) {
            throw new BallStuckException(penetration.mNormalAxis);
        }
        float distanceThreshold = ball.getRadius();

        Log.d("my app","ball velocitY: " + ballVelocity);
        Log.d("my app","obstacleVelocity: " + obstacleVelocity);
        Log.d("my app","velocity difference: " + velocityDifference);
        Log.d("my app","distance difference: " + distanceFromVertex);
        Log.d("my app","distance value: " + distanceFromVertex.length());

        //calculate the collision time using the quadratic formula
        float collisionTime = 0.01f;
        try {
            collisionTime = (float) quadraticCollisionTime(velocityDifference, distanceFromVertex, distanceThreshold, timeStep);
        } catch (Exception e) {}

        if (collisionTime > timeStep) {
            System.out.println("error.");
            throw new BallStuckException(penetration.mNormalAxis);
        }

        boolean displaceBallFromObstacle = false;


        //For now, we are only handling moving nearest vertex collisions.
        // if we want to handle moving target collisions, this if-statement will need to change.
        PointF boundaryAxis = new PointF(0f,0f);
        if (true) {
            //update the boundary axis based on the collision-point location of the ball (this will make velocity calculation more accurate)
            boundaryAxis = updateBoundaryAxisMovingVertex(ball, collisionTime, obstacleVelocity, vertex);
            System.out.println("old boundary axis: " + penetration.mNormalAxis);
            System.out.println("new boundary axis: " + boundaryAxis);
        }
        /*
        if (displaceBallFromObstacle) {
            float penetrationDistance = calculateCurrentPenetration(ball, collisionTime, obstacleVelocity, vertex);
            displaceBallFromObstacle(ball, penetrationDistance, boundaryAxis);
        }*/

        if (collisionTime < 0) {
            throw new BallStuckException(penetration.mNormalAxis);
        }


        //add this collision to the collection
        Collision collision = new Collision(collisionTime, boundaryAxis, obstacle, ball);
        collision.setPenetrationDepth(penetration.mPenetrationDistance);
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

        Log.d("my app","quadratic result 1: " + result1);
        Log.d("my app","quadratic result 2: " + result2);

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
        if ((result > 0) && (result <= timeStep)) {
            return true;
        } else {
            return false;
        }
    }

    private void doPolygonCollisionDetection(Ball ball, Obstacle obstacle, float timeStep) throws BallStuckException{

        //reset projection history
        clearPenetrationHistory();

        //gather info
        PointF ballCenter = ball.getCenter();

        //Log.d("my app","###### collision testing - ball: " + ball.hashCode() + " _ obstacle: " + obstacle.hashCode());

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
        Log.d("my app","nearest vertex: " + nearestVertex);
        Log.d("my app","nearestVertexLength to ball length: " + nearestVertexLength);
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

    /**
     * Calculate precise collision information so we can handle it appropriately.
     * Use one of four methods, depending on if the obstacle is moving, and if the
     * collision occurred on a flat part of the obstacle or a point.
     * @param ball
     * @param obstacle
     * @param timeStep
     * @throws Exception
     */
    private void getBoundaryCollisionInfo(Ball ball, Obstacle obstacle, float timeStep) throws BallStuckException{

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
                calculateBallPointCollisionInfo(ball, pHistory.mVertex, obstacle, timeStep, 0, pHistory.mPenetrationDistance);
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

    private float calculateCurrentPenetration(Ball ball, float collisionTime, PointF obstacleVelocity, PointF nearestVertex) {
        //move ball to new pos
        PointF newBallPos = calculateNewBallPos(ball, collisionTime);

        //Move vertex to new pos
        PointF newVertexPos = calculateNewVertexPos(nearestVertex, collisionTime, obstacleVelocity);

        //Calculate distance between ball and vertex
        float distance = new PointF(newBallPos.x - newVertexPos.x, newBallPos.y - newVertexPos.y).length();
        float penetration = ball.getRadius() - distance;

        return penetration;
    }

    private PointF calculateNewBallPos(Ball ball, float collisionTime) {
        PointF prevBallPos = ball.getPrevCenter();
        PointF ballDisplacement = ball.calculatePositionChange(collisionTime);
        //new ball position
        return new PointF(prevBallPos.x + ballDisplacement.x, prevBallPos.y + ballDisplacement.y);
    }

    private PointF calculateNewVertexPos(PointF vertex, float collisionTime, PointF vertexVelocity) {
        PointF vertexDisplacement = new PointF(vertexVelocity.x * collisionTime, vertexVelocity.y * collisionTime);
        return new PointF(vertex.x + vertexDisplacement.x, vertex.y + vertexDisplacement.y);
    }

    private void displaceBallFromObstacle(Ball ball, float penetrationDistance, PointF boundaryAxis) {
        PointF displacementVector = new PointF(-boundaryAxis.x * penetrationDistance, -boundaryAxis.y * penetrationDistance);
        System.out.println("BALL DISPLACED: " + displacementVector);
        ball.addToDisplacementVector(displacementVector);
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
            float vertexProjection = GameState.dotProduct(normalAxis, obstacleCoords[i]);

            if (vertexProjection > vertexMax)
                vertexMax = vertexProjection;
            if (vertexProjection < vertexMin)
                vertexMin = vertexProjection;
        }

        //project circle points onto normal axis (max is radius, min is negative radius)
        float circleProjection = GameState.dotProduct(normalAxis, ballCenter);
        float circleMin = circleProjection - radius;
        float circleMax = circleProjection + radius;

        //calculate results comparing vertex / circle
        float result1 = vertexMin - circleMax;
        float result2 = circleMin - vertexMax;

        //check if the gap is greater than 0 (min - max > 0), which means definitely no collision!
        if ((result1 > 0) || (result2 > 0 )){
            Log.d("my app","distances: " + result1 + ", " + result2);
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
            if (tempBall.isBallActive() && !tempBall.hasBallMoved()) {
                return false;
            }

        }
/*
        Log.d("my app","  ball min x: " + ball.getMinX() + ", ball max x: " + ball.getMaxX());
        Log.d("my app","  ball min y: " + ball.getMinY() + ", ball max Y: " + ball.getMaxY());
        Log.d("my app","__");
        Log.d("my app","  obstacle min x: " + obstacle.getMinX() + ", obstacle max x: " + obstacle.getMaxX());
        Log.d("my app","  obstacle min y: " + obstacle.getMinY() + ", obstacle max Y: " + obstacle.getMaxY());
*/
        //Test Bounding boxes
        //x axis
        if (((ball.getMaxX()) + 1 >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX() - 1)){

            //Log.d("my app"," x axis: True");
            //y axis
            if (((ball.getMaxY()) + 1 >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY() - 1)) {

                //Log.d("my app"," y axis: true");
                return true;
            }
        }

        return false;
    }

}
