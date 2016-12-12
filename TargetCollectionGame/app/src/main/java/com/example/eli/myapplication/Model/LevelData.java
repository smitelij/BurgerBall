package com.example.eli.myapplication.Model;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.GameState;

import java.util.ArrayList;

/**
 * Created by Eli on 6/2/2016.
 */
public class LevelData {

    //Collections of coordinates to be used later for initializing objects
    ArrayList<float[]> mObstacleCoords = new ArrayList<>();
    ArrayList<float[]> mTargetCoords = new ArrayList<>();
    ArrayList<float[]> mMovingObstacleCoords = new ArrayList<>();
    ArrayList<MovePath> mMovingObstaclePaths = new ArrayList<>();

    int mNumOfBalls;
    int levelIndex;

    public LevelData(int chapter, int level){
        switch (chapter){
            case 1:
                loadSet1Level(level);
                break;

            case 2:
                loadSet2Level(level);
                break;
        }
    }

    private void loadSet1Level(int levelIndex){
        switch (levelIndex){

            //Level 1 Data
            case 1:
                //Obstacles
                float obstacle111coords[] = {
                    80f, 150f, 0.0f,   // top left
                    80f, 100f, 0.0f,   // bottom left
                    100f, 100f, 0.0f,   // bottom right
                    100f, 150f, 0.0f }; // top right


                float obstacle112coords[] = {
                    140f,  170f, 0.0f,   // top left
                    140f, 120f, 0.0f,   // bottom left
                    160f, 120f, 0.0f,   // bottom right
                    160f, 170f, 0.0f }; // top right

                mObstacleCoords.add(obstacle111coords);
                mObstacleCoords.add(obstacle112coords);

                //Targets

                float[] target111coords = GameState.createCircleCoords(90, 160, 8f);
                float[] target112coords = GameState.createCircleCoords(150,180,8f);

                mTargetCoords.add(target111coords);
                mTargetCoords.add(target112coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            //Level 2 Data
            case 2:
                //Obstacles
                float obstacle151coords[] = {
                        0f, 120f, 0.0f,   // top left
                        0f, 50f, 0.0f,   // bottom left
                        75f, 50f, 0.0f,   // bottom right
                        30f, 120f, 0.0f }; // top right


                float obstacle152coords[] = {
                        170f, 120f, 0.0f,   // top left
                        125f, 50f, 0.0f,   // bottom left
                        200f, 50f, 0.0f,   // bottom right
                        200f, 120f, 0.0f }; // top right

                float obstacle153coords[] = {
                        87f, 220f, 0f,     // top
                        47f, 200f, 0.0f,   // top left
                        87f, 140f, 0.0f,   // bottom
                        127f, 200f, 0.0f,}; // top right

                MovePath path = new MovePath();
                path.addMovement(new SingleMovement(new PointF(0.5f,0f), 100));
                path.addMovement(new SingleMovement(new PointF(-0.5f, 0f), 100));


                mObstacleCoords.add(obstacle151coords);
                mObstacleCoords.add(obstacle152coords);
                mMovingObstacleCoords.add(obstacle153coords);
                mMovingObstaclePaths.add(path);

                //Targets

                float[] target151coords = GameState.createCircleCoords(100,235,8f);
                float[] target152coords = GameState.createCircleCoords(15,128,8f);
                float[] target153coords = GameState.createCircleCoords(185,128,8f);

                mTargetCoords.add(target151coords);
                mTargetCoords.add(target152coords);
                mTargetCoords.add(target153coords);

                //num of balls
                mNumOfBalls = 5;

                break;


            //Level 3 Data
            case 3:
                //Obstacles
                float obstacle131coords[] = {
                        50f, 140f, 0.0f,   // top left
                        30f, 120f, 0.0f,   // bottom left
                        50f, 100f, 0.0f,   // bottom right
                        70f, 120f, 0.0f }; // top right


                float obstacle132coords[] = {
                        150f, 140f, 0.0f,   // top left
                        130f, 120f, 0.0f,   // bottom left
                        150f, 100f, 0.0f,   // bottom right
                        170f, 120f, 0.0f }; // top right

                float obstacle133coords[] = {
                        100f, 230f, 0.0f,   // top left
                        80f, 210f, 0.0f,   // bottom left
                        100f, 190f, 0.0f,   // bottom right
                        120f, 210f, 0.0f }; // top right


                mObstacleCoords.add(obstacle131coords);
                mObstacleCoords.add(obstacle132coords);
                mObstacleCoords.add(obstacle133coords);

                //Targets

                float[] target131coords = GameState.createCircleCoords(20,135,8f);
                float[] target132coords = GameState.createCircleCoords(180,135,8f);

                mTargetCoords.add(target131coords);
                mTargetCoords.add(target132coords);

                //num of balls
                mNumOfBalls = 4;

                break;

            //Level 4 Data
            case 4:
                //Obstacles
                float obstacle121coords[] = {
                        20f, 180f, 0.0f,   // top left
                        20f, 120f, 0.0f,   // bottom left
                        40f, 120f, 0.0f,   // bottom right
                        40f, 180f, 0.0f }; // top right


                float obstacle122coords[] = {
                        100f,  130f, 0.0f,   // top left
                        100f, 120f, 0.0f,   // bottom left
                        200f, 120f, 0.0f,   // bottom right
                        200f, 140f, 0.0f }; // top right

                float obstacle123coords[] = {
                        20f,  270f, 0.0f,   // top left
                        20f, 210f, 0.0f,   // bottom left
                        40f, 210f, 0.0f,   // bottom right
                        40f, 270f, 0.0f }; // top right

                float obstacle124coords[] = {
                        175f,  230f, 0.0f,   // top left
                        185f, 210f, 0.0f,   // bottom left
                        195f, 210f, 0.0f,   // bottom right
                        195f, 230f, 0.0f }; // top right

                mObstacleCoords.add(obstacle121coords);
                mObstacleCoords.add(obstacle122coords);
                mObstacleCoords.add(obstacle123coords);
                mObstacleCoords.add(obstacle124coords);

                //Targets

                float[] target121coords = GameState.createCircleCoords(30,190,8f);
                float[] target122coords = GameState.createCircleCoords(185,240,8f);

                mTargetCoords.add(target121coords);
                mTargetCoords.add(target122coords);

                //num of balls
                mNumOfBalls = 3;

                break;


            //Level 5 Data
            case 5:
                //Obstacles
                float obstacle141coords[] = {
                        6f, 100f, 0.0f,   // top left
                        6f, 90f, 0.0f,   // bottom left
                        50f, 90f, 0.0f,   // bottom right
                        50f, 100f, 0.0f }; // top right


                float obstacle142coords[] = {
                        130f, 140f, 0.0f,   // top left
                        130f, 115f, 0.0f,   // bottom left
                        210f, 115f, 0.0f,   // bottom right
                        210f, 140f, 0.0f }; // top right

                float obstacle143coords[] = {
                        40f, 250f, 0.0f,   // top left
                        40f, 240f, 0.0f,   // bottom left
                        90f, 240f, 0.0f,   // bottom right
                        90f, 250f, 0.0f }; // top right

                float obstacle144coords[] = {
                        6f, 160f, 0.0f,   // top left
                        6f, 150f, 0.0f,   // bottom left
                        75f, 150f, 0.0f,   // bottom right
                        75f, 160f, 0.0f }; // top right

                float obstacle145coords[] = {
                        130f, 85f, 0.0f,   // top left
                        130f, 0f, 0.0f,   // bottom left
                        220f, 0f, 0.0f,   // bottom right
                        220f, 85f, 0.0f }; // top right




                mObstacleCoords.add(obstacle141coords);
                mObstacleCoords.add(obstacle142coords);
                mObstacleCoords.add(obstacle143coords);
                mObstacleCoords.add(obstacle144coords);
                mObstacleCoords.add(obstacle145coords);

                //Targets

                float[] target141coords = GameState.createCircleCoords(60,260,8f);
                float[] target142coords = GameState.createCircleCoords(185,100,8f);

                mTargetCoords.add(target141coords);
                mTargetCoords.add(target142coords);

                //num of balls
                mNumOfBalls = 4;

                break;

        }






    }

    private void loadSet2Level(int levelIndex) {
        switch (levelIndex) {
            case 1:
                //
                break;

            case 2:
                //
                break;
        }
    }

    public ArrayList<float[]> getObstacleCoords(){
        return mObstacleCoords;
    }

    public ArrayList<float[]> getMovingObstacleCoords() {
        return mMovingObstacleCoords;
    }

    public ArrayList<MovePath> getMovePaths() {
        return mMovingObstaclePaths;
    }

    public ArrayList<float[]> getTargetCoords(){
        return mTargetCoords;
    }

    public int getNumOfBalls(){
        return mNumOfBalls;
    }

    public int getNumOfTargets(){
        return mTargetCoords.size();
    }



}
