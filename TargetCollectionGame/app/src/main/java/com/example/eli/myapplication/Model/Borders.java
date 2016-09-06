package com.example.eli.myapplication.Model;

import java.util.ArrayList;

/**
 * Created by Eli on 3/4/2016.
 */

public class Borders {

    private Obstacle mBorderLeft;
    private Obstacle mBorderRight;
    private Obstacle mBorderTop;
    private Obstacle mBorderBottomRight;
    private Obstacle mBorderBottomLeft;
    private Obstacle mBackground;

    //private Obstacle testP;
   // private Obstacle testP2;

    public ArrayList<Obstacle> allBorders = new ArrayList<>();


    private static float backgroundCoords[] = {
            GameState.BORDER_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,  //top left
            GameState.BORDER_WIDTH, 0.0f, 0.0f,  //bottom left
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH, 0.0f, 0.0f,  //bottom right
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f};  //top right

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

    private static float borderBottomRightCoords[] = {
            (GameState.FULL_WIDTH / 2),  GameState.BORDER_WIDTH * 2, 0.0f,   // top left
            (GameState.FULL_WIDTH / 2),  0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom left
            GameState.FULL_WIDTH, 0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom right
            GameState.FULL_WIDTH,  2 * GameState.BORDER_WIDTH , 0.0f }; // top right

    private static float borderBottomLeftCoords[] = {
            0.0f,  2 * GameState.BORDER_WIDTH, 0.0f,   // top left
            0.0f, 0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom left
            (GameState.FULL_WIDTH / 2), 0.0f - (GameState.BORDER_WIDTH * 2), 0.0f,   // bottom right
            (GameState.FULL_WIDTH / 2), GameState.BORDER_WIDTH * 2, 0.0f }; // top right

    /*
    private static float testCoords[] = {
            160f, 240f, 0f, //top left  (x,y)
            120f, 190f, 0f,  // bottom left
            180f, 220f, 0f,  //bottom right
            180f, 240f, 0f  //top right
    };

    private static float testCoords2[] = {
            60f, 240f, 0f, //top left  (x,y)
            60f, 220f, 0f,  // bottom left
            110f, 205f, 0f,  //bottom right
            100f, 240f, 0f  //top right
    };*/

    public Borders(int texturePointer){


        //mBackground = new Obstacle(backgroundCoords, GameEngine.INTERACTABLE_OBSTACLE);
       // mBackground.setColor(GameEngine.backgroundColor);

        mBorderLeft = new Obstacle(borderLeftCoords, texturePointer);
        //mBorderLeft.setColor(GameState.borderColor);

        mBorderRight = new Obstacle(borderRightCoords, texturePointer);
        //mBorderRight.setColor(GameState.borderColor);

        mBorderTop = new Obstacle(borderTopCoords, texturePointer);
        //mBorderTop.setColor(GameState.borderColor);

        mBorderBottomRight = new Obstacle(borderBottomRightCoords, texturePointer);
        //mBorderBottomRight.setColor(GameState.borderColor);

        mBorderBottomLeft = new Obstacle(borderBottomLeftCoords, texturePointer);
        //mBorderBottomLeft.setColor(GameState.borderColor);

        //testP = new Obstacle(testCoords, GameState.INTERACTABLE_OBSTACLE, texturePointer);
        //testP2 = new Obstacle(testCoords2, GameState.INTERACTABLE_OBSTACLE, texturePointer);

        allBorders.add(mBorderLeft);
        allBorders.add(mBorderRight);
        allBorders.add(mBorderTop);
        allBorders.add(mBorderBottomRight);
        allBorders.add(mBorderBottomLeft);
        //allBorders.add(testP);
        //allBorders.add(testP2);

    }

    public void drawAllBorders(float[] mProjectionMatrix){

        for (Obstacle border : allBorders){
            border.draw(mProjectionMatrix);
        }
    }

    public ArrayList<Obstacle> getAllBorders(){
        return allBorders;
    }
}
