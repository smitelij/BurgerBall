package com.example.eli.myapplication;

import android.graphics.PointF;

import java.util.ArrayList;


/**
 * Created by Eli on 4/3/2016.
 */
public class CollisionDetection {

    private class penetrationHistory{
        PointF mNormalAxis;
        float mPenetrationDistance;
        PointF mVertex;
        boolean mNearestVertexAxis;

        penetrationHistory(PointF normalAxis, float penetrationDistance, PointF vertex, boolean nearestVertexAxis){
            mNormalAxis = normalAxis;
            mPenetrationDistance = penetrationDistance;
            mVertex = vertex;
            mNearestVertexAxis = nearestVertexAxis;
        }
    }

    private ArrayList<penetrationHistory> pHistory = new ArrayList<penetrationHistory>();
    private ArrayList<CollisionHistory> mCollisions = new ArrayList<>();

    public ArrayList<CollisionHistory> getCollisions(){
        return mCollisions;
    }

    public void addProjectionToHistory(PointF axis, float penetrationDistance, PointF vertex, boolean nearestVertexAxis){
        pHistory.add(new penetrationHistory(axis, penetrationDistance, vertex, nearestVertexAxis));
    }

    public void clearProjectionHistory(){
        pHistory.clear();
    }

    //closest axis = minimum penetration
    public penetrationHistory findClosestAxis(Ball ball, Polygon obstacle){

         //sanity check
        if (pHistory.size() == 0) {
            return null;
        }

        //set the defaults
        penetrationHistory minHistoryItem = pHistory.get(0);
        float minPenetration = pHistory.get(0).mPenetrationDistance;

        for (penetrationHistory history : pHistory){

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

    public void detailedCollisionTesting(Ball ball, Interactable object, float timeStep){

        if (object.getType()== GameState.OBSTACLE_BALL){
            doBallCollisionDetection(ball, (Ball) object, timeStep);
            return;
        }

        if (object.getType()== GameState.OBSTACLE_POLYGON){
            doPolygonCollisionDetection(ball, (Polygon) object, timeStep);
            return;
        }

        if (object.getType()== GameState.OBSTACLE_TARGET){
            doTargetCollisionDetection(ball, (Target) object, timeStep);
            return;
        }

    }

    private void doBallCollisionDetection(Ball ball1, Ball ball2, float timeStep){
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

    private void calculateBallBoundaryCollisionInfo(Ball ball, penetrationHistory pHistory, Interactable obstacle, float timeStep){

        //grab information from pHistory
        PointF boundaryAxis = pHistory.mNormalAxis;
        float penetration = pHistory.mPenetrationDistance;

        //get general info
        PointF velocity = ball.getAvgVelocity(timeStep);
        PointF prevVelocityStep = new PointF(velocity.x * timeStep, velocity.y * timeStep);

        //Calculate the angle of the ball's velocity against the boundary axis
        double prevAngle = Math.acos(GameState.dotProduct(boundaryAxis, velocity) / (boundaryAxis.length() * velocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetration) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        //once a ball gets really close, this can happen... not sure why
        if (collisionTime < 0){
            collisionTime = 0.01f;
        }

        //add this collision to the collection
        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, obstacle, ball));
    }


    private void calculateBallBallCollisionInfo(Ball ball1, Ball ball2, float timeStep){

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

        //calculate the collision time using quadratic formula
        float collisionTime = (float) quadraticCollisionTime(velocityDifference, oldDistanceVector, distanceBetweenBalls);

        //sanity check
        if (collisionTime < 0){
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
        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, ball2, ball1));
    }

    private void calculateBallPointCollisionInfo(Ball ball, PointF vertex, Interactable obstacle, float timeStep, float radius){

        //get necessary information to calculate quadratic
        PointF boundaryAxis = new PointF(0f,0f);
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF velocityDifference = ball.getAvgVelocity(timeStep);
        float distanceThreshold = ball.getRadius() + radius;

        //calculate the collision time using the quadratic formula
        float collisionTime = (float) quadraticCollisionTime(velocityDifference, distanceFromVertex, distanceThreshold);

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
        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, obstacle, ball));
    }

    //To calculate the collision time of two objects, we need three pieces of information:
    //  velocityDifference: vector representing how the distance between the objects is changing
    //  distanceDifference: vector representing the current distance between the objects
    //  distanceThreshold: How close the objects need to be before they are considered collided
    private double quadraticCollisionTime(PointF velocityDifference, PointF distanceDifference, float distanceThreshold){
        double quadA = (velocityDifference.x * velocityDifference.x) + (velocityDifference.y * velocityDifference.y);
        double quadB =  (2 * (distanceDifference.x * velocityDifference.x)) + (2 * distanceDifference.y * velocityDifference.y);
        double quadC = (distanceDifference.x * distanceDifference.x) + (distanceDifference.y * distanceDifference.y) - (distanceThreshold * distanceThreshold);

        //-b +/- sqrt(b^2 - 4ac) / 2a

        double squareRoot = Math.sqrt((quadB * quadB) - (4*quadA*quadC));
        double result1 = (-quadB + squareRoot) / (2*quadA);
        double result2 = (-quadB - squareRoot) / (2*quadA);

        if (result1 < 0){
            return result2;
        } else if (result2 < 0){
            return result1;
        } else {
            return Math.min(result1, result2);
        }
    }

    private void doPolygonCollisionDetection(Ball ball, Polygon obstacle, float timeStep) {

        //reset projection history
        clearProjectionHistory();

        //gather info
        PointF ballCenter = ball.getCenter();
        PointF[] obstacleCoords = obstacle.get2dCoordArray();
        float radius = ball.getRadius();

        //First, we will check the nearest vertex axis.
        //find distance to closest vertex
        PointF nearestVertex = getNearestVertex(obstacleCoords, ballCenter);
        PointF nearestVertexToBall = new PointF(nearestVertex.x - ballCenter.x,nearestVertex.y - ballCenter.y);

        //normalize vector
        float nearestVertexLength = nearestVertexToBall.length();
        PointF normalAxis = new PointF(nearestVertexToBall.x / nearestVertexLength, nearestVertexToBall.y / nearestVertexLength);

        //Determine if a gap exists in the projection
        boolean gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, nearestVertex, true);

        if (gapDetected) {
            //definitely no collision, exit
            return;
        }

        //or else we need to keep on going.
        //Now, do a projection test on every vertex pair.
        //calculate normal axis for each vertex pair, and project all the points onto each normal axis
        for (int i = 0; i < obstacleCoords.length; i++) {
            normalAxis = makeNormalVectorBetweenPoints(obstacleCoords, i);

            //project each vertex / circle onto the current normal axis, check for gap
            gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, obstacleCoords[i], false);

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
        //We make need to make a line between two vertexes
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

    private void getBoundaryCollisionInfo(Ball ball, Polygon obstacle, float timeStep){

        //Find the most likely axis of penetration, based on depth of penetration.
        penetrationHistory pHistory = findClosestAxis(ball, obstacle);

        //Calculate info based on whether the ball collided with a point or a flat wall.
        if (pHistory.mNearestVertexAxis){
            //If mNearestVertexAxis, then we know ball collided with the corner of an obstacle
            calculateBallPointCollisionInfo(ball, pHistory.mVertex, obstacle, timeStep, 0);
        } else {
            //Otherwise, we know the ball collided on a normal boundary of the obstacle
            calculateBallBoundaryCollisionInfo(ball, pHistory, obstacle, timeStep);
        }

    }

    private PointF updateBoundaryAxis(Ball ball, float collisionTime, PointF nearestVertex){
        PointF prevBallPos = ball.getPrevCenter();
        PointF ballDisplacement = ball.calculatePositionChange(collisionTime);
        PointF newBallPos = new PointF(prevBallPos.x + ballDisplacement.x, prevBallPos.y + ballDisplacement.y);
        PointF collisionAxis = new PointF(nearestVertex.x - newBallPos.x, nearestVertex.y - newBallPos.y);
        float collisionAxisLength = collisionAxis.length();
        collisionAxis.set(collisionAxis.x / collisionAxisLength, collisionAxis.y / collisionAxisLength);
        return collisionAxis;
    }

    private PointF getNearestVertex(PointF[] obstacleCoords, PointF ballCenter){
        float smallestLength = GameState.LARGE_NUMBER;
        float currentLength;
        PointF nearestVertex = new PointF();
        PointF distanceVector;

        for (int i = 0; i < obstacleCoords.length; i++){
            distanceVector = new PointF(obstacleCoords[i].x - ballCenter.x, obstacleCoords[i].y - ballCenter.y);
            currentLength = distanceVector.length();
            if (currentLength < smallestLength){
                smallestLength = currentLength;
                nearestVertex.set(obstacleCoords[i]);
            }
        }

        return nearestVertex;
    }

    private boolean projectPointsAndTestForGap(PointF normalAxis, PointF[] obstacleCoords, PointF ballCenter, float radius, PointF vertex, boolean nearestVertexAxis){
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
            return true;
        } else {
            //Penetration will be a negative number; because a positive number indicates a gap
            addProjectionToHistory(normalAxis, Math.max(result1,result2), vertex, nearestVertexAxis);
            return false;
        }

    }

    //pre checks before getting into detailed testing
    public boolean coarseCollisionTesting(Ball ball, Interactable obstacle){

        //if the other object is a ball, we need to make sure it has already moved this frame.
        if (obstacle.getType() == GameState.OBSTACLE_BALL){
            Ball tempBall = (Ball) obstacle;
            if (!tempBall.hasBallMoved()){
                return false;
            }
        }

        //Test Bounding boxes
        //x axis
        if (((ball.getMaxX()) >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX())){
            //y axis
            if (((ball.getMaxY()) >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY())) {
                return true;
            }
        }

        return false;
    }

}
