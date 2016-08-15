package com.example.eli.myapplication;

import android.graphics.PointF;

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
            calculateVelocityBorderCollision(currentCollision);
        }
    }

    public void handleBallCollisions(){
        for (Collision currentCollision : mBallCollisions){
            calculateVelocityBallCollision(currentCollision);
        }
    }

    private void calculateVelocityBorderCollision(Collision collision){
        /*
        System.out.println("Obstacle center: " + collision.getObstacle().getCenter());
        System.out.println("Boundary axis: " + collision.getBoundaryAxis());
        System.out.println("Collision time: " + collision.getTime());
        System.out.println("Ball center: " + collision.getBall().getCenter());
        System.out.println("Ball old velocity: " + collision.getBall().getVelocity().x + ";" + collision.getBall().getVelocity().y);
        */


        Ball ball = collision.getBall();
        float velocityChange;
        PointF velocityChangeVector;
        PointF newVelocity;
        PointF boundaryAxis = collision.getBoundaryAxis();
        PointF oldVelocity = ball.getVelocity(collision.getTime());

        velocityChange = 2 * GameState.dotProduct(oldVelocity, boundaryAxis);
        velocityChangeVector = new PointF(boundaryAxis.x * velocityChange, boundaryAxis.y * velocityChange);
        newVelocity = new PointF(oldVelocity.x - velocityChangeVector.x, oldVelocity.y - velocityChangeVector.y);
        newVelocity.set(newVelocity.x * GameState.ELASTIC_CONSTANT, newVelocity.y * GameState.ELASTIC_CONSTANT);
        ball.setVelocity(newVelocity);

        //System.out.println("Ball new velocity: " + newVelocity.x + ";" + newVelocity.y);

    }

    private void calculateVelocityBallCollision(Collision collision){

        //System.out.println("Ball collision " + collision.hashCode());
        //System.out.println("time: " + collision.getTime());

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
        //System.out.println("ball " + ball1.getID() + " available velocity: " + ball1velocity.x + ";" + ball1velocity.y);
        //System.out.println("ball " + ball2.getID() + " available velocity: " + ball2velocity.x + ";" + ball2velocity.y);

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
        //System.out.println("ball " + ball1.getID() + " new velocity: " + newVelocity1.x + ";" + newVelocity1.y);
        //System.out.println("ball " + ball2.getID() + " new velocity: " + newVelocity2.x + ";" + newVelocity2.y);
    }

    public void updateCollisionCollections(ArrayList<Collision> collisions){

        for (Collision currentCollision : collisions) {

            if (currentCollision.getObstacle().getType() == GameState.INTERACTABLE_BALL) {

                Ball currentBall = currentCollision.getBall();
                Ball otherBall = (Ball) currentCollision.getObstacle();

                currentBall.increaseCollisionCount();
                otherBall.increaseCollisionCount();

                mBallCollisions.add(currentCollision);

            } else {
                mBoundaryCollisions.add(currentCollision);
                currentCollision.getBall().increaseCollisionCount();
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

}
