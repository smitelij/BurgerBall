package com.example.eli.myapplication;

import java.util.ArrayList;

/**
 * Created by Eli on 3/4/2016.
 */

public class Borders {

    private Polygon mBorderLeft;
    private Polygon mBorderRight;
    private Polygon mBorderTop;
    private Polygon mBorderBottomRight;
    private Polygon mBorderBottomLeft;
    private Polygon mBackground;

    private Polygon testP;

    public ArrayList<Polygon> allBorders = new ArrayList<>();


    private static float backgroundCoords[] = {
            GameState.BORDER_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,  //top left
            GameState.BORDER_WIDTH, 0.0f, 0.0f,  //bottom left
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH, 0.0f, 0.0f,  //bottom right
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f};  //top right

    private static float borderLeftCoords[] = {
            0.0f,  GameState.FULL_HEIGHT, 0.0f,   // top left
            0.0f, 0.0f, 0.0f,   // bottom left
            GameState.BORDER_WIDTH, 0.0f, 0.0f,   // bottom right
            GameState.BORDER_WIDTH, GameState.FULL_HEIGHT, 0.0f }; // top right

    private static float borderRightCoords[] = {
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH,  GameState.FULL_HEIGHT, 0.0f,   // top left
            GameState.FULL_WIDTH - GameState.BORDER_WIDTH,  0.0f, 0.0f,   // bottom left
            GameState.FULL_WIDTH,  0.0f, 0.0f,   // bottom right
            GameState.FULL_WIDTH,  GameState.FULL_HEIGHT, 0.0f }; // top right

    private static float borderTopCoords[] = {
            0.0f,  GameState.FULL_HEIGHT, 0.0f,   // top left
            0.0f, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,   // bottom left
            GameState.FULL_WIDTH, GameState.FULL_HEIGHT - GameState.BORDER_WIDTH, 0.0f,   // bottom right
            GameState.FULL_WIDTH, GameState.FULL_HEIGHT, 0.0f }; // top right

    private static float borderBottomRightCoords[] = {
            (GameState.FULL_WIDTH / 2) + GameState.BORDER_WIDTH,  GameState.BORDER_WIDTH, 0.0f,   // top left
            (GameState.FULL_WIDTH / 2) + GameState.BORDER_WIDTH,  0.0f, 0.0f,   // bottom left
            GameState.FULL_WIDTH, 0.0f, 0.0f,   // bottom right
            GameState.FULL_WIDTH,  2 * GameState.BORDER_WIDTH , 0.0f }; // top right

    private static float borderBottomLeftCoords[] = {
            0.0f,  2 * GameState.BORDER_WIDTH, 0.0f,   // top left
            0.0f, 0.0f, 0.0f,   // bottom left
            (GameState.FULL_WIDTH / 2) - GameState.BORDER_WIDTH, 0.0f, 0.0f,   // bottom right
            (GameState.FULL_WIDTH / 2) - GameState.BORDER_WIDTH, GameState.BORDER_WIDTH, 0.0f }; // top right

    private static float testCoords[] = {
            160f, 240f, 0f, //top left  (x,y)
            110f, 180f, 0f,  // bottom left
            180f, 220f, 0f,  //bottom right
            180f, 240f, 0f  //top right
    };

    public Borders(){

        mBackground = new Polygon(backgroundCoords, GameState.OBSTACLE_POLYGON);
        mBackground.setColor(GameState.backgroundColor);

        mBorderLeft = new Polygon(borderLeftCoords, GameState.OBSTACLE_POLYGON);
        mBorderLeft.setColor(GameState.borderColor);

        mBorderRight = new Polygon(borderRightCoords, GameState.OBSTACLE_POLYGON);
        mBorderRight.setColor(GameState.borderColor);

        mBorderTop = new Polygon(borderTopCoords, GameState.OBSTACLE_POLYGON);
        mBorderTop.setColor(GameState.borderColor);

        mBorderBottomRight = new Polygon(borderBottomRightCoords, GameState.OBSTACLE_POLYGON);
        mBorderBottomRight.setColor(GameState.borderColor);

        mBorderBottomLeft = new Polygon(borderBottomLeftCoords, GameState.OBSTACLE_POLYGON);
        mBorderBottomLeft.setColor(GameState.borderColor);

        testP = new Polygon(testCoords, GameState.OBSTACLE_POLYGON);
        testP.setColor(GameState.borderColor);

        allBorders.add(mBorderLeft);
        allBorders.add(mBorderRight);
        allBorders.add(mBorderTop);
        allBorders.add(mBorderBottomRight);
        allBorders.add(mBorderBottomLeft);

        allBorders.add(testP);

    }

    public void drawAllBorders(float[] mProjectionMatrix){

        mBackground.draw(mProjectionMatrix);
        mBorderLeft.draw(mProjectionMatrix);
        mBorderRight.draw(mProjectionMatrix);
        mBorderTop.draw(mProjectionMatrix);
        mBorderBottomRight.draw(mProjectionMatrix);
        mBorderBottomLeft.draw(mProjectionMatrix);

        testP.draw(mProjectionMatrix);
    }
}
