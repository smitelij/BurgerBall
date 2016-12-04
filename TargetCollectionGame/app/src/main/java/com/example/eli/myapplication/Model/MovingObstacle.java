package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 9/10/2016.
 */
public class MovingObstacle extends Obstacle implements Movable {

    private MovePath path;
    private PointF mVelocity;
    private float[] mPrevAABB = new float[4];
    private PointF[] tempCoords;
    private PointF[] baseCoords;

    public MovingObstacle(float[] borderCoords, int texturePointer, MovePath path) {
        // initialize vertex byte buffer for shape coordinates
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_MOVING_OBSTACLE);
        this.path = path;
        updatePrevAABB();
        resetTempCoords();
        baseCoords = get2dCoordArray().clone();
        mVelocity = path.getCurrentVelocity();

    }

    public void moveObstacle(){
        // increment path counter
        path.incrementDuration();
        mVelocity = path.getCurrentVelocity();

        //Every full cycle reset to original coords to prevent 'slippage'
        if (path.isAtBeginning()) {
            set2dCoordArray(baseCoords);
            setupAABB();
        }

        //transpose back to openGL coords
        float[] fullCoords = getFullCoordsFrom2dCoordArray();
        setCoords(fullCoords);

    }

    private void update2dCoordArray(float timeStep){

        //Update actual coords
        PointF[] coords = get2dCoordArray();
        coords = increment2dCoordArray(coords, timeStep);
        set2dCoordArray(coords);

        //Update temp coords
        setTempCoordArray(coords.clone());
    }

    private PointF[] increment2dCoordArray(PointF[] coords, float timeStep){

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

    private void setTempCoordArray(PointF[] newCoords) { tempCoords = newCoords; }

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

        moveObstacle();
        super.draw(mvpMatrix);
        updatePrevAABB();
    }

    /////*********************

    public void moveByFrame(float percentOfFrame){
        updateAABB(percentOfFrame * mVelocity.x, percentOfFrame* mVelocity.y);
        update2dCoordArray(percentOfFrame);
    }

    public void moveTempCoordsByFrame(float percentOfFrame) {
        updateAABB(percentOfFrame * mVelocity.x, percentOfFrame* mVelocity.y);
        incrementTempCoords(percentOfFrame);
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


    public void incrementTempCoords(float timeStep){
        tempCoords = getTempCoords();
        tempCoords = increment2dCoordArray(tempCoords, timeStep);
    }

    public PointF[] getTempCoords() {
        return tempCoords;
    }

    public void resetTempCoords() {
        tempCoords = get2dCoordArray().clone();
    }

}
