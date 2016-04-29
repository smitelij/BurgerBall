package com.example.eli.myapplication;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Eli on 4/3/2016.
 */
public class CollisionDetection {

    private class penetrationHistory{
        PointF mNormalAxis;
        PointF mDisplacementVector;
        float mPenetrationDistance;
        PointF mVertex;



        penetrationHistory(PointF normalAxis, float penetrationDistance, PointF vertex){
            mNormalAxis = normalAxis;
            mPenetrationDistance = penetrationDistance;
            mVertex = vertex;

            float displacementVectorX;
            float displacementVectorY;

            displacementVectorX = normalAxis.x * penetrationDistance;
            displacementVectorY = normalAxis.y * penetrationDistance;

            mDisplacementVector = new PointF(displacementVectorX, displacementVectorY);
        }
    }
    public class collisionHistory{
        private float mTime; //percent time into the frame that collision occurred
        private PointF mBoundaryAxis; //normalized boundary axis of the obstacle where collision occurred
        private Polygon mObstacle; //the obstacle that was struck

        collisionHistory(float time, PointF boundaryAxis, Polygon obstacle){
            mTime = time;
            mBoundaryAxis = boundaryAxis;
            mObstacle = obstacle;
        }

        public float getTime(){
            return mTime;
        }
    }

    private ArrayList<penetrationHistory> pHistory = new ArrayList<penetrationHistory>();
    private ArrayList<collisionHistory> mCollisions = new ArrayList<>();

    private PointF displacementVector;

