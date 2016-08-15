package com.example.eli.myapplication;

/**
 * Created by Eli on 8/14/2016.
 */
public class GhostBall extends Circle {

    public GhostBall(int texturePointer){
        super(GameState.getInitialBallCoords(), texturePointer);
        setType(GameState.DRAWABLE_GHOST_CIRCLES);
        setAlpha(GameState.GHOST_ALPHA);
    }
}
