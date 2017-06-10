package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 8/14/2016.
 */

public class FinalScoreText extends Square {

    private float[] randomMultiplier = new float[4];
    private float[] currentColor = new float[]{1f,1f,1f,1f};

    public FinalScoreText(int texturePointer) {
        super(CommonFunctions.getFinalScoreTextCoords(0, null), texturePointer);
        randomMultiplier = calculateRandomWaver();
        setType(GameState.DRAWABLE_POST_LEVEL_IMAGE);
    }

    public void updateImage(int frame) {
        float[] coords = CommonFunctions.getFinalScoreTextCoords(frame, randomMultiplier);
        setCoords(coords);
        updateColor();
        setColor(currentColor);
    }

    private float[] calculateRandomWaver() {
        float[] multipliers = new float[4];
        multipliers[0] = (float) ((Math.random() - 0.5) / 2400);
        multipliers[1] = (float) ((Math.random() - 0.5) / 2400);
        multipliers[2] = (float) ((Math.random() - 0.5) / 2400);
        multipliers[3] = (float) ((Math.random() - 0.5) / 2400);

        return multipliers;
    }

    private void updateColor() {
        currentColor[0] = (currentColor[0] + (randomMultiplier[0] * 4));
        currentColor[1] = (currentColor[1] + (randomMultiplier[1] * 4));
        currentColor[2] = (currentColor[2] + (randomMultiplier[2] * 4));
        currentColor[3] = (currentColor[3] + (randomMultiplier[3] * 4));
    }

    public float[] getRandomMultiplier() {
        return randomMultiplier;
    }
}
