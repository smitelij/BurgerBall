package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 8/14/2016.
 */

public class EndLevelFailImage extends Square {

    private float[] currentColor = new float[]{1f,1f,1f,1f};

    public EndLevelFailImage(int texturePointer) {
        super(CommonFunctions.getEndLevelFailImageCoords(), texturePointer);
        setType(GameState.DRAWABLE_POST_LEVEL_IMAGE);
    }

    public void updateImage() {
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
        currentColor[0] = (float) (Math.random());
        currentColor[1] = (float) (Math.random());
        currentColor[2] = (float) (Math.random());
        currentColor[3] = (float) (Math.random());
    }
}
