package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.CommonFunctions;
import com.example.eli.myapplication.Resources.GameState;

/**
 * Created by Eli on 8/14/2016.
 */
public class GhostBall extends Circle {

    public GhostBall(int texturePointer, float[] startingCoords){
        super(startingCoords, texturePointer);
        setType(GameState.DRAWABLE_GHOST_CIRCLES);
        setAlpha(GameState.GHOST_ALPHA);
    }
}
