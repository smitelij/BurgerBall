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

    private ArrayList<CollisionHistory> mBoundaryCollisions = new ArrayList<>();
    private ArrayList<CollisionHistory> mBallCollisions = new ArrayList<>();

    public ArrayList<CollisionHistory> getCollisions(){
        return mCollisions;
    }
    public float getFirstCollisionTime(){
        ArrayList<CollisionHistory> collisions = new ArrayList<>();
        collisions = getFirstCollision();

        if (collisions.size() == 0)
            System.out.println("no collisions found, bad error.");

        //If getFirstCollision returns multiple collisions, they will all have the same time, so we can use the first.
        return collisions.get(0).getTime();
    }
    public ArrayList<CollisionHistory> getFirstCollision() {

        //sanity check
        if (mCollisions.size() == 0){
            return null;
        }

        //Initialize arraylist
        ArrayList<CollisionHistory> firstCollision = new ArrayList<>();
        float firstCollisionTime = 1f; //time should always be less than 1

        //loop through all finding the earliest collision
        for (CollisionHistory collision : mCollisions){

            float curCollisionTime = collision.getTime();

            //Set defaults
            if (firstCollision.size() == 0){
                firstCollision.add(collision);
                firstCollisionTime = curCollisionTime;

            //If current collision is the earliest thus far
            } else if (curCollisionTime < firstCollisionTime){
                firstCollision.clear();
                firstCollision.add(collision);
                firstCollisionTime = curCollisionTime;

            //If current collision tied for the earliest, add it to the list
            } else if (curCollisionTime == firstCollisionTime){
                firstCollision.add(collision);
            }

        }

        return firstCollision;
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

    public boolean doCollisionDetection(Ball ball, Interactable object, float timeStep){

        if (object.getType()== GameState.OBSTACLE_BALL){
            return doBallCollisionDetection(ball, (Ball) object, timeStep);
        }

        if (object.getType()== GameState.OBSTACLE_POLYGON){
            return doPolygonCollisionDetection(ball, (Polygon) object, timeStep);
        }

        //should never get here
        return false;
    }

    private boolean doBallCollisionDetection(Ball ball1, Ball ball2, float timeStep){
        PointF ball1center;
        PointF ball2center;

        PointF distanceVector;
        float distance;

        ball1center = ball1.getCenter();
        ball2center = ball2.getCenter();

        distanceVector = new PointF(ball1center.x - ball2center.x, ball1center.y - ball2center.y);
        distance = distanceVector.length();

        if (distance >= (ball1.getRadius() + ball2.getRadius())){
            return false;
        }

        //calculate more collision info, such as timing
        System.out.println("collision between ball " + ball1.getID() + " & ball " + ball2.getID() + ". distance: " + distance);
        calculateBallBallCollisionInfo(ball1, ball2, distance, timeStep);
        return true;
    }

    private void calculateBallBoundaryCollisionInfo(Ball ball, penetrationHistory pHistory, Interactable obstacle, float timeStep){

        //grab information from pHistory
        PointF boundaryAxis = pHistory.mNormalAxis;
        float penetration = pHistory.mPenetrationDistance;

        //get general info
        PointF velocity = new PointF(ball.getXVelocity(), ball.getYVelocity());
        PointF prevVelocityStep = new PointF(velocity.x * timeStep, velocity.y * timeStep);

        //Calculate the angle of the ball's velocity against the boundary axis
        double prevAngle = Math.acos(dotProduct(boundaryAxis, velocity) / (boundaryAxis.length() * velocity.length()));

        //Use prev angle and basic trig to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetration) / Math.cos(prevAngle);

        //Calculate the collision time, based on the percent of velocity used before the collision, and the time step.
        //For example, if a ball uses 50% of velocity before colliding, and the time step was 0.5, then the collision occurred at 0.25.
        float percentOfVelocityBeforeCollision = (prevVelocityStep.length() - (float) Math.abs(hypotenuse)) / prevVelocityStep.length();
        float collisionTime = (percentOfVelocityBeforeCollision) * timeStep;

        //add this collision to the collection
        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, obstacle, ball));
    }


    private void calculateBallBallCollisionInfo(Ball ball1, Ball ball2, float currentDistance, float timeStep){

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball1.getPrevCenter();
        PointF ball2PrevCenter = ball2.getPrevCenter();
        PointF oldDistanceVector = new PointF(ball1PrevCenter.x - ball2PrevCenter.x, ball1PrevCenter.y - ball2PrevCenter.y);
        PointF velocityDifference = new PointF(ball1.getXVelocity() - ball2.getXVelocity(), ball1.getYVelocity() - ball2.getYVelocity());
        float distanceBetweenBalls = ball1.getRadius() + ball2.getRadius();

        //calculate the collision time using quadratic formula
        float collisionTime = (float) quadraticCollisionTime(velocityDifference, oldDistanceVector, distanceBetweenBalls);

        //calculate the boundary axis based on the collision-point location of the two balls
        PointF ball1NewPos = new PointF(ball1PrevCenter.x + (collisionTime * ball1.getXVelocity()), ball1PrevCenter.y + (collisionTime * ball1.getYVelocity()));
        PointF ball2NewPos = new PointF(ball2PrevCenter.x + (collisionTime * ball2.getXVelocity()), ball2PrevCenter.y + (collisionTime * ball2.getYVelocity()));
        PointF[] vertexArray = new PointF[]{ball1NewPos, ball2NewPos};
        PointF boundaryAxis = makeNormalVectorBetweenPoints(vertexArray, 0);

        //add this collision to the collection
        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, ball2, ball1));
    }

    private void calculateBallPointCollisionInfo(Ball ball, PointF vertex, Interactable obstacle){

        //get necessary information to calculate quadratic
        PointF ball1PrevCenter = ball.getPrevCenter();
        PointF distanceFromVertex = new PointF(ball1PrevCenter.x - vertex.x, ball1PrevCenter.y - vertex.y);
        PointF velocityDifference = ball.getVelocity();
        float distanceThreshold = ball.getRadius();

        //calculate the collision time using the quadratic formula
        float collisionTime = (float) quadraticCollisionTime(velocityDifference, distanceFromVertex, distanceThreshold);

        //update the boundary axis based on the collision-point location of the ball (this will make velocity calculation more accurate)
        PointF boundaryAxis = updateBoundaryAxis(ball,collisionTime,vertex);

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

    private boolean doPolygonCollisionDetection(Ball ball, Polygon obstacle, float timeStep) {


        PointF ballCenter = new PointF();
        PointF obstacleCenter = new PointF();
        PointF[] obstacleCoords;

        PointF normalAxis = new PointF();
        float radius;

        boolean gapDetected;

        //Get vertexes for obstacle
        //Get radius/center for ball
        //gather info
        ballCenter.set(ball.getCenter());
        obstacleCenter.set(obstacle.getCenter());
        obstacleCoords = obstacle.get2dCoordArray();
        radius = ball.getRadius();
        //reset projection history
        clearProjectionHistory();

        //find distance to closest vertex and normalize
        PointF nearestVertex = getNearestVertex(obstacleCoords, ballCenter);
        vectorPrint(nearestVertex, "&&&&nearestVertex");
        PointF nearestVertexToBall = new PointF(nearestVertex.x - ballCenter.x,nearestVertex.y - ballCenter.y);
        vectorPrint(nearestVertexToBall, "-----boundary axis vector");

        //normalize vector
        float nearestVertexLength = nearestVertexToBall.length();
        normalAxis = new PointF(nearestVertexToBall.x / nearestVertexLength, nearestVertexToBall.y / nearestVertexLength);

        //Determine if a gap exists in the projection
        gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, nearestVertex, true);

        if (gapDetected) {
            //definitely no collision, exit
            return false;
        }

        //or else we need to keep on going

        //calculate normal axis for each vertex pair, and project all the points onto each normal axis
        for (int i = 0; i < obstacleCoords.length; i++) {
            normalAxis = makeNormalVectorBetweenPoints(obstacleCoords, i);

            //project each vertex / circle onto the current normal axis, check for gap
            gapDetected = projectPointsAndTestForGap(normalAxis, obstacleCoords, ballCenter, radius, obstacleCoords[i], false);

            if (gapDetected) {
                //definitely no collision, exit
                return false;
            }
        }

        System.out.println("time step: " + timeStep);

        //Collision has occurred.
        getBoundaryCollisionInfo(ball, obstacle, timeStep);
        return true;
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
            calculateBallPointCollisionInfo(ball, pHistory.mVertex, obstacle);
        } else {
            //Otherwise, we know the ball collided on a normal boundary of the obstacle
            calculateBallBoundaryCollisionInfo(ball, pHistory, obstacle, timeStep);
        }

    }

    private PointF updateBoundaryAxis(Ball ball, float collisionTime, PointF nearestVertex){
        PointF prevBallPos = ball.getPrevCenter();
        PointF newBallPos = new PointF(prevBallPos.x + (collisionTime * ball.getXVelocity()), prevBallPos.y + (collisionTime * ball.getYVelocity()));
        PointF collisionAxis = new PointF(nearestVertex.x - newBallPos.x, nearestVertex.y - newBallPos.y);
        float collisionAxisLength = collisionAxis.length();
        collisionAxis.set(collisionAxis.x / collisionAxisLength, collisionAxis.y / collisionAxisLength);
        return collisionAxis;
    }


    //A reasonable question- Why are we splitting this into two methods?
    // Why don't we just go through all collisions, and check obstacle type, to determine
    //which calculateVelocity method to use?
    // ...
    // In order to better handle an edge case involving a ball simultaneously colliding with
    // a boundary and another ball, it makes more sense to handle boundary collisions first
    public void handleBoundaryCollisions(){
        for (CollisionHistory currentCollision : mBoundaryCollisions){
            calculateVelocityBorderCollision(currentCollision);
        }
    }

    public void handleBallCollisions(){
        for (CollisionHistory currentCollision : mBallCollisions){
            calculateVelocityBallCollision(currentCollision);
        }
    }

    private void calculateVelocityBorderCollision(CollisionHistory collision){

        System.out.println("Border center: " + collision.getObstacle().getCenter());
        System.out.println("Boundary axis: " + collision.getBoundaryAxis());
        System.out.println("Collision time: " + collision.getTime());
        System.out.println("Ball center: " + collision.getBall().getCenter());
        System.out.println("Ball old velocity: " + collision.getBall().getVelocity().x + ";" + collision.getBall().getVelocity().y);


        Ball ball = collision.getBall();
        float velocityChange;
        PointF velocityChangeVector;
        PointF newVelocity;
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ball.getVelocity();

        velocityChange = 2 * dotProduct(oldVelocity, boundaryAxis);
        velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        newVelocity = new PointF(oldVelocity.x - velocityChangeVector.x, oldVelocity.y - velocityChangeVector.y);
        ball.setVelocity(newVelocity);

        System.out.println("Ball new velocity: " + newVelocity.x + ";" + newVelocity.y);

    }

    private void calculateVelocityBallCollision(CollisionHistory collision){

        System.out.println("Ball collision " + collision.hashCode());
        System.out.println("time: " + collision.getTime());

        //get balls
        Ball ball1 = collision.getBall();
        Ball ball2 = (Ball) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UTangentVector = collision.getBoundaryAxis();
        PointF UNormalVector = new PointF(UTangentVector.y, -UTangentVector.x);

        //get velocities for balls
        PointF ball1velocity = ball1.getAvailableVelocity();  //if a ball collides with more than one other ball,
        PointF ball2velocity = ball2.getAvailableVelocity();  //available velocity will differ from normal velocity
        System.out.println("ball " + ball1.getID() + " available velocity: " + ball1velocity.x + ";" + ball1velocity.y);
        System.out.println("ball " + ball2.getID() + " available velocity: " + ball2velocity.x + ";" + ball2velocity.y);

        //determine component velocities for ball1 / ball2 in the tangent / normal directions
        float velocity1tangent = dotProduct(ball1velocity, UTangentVector);
        float velocity1normal = dotProduct(ball1velocity, UNormalVector);
        float velocity2tangent = dotProduct(ball2velocity, UTangentVector);
        float velocity2normal = dotProduct(ball2velocity, UNormalVector);

        //calculate new tangential velocities (they are the same, no force between objects in tangential direction)
        float newVelocity1tangent = velocity1tangent;
        float newVelocity2tangent = velocity2tangent;

        //calculate new normal velocities ( same as the normal component of the other ball)
        float newVelocity1normal = velocity2normal;
        float newVelocity2normal = velocity1normal;

        //convert scalar tangential & normal values into vectors
        PointF newVelocity1normalVector = new PointF(newVelocity1normal * UNormalVector.x, newVelocity1normal * UNormalVector.y);
        PointF newVelocity1tangentVector = new PointF(newVelocity1tangent * UTangentVector.x, newVelocity1tangent * UTangentVector.y);
        PointF newVelocity2normalVector = new PointF(newVelocity2normal * UNormalVector.x, newVelocity2normal * UNormalVector.y);
        PointF newVelocity2tangentVector = new PointF(newVelocity2tangent * UTangentVector.x, newVelocity2tangent * UTangentVector.y);

        //add tangential and normal components together to get sum velocity
        PointF newVelocity1 = new PointF(newVelocity1normalVector.x + newVelocity1tangentVector.x, newVelocity1normalVector.y + newVelocity1tangentVector.y);
        PointF newVelocity2 = new PointF(newVelocity2normalVector.x + newVelocity2tangentVector.x, newVelocity2normalVector.y + newVelocity2tangentVector.y);

        //set velocity
        ball1.addNewVelocity(newVelocity1);
        ball2.addNewVelocity(newVelocity2);
        System.out.println("ball " + ball1.getID() + " new velocity: " + newVelocity1.x + ";" + newVelocity1.y);
        System.out.println("ball " + ball2.getID() + " new velocity: " + newVelocity2.x + ";" + newVelocity2.y);
    }


    private double getAngleOfVector(PointF vector){
        double collisionAngle = Math.atan((double) vector.y / vector.x);
        if ((vector.y < 0) && (vector.x < 0)){
            collisionAngle = collisionAngle + 3.14159265359;
        }

        return collisionAngle;
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
            float vertexProjection = dotProduct(normalAxis, obstacleCoords[i]);

            if (vertexProjection > vertexMax)
                vertexMax = vertexProjection;
            if (vertexProjection < vertexMin)
                vertexMin = vertexProjection;
        }

        //project circle points onto normal axis (max is radius, min is negative radius)
        float circleProjection = dotProduct(normalAxis, ballCenter);
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

    private float dotProduct(PointF vector1, PointF vector2){
        return ((vector1.x * vector2.x) + (vector1.y * vector2.y));
    }

    //pre checks before getting into in depth checks
    public boolean testBoundingBoxes(Ball ball, Interactable obstacle){

        //first, we need to make sure the other object has already been moved this frame.
        if (!obstacle.hasObjectMoved()){
            return false;
        }

        //test x collision
        if (((ball.getMaxX()) >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX())){
            //test y collision
            if (((ball.getMaxY()) >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY())) {
                return true;
            }
        }

        return false;
    }

    public void updateCollisionCollections(ArrayList<CollisionHistory> collisions){

        for (CollisionHistory currentCollision : collisions) {
            if (currentCollision.getObstacle().getType() == GameState.OBSTACLE_BALL) {

                Ball currentBall = currentCollision.getBall();
                Ball otherBall = (Ball) currentCollision.getObstacle();

                currentBall.increaseCollisionCount();
                otherBall.increaseCollisionCount();

                mBallCollisions.add(currentCollision);

            } else {
                mBoundaryCollisions.add(currentCollision);
            }
        }
    }

    private void vectorPrint(PointF vector, String msg){
        System.out.println(msg + ": " + vector.x + ";" + vector.y);
    }

}
