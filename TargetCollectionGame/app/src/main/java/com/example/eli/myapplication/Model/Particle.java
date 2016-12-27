package com.example.eli.myapplication.Model;

/**
 * Created by Eli on 12/17/2016.
 */

public class Particle {

    private float[] positionCoords = {
            0f,  0f, 0f,   // top left
            0f, 0f, 0f,   // bottom left
            0f, 0f, 0f,   // bottom right
    };

    private float[] color = {
            1f, 1f, 1f, 1f
    };

    private int remainingLife;

    public Particle(float[] coords) {
        setPosition(coords);
        remainingLife = 40;
    }

    public float[] getPosition() {
        return positionCoords;
    }

    public float[] getColor() {
        return color;
    }

    public void setPosition(float[] newPosition) {
        positionCoords = newPosition;
    }

    public void setColor(float[] newColor) {
        color = newColor;
    }

    public void decreaseLife() {
        remainingLife--;
    }

    public int getRemainingLife() {
        return remainingLife;
    }

}
