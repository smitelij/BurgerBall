package com.example.eli.myapplication;

/**
 * Created by Eli on 8/14/2016.
 */
public class VelocityArrow extends Circle{
    public VelocityArrow(){
        super(GameState.getInitialBallCoords(), GameEngine.loadGLTexture(GameState.TEXTURE_SELECTION_ARROW));
        setType(GameState.DRAWABLE_VELOCITY_ARROW);
        setAlpha(GameState.GHOST_ALPHA);
    }
}
