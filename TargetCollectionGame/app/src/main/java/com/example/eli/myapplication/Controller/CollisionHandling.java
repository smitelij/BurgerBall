package com.example.eli.myapplication.Controller;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.GameState;
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
    public void handleBoundaryCollisions(){
        for (Collision currentCollision : mBoundaryCollisions){
            if (currentCollision.getObstacle().getType() == GameState.INTERACTABLE_MOVING_OBSTACLE){
                calculateVelocityMovingBorderCollision(currentCollision, false);
            } else {
                calculateVelocityStationaryBorderCollision(currentCollision, false);
            }
        }
    }

    public void handleBallCollisions(){
        for (Collision currentCollision : mBallCollisions){
            calculateVelocityBallCollision(currentCollision);
        }
    }

    //**Reference:
    //http://gamedev.stackexchange.com/questions/23672/determine-resulting-angle-of-wall-collision
    //
    private void calculateVelocityStationaryBorderCollision(Collision collision, boolean debug){
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ball.getVelocity(collision.getTime());

        //Formula to use:
        // New Velocity =  v - (2(n · v) n )
        // n= normal vector (boundary axis), v= incoming vector

        float velocityChange = 2 * GameState.dotProduct(oldVelocity, boundaryAxis);
        PointF velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        PointF newVelocity = new PointF(oldVelocity.x - velocityChangeVector.x, oldVelocity.y - velocityChangeVector.y);
        newVelocity.set(newVelocity.x * GameState.ELASTIC_CONSTANT, newVelocity.y * GameState.ELASTIC_CONSTANT);

        if (debug == false) {
            ball.setVelocity(newVelocity);
        } else {
            System.out.println("NEW VELOCITY (old method): " + newVelocity);
        }

    }

    //**Reference material:
    //http://vobarian.com/collisions/2dcollisions2.pdf
    private void calculateVelocityBallCollision(Collision collision){

        //get balls
        Ball ball1 = collision.getBall();
        Ball ball2 = (Ball) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UTangentVector = collision.getBoundaryAxis();
        PointF UNormalVector = new PointF(UTangentVector.y, -UTangentVector.x);
        //System.out.println("boundary axis length: " + collision.getBoundaryAxis().length());

        //get velocities for balls
        PointF ball1velocity = ball1.getAvailableVelocity(collision.getTime());  //if a ball collides with more than one other ball,
        PointF ball2velocity = ball2.getAvailableVelocity(collision.getTime());  //available velocity will differ from normal velocity

        //determine component velocities for ball1 / ball2 in the tangent / normal directions
        float velocity1tangent = GameState.dotProduct(ball1velocity, UTangentVector);
        float velocity1normal = GameState.dotProduct(ball1velocity, UNormalVector);
        float velocity2tangent = GameState.dotProduct(ball2velocity, UTangentVector);
        float velocity2normal = GameState.dotProduct(ball2velocity, UNormalVector);

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
        newVelocity1.set(newVelocity1.x * GameState.ELASTIC_CONSTANT, newVelocity1.y * GameState.ELASTIC_CONSTANT);
        newVelocity2.set(newVelocity2.x * GameState.ELASTIC_CONSTANT, newVelocity2.y * GameState.ELASTIC_CONSTANT);

        //set velocity
        ball1.addNewVelocity(newVelocity1);
        ball2.addNewVelocity(newVelocity2);
    }
