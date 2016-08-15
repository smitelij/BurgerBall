package com.example.eli.myapplication;

/**
 * Created by Eli on 7/25/2016.
 */

public class ScoreDigits extends Square{

    public ScoreDigits(float[] borderCoords, int texturePointer){
        super(borderCoords, texturePointer);
        setType(GameState.DRAWABLE_SCOREDIGIT);
    }

    protected void updateTexture(int newDigit){
        super.updateTexture(newDigit);
    }

}
