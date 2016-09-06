package com.example.eli.myapplication.Model;

/**
 * Created by Eli on 8/14/2016.
 */
public class BallsRemainingIcon extends Circle {

    private int mIndex;

    public BallsRemainingIcon(float[] coords, int currentTexture, int index){
        super(coords, currentTexture);
        setType(GameState.DRAWABLE_BALLS_REMAINING);
        mIndex = index;
    }

    public int getIndex(){
        return mIndex;
    }
}
