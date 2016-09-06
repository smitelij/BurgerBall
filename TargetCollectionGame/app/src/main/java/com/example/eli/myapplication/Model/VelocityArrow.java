package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Model.Circle;
import com.example.eli.myapplication.Model.GameState;

/**
 * Created by Eli on 8/14/2016.
 */
public class VelocityArrow extends Circle {
    public VelocityArrow(int texture){
        super(GameState.getInitialBallCoords(), texture);
        setType(GameState.DRAWABLE_VELOCITY_ARROW);
        setAlpha(GameState.GHOST_ALPHA);
    }
}
