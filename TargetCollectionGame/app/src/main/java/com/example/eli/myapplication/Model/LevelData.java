package com.example.eli.myapplication.Model;

import com.example.eli.myapplication.Model.GameState;

import java.util.ArrayList;

/**
 * Created by Eli on 6/2/2016.
 */
public class LevelData {

    //Collections of coordinates to be used later for initializing objects
    ArrayList<float[]> mObstacleCoords = new ArrayList<>();
    ArrayList<float[]> mTargetCoords = new ArrayList<>();

    int mNumOfBalls;
    int levelIndex;

    public LevelData(int index){
        levelIndex = index;
        loadLevel();
    }

    private void loadLevel(){
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
                        80f, 200f, 0.0f,   // top left
                        40f, 130f, 0.0f,   // bottom left
                        55f, 130f, 0.0f,   // bottom right
                        95f, 200f, 0.0f }; // top right


                float obstacle152coords[] = {
                        180f, 145f, 0.0f,   // top left
                        140f, 65f, 0.0f,   // bottom left
                        155f, 65f, 0.0f,   // bottom right
                        195f, 145f, 0.0f }; // top right

                float obstacle153coords[] = {
                        155f, 240f, 0.0f,   // top left
                        155f, 215f, 0.0f,   // bottom left
                        195f, 215f, 0.0f,   // bottom right
                        195f, 240f, 0.0f }; // top right

                float obstacle154coords[] = {
                        185f, 12f, 0.0f,   // left
                        195f, 2f, 0.0f,   // bottom
                        205f, 12f, 0.0f, // right
                        195f, 22f, 0.0f }; //top




                mObstacleCoords.add(obstacle151coords);
                mObstacleCoords.add(obstacle152coords);
                mObstacleCoords.add(obstacle153coords);
                mObstacleCoords.add(obstacle154coords);
                //mObstacleCoords.add(obstacle155coords);

                //Targets

                float[] target151coords = GameState.createCircleCoords(55,255,8f);
                float[] target152coords = GameState.createCircleCoords(185,100,8f);
                float[] target153coords = GameState.createCircleCoords(185,275,8f);

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

    public ArrayList<float[]> getObstacleCoords(){
        return mObstacleCoords;
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
