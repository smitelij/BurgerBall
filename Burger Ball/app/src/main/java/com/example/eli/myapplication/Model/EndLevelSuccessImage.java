package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 8/14/2016.
 */

public class EndLevelSuccessImage extends Square {

    private float[] coordMultipliers = new float[4];
    private float[] currentColor = new float[]{1f,1f,1f,1f};

    public EndLevelSuccessImage(int texturePointer) {
        super(CommonFunctions.getEndLevelSuccessImageCoords(0, null), texturePointer);
        coordMultipliers = calculateRandomWaver();
        setType(GameState.DRAWABLE_POST_LEVEL_IMAGE);
    }

    public void updateImage(int frame) {
        float[] coords = CommonFunctions.getEndLevelSuccessImageCoords(frame, coordMultipliers);
        setCoords(coords);
        updateColor();
        setColor(currentColor);
    }

    private float[] calculateRandomWaver() {
        float[] multipliers = new float[4];
        multipliers[0] = (float) ((Math.random() - 0.5) / 800);
        multipliers[1] = (float) ((Math.random() - 0.5) / 800);
        multipliers[2] = (float) ((Math.random() - 0.5) / 800);
        multipliers[3] = (float) ((Math.random() - 0.5) / 800);

        return multipliers;
    }

    private void updateColor() {
        currentColor[0] = (currentColor[0] + (coordMultipliers[0] * 2));
        currentColor[1] = (currentColor[1] + (coordMultipliers[1] * 2));
        currentColor[2] = (currentColor[2] + (coordMultipliers[2] * 2));
        currentColor[3] = (currentColor[3] + (coordMultipliers[3] * 2));
    }
}
