package com.example.eli.myapplication.Logic.Ball;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.Ball;
import com.example.eli.myapplication.Resources.CommonFunctions;

import java.util.ArrayList;

/**
 * Created by Eli on 2/4/2017.
 */

public class ActivateBallLogic {

    //-----------------
    //This function does checks to see if we can currently activate a ball.
    //PARAMS:
    //  newBall- Ball object, used to get the current ball radius
    //RETURNS:
    // True if we can activate the ball, false if we can't.
    //
    public static boolean canActivateBall(ArrayList<Ball> allBalls, float[] startingCoords){
        //Checks before we can activate a ball:
        //1: The firing zone must be clear

        return (isFiringZoneClear(allBalls, startingCoords));
    }

    //-------------------------
    //This function checks if any other balls are currently in the firing zone.
    //By firing zone, we just mean the starting location of the new ball.
    //Used for a check in canActivateBall.
    private static boolean isFiringZoneClear(ArrayList<Ball> allBalls, float[] startingCoords){

        PointF firingZoneCenter = CommonFunctions.getFiringZoneCenter(startingCoords);

        for (Ball currentBall : allBalls){

            if (!currentBall.isBallMoving()){
                continue;
            }

            //If a ball is in the firing zone, it is NOT clear
            if (isBallInFiringZone(firingZoneCenter, currentBall, startingCoords)) {
                return false;
            }

        }

        //if we made it through all balls, then the firing zone is clear.
        return true;
    }

    public static boolean isBallInFiringZone(PointF firingZoneCenter, Ball ball, float[] startingCoords) {
        if (firingZoneCenter == null) {
            firingZoneCenter = CommonFunctions.getFiringZoneCenter(startingCoords);
        }

        PointF currentBallCenter = ball.getCenter();

        PointF distanceVector = new PointF(firingZoneCenter.x - currentBallCenter.x, firingZoneCenter.y - currentBallCenter.y);
        float distance = distanceVector.length();

        //If the distance between them is less than the diameter, then this ball is in the firing zone.
        if (distance < (ball.getRadius() * 2)){
            return true;
        }

        return false;
    }

}
