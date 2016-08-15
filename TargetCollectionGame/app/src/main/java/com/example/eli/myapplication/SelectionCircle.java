package com.example.eli.myapplication;

/**
 * Created by Eli on 8/14/2016.
 */
public class SelectionCircle extends Circle {

    public SelectionCircle(){
        super(GameState.getSelectionCircleCoords(), GameEngine.loadGLTexture(GameState.TEXTURE_SELECTION_CIRCLE));
        setType(GameState.DRAWABLE_GHOST_CIRCLES);
        setAlpha(GameState.GHOST_ALPHA);
    }
}

