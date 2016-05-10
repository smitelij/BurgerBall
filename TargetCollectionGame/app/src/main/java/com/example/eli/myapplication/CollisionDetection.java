package com.example.eli.myapplication;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.HashMap;
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

    private ArrayList<penetrationHistory> pHistory = new ArrayList<penetrationHistory>();
    private ArrayList<CollisionHistory> mCollisions = new ArrayList<>();

    private PointF displacementVector;

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

    public boolean doBallCollisionDetection(Ball ball1, Ball ball2, float timeStep){
        PointF ball1center;
        PointF ball2center;

        PointF distanceVector;
        float distance;

        ball1center = ball1.getCenter();
        ball2center = ball2.getCenter();

        System.out.println("ball1 center current: " + ball1center.x + ";" + ball1center.y);
        System.out.println("ball2 center current: " + ball2center.x + ";" + ball2center.y);

        distanceVector = new PointF(ball1center.x - ball2center.x, ball1center.y - ball2center.y);
        distance = distanceVector.length();

        if (distance >= (ball1.getRadius() + ball2.getRadius())){
            return false;
        }

        //calculate more collision info, such as timing
        calculateBallCollisionInfo(ball1, ball2, distance, timeStep);
        return true;
    }

    private void calculateBallCollisionInfo(Ball ball1, Ball ball2, float currentDistance, float timeStep){
        PointF ball1PrevCenter = ball1.getPrevCenter();
        PointF ball2PrevCenter = ball2.getPrevCenter();

        PointF oldDistanceVector = new PointF(ball1PrevCenter.x - ball2PrevCenter.x, ball1PrevCenter.y - ball2PrevCenter.y);

        PointF velocityDifference = new PointF(ball1.getXVelocity() - ball2.getXVelocity(), ball1.getYVelocity() - ball2.getYVelocity());

        float distanceBetweenBalls = ball1.getRadius() + ball2.getRadius();

        System.out.println("distance x: " + oldDistanceVector.x );
        System.out.println("distance y: " + oldDistanceVector.y);
        System.out.println("velocity difference x: " + velocityDifference.x);
        System.out.println("velocity difference y: " + velocityDifference.y);

        double quadA = (velocityDifference.x * velocityDifference.x) + (velocityDifference.y * velocityDifference.y);
        double quadB =  (2 * (oldDistanceVector.x * velocityDifference.x)) + (2 * oldDistanceVector.y * velocityDifference.y);
        double quadC = (oldDistanceVector.x * oldDistanceVector.x) + (oldDistanceVector.y * oldDistanceVector.y) - (distanceBetweenBalls * distanceBetweenBalls);

        System.out.println("quad a :" + quadA);
        System.out.println("quad b : " + quadB);
        System.out.println("quad c: " + quadC);
        float collisionTime = (float) doQuadratic(quadA,quadB,quadC);

        System.out.println("collision time: " + collisionTime);

        PointF ball1NewPos = new PointF(ball1PrevCenter.x + (collisionTime * ball1.getXVelocity()), ball1PrevCenter.y + (collisionTime * ball1.getYVelocity()));
        PointF ball2NewPos = new PointF(ball2PrevCenter.x + (collisionTime * ball2.getXVelocity()), ball2PrevCenter.y + (collisionTime * ball2.getYVelocity()));

        PointF distanceVector = new PointF(ball1NewPos.x - ball2NewPos.x, ball1NewPos.y - ball2NewPos.y);
        System.out.println("distance between points: " + distanceVector.length());

        PointF[] vertexArray = new PointF[]{ball1NewPos, ball2NewPos};
        PointF boundaryAxis = makeNormalVectorBetweenPoints(vertexArray,0);

        mCollisions.add(new CollisionHistory(collisionTime, boundaryAxis, ball2, ball1));
    }

    private double doQuadratic(double a, double b, double c){
        //-b +/- sqrt(b^2 - 4ac) / 2a

        double squareRoot = Math.sqrt((b * b) - (4*a*c));
        double result1 = (-b + squareRoot) / (2*a);
        double result2 = (-b - squareRoot) / (2*a);

        if (result1 < 0){
            return result2;
        } else if (result2 < 0){
            return result1;
        } else {
            return Math.min(result1, result2);
        }
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
        calculateBoundaryCollisionInfo(ball, obstacle, timeStep);
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

    private void calculateBoundaryCollisionInfo(Ball ball, Polygon obstacle, float timeStep){
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

        mCollisions.add(new CollisionHistory(percentOfVelocityUsed, boundaryAxis, obstacle, ball));

    }

    public PointF calculateNewVelocity(Ball ball, ArrayList<CollisionHistory> collisions){

        int collisionType = determineCollisionType(collisions);

        if ((collisionType == 1) || (collisionType ==3)) {

            float velocityChange;
            PointF velocityChangeVector;
            PointF newVelocity;
            PointF combinedBoundaryAxis = new PointF(0.0f, 0.0f);

            for (CollisionHistory collision : collisions) {
                float currentBAX = collision.getBoundaryAxis().x;
                float currentBAY = collision.getBoundaryAxis().y;
                combinedBoundaryAxis.set(combinedBoundaryAxis.x + currentBAX, combinedBoundaryAxis.y + currentBAY);
            }

            //normalize
            float CBAlength = combinedBoundaryAxis.length();
            combinedBoundaryAxis.set(combinedBoundaryAxis.x / CBAlength, combinedBoundaryAxis.y / CBAlength);

            velocityChange = 2 * dotProduct(ball.getVelocity(), combinedBoundaryAxis);
            velocityChangeVector = new PointF(combinedBoundaryAxis.x * velocityChange, combinedBoundaryAxis.y * velocityChange);
            newVelocity = new PointF(ball.getXVelocity() - velocityChangeVector.x, ball.getYVelocity() - velocityChangeVector.y);
            ball.setVelocity(newVelocity);

            return newVelocity;

        } else if(collisionType == 2){
            return calculateVelocitiesBallBallCollision(collisions.get(0));
        }

        return null;
    }

    private PointF calculateVelocitiesBallBallCollision(CollisionHistory collision){
        //get balls
        Ball ball1 = collision.getBall();
        Ball ball2 = (Ball) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UTangentVector = collision.getBoundaryAxis();
        PointF UNormalVector = new PointF(UTangentVector.y, -UTangentVector.x);

        //get velocities for balls
        PointF ball1velocity = ball1.getVelocity();
        PointF ball2velocity = ball2.getVelocity();

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
        ball1.setVelocity(newVelocity1);
        ball2.setVelocity(newVelocity2);

        return null;
    }

    private double getAngleOfVector(PointF vector){
        double collisionAngle = Math.atan((double) vector.y / vector.x);
        if ((vector.y < 0) && (vector.x < 0)){
            collisionAngle = collisionAngle + 3.14159265359;
        }

        return collisionAngle;
    }

    /*
    1 = single boundary collision
    2 = single ball collision
    3 = multiple boundary collision
    4 = multiple ball collision
    5 = multiple boundary / ball collision
    */
    private int determineCollisionType(ArrayList<CollisionHistory> collisions){
        int type = 0;

        if (collisions.size()==1){
            if (collisions.get(0).getObstacle().getType() == GameState.OBSTACLE_POLYGON){
                return 1;
            } else {
                //if not a polygon, then must be a ball
                return 2;
            }

        //if collision size is not 1, we know it must be 2 or greater.
        } else {
            int BoundaryCounter = 0;
            int BallCounter = 0;

            for (CollisionHistory curCollision : collisions){
                if (curCollision.getObstacle().getType() == GameState.OBSTACLE_POLYGON){
                    BoundaryCounter++;
                } else {
                    BallCounter++;
                }
            }

            //if no balls were found, must have been a multiple collision with only boundaries
            if (BallCounter == 0){
                return 3;
            }
            //if no boundaries were found, must have been a multiple collision with only balls
            if (BoundaryCounter == 0){
                return 4;
            }
            //otherwise, there must have been balls and boundaries that collided
            return 5;
        }

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
    public static boolean testBoundingBoxes(Ball ball, Interactable obstacle){
        //test x collision
        if (((ball.getMaxX()) >= obstacle.getMinX()) && (obstacle.getMaxX() >= ball.getMinX())){
            //test y collision
            if (((ball.getMaxY()) >= obstacle.getMinY()) && (obstacle.getMaxY() >= ball.getMinY())) {
                return true;
            }
        }

        return false;
    }

    public boolean didCollisionHappen(){
        return (getCollisions().size() >= 1);
    }

    //Collision info key:
    // 0 = no collisions
    // 1 = 1 ball collided
    // 2 = multiple balls (possibly colliding with multiple boundaries
    public int getCollisionInfo(){

        //no collisions
        if (getCollisions().size() == 0) {
            return 0;
        }

        //If we had at least one collision, grab the first collision(s), because
        //that is the only one that matters

        ArrayList<CollisionHistory> firstCollisions = getFirstCollision();
        int numberOfCollisions = firstCollisions.size();

        //one ball one collision
        if (numberOfCollisions == 1){
            return 1;
        }

        if (firstCollisions.size() > 0){

            int firstBallID = firstCollisions.get(0).getBall().getID();
            boolean multipleBalls = false; //default to false

            for (CollisionHistory currentCollision : firstCollisions){
                if (firstBallID != currentCollision.getBall().getID()){
                    multipleBalls = true;
                }
            }

            //more than one ball collided (possibly with multiple boundaries)
            if (multipleBalls){
                return 2;

            //only one ball collided, but with multiple boundaries
            } else {
                return 1; //Possibility here for a different status number, but currently doesn't seem necessary
            }
        }

        //should never get here
        return 4;
    }


    public ArrayList<CollisionHistory>[] createBallCollisionArray(ArrayList<CollisionHistory> collisions){
        ArrayList<CollisionHistory>[] mapping = new ArrayList[GameState.currentBalls];

        for (CollisionHistory currentCollision : collisions){
            int currentBallID = currentCollision.getBall().getID();

            //initialize arraylist as needed for each element
            if (mapping[currentBallID] == null){
                mapping[currentBallID] = new ArrayList<>();
            }

            mapping[currentBallID].add(currentCollision);
        }

        return mapping;
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
