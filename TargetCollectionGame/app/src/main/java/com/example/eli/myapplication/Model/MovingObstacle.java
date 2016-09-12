package com.example.eli.myapplication.Model;

import android.graphics.PointF;

/**
 * Created by Eli on 9/10/2016.
 */
public class MovingObstacle extends Obstacle {

    MovePath path;

    public MovingObstacle(float[] borderCoords, int texturePointer, MovePath path) {
        // initialize vertex byte buffer for shape coordinates
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_OBSTACLE);
        this.path = path;

    }

    public void moveObstacle(){

        PointF velocity = path.getCurrentVelocity();

        update2dCoordArray(velocity);

        float[] fullCoords = getFullCoordsFrom2dCoordArray();

        setCoords(fullCoords);

    }

    private void update2dCoordArray(PointF velocity){
        PointF[] coords = get2dCoordArray();

        for (int i = 0; i < coords.length; i++){
            PointF curCoordinate = coords[i];
            PointF newCoordinate = new PointF(curCoordinate.x + velocity.x, curCoordinate.y + velocity.y);
            coords[i]=newCoordinate;
        }

        set2dCoordArray(coords);
    }

    private void set2dCoordArray(PointF[] newCoords){
        m2dCoordArray = newCoords;
    }

    private float[] getFullCoordsFrom2dCoordArray(){
        PointF[] coords = get2dCoordArray();
        float[] fullCoords= new float[coords.length * 3];

        for (int i = 0; i < coords.length; i=i+3){
            int arrayIndex = i /3;
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
    }
}
