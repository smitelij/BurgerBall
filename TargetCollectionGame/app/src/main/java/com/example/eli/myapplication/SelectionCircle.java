package com.example.eli.myapplication;

/**
 * Created by Eli on 8/14/2016.
 */
public class SelectionCircle extends Circle {

    public SelectionCircle(int texture){
        super(GameState.getSelectionCircleCoords(), texture);
        setType(GameState.DRAWABLE_GHOST_CIRCLES);
        setAlpha(GameState.GHOST_ALPHA);
    }
}

