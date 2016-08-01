package com.example.eli.myapplication;

import android.graphics.PointF;

/**
 * Created by Eli on 7/24/2016.
*/
public class Circle extends Drawable{

    public float[] mModelMatrix = new float[16];
    private float mRadius;
    float[] mBorderCoords; // top right

    public Circle(float[] borderCoords, int texturePointer) {
        super(borderCoords,texturePointer);
    }


    public void setCoords(float[] newCoords){
        mBorderCoords = newCoords;
        super.setCoords(newCoords);
    }




}
