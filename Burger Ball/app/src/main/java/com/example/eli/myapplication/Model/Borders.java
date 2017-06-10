package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Resources.GameState;

import java.util.ArrayList;

/**
 * Created by Eli on 3/4/2016.
 */

public class Borders {

    public ArrayList<Obstacle> allBorders = new ArrayList<>();

    private static float borderLeftCoords[] = {
            0.0f - (GameState.BORDER_WIDTH * 2),  GameState.FULL_HEIGHT, 0.0f,   // top left
            0.0f - (GameState.BORDER_WIDTH * 2), 0.0f, 0.0f,   // bottom left
            GameState.BORDER_WIDTH, 0.0f, 0.0f,   // bottom right
            GameState.BORDER_WIDTH, GameState.FULL_HEIGHT, 0.0f }; // top right

    private static float borderRightCoords[] = {
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH,  GameState.FULL_HEIGHT, 0.0f,   // top left
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH,  0.0f, 0.0f,   // bottom left
            GameState.FULL_WIDTH + (GameState.BORDER_WIDTH * 2),  0.0f, 0.0f,   // bottom right
            GameState.FULL_WIDTH + (GameState.BORDER_WIDTH * 2),  GameState.FULL_HEIGHT, 0.0f }; // top right

    private static float borderTopCoords[] = {
            0.0f,  GameState.FULL_HEIGHT + (GameState.BORDER_WIDTH * 3), 0.0f,   // top left
            0.0f, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,   // bottom left
            GameState.FULL_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,   // bottom right
            GameState.FULL_WIDTH, GameState.FULL_HEIGHT + (GameState.BORDER_WIDTH * 3), 0.0f }; // top right

    private static float borderBottomCoords[] = {
            0.0f,  2 * GameState.BORDER_WIDTH, 0.0f,   // top left
            0.0f, 0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom left
            GameState.FULL_WIDTH, 0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom right
            GameState.FULL_WIDTH, GameState.BORDER_WIDTH * 2, 0.0f }; // top right


    public Borders(int texturePointer){

        allBorders.add(new Obstacle(borderLeftCoords, texturePointer));
        allBorders.add(new Obstacle(borderRightCoords, texturePointer));
        allBorders.add(new Obstacle(borderTopCoords, texturePointer));
        Obstacle borderBottom = new Obstacle(borderBottomCoords, texturePointer);
        borderBottom.setBottomBoundary();
        allBorders.add(borderBottom);

    }

    public ArrayList<Obstacle> getAllBorders(){
        return allBorders;
    }
}
