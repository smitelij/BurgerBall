package com.example.eli.myapplication.Resources;

import android.graphics.PointF;

import com.example.eli.myapplication.Model.MovePath;
import com.example.eli.myapplication.Model.SingleMovement;

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

                float[] target111coords = CommonFunctions.createCircleCoords(90, 160, 8f);
                float[] target112coords = CommonFunctions.createCircleCoords(150,180,8f);

                mTargetCoords.add(target111coords);
                mTargetCoords.add(target112coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            //Level 2 Data
            case 2:
                //Obstacles
                float obstacle121coords[] = {
                        50f, 140f, 0.0f,   // top left
                        30f, 120f, 0.0f,   // bottom left
                        50f, 100f, 0.0f,   // bottom right
                        70f, 120f, 0.0f }; // top right


                float obstacle122coords[] = {
                        150f, 140f, 0.0f,   // top left
                        130f, 120f, 0.0f,   // bottom left
                        150f, 100f, 0.0f,   // bottom right
                        170f, 120f, 0.0f }; // top right

                float obstacle123coords[] = {
                        100f, 230f, 0.0f,   // top left
                        80f, 210f, 0.0f,   // bottom left
                        100f, 190f, 0.0f,   // bottom right
                        120f, 210f, 0.0f }; // top right


                mObstacleCoords.add(obstacle121coords);
                mObstacleCoords.add(obstacle122coords);
                mObstacleCoords.add(obstacle123coords);

                //Targets

                float[] target121coords = CommonFunctions.createCircleCoords(20,135,8f);
                float[] target122coords = CommonFunctions.createCircleCoords(180,135,8f);

                mTargetCoords.add(target121coords);
                mTargetCoords.add(target122coords);
                //mTargetCoords.add(target133coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            //Level 3 Data
            case 3:
                //Obstacles
                float obstacle131coords[] = {
                        0f, 120f, 0.0f,   // top left
                        0f, 50f, 0.0f,   // bottom left
                        75f, 50f, 0.0f,   // bottom right
                        30f, 120f, 0.0f }; // top right


                float obstacle132coords[] = {
                        170f, 120f, 0.0f,   // top left
                        125f, 50f, 0.0f,   // bottom left
                        200f, 50f, 0.0f,   // bottom right
                        200f, 120f, 0.0f }; // top right

                float obstacle133coords[] = {
                        87f, 220f, 0f,     // top
                        47f, 200f, 0.0f,   // top left
                        87f, 140f, 0.0f,   // bottom
                        127f, 200f, 0.0f,}; // top right

                MovePath path = new MovePath();
                path.addMovement(new SingleMovement(new PointF(0.5f,0f), 100));
                path.addMovement(new SingleMovement(new PointF(-0.5f, 0f), 100));


                mObstacleCoords.add(obstacle131coords);
                mObstacleCoords.add(obstacle132coords);
                mMovingObstacleCoords.add(obstacle133coords);
                mMovingObstaclePaths.add(path);

                //Targets

                float[] target131coords = CommonFunctions.createCircleCoords(100,235,8f);
                float[] target132coords = CommonFunctions.createCircleCoords(15,128,8f);
                float[] target133coords = CommonFunctions.createCircleCoords(185,128,8f);

                mTargetCoords.add(target131coords);
                mTargetCoords.add(target132coords);
                mTargetCoords.add(target133coords);

                //num of balls
                mNumOfBalls = 2;

                break;


            //Level 4 Data
            case 4:
                //Obstacles
                float obstacle141coords[] = {
                        20f, 180f, 0.0f,   // top left
                        20f, 120f, 0.0f,   // bottom left
                        40f, 120f, 0.0f,   // bottom right
                        40f, 180f, 0.0f }; // top right


                float obstacle142coords[] = {
                        100f,  130f, 0.0f,   // top left
                        100f, 120f, 0.0f,   // bottom left
                        200f, 120f, 0.0f,   // bottom right
                        200f, 140f, 0.0f }; // top right

                float obstacle143coords[] = {
                        20f,  270f, 0.0f,   // top left
                        20f, 210f, 0.0f,   // bottom left
                        40f, 210f, 0.0f,   // bottom right
                        40f, 270f, 0.0f }; // top right

                float obstacle144coords[] = {
                        175f,  230f, 0.0f,   // top left
                        185f, 210f, 0.0f,   // bottom left
                        195f, 210f, 0.0f,   // bottom right
                        195f, 230f, 0.0f }; // top right

                mObstacleCoords.add(obstacle141coords);
                mObstacleCoords.add(obstacle142coords);
                mObstacleCoords.add(obstacle143coords);
                mObstacleCoords.add(obstacle144coords);

                //Targets

                float[] target141coords = CommonFunctions.createCircleCoords(30,190,8f);
                float[] target142coords = CommonFunctions.createCircleCoords(185,240,8f);

                mTargetCoords.add(target141coords);
                mTargetCoords.add(target142coords);

                //num of balls
                mNumOfBalls = 3;

                break;


            //Level 5 Data
            case 5:
                //Obstacles
                float obstacle151coords[] = {
                        6f, 80f, 0.0f,   // top left
                        6f, 70f, 0.0f,   // bottom left
                        50f, 70f, 0.0f,   // bottom right
                        50f, 80f, 0.0f }; // top right


                float obstacle152coords[] = {
                        130f, 140f, 0.0f,   // top left
                        130f, 115f, 0.0f,   // bottom left
                        210f, 115f, 0.0f,   // bottom right
                        210f, 140f, 0.0f }; // top right

                float obstacle153coords[] = {
                        40f, 250f, 0.0f,   // top left
                        40f, 240f, 0.0f,   // bottom left
                        90f, 240f, 0.0f,   // bottom right
                        90f, 250f, 0.0f }; // top right

                float obstacle154coords[] = {
                        6f, 140f, 0.0f,   // top left
                        6f, 130f, 0.0f,   // bottom left
                        75f, 130f, 0.0f,   // bottom right
                        75f, 140f, 0.0f }; // top right

                float obstacle155coords[] = {
                        130f, 85f, 0.0f,   // top left
                        130f, 0f, 0.0f,   // bottom left
                        220f, 0f, 0.0f,   // bottom right
                        220f, 85f, 0.0f }; // top right



                MovePath path51 = new MovePath();
                path51.addMovement(new SingleMovement(new PointF(0f,-0.25f), 170));
                path51.addMovement(new SingleMovement(new PointF(0f, 0.25f), 170));

                MovePath path52 = new MovePath();
                path52.addMovement(new SingleMovement(new PointF(0f,-0.25f), 170));
                path52.addMovement(new SingleMovement(new PointF(0f, 0.25f), 170));

                mObstacleCoords.add(obstacle152coords);
                mObstacleCoords.add(obstacle153coords);
                mObstacleCoords.add(obstacle155coords);

                mMovingObstacleCoords.add(obstacle151coords);
                mMovingObstacleCoords.add(obstacle154coords);

                mMovingObstaclePaths.add(path51);
                mMovingObstaclePaths.add(path52);

                //Targets

                float[] target151coords = CommonFunctions.createCircleCoords(60,260,8f);
                float[] target152coords = CommonFunctions.createCircleCoords(185,100,8f);

                mTargetCoords.add(target151coords);
                mTargetCoords.add(target152coords);

                //num of balls
                mNumOfBalls = 4;

                break;

        }






    }

    private void loadSet2Level(int levelIndex) {
        switch (levelIndex) {
            case 1:

                //Obstacles
                float obstacle211coords[] = {
                        80f, 140f, 0.0f,   // top left
                        80f, 95f, 0.0f,   // bottom left
                        130f, 95f, 0.0f,   // bottom right
                        130f, 105f, 0.0f }; // top right


                float obstacle212coords[] = {
                        0f,  170f, 0.0f,   // top left
                        0f, 0, 0.0f,   // bottom left
                        60f, 0f, 0.0f,   // bottom right
                        10f, 170f, 0.0f }; // top right

                float obstacle213coords[] = {
                        130f,  170f, 0.0f,   // top left
                        190f, 6f, 0.0f,   // bottom left
                        200f, 6f, 0.0f,   // bottom right
                        200f, 170f, 0.0f }; // top right

                MovePath path211 = new MovePath();
                path211.addMovement(new SingleMovement(new PointF(-.4f,0f), 170));
                path211.addMovement(new SingleMovement(new PointF(.4f, 0f), 170));

                MovePath path212 = new MovePath();
                path212.addMovement(new SingleMovement(new PointF(-0.2f,0f), 85));
                path212.addMovement(new SingleMovement(new PointF(0.2f, 0f), 85));

                mMovingObstacleCoords.add(obstacle211coords);
                mMovingObstacleCoords.add(obstacle212coords);

                mMovingObstaclePaths.add(path211);
                mMovingObstaclePaths.add(path212);

                mObstacleCoords.add(obstacle213coords);

                //Targets

                float[] target211coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target212coords = CommonFunctions.createCircleCoords(130,150,8f);

                mTargetCoords.add(target211coords);
                mTargetCoords.add(target212coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            case 2:

                //Obstacles
                float obstacle221coords[] = {
                        30f, 0f, 0.0f,   // top left
                        30f, -10f, 0.0f,   // bottom left
                        75f, 20f, 0.0f,   // bottom right
                        75f, 30f, 0.0f }; // top right


                float obstacle222coords[] = {
                        125f,  60f, 0.0f,   // top left
                        125f, 50, 0.0f,   // bottom left
                        170f, 20f, 0.0f,   // bottom right
                        170f, 30f, 0.0f }; // top right

                float obstacle223coords[] = {
                        60f,  170f, 0.0f,   // top left
                        60f, 160f, 0.0f,   // bottom left
                        135f, 160f, 0.0f,   // bottom right
                        135f, 170f, 0.0f }; // top right

                MovePath path221 = new MovePath();
                path221.addMovement(new SingleMovement(new PointF(0f,0.8f), 550));
                path221.addMovement(new SingleMovement(new PointF(-1.0f, 0f), 100));
                path221.addMovement(new SingleMovement(new PointF(0f, -0.8f), 550));
                path221.addMovement(new SingleMovement(new PointF(1.0f, 0f), 100));

                MovePath path222 = new MovePath();
                path222.addMovement(new SingleMovement(new PointF(0f,0.8f), 512));
                path222.addMovement(new SingleMovement(new PointF(1.0f, 0f), 100));
                path222.addMovement(new SingleMovement(new PointF(0f, -0.8f), 550));
                path222.addMovement(new SingleMovement(new PointF(-1.0f, 0f), 100));
                path222.addMovement(new SingleMovement(new PointF(0f,0.8f), 38));

                mMovingObstacleCoords.add(obstacle221coords);
                mMovingObstacleCoords.add(obstacle222coords);

                mMovingObstaclePaths.add(path221);
                mMovingObstaclePaths.add(path222);

                mObstacleCoords.add(obstacle223coords);

                //Targets

                float[] target221coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target222coords = CommonFunctions.createCircleCoords(10,260,8f);

                mTargetCoords.add(target221coords);
                mTargetCoords.add(target222coords);

                //num of balls
                mNumOfBalls = 3;


                //
                break;

            case 3:

                //Obstacles
                float obstacle231coords[] = {
                        44f, 120f, 0.0f,   // top left
                        44f, 60f, 0.0f,   // bottom left
                        64f, 60f, 0.0f,   // bottom right
                        64f, 120f, 0.0f }; // top right


                float obstacle232coords[] = {
                        135f,  180f, 0.0f,   // top left
                        135f, 120, 0.0f,   // bottom left
                        155f, 120f, 0.0f,   // bottom right
                        155f, 180f, 0.0f }; // top right

                float obstacle233coords[] = {
                        80f,  250f, 0.0f,   // top left
                        80f, 190f, 0.0f,   // bottom left
                        100f, 190f, 0.0f,   // bottom right
                        100f, 250f, 0.0f }; // top right


                mObstacleCoords.add(obstacle231coords);
                mObstacleCoords.add(obstacle232coords);
                mObstacleCoords.add(obstacle233coords);

                //Targets

                float[] target231coords = CommonFunctions.createCircleCoords(22, 110, 8f);
                float[] target232coords = CommonFunctions.createCircleCoords(90,260,8f);
                float[] target233coords = CommonFunctions.createCircleCoords(165,150,8f);

                mTargetCoords.add(target231coords);
                mTargetCoords.add(target232coords);
                mTargetCoords.add(target233coords);

                //num of balls
                mNumOfBalls = 2;

                break;

            case 4:

                //Obstacles
                float obstacle241coords[] = {
                        6f, 30f, 0.0f,   // left
                        36f, 12f, 0.0f,   // bottom
                        66f, 30f, 0.0f,   // right
                        36f, 48f, 0.0f }; // top

                //Obstacles
                float obstacle242coords[] = {
                        86f, 130f, 0.0f,   // left
                        116f, 112f, 0.0f,   // bottom
                        146f, 130f, 0.0f,   // right
                        116f, 148f, 0.0f }; // top

                //Obstacles
                float obstacle243coords[] = {
                        6f, 180f, 0.0f,   // left
                        36f, 162f, 0.0f,   // bottom
                        66f, 180f, 0.0f,   // right
                        36f, 198f, 0.0f }; // top

                MovePath path241 = new MovePath();
                path241.addMovement(new SingleMovement(new PointF(0.3f,0.5f), 100));
                path241.addMovement(new SingleMovement(new PointF(-0.3f, -0.5f), 100));

                MovePath path242 = new MovePath();
                path242.addMovement(new SingleMovement(new PointF(0.4f,-0.4f), 100));
                path242.addMovement(new SingleMovement(new PointF(-0.4f, 0.4f), 100));

                MovePath path243 = new MovePath();
                path243.addMovement(new SingleMovement(new PointF(0.2f,-0.6f), 100));
                path243.addMovement(new SingleMovement(new PointF(-0.2f, 0.6f), 100));

                mMovingObstacleCoords.add(obstacle241coords);
                mMovingObstacleCoords.add(obstacle242coords);
                mMovingObstacleCoords.add(obstacle243coords);

                mMovingObstaclePaths.add(path241);
                mMovingObstaclePaths.add(path242);
                mMovingObstaclePaths.add(path243);

                //Targets

                float[] target241coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target242coords = CommonFunctions.createCircleCoords(10,260,8f);

                mTargetCoords.add(target241coords);
                mTargetCoords.add(target242coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            case 5:

                //Obstacles
                float obstacle251coords[] = {
                        6f, 20f, 0.0f,   // top left
                        6f, 12f, 0.0f,   //bottom left
                        80f, 12f, 0.0f,   //bottom right
                        80f, 20f, 0.0f }; //top right


                MovePath path251 = new MovePath();
                path251.addMovement(new SingleMovement(new PointF(0f,0.2f), 100));
                path251.addMovement(new SingleMovement(new PointF(0f, -0.2f), 100));

                mMovingObstacleCoords.add(obstacle251coords);
                mMovingObstaclePaths.add(path251);

                //Targets

                float[] target251coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target252coords = CommonFunctions.createCircleCoords(10,260,8f);

                mTargetCoords.add(target251coords);
                mTargetCoords.add(target252coords);

                //num of balls
                mNumOfBalls = 3;

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