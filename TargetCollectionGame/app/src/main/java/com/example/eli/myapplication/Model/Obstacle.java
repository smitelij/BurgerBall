/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.eli.myapplication.Model;

import android.graphics.PointF;

import com.example.eli.myapplication.Resources.GameState;

import java.util.ArrayList;

/**
 * A two-dimensional border
 */
public class Obstacle extends Interactable {

    ArrayList<PointF> boundaryAxisCollection = new ArrayList<>();
    boolean isBottomBoundary = false;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Obstacle(float[] borderCoords, int texturePointer) {
        // initialize vertex byte buffer for shape coordinates
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_OBSTACLE);
        calculateBoundaryAxes();

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        super.draw(mvpMatrix);
    }

    private void calculateBoundaryAxes() {
        PointF[] obstacleCoords = get2dCoordArray();

        for (int index = 0; index < obstacleCoords.length; index++) {
            PointF normalAxis = makeNormalVectorBetweenPoints(obstacleCoords, index);
            boundaryAxisCollection.add(normalAxis);
        }
    }

    private PointF makeNormalVectorBetweenPoints(PointF[] obstacleCoords, int index) {
        //We need to make a line between two vertexes
        int vertexA = index;
        int vertexB = (index + 1) % obstacleCoords.length; //We need to wrap back to the first vertex at the end, so use modulus

        //formula to find the normal vector from a line is (-y, x)
        float xComponent = -(obstacleCoords[vertexB].y - obstacleCoords[vertexA].y);
        float yComponent = (obstacleCoords[vertexB].x - obstacleCoords[vertexA].x);

        //create vector and normalize
        PointF normalAxis = new PointF(xComponent, yComponent);
        float normalAxisLength = normalAxis.length();
        normalAxis.set(normalAxis.x / normalAxisLength, normalAxis.y / normalAxisLength);

        return normalAxis;
    }

    public PointF getBoundaryAxis(int index) {
        if (index < boundaryAxisCollection.size()) {
            return boundaryAxisCollection.get(index);
        }
        return new PointF(0f,0f);
    }

    public ArrayList<PointF> getAllBoundaryAxis() {
        return boundaryAxisCollection;
    }

    public ArrayList<PointF> getSurfaceVertices(int surfaceIndex) {
        PointF[] obstacleCoords = get2dCoordArray();

        int vertexA = surfaceIndex;
        int vertexB = (surfaceIndex + 1) % obstacleCoords.length; //We need to wrap back to the first vertex at the end, so use modulus

        ArrayList<PointF> vertices = new ArrayList<>();
        vertices.add(obstacleCoords[vertexA]);
        vertices.add(obstacleCoords[vertexB]);
        return vertices;
    }

    public void setBottomBoundary() {
        isBottomBoundary = true;
    }

    public boolean isBottomBoundary() {
        return isBottomBoundary;
    }

}