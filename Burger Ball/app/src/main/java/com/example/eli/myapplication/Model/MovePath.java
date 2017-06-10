package com.example.eli.myapplication.Model;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by Eli on 9/10/2016.
 */
public class MovePath {

    //This collection of single movements determines how the path proceeds
    ArrayList<SingleMovement> path = new ArrayList();

    //These counters keep track of where we currently are on the path
    int currentIndex; //Index determines which single movement we are executing
    int currentDuration;  //Duration determines how far on the single movement we are

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

    public boolean isAtBeginning() {
        return ((currentDuration == 0) && (currentIndex == 0));
    }

    public String getStatus() {
        String status = "Duration: " + currentDuration + " / " + path.get(currentIndex).getDuration();
        status = status + "| Index: " + currentIndex + " / " + path.size();
        return status;
    }
}
