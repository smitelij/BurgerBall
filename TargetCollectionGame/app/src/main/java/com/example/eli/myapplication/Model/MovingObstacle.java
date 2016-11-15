package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 9/10/2016.
 */
public class MovingObstacle extends Obstacle implements Movable {

    private MovePath path;
    private PointF mVelocity;
    private float[] mPrevAABB = new float[4];

    public MovingObstacle(float[] borderCoords, int texturePointer, MovePath path) {
        // initialize vertex byte buffer for shape coordinates
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_MOVING_OBSTACLE);
        this.path = path;
        updatePrevAABB();
        mVelocity = path.getCurrentVelocity();

    }

    public void moveObstacle(){

        //update velocity
        mVelocity = path.getCurrentVelocity();
        update2dCoordArray();

        //transpose back to openGL coords
        float[] fullCoords = getFullCoordsFrom2dCoordArray();
        setCoords(fullCoords);

    }

    private void update2dCoordArray(){
        PointF[] coords = increment2dCoordArray(GameState.FRAME_SIZE);
        set2dCoordArray(coords);
    }

    private PointF[] increment2dCoordArray(float timeStep){
        PointF[] coords = get2dCoordArray().clone();

        for (int i = 0; i < coords.length; i++){
            PointF curCoordinate = coords[i];
            PointF newCoordinate = new PointF(curCoordinate.x + (mVelocity.x * timeStep), curCoordinate.y + (mVelocity.y * timeStep));
            coords[i]=newCoordinate;
        }

        return coords;
    }

    private void set2dCoordArray(PointF[] newCoords){
        m2dCoordArray = newCoords;
    }

    private float[] getFullCoordsFrom2dCoordArray(){
        PointF[] coords = get2dCoordArray();
        float[] fullCoords= new float[coords.length * 3];

        //'Full coords' is a single array of floats, with slots for x,y,and z.
        //For an object with 4 points, this would be float[12].
        for (int i = 0; i < fullCoords.length; i=i+3){
            int arrayIndex = i / 3;
            fullCoords[i] = coords[arrayIndex].x;
            fullCoords[i+1] = coords[arrayIndex].y;
            fullCoords[i+2] = 0f;
        }

        return fullCoords;
    }

    @Override
    public void draw(float[] mvpMatrix){
        path.incrementDuration();
        moveObstacle();
        super.draw(mvpMatrix);
        updatePrevAABB();
    }

    /////*********************

    public void moveByFrame(float percentOfFrame){
        updateAABB(percentOfFrame * mVelocity.x, percentOfFrame* mVelocity.y);
    }

    public void resetAABB(){
        mMinXCoord = mPrevAABB[0];
        mMaxXCoord = mPrevAABB[1];

        mMinYCoord = mPrevAABB[2];
        mMaxYCoord = mPrevAABB[3];
    }

    public void updatePrevAABB(){
        mPrevAABB[0] = mMinXCoord;
        mPrevAABB[1] = mMaxXCoord;
        mPrevAABB[2] = mMinYCoord;
        mPrevAABB[3] = mMaxYCoord;
    }

    //Get a balls velocity after timeStep (calculates gravity)
    public PointF getVelocity(){
        return mVelocity;
    }

    @Override
    public PointF[] getTemporaryCoords(float timeStep){
        return increment2dCoordArray(timeStep).clone();
    }

}
