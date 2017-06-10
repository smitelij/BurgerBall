package com.example.eli.myapplication.Model;

import android.graphics.PointF;

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

    private PointF velocity = new PointF(0f,0f);

    private int remainingLife;
    private int totalLife;

    public Particle(float[] coords, PointF velocity, float[] color, int life) {
        setPosition(coords);
        this.velocity = velocity;

        totalLife = life;
        remainingLife = life;
        this.color = color;
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

    public void setColorAlpha(float alphaValue) {
        color[3] = alphaValue;
    }

    public void decreaseLife() {
        remainingLife--;
    }

    public int getRemainingLife() {
        return remainingLife;
    }

    public float getPercentLife() { return remainingLife / (float) totalLife; }

    public PointF getVelocity() {
        return velocity;
    }

    public void setVelocity(PointF newVelocity) {
        velocity = newVelocity;
    }

}