    public ArrayList<collisionHistory> getCollisions(){
        return mCollisions;
    }
    public float getFirstCollisionTime(){
        ArrayList<collisionHistory> collisions = new ArrayList<>();
        collisions = getFirstCollision();

        if (collisions.size() == 0)
            System.out.println("no collisions found, bad error.");

        //If getFirstCollision returns multiple collisions, they will all have the same time, so we can use the first.
        return collisions.get(0).getTime();
    }
    public ArrayList<collisionHistory> getFirstCollision() {

        //sanity check
        if (mCollisions.size() == 0){
            return null;
        }

        //Initialize
        ArrayList<collisionHistory> firstCollision = new ArrayList<>();
        float firstCollisionTime = 1f; //time should always be less than 1

        //loop through all finding the earliest collision
        for (collisionHistory collision : mCollisions){

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

    public void addProjectionToHistory(PointF axis, float penetrationDistance, PointF vertex){
        pHistory.add(new penetrationHistory(axis, penetrationDistance, vertex));
    }

    public void clearProjectionHistory(){
        pHistory.clear();
    }

    public penetrationHistory findClosestAxis(Ball ball, Polygon obstacle){
        //default to the first

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

    public boolean doPolygonCollisionDetection(Ball ball, Polygon obstacle, float timeStep) {


        PointF ballCenter = new PointF();
        PointF ballCenterPrev = new PointF();
        PointF obstacleCenter = new PointF();
        PointF[] obstacleCoords;

        PointF offsetVector = new PointF();
        PointF normalAxis = new PointF();
        float radius;

        PointF newVelocity = new PointF();

        boolean gapDetected;

        //Get vertexes for obstacle
        //Get radius/center for ball
        //gather info
        ballCenter.set(ball.getCenter());
        ballCenterPrev.set(ballCenter.x - ball.getXVelocity(), ballCenter.y - ball.getYVelocity());
        obstacleCenter.set(obstacle.getCenter());
        obstacleCoords = obstacle.get2dCoordArray();
        radius = ball.getRadius();
        //reset projection history
        clearProjectionHistory();

        //calculate offset vector between obstacles
        offsetVector = new PointF(ballCenter.x - obstacleCenter.x, ballCenter.y - obstacleCenter.y);

        //find distance to closest vertex and normalize
        PointF nearestVertex = getNearestVertex(obstacleCoords, ballCenter);

        //normalize vector
        float nearestVertexLength = nearestVertex.length();
        normalAxis = new PointF(nearestVertex.x / nearestVertexLength, nearestVertex.y / nearestVertexLength);

        //Determine if a gap exists in the projection
        gapDetected = projectPointsAndTestForGap(normalAxis, offsetVector, obstacleCoords, ballCenter, radius, nearestVertex);

        if (gapDetected) {
            //definitely no collision, exit
            return false;
        }

        //or else we need to keep on going

        //calculate normal axis for each vertex pair, and project all the points onto each normal axis
        for (int i = 0; i < obstacleCoords.length; i++) {
            normalAxis = makeNormalVectorBetweenPoints(obstacleCoords, i);

            //project each vertex / circle onto the current normal axis, check for gap
            gapDetected = projectPointsAndTestForGap(normalAxis, offsetVector, obstacleCoords, ballCenter, radius, obstacleCoords[i]);

            if (gapDetected) {
                //definitely no collision, exit
                return false;
            }
        }

        //Collision has occurred.
        //
        calculateCollisionInfo(ball, obstacle, timeStep);
        return true;

        // need to calculate where the ball position will be after collision.
        //New resultant velocity is also determined / set within here.
        //return calculateChangeInCoords(ball, obstacle);
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

    private void calculateCollisionInfo(Ball ball, Polygon obstacle, float timeStep){
        //Find the most likely axis of penetration, based on depth of penetration.
        penetrationHistory pHistory = findClosestAxis(ball, obstacle);
        PointF boundaryAxis = pHistory.mNormalAxis;
        displacementVector = pHistory.mDisplacementVector;
        float penetration = pHistory.mPenetrationDistance;

        //save some information
        PointF ballCenterPrev = new PointF();
        PointF prevVelocity = new PointF(ball.getXVelocity(), ball.getYVelocity());
        PointF prevVelocityStep = new PointF(prevVelocity.x * timeStep, prevVelocity.y * timeStep);
        ballCenterPrev.set(ball.getCenter().x - prevVelocityStep.x, ball.getCenter().y - prevVelocityStep.y);

        //Calculate the angle of the ball's velocity against the boundary axis
        double prevAngle = Math.acos(dotProduct(boundaryAxis,prevVelocityStep) / (boundaryAxis.length() * prevVelocityStep.length()));
        //Use this angle to calculate how far the ball traveled through the obstacle (hypotenuse)
        double hypotenuse = Math.abs(penetration) / Math.cos(prevAngle);

        //Calculate the % of velocity used before collision
        float percentOfVelocityUsed = ((prevVelocity.length() - (float) Math.abs(hypotenuse)) / prevVelocity.length());
        /*System.out.println(". ");
        System.out.println("prev velocity: " + prevVelocity.x + ";" + prevVelocity.y);
        System.out.println("hypotenuse: " + hypotenuse);
        System.out.println("penetration: " + penetration);
        System.out.println("prev angle: " + prevAngle);
        System.out.println("normal axis: " + boundaryAxis);
        System.out.println("mVertex: " + pHistory.mVertex);
        System.out.println("circle x,y top and right bounds: " + (ball.getCenter().x + ball.getRadius()) + ";" + (ball.getCenter().y + ball.getRadius()));
        System.out.println(".");*/

        mCollisions.add(new collisionHistory(percentOfVelocityUsed, boundaryAxis, obstacle));

    }

    public PointF calculateChangeInCoords(Ball ball, ArrayList<collisionHistory> collisions, float timeStep){

        boolean multipleCollisions = (collisions.size() > 1);
        //Even if there were multiple collisions, we can just use information from one of the collisions
        //for almost all the calculations we need to do.
        collisionHistory collision = collisions.get(0);

        PointF newVelocity;
        PointF changeInCoords;

        PointF ballCenterPrev = ball.getPrevCenter();
        PointF prevVelocity = new PointF(ball.getXVelocity(), ball.getYVelocity());
        PointF boundaryAxis = collision.mBoundaryAxis;

        //Calculate the % of velocity used before collision, and % after collision
        //TODO will this change after the nth collision in 1 frame?
        //System.out.println("collision.mTime: " + collision.mTime);
        float percentOfVelocityUsed = collision.mTime;  //before
        float percentOfVelocityOver = 1 - collision.mTime;      //after

        //cover corner case where new ball location barely hits boundary
        if (percentOfVelocityUsed == 0){

            //Move ball right up to the edge
            changeInCoords = new PointF(prevVelocity.x, prevVelocity.y);
            //Update with new resultant velocity
            calculateNewVelocity(ball, boundaryAxis);

            //most normal cases where ball needs to be moved back to collision point
        } else {

            //System.out.println("percent of velocity over: " + percentOfVelocityOver);
            //calculate amount that ball must be moved back (to where ball first collided), and move ball there.
            //displacementVector = new PointF(-prevVelocity.x * percentOfVelocityOver, -prevVelocity.y * percentOfVelocityOver);
            //System.out.println("prev velocity: " + prevVelocity.x + ";" + prevVelocity.y);
            displacementVector = new PointF(-prevVelocity.x * (percentOfVelocityOver), -prevVelocity.y * (percentOfVelocityOver));
            ball.updateAABB(displacementVector.x, displacementVector.y);

            //System.out.println("inner displacement vector: " + displacementVector.x + ";" + displacementVector.y);

            //Calculate new resultant velocity after collision
            if (multipleCollisions){
                System.out.println("new velocity (multiple collisions): " + calculateNewVelocity(ball, collisions));
            } else {
                System.out.println("new velocity: " + calculateNewVelocity(ball, boundaryAxis));
            }

            //Based on the new ball location, calculate what the change was from the original location.
            PointF newCenter = ball.getCenter();
            //System.out.println("ball center prev: " + ballCenterPrev.x + ";" + ballCenterPrev.y);
            changeInCoords = new PointF(newCenter.x - ballCenterPrev.x, newCenter.y - ballCenterPrev.y);
        }

        return changeInCoords;
    }

    /*
    private void moveBallAfterCollision(){
        //Calculate vector that ball should move after colliding, and move ball there.
        PointF secondDisplacementVector = new PointF(newVelocity.x * percentOfVelocityOver, newVelocity.y * percentOfVelocityOver);
        ball.updateAABB(secondDisplacementVector.x, secondDisplacementVector.y);

        //Based on the new ball location, calculate what the change was from the original location.
        PointF newCenter = ball.getCenter();
        changeInCoords = new PointF(newCenter.x - ballCenterPrev.x, newCenter.y - ballCenterPrev.y);
    }*/

    private PointF calculateNewVelocity(Ball ball, PointF boundaryAxis){
        float velocityChange;
        PointF velocityChangeVector;
        PointF newVelocity;

        velocityChange = 2 * dotProduct(ball.getVelocity(),boundaryAxis);
        velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        newVelocity = new PointF(ball.getXVelocity() - velocityChangeVector.x, ball.getYVelocity() - velocityChangeVector.y);
        ball.setVelocity(newVelocity);

        return newVelocity;
    }

    private PointF calculateNewVelocity(Ball ball, ArrayList<collisionHistory> collisions){
        float velocityChange;
        PointF velocityChangeVector;
        PointF newVelocity;
        PointF combinedBoundaryAxis = new PointF(0.0f, 0.0f);

        for (collisionHistory collision : collisions){
            float currentBAX = collision.mBoundaryAxis.x;
            float currentBAY = collision.mBoundaryAxis.y;
            combinedBoundaryAxis.set(combinedBoundaryAxis.x + currentBAX, combinedBoundaryAxis.y + currentBAY);
        }

        //normalize
        float CBAlength = combinedBoundaryAxis.length();
        combinedBoundaryAxis.set(combinedBoundaryAxis.x / CBAlength, combinedBoundaryAxis.y / CBAlength);

        velocityChange = 2 * dotProduct(ball.getVelocity(),combinedBoundaryAxis);
        velocityChangeVector = new PointF(combinedBoundaryAxis.x * velocityChange, combinedBoundaryAxis.y * velocityChange);
        newVelocity = new PointF(ball.getXVelocity() - velocityChangeVector.x, ball.getYVelocity() - velocityChangeVector.y);
        ball.setVelocity(newVelocity);

        return newVelocity;
    }

    private float clamp(float min, float max, float target){
        float result;

        if (target > max) {
            result = max;
        } else if (target < min) {
            result = min;
        } else {
            result = target;
        }

        return result;
    }

    private PointF getNearestVertex(PointF[] obstacleCoords, PointF ballCenter){
        float smallestLength = GameState.LARGE_NUMBER;
        float currentLength;
        PointF smallestVector = new PointF();
        PointF distanceVector;

        for (int i = 0; i < obstacleCoords.length; i++){
            distanceVector = new PointF(obstacleCoords[i].x - ballCenter.x, obstacleCoords[i].y - ballCenter.y);
            currentLength = distanceVector.length();
            if (currentLength < smallestLength){
                smallestLength = currentLength;
                smallestVector.set(distanceVector);
            }
        }

        return smallestVector;
    }

    //TODO - remove offset vector param
    private boolean projectPointsAndTestForGap(PointF normalAxis, PointF offsetVector, PointF[] obstacleCoords, PointF ballCenter, float radius, PointF vertex){
        //Project points onto normal axis and find min / max
        float vertexMin = GameState.LARGE_NUMBER;
        float vertexMax = GameState.SMALL_NUMBER;

        //System.out.println("Projection Axis: " + normalAxis);

        for (int i = 0; i < obstacleCoords.length; i++) {
            float vertexProjection = dotProduct(normalAxis, obstacleCoords[i]);

            if (vertexProjection > vertexMax)
                vertexMax = vertexProjection;
            if (vertexProjection < vertexMin)
                vertexMin = vertexProjection;
        }

        //System.out.println("min point: " + vertexMin + " max point: " + vertexMax);

        //project circle points onto normal axis (max is radius, min is negative radius)
        float circleProjection = dotProduct(normalAxis, ballCenter);
        float circleMin = circleProjection - radius;
        float circleMax = circleProjection + radius;

        float offsetValue = dotProduct(normalAxis, offsetVector);

        //add offset value to min/max from polygon  ?don't need offset anymore?
       /* vertexMin = vertexMin + offsetValue;
        vertexMax = vertexMax + offsetValue;*/

        //System.out.println("vertex min: " + vertexMin + " vertex max: " + vertexMax + " circleMin: " + circleMin + " circleMax: " + circleMax);

        float result1 = vertexMin - circleMax;
        float result2 = circleMin - vertexMax;

        //check if the gap is greater than 0 (min - max > 0), which means definitely no collision!
        if ((result1 > 0) || (result2 > 0 )){
            return true;
        } else {
            //Penetration will be a negative number; because a positive number indicates a gap
            addProjectionToHistory(normalAxis, Math.max(result1,result2), vertex);
            return false;
        }

    }

    private float dotProduct(PointF vector1, PointF vector2){
        return ((vector1.x * vector2.x) + (vector1.y * vector2.y));
    }

    public PointF getDisplacementVector(){
        return displacementVector;
    }

    //Static to avoid initializing CollisionDetection class unless we think there may be a collision
    public static boolean testBoundingBoxes(Ball ball, Polygon obstacle){
        //test x collision
        if (((ball.getMaxX()) >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX())){
            //test y collision
            if (((ball.getMaxY()) >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY())) {
                return true;
            }
        }

        return false;
    }

/*
    private ArrayList<PointF> getIntersectionPoints(Ball ball, PointF lineNormal, PointF vertex){
        ArrayList<PointF> results = new ArrayList<>();
        PointF ballCenter = ball.getCenter();
        float ballRadius = ball.getRadius();

        //To get line BA from the normal of BA, flip x/y and take the negative of x component
        float baX = lineNormal.y;
        float baY = -lineNormal.x;
        float caX = ballCenter.x - vertex.x;
        float caY = ballCenter.y - vertex.y;

        float a = baX * baX + baY * baY;
        float bBy2 = baX * caX + baY * caY;
        float c = caX * caX + caY * caY - ballRadius * ballRadius;

        float pBy2 = bBy2 / a;
        float q = c / a;

        float disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return null;
        }
        // if disc == 0 ... dealt with later
        float tmpSqrt = (float) Math.sqrt(disc);
        float abScalingFactor1 = -pBy2 + tmpSqrt;
        float abScalingFactor2 = -pBy2 - tmpSqrt;

        PointF p1 = new PointF(vertex.x - baX * abScalingFactor1, vertex.y
                - baY * abScalingFactor1);
        results.add(p1);

        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return results;
        }
        PointF p2 = new PointF(vertex.x - baX * abScalingFactor2, vertex.y
                - baY * abScalingFactor2);
        results.add(p2);
        return results;
    } */

/* no longer using
    public PointF doRectCollisionDetection(Ball ball, Polygon obstacle){
        PointF ballCenter = new PointF();
        PointF ballCenterPrev = new PointF();
        PointF obstacleCenter = new PointF();
        PointF nearestPoint;
        PointF distanceFromNearestPoint;

        float lengthDFNP;
        float penetration;

        PointF distanceVector;
        PointF clampedVector;

        float obstacleHalfX;
        float obstacleHalfY;
        float clampedXValue;
        float clampedYValue;

        PointF prevVelocity = new PointF();
        PointF newVelocity = new PointF();

        PointF changeInCoords = new PointF();

        //gather info
        ballCenter.set(ball.getCenter());
        ballCenterPrev.set(ballCenter.x - ball.getXVelocity(), ballCenter.y - ball.getYVelocity());
        obstacleCenter.set(obstacle.getCenter());
        obstacleHalfX = (obstacle.getMaxX() - obstacle.getMinX()) / 2;
        obstacleHalfY = (obstacle.getMaxY() - obstacle.getMinY()) / 2;

        //calculate distance vector between obstacles
        distanceVector = new PointF(ballCenter.x - obstacleCenter.x, ballCenter.y - obstacleCenter.y);

        //clamp distance vector to half values
        clampedXValue = clamp(-obstacleHalfX,obstacleHalfX,distanceVector.x);
        clampedYValue = clamp(-obstacleHalfY,obstacleHalfY,distanceVector.y);
        clampedVector = new PointF(clampedXValue, clampedYValue);

        //calculate nearest point within obstacle (to the ball center) by adding clamped vector to center
        nearestPoint = new PointF(obstacleCenter.x + clampedVector.x, obstacleCenter.y + clampedVector.y);

        //calculate the distance vector of the ball to the nearest point
        distanceFromNearestPoint = new PointF(nearestPoint.x - ballCenter.x, nearestPoint.y - ballCenter.y);

        //calculate length of DFNP vector
        lengthDFNP = distanceFromNearestPoint.length();

        //check if the final length is smaller than the radius of the ball
        penetration = (ball.getRadius() - lengthDFNP);


        if (penetration < 0) {
            return null;
        }

        ////HANDLE COLLISION

        System.out.println(" current ball velocity: " + ball.getXVelocity() + " . " + ball.getYVelocity());

        //If the intersection point is on left or right side, flip x velocity
        if ((nearestPoint.x == obstacle.getMinX()) || (nearestPoint.x == obstacle.getMaxX())){

            changeInCoords = calculateNewPosition(ball, penetration, true);

            //if the point is on top or bottom, flip y velocity
        } else if ((nearestPoint.y == obstacle.getMinY()) || (nearestPoint.y == obstacle.getMaxY())){

            changeInCoords = calculateNewPosition(ball, penetration, false);

        //corner case where ball is moving fast enough that center is inside the obstacle
        } else {
            //so we need to determine which edge it was closest to
            float diffMinY = Math.abs(nearestPoint.y - obstacle.getMinY());
            float diffMaxY = Math.abs(nearestPoint.y - obstacle.getMaxY());
            float diffMinX = Math.abs(nearestPoint.x - obstacle.getMinX());
            float diffMaxX = Math.abs(nearestPoint.x - obstacle.getMaxX());

            //and then, we can do the same as before- move it out and calculate new position
            if (Math.min(diffMinX, diffMaxX) < Math.min(diffMinY, diffMaxY)){
                changeInCoords = calculateNewPosition(ball, penetration, true);
            } else {
                changeInCoords = calculateNewPosition(ball, penetration, true);
            }

        }

        return changeInCoords;
    }

    private PointF calculateNewPosition(Ball ball, float penetration, boolean collisionOnXSide){
        PointF changeInCoords;
        PointF ballCenterPrev = new PointF();
        PointF prevVelocity = new PointF(ball.getXVelocity(), ball.getYVelocity());
        ballCenterPrev.set(ball.getCenter().x - prevVelocity.x, ball.getCenter().y - prevVelocity.y);

        //Move ball back to original collision spot
        double prevAngle = Math.atan2 ((double) prevVelocity.y, (double) prevVelocity.x);
        double hypotenuse = penetration / Math.cos(prevAngle);

        float percentOfVelocityUsed = ((prevVelocity.length() - (float) Math.abs(hypotenuse)) / prevVelocity.length());
        System.out.println("percent: " + percentOfVelocityUsed);
        System.out.println("prev vel len: " + prevVelocity.length());
        float percentOfVelocityOver = 1 - percentOfVelocityUsed;

        //cover corner case where new ball location barely hits boundary
        if (percentOfVelocityUsed == 0){
            changeInCoords = new PointF(prevVelocity.x, prevVelocity.y);
            flipVelocityAndUpdate(ball, collisionOnXSide);

        //most normal cases where ball needs to be moved back to collision point and then bounced
        } else {
            //calculate amount that ball must be moved back (where ball first collided)
            displacementVector = new PointF(-prevVelocity.x * percentOfVelocityOver, -prevVelocity.y * percentOfVelocityOver);
            ball.updateAABB(displacementVector.x, displacementVector.y);
            System.out.println("displacement vector 1 x: " + displacementVector.x + " displacement vector 1 y: " + displacementVector.y);

            //Move ball to new spot using remainder of velocity
            float remainderOfVelocity = 1 - percentOfVelocityUsed;
            PointF newVelocity = flipVelocityAndUpdate(ball, collisionOnXSide);

            PointF secondDisplacementVector = new PointF(newVelocity.x * percentOfVelocityOver, newVelocity.y * percentOfVelocityOver);
            ball.updateAABB(secondDisplacementVector.x, secondDisplacementVector.y);
            System.out.println("displacement vector 2 x: " + secondDisplacementVector.x + " displacement vector 2 y: " + secondDisplacementVector.y);

            PointF newCenter = ball.getCenter();
            changeInCoords = new PointF(newCenter.x - ballCenterPrev.x, newCenter.y - ballCenterPrev.y);

        }

        System.out.println("prev angle " + prevAngle);
        System.out.println("penetration" + penetration);
        System.out.println("hypotenuse " + hypotenuse);
        System.out.println("change in Coords x: " + changeInCoords.x + " change in coords y: " + changeInCoords.y);

        return changeInCoords;
    }

    private PointF flipVelocityAndUpdate(Ball ball, boolean xSide){
        PointF newVelocity;

        if (xSide) {
            newVelocity = new PointF(ball.getXVelocity() * -1, ball.getYVelocity());
        } else {
            newVelocity = new PointF(ball.getXVelocity(), ball.getYVelocity() * -1);
        }

        ball.setVelocity(newVelocity);
        return newVelocity;
    } */

}