/*
    //**Reference material:
    //http://vobarian.com/collisions/2dcollisions2.pdf
    // There is probably a better way to do this one, but reusing the same formulas
    // as the two-ball collision seemed the easiest. To do this we just pretend that
    // the obstacle is ball two (but with infinite mass), and don't worry about calculating
    // any new velocities for the obstacle.
    private void calculateVelocityMovingBorderCollision(Collision collision){

        //get balls
        Ball ball1 = collision.getBall();
        MovingObstacle obstacle = (MovingObstacle) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UTangentVector = collision.getBoundaryAxis();
        PointF UNormalVector = new PointF(UTangentVector.y, -UTangentVector.x);

        //get velocities for balls
        PointF ball1velocity = ball1.getAvailableVelocity(collision.getTime());  //if a ball collides with more than one other ball,
        PointF obstacleVelocity = obstacle.getVelocity();  //available velocity will differ from normal velocity

        System.out.println("ball velocity: " + ball1velocity);
        System.out.println("obstacle velocity: " + obstacleVelocity);
        System.out.println("UNormal vector: " + UNormalVector);
        System.out.println("UTangent vector: " + UTangentVector);

        //determine component velocities for ball1 / obstacle in the tangent / normal directions
        float velocity1tangent = GameState.dotProduct(ball1velocity, UTangentVector);
        float velocity1normal = GameState.dotProduct(ball1velocity, UNormalVector);
        float velocity2normal = GameState.dotProduct(obstacleVelocity, UNormalVector);

        //calculate new tangential velocity (it is the same, no force between objects in tangential direction)
        float newVelocity1tangent = velocity1tangent;

        //calculate new normal velocity (derived by using infinite mass for ball 2 in formula)
        //float newVelocity1normal = (2 * velocity2normal) - velocity1normal;
        float newVelocity1normal = velocity2normal;
        System.out.println("Velocity 1 normal: " + velocity1normal);
        System.out.println("Velocity 2 normal: " + velocity2normal);

        //convert scalar tangential & normal values into vectors
        PointF newVelocity1normalVector = new PointF(newVelocity1normal * UNormalVector.x, newVelocity1normal * UNormalVector.y);
        PointF newVelocity1tangentVector = new PointF(newVelocity1tangent * UTangentVector.x, newVelocity1tangent * UTangentVector.y);

        //add tangential and normal components together to get sum velocity
                PointF newVelocity1 = new PointF(newVelocity1normalVector.x + newVelocity1tangentVector.x, newVelocity1normalVector.y + newVelocity1tangentVector.y);

        //subtract for elasticity
        newVelocity1.set(newVelocity1.x * GameState.ELASTIC_CONSTANT, newVelocity1.y * GameState.ELASTIC_CONSTANT);

        System.out.println("NEW VELOCITY (new method): " + newVelocity1);

        //set velocity
        //ball1.setVelocity(newVelocity1);
    } */

    public void updateCollisionCollections(ArrayList<Collision> collisions){

        for (Collision currentCollision : collisions) {

            if (currentCollision.getObstacle().getType() == GameState.INTERACTABLE_BALL) {

                Ball currentBall = currentCollision.getBall();
                Ball otherBall = (Ball) currentCollision.getObstacle();

                currentBall.increaseBallCollisionCounts();
                otherBall.increaseBallCollisionCounts();

                mBallCollisions.add(currentCollision);

            } else {
                mBoundaryCollisions.add(currentCollision);
                currentCollision.getBall().addObstacleCollision(currentCollision);
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

/*
    //**Reference material:
    //http://vobarian.com/collisions/2dcollisions2.pdf
    private void calculateVelocityBallCollision2(Collision collision){

        //get balls
        Ball ball1 = collision.getBall();
        MovingObstacle obstacle = (MovingObstacle) collision.getObstacle();

        //get tangent vector and normal vector of the collision
        PointF UNormalVector = collision.getBoundaryAxis();
        PointF UTangentVector = new PointF(UNormalVector.y, -UNormalVector.x);
        //System.out.println("boundary axis length: " + collision.getBoundaryAxis().length());

        //get velocities for balls
        PointF ball1velocity = ball1.getAvailableVelocity(collision.getTime());  //if a ball collides with more than one other ball,
        PointF ball2velocity = obstacle.getVelocity();  //available velocity will differ from normal velocity

        System.out.println("obstacle velocity: " + ball2velocity);
        System.out.println("UNormal vector: " + UNormalVector);

        //determine component velocities for ball1 / ball2 in the tangent / normal directions
        float velocity1tangent = GameState.dotProduct(ball1velocity, UTangentVector);
        float velocity1normal = GameState.dotProduct(ball1velocity, UNormalVector);
        float velocity2tangent = GameState.dotProduct(ball2velocity, UTangentVector);
        float velocity2normal = GameState.dotProduct(ball2velocity, UNormalVector);

        System.out.println("Velocity 1 normal: " + velocity1normal);
        System.out.println("Velocity 2 normal: " + velocity2normal);

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
        newVelocity1.set(newVelocity1.x * GameState.ELASTIC_CONSTANT, newVelocity1.y * GameState.ELASTIC_CONSTANT);
        newVelocity2.set(newVelocity2.x * GameState.ELASTIC_CONSTANT, newVelocity2.y * GameState.ELASTIC_CONSTANT);

        System.out.println("NEW VELOCITY (ball ball method): " + newVelocity1);
    }
    */

    //**Reference:
    //http://gamedev.stackexchange.com/questions/23672/determine-resulting-angle-of-wall-collision
    //
    private void calculateVelocityMovingBorderCollision(Collision collision, boolean debug){
        Ball ball = collision.getBall();
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ball.getVelocity(collision.getTime());
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

        float velocityChange = 2 * GameState.dotProduct(totalCollisionVelocity, boundaryAxis);
        PointF velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        PointF newVelocity = new PointF(totalCollisionVelocity.x - velocityChangeVector.x, totalCollisionVelocity.y - velocityChangeVector.y);
        PointF newVelocityElastic = new PointF(newVelocity.x * GameState.ELASTIC_CONSTANT, newVelocity.y * GameState.ELASTIC_CONSTANT);
        System.out.println("new velocity elastic: " + newVelocityElastic);

        //Finally, we must add the obstacle directional velocity with the post-collision change
        // in velocity (newVelocityElastic), to get our ball's final velocity.
        PointF finalVelocity = new PointF(newVelocityElastic.x + obstacleDirectionalVel.x, newVelocityElastic.y + obstacleDirectionalVel.y);
        System.out.println("final velocity: " + finalVelocity);

        if (debug == false) {
            ball.setVelocity(finalVelocity);
            System.out.println("NEW VELOCITY (additive velocities method): " + finalVelocity);
        } else {
            System.out.println("NEW VELOCITY (additive velocities method): " + finalVelocity);
        }

    }

}
