package com.example.eli.myapplication;

/**
 * Created by Eli on 7/25/2016.
 */

public class ScoreDigits extends Square{

    private final int[] mDigitTextures;

    public ScoreDigits(float[] borderCoords, int texturePointer, int[] digitTextures){
        super(borderCoords, texturePointer);
        mDigitTextures = digitTextures;
        setType(GameState.DRAWABLE_SCOREDIGIT);
    }

    protected void updateTexture(int newDigit){
        int textureToUse;

        if ((newDigit < 0) || (newDigit > 9)){
            textureToUse = mDigitTextures[9];
        } else {
            textureToUse = mDigitTextures[newDigit];
        }

        super.updateTexture(textureToUse);

    }

}
