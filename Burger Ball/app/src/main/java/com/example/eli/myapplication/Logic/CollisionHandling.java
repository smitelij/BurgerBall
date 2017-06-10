package com.example.eli.myapplication.Logic;

import android.graphics.PointF;

import com.example.eli.myapplication.Logic.Ball.BallEngine;
import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;
import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Model.Collision;
import com.example.eli.myapplication.Model.MovingObstacle;
import com.example.eli.myapplication.Model.Target;

import java.util.ArrayList;

/**
 * Created by Eli on 6/6/2016.
 */
public class CollisionHandling {

    ArrayList<Collision> mCollisions;
    ArrayList<Collision> mBoundaryCollisions = new ArrayList<>();
    ArrayList<Collision> mBallCollisions = new ArrayList<>();



    public CollisionHandling(ArrayList<Collision> allCollisions){
        mCollisions = allCollisions;
    }

    public ArrayList<Collision> getFirstCollision() {

        //sanity check
        if (mCollisions.size() == 0){
            return null;
        }

        //Initialize arraylist
        ArrayList<Collision> firstCollision = new ArrayList<>();
        float firstCollisionTime = GameState.LARGE_NUMBER; //time should always be less than this big number...

        //loop through all finding the earliest collision
        for (Collision collision : mCollisions){

            //Don't care about target collisions here-  they don't affect any trajectories
            if(collision.getObstacle().getType() == GameState.INTERACTABLE_TARGET){
                continue;
            }

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

        //caller of this function expects a null value if there are no first collisions.
        if (firstCollision.size()==0){
            return null;
        }

        return firstCollision;
    }


    //A reasonable question- Why are we splitting this into two methods?
    // Why don't we just go through all collisions, and check obstacle type, to determine
    //which calculateVelocity method to use?
    // ...
    // In order to better handle an edge case involving a ball simultaneously colliding with
    // a boundary and another ball, it makes more sense to handle boundary collisions first
    public void handleBoundaryCollisions(BallEngine ballEngine){
        for (Collision currentCollision : mBoundaryCollisions){
            if (currentCollision.getObstacle().getType() == GameState.INTERACTABLE_MOVING_OBSTACLE){
                calculateSpinChangeMovingBorder(ballEngine, currentCollision);
                calculateVelocityMovingBorderCollision(ballEngine, currentCollision);
            } else {
                calculateSpinChangeStationaryBorder(ballEngine, currentCollision);
                calculateVelocityStationaryBorderCollision(ballEngine, currentCollision);
            }
        }
    }

    public void handleBallCollisions(BallEngine ballEngine){
        for (Collision currentCollision : mBallCollisions){
            calculateSpinChangeBallCollision(currentCollision, ballEngine);
            calculateVelocityBallCollision(currentCollision, ballEngine);
        }
    }

    //**Reference:
    //http://gamedev.stackexchange.com/questions/23672/determine-resulting-angle-of-wall-collision
    //
    private void calculateVelocityStationaryBorderCollision(BallEngine ballEngine, Collision collision){
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ballEngine.getVelocity(ball, collision.getTime());

        //Formula to use:
        // New Velocity =  v - (2(n · v) n )
        // n= normal vector (boundary axis), v= incoming vector

        float velocityChange = 2 * CommonFunctions.dotProduct(oldVelocity, boundaryAxis);
        PointF velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        PointF newVelocity = new PointF(oldVelocity.x - velocityChangeVector.x, oldVelocity.y - velocityChangeVector.y);

        newVelocity = reduceVelocityElasticLoss(ballEngine, ball, newVelocity);

        System.out.println("stationary border collision set velocity.");
        ball.setVelocity(newVelocity);
    }

    //**Reference material:
    //http://vobarian.com/collisions/2dcollisions2.pdf
    private void calculateVelocityBallCollision(Collision collision, BallEngine ballEngine){

        //get balls
        Ball ball1 = collision.getBall();
        Ball ball2 = (Ball) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UTangentVector = collision.getBoundaryAxis();
        PointF UNormalVector = new PointF(UTangentVector.y, -UTangentVector.x);
        //System.out.println("boundary axis length: " + collision.getBoundaryAxis().length());

        //get velocities for balls
        PointF ball1velocity = ballEngine.getAvailableVelocity(ball1, collision.getTime());  //if a ball collides with more than one other ball,
        PointF ball2velocity = ballEngine.getAvailableVelocity(ball2, collision.getTime());  //available velocity will differ from normal velocity

        //determine component velocities for ball1 / ball2 in the tangent / normal directions
        float velocity1tangent = CommonFunctions.dotProduct(ball1velocity, UTangentVector);
        float velocity1normal = CommonFunctions.dotProduct(ball1velocity, UNormalVector);
        float velocity2tangent = CommonFunctions.dotProduct(ball2velocity, UTangentVector);
        float velocity2normal = CommonFunctions.dotProduct(ball2velocity, UNormalVector);

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

        //subtract for elasticity
        newVelocity1 = reduceVelocityElasticLoss(ballEngine, ball1, newVelocity1);
        newVelocity2 = reduceVelocityElasticLoss(ballEngine, ball2, newVelocity2);

        //set velocity
        ball1.addNewVelocity(newVelocity1);
        ball2.addNewVelocity(newVelocity2);
    }

    public void updateCollisionCollections(BallEngine ballEngine, ArrayList<Collision> collisions){

        for (Collision currentCollision : collisions) {

            if (currentCollision.getObstacle().getType() == GameState.INTERACTABLE_BALL) {

                Ball currentBall = currentCollision.getBall();
                Ball otherBall = (Ball) currentCollision.getObstacle();

                ballEngine.addBallCollision(currentBall, currentCollision);
                ballEngine.addBallCollision(otherBall, currentCollision);

                mBallCollisions.add(currentCollision);

            } else {
                mBoundaryCollisions.add(currentCollision);
                ballEngine.addObstacleCollision(currentCollision.getBall(), currentCollision);
            }
        }
    }

    public ArrayList<Target> getTargetCollisions(float firstCollisionTime){
        ArrayList<Target> hitTargets = new ArrayList<>();

        for (Collision collision : mCollisions){
            if (collision.getObstacle().getType() == GameState.INTERACTABLE_TARGET){
                if (collision.getTime() <= firstCollisionTime){
                    hitTargets.add((Target) collision.getObstacle());
                }
            }
        }
        return hitTargets;
    }

    //**Reference:
    //http://gamedev.stackexchange.com/questions/23672/determine-resulting-angle-of-wall-collision
    //
    private void calculateVelocityMovingBorderCollision(BallEngine ballEngine, Collision collision){
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ballEngine.getVelocity(ball, collision.getTime());
        MovingObstacle obstacle = (MovingObstacle)collision.getObstacle();
        PointF obstacleVelocity = obstacle.getVelocity();

        //In order to calculate the new velocity, we will need to add together the velocities of
        // the moving obstacle and the ball. In order to do that, we will need to first calculate
        // how much of the obstacle velocity is in the direction of the collision.
        PointF outerBoundaryAxis = new PointF(-boundaryAxis.x, -boundaryAxis.y); //normal boundary axis points inside, we want to point outside.
        float velocityInCollisionDirection = (obstacleVelocity.x * outerBoundaryAxis.x) + (obstacleVelocity.y * outerBoundaryAxis.y);
        PointF obstacleDirectionalVel = new PointF(outerBoundaryAxis.x * velocityInCollisionDirection, outerBoundaryAxis.y * velocityInCollisionDirection);
        PointF totalCollisionVelocity = new PointF(oldVelocity.x - obstacleDirectionalVel.x, oldVelocity.y - obstacleDirectionalVel.y);

        System.out.println("ball velocity: " + oldVelocity);
        System.out.println("obstacle velocity: " + obstacleVelocity);
        System.out.println("boundary axis: " + boundaryAxis);
        System.out.println("velocity in collision direction: " + velocityInCollisionDirection);
        System.out.println("obstacle directional vel: " + obstacleDirectionalVel);
        System.out.println("total collision velocity: " + totalCollisionVelocity);

        //Now that we know how fast the ball and obstacle collided, we can pretend the
        // obstacle is stationary to calculate the change in velocity the collision will cause

        //Formula to use:
        // New Velocity =  v - (2(n · v) n )
        // n= normal vector (boundary axis), v= incoming vector

        float velocityChange = 2 * CommonFunctions.dotProduct(totalCollisionVelocity, boundaryAxis);
        PointF velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        PointF newVelocity = new PointF(totalCollisionVelocity.x - velocityChangeVector.x, totalCollisionVelocity.y - velocityChangeVector.y);

        PointF newVelocityElastic = reduceVelocityElasticLoss(ballEngine, ball, newVelocity);
        System.out.println("new velocity elastic: " + newVelocityElastic);

        //Finally, we must add the obstacle directional velocity with the post-collision change
        // in velocity (newVelocityElastic), to get our ball's final velocity.
        PointF finalVelocity = new PointF(newVelocityElastic.x + obstacleDirectionalVel.x, newVelocityElastic.y + obstacleDirectionalVel.y);
        System.out.println("final velocity: " + finalVelocity);

        ball.setVelocity(finalVelocity);

    }

    private PointF reduceVelocityElasticLoss(BallEngine ballEngine, Ball currentBall, PointF velocity) {
        if (ballEngine.shouldElasticLossBeAppliedForCollision(currentBall)) {
            velocity = new PointF(velocity.x * GameState.ELASTIC_CONSTANT, velocity.y * GameState.ELASTIC_CONSTANT);
        }
        return velocity;
    }

    private void calculateSpinChangeStationaryBorder(BallEngine ballEngine, Collision collision) {
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ballEngine.getVelocity(ball, collision.getTime());

        calculateSpinChange(ball,oldVelocity,boundaryAxis);
    }

    private void calculateSpinChangeMovingBorder(BallEngine ballEngine, Collision collision) {
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ballEngine.getVelocity(ball, collision.getTime());
        MovingObstacle obstacle = (MovingObstacle)collision.getObstacle();
        PointF obstacleVelocity = obstacle.getVelocity();

        //Use code from calculateVelocityMovingBorderCollision to calculate total impact velocity
        PointF outerBoundaryAxis = new PointF(-boundaryAxis.x, -boundaryAxis.y); //normal boundary axis points inside, we want to point outside.
        float velocityInCollisionDirection = (obstacleVelocity.x * outerBoundaryAxis.x) + (obstacleVelocity.y * outerBoundaryAxis.y);
        PointF obstacleDirectionalVel = new PointF(outerBoundaryAxis.x * velocityInCollisionDirection, outerBoundaryAxis.y * velocityInCollisionDirection);
        PointF totalCollisionVelocity = new PointF(oldVelocity.x - obstacleDirectionalVel.x, oldVelocity.y - obstacleDirectionalVel.y);

        calculateSpinChange(ball, totalCollisionVelocity, boundaryAxis);
    }

    private void calculateSpinChangeBallCollision(Collision collision, BallEngine ballEngine) {
        //get balls
        Ball ball1 = collision.getBall();
        Ball ball2 = (Ball) collision.getObstacle();

        //get velocities
        PointF ball1vel = ballEngine.getVelocity(ball1, collision.getTime());
        PointF ball2vel = ballEngine.getVelocity(ball2, collision.getTime());

        //get collision axis
        PointF collisionAxis1 = collision.getBoundaryAxis();
        collisionAxis1 = new PointF(collisionAxis1.y, -collisionAxis1.x);

        //calculate spin changes
        calculateSpinChange(ball1, ball1vel, collisionAxis1);
        calculateSpinChange(ball2, ball2vel, collisionAxis1);
    }

    private void calculateSpinChange(Ball ball, PointF oldVelocity, PointF boundaryAxis) {

        System.out.println("calculate spin change: boundary axis - " + boundaryAxis);
        System.out.println("calculate spin change: old velocity - " + oldVelocity);

        PointF surfaceVector = new PointF(-boundaryAxis.y, boundaryAxis.x);
        float surfaceVelocity = CommonFunctions.dotProduct(oldVelocity, surfaceVector);

        System.out.println("surface velocity: " + surfaceVelocity);

        if (Math.abs(oldVelocity.length()) < 1.2) {
            System.out.println("less than 1- using normal spin");
            ball.setSpin(surfaceVelocity / -11);
        } else if ((Math.abs(surfaceVelocity) < 1.2) && (Math.abs(ball.getCurrentRotation()) > 0.05 )) {
            System.out.println("surface veloc less than 1: reversing spin");
            ball.reverseSpin();
        } else {
            System.out.println("setting normal spin.");
            ball.setSpin(surfaceVelocity / -11);
        }

        //ball.normalizeSpin();

    }

}
