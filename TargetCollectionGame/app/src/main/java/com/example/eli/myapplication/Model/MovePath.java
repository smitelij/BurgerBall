package com.example.eli.myapplication.Model;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by Eli on 9/10/2016.
 */
public class MovePath {

    ArrayList<SingleMovement> path = new ArrayList();
    int currentIndex;
    int currentDuration;

    public MovePath(){
        currentIndex=0;
        currentDuration = 0;
    }

    public void addMovement(SingleMovement singleMovement){
        path.add(singleMovement);
    }

    public ArrayList<SingleMovement> getPath(){
        return path;
    }

    public void incrementDuration(){
        int currentMovementDuration = path.get(currentIndex).getDuration();

        if (currentDuration < currentMovementDuration){
            currentDuration++;
        } else if (currentDuration >= currentMovementDuration) {
            currentDuration = 0;
            currentIndex = ((currentIndex + 1) % path.size());
        }
    }

    public PointF getCurrentVelocity(){
        return path.get(currentIndex).getVelocity();
    }
}
