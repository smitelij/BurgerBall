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

import com.example.eli.myapplication.Model.GameState;
import com.example.eli.myapplication.Model.Interactable;

/**
 * A two-dimensional border
 */
public class Obstacle extends Interactable {


    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Obstacle(float[] borderCoords, int texturePointer) {
        // initialize vertex byte buffer for shape coordinates
        super(borderCoords, texturePointer);
        setType(GameState.INTERACTABLE_OBSTACLE);

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

}