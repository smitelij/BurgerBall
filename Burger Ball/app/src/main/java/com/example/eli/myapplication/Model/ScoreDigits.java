package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 7/25/2016.
 */

public class ScoreDigits extends Square {

    private float[] randomMultiplier = new float[4];
    private float[] currentColor = new float[]{1f,1f,1f,1f};
    private final int[] mDigitTextures;

    public ScoreDigits(float[] borderCoords, int texturePointer, int[] digitTextures){
        super(borderCoords, texturePointer);
        mDigitTextures = digitTextures;
        setType(GameState.DRAWABLE_SCOREDIGIT);
    }

    public void updateTexture(int newDigit){
        int textureToUse;

        if ((newDigit < 0) || (newDigit > 9)){
            textureToUse = mDigitTextures[9];
        } else {
            textureToUse = mDigitTextures[newDigit];
        }

        super.updateTexture(textureToUse);

    }

    public void setRandomMultiplier(float[] finalScoreTextMultiplier) {
        //Mirror the indices for a mirror effect with FinalScoreText

        randomMultiplier[0] = finalScoreTextMultiplier[0] * 0.5f;
        randomMultiplier[1] = finalScoreTextMultiplier[1] * 0.5f;
        randomMultiplier[2] = finalScoreTextMultiplier[2] * 0.5f;
        randomMultiplier[3] = finalScoreTextMultiplier[3] * 0.5f;
    }

    private void updateColor() {
        currentColor[0] = (currentColor[0] + (randomMultiplier[0] * 2));
        currentColor[1] = (currentColor[1] + (randomMultiplier[1] * 2));
        currentColor[2] = (currentColor[2] + (randomMultiplier[2] * 2));
        currentColor[3] = (currentColor[3] + (randomMultiplier[3] * 2));
    }

    public void updateImage(int frame, int digitIndex) {
        float[] coords = CommonFunctions.getFinalScoreDigitCoords(digitIndex, frame, randomMultiplier);
        setCoords(coords);
        updateColor();
        setColor(currentColor);
    }

}
