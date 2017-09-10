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

    float[] ballInitialCoords = null;

    public LevelData(int chapter, int level){
        switch (chapter){
            case 1:
                loadSet1Level(level);
                break;

            case 2:
                loadSet2Level(level);
                break;

            case 3:
                loadSet3Level(level);
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
                        6f, 180f, 0.0f,   // top left
                        6f, 90f, 0.0f,   // bottom left
                        40f, 120f, 0.0f,   // bottom right
                        40f, 180f, 0.0f }; // top right


                float obstacle132coords[] = {
                        100f,  130f, 0.0f,   // top left
                        100f, 120f, 0.0f,   // bottom left
                        200f, 100f, 0.0f,   // bottom right
                        200f, 140f, 0.0f }; // top right

                float obstacle133coords[] = {
                        6f,  300f, 0.0f,   // top left
                        6f, 210f, 0.0f,   // bottom left
                        40f, 210f, 0.0f,   // bottom right
                        40f, 270f, 0.0f }; // top right

                float obstacle134coords[] = {
                        175f,  230f, 0.0f,   // top left
                        185f, 210f, 0.0f,   // bottom left
                        195f, 200f, 0.0f,   // bottom right
                        195f, 230f, 0.0f }; // top right

                mObstacleCoords.add(obstacle131coords);
                mObstacleCoords.add(obstacle132coords);
                mObstacleCoords.add(obstacle133coords);
                mObstacleCoords.add(obstacle134coords);

                //Targets

                float[] target131coords = CommonFunctions.createCircleCoords(30,190,8f);
                float[] target132coords = CommonFunctions.createCircleCoords(185,240,8f);

                mTargetCoords.add(target131coords);
                mTargetCoords.add(target132coords);

                //num of balls
                mNumOfBalls = 3;

                break;


            //Level 4 Data
            case 4:



            //Level 5 Data
            case 5:
                //Obstacles
                float obstacle151coords[] = {
                        6f, 80f, 0.0f,   // top left
                        6f, 60f, 0.0f,   // bottom left
                        50f, 60f, 0.0f,   // bottom right
                        50f, 80f, 0.0f }; // top right


                float obstacle152coords[] = {
                        130f, 140f, 0.0f,   // top left
                        130f, 115f, 0.0f,   // bottom left
                        210f, 115f, 0.0f,   // bottom right
                        210f, 140f, 0.0f }; // top right

                float obstacle153coords[] = {
                        40f, 250f, 0.0f,   // top left
                        50f, 220f, 0.0f,   // bottom left
                        80f, 220f, 0.0f,   // bottom right
                        90f, 250f, 0.0f }; // top right

                float obstacle154coords[] = {
                        6f, 150f, 0.0f,   // top left
                        6f, 130f, 0.0f,   // bottom left
                        75f, 130f, 0.0f,   // bottom right
                        75f, 150f, 0.0f }; // top right

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
                        40f, 0f, 0.0f,   // top left
                        40f, -10f, 0.0f,   // bottom left
                        75f, 20f, 0.0f,   // bottom right
                        75f, 30f, 0.0f }; // top right


                float obstacle222coords[] = {
                        125f,  60f, 0.0f,   // top left
                        125f, 50, 0.0f,   // bottom left
                        160f, 20f, 0.0f,   // bottom right
                        160f, 30f, 0.0f }; // top right

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

                float obstacle244coords[] = {
                        126f, 195f, 0.0f,   // left
                        156f, 177f, 0.0f,   // bottom
                        186f, 195f, 0.0f,   // right
                        156f, 213f, 0.0f }; // top

                float obstacle245coords[] = {
                        61f, 232f, 0.0f,   // left
                        91f, 214f, 0.0f,   // bottom
                        121f, 232f, 0.0f,   // right
                        91f, 250f, 0.0f }; // top

                MovePath path241 = new MovePath();
                path241.addMovement(new SingleMovement(new PointF(0.3f,0.5f), 100));
                path241.addMovement(new SingleMovement(new PointF(-0.3f, -0.5f), 100));

                MovePath path242 = new MovePath();
                path242.addMovement(new SingleMovement(new PointF(0.4f,-0.4f), 100));
                path242.addMovement(new SingleMovement(new PointF(-0.4f, 0.4f), 100));

                MovePath path243 = new MovePath();
                path243.addMovement(new SingleMovement(new PointF(0.2f,-0.6f), 100));
                path243.addMovement(new SingleMovement(new PointF(-0.2f, 0.6f), 100));

                MovePath path244 = new MovePath();
                path244.addMovement(new SingleMovement(new PointF(-0.2f,-0.6f), 100));
                path244.addMovement(new SingleMovement(new PointF(0.2f, 0.6f), 100));

                MovePath path245 = new MovePath();
                path245.addMovement(new SingleMovement(new PointF(-0.6f,-0.2f), 100));
                path245.addMovement(new SingleMovement(new PointF(0.6f, 0.2f), 100));

                mMovingObstacleCoords.add(obstacle241coords);
                mMovingObstacleCoords.add(obstacle242coords);
                mMovingObstacleCoords.add(obstacle243coords);
                mMovingObstacleCoords.add(obstacle244coords);
                mMovingObstacleCoords.add(obstacle245coords);

                mMovingObstaclePaths.add(path241);
                mMovingObstaclePaths.add(path242);
                mMovingObstaclePaths.add(path243);
                mMovingObstaclePaths.add(path244);
                mMovingObstaclePaths.add(path245);

                //Targets

                float[] target241coords = CommonFunctions.createCircleCoords(185, 283, 8f);
                float[] target242coords = CommonFunctions.createCircleCoords(20,242,8f);

                mTargetCoords.add(target241coords);
                mTargetCoords.add(target242coords);

                //num of balls
                mNumOfBalls = 3;

                break;

            case 5:

                //Obstacles LEFT
                float obstacle251coords[] = {
                        6f, 60f, 0.0f,   // top left
                        6f, 42f, 0.0f,   //bottom left
                        70f, 42f, 0.0f,   //bottom right
                        65f, 50f, 0.0f }; //top right

                float obstacle252coords[] = {
                        6f, 95f, 0.0f,   // top left
                        6f, 77f, 0.0f,   //bottom left
                        48f, 77f, 0.0f,   //bottom right
                        45f, 85f, 0.0f }; //top right

                float obstacle253coords[] = {
                        6f, 122f, 0.0f,   // top left
                        6f, 112f, 0.0f,   //bottom left
                        34f, 112f, 0.0f,   //bottom right
                        33f, 120f, 0.0f }; //top right

                float obstacle254coords[] = {
                        6f, 155f, 0.0f,   // top left
                        6f, 147f, 0.0f,   //bottom left
                        22f, 147f, 0.0f,   //bottom right
                        22f, 155f, 0.0f }; //top right

                float obstacle255coords[] = {
                        6f, 190f, 0.0f,   // top left
                        6f, 180f, 0.0f,   //bottom left
                        33f, 182f, 0.0f,   //bottom right
                        34f, 190f, 0.0f }; //top right

                float obstacle256coords[] = {
                        6f, 225f, 0.0f,   // top left
                        6f, 207f, 0.0f,   //bottom left
                        45f, 217f, 0.0f,   //bottom right
                        48f, 225f, 0.0f }; //top right*/

                float obstacle257coords[] = {
                        6f, 260f, 0.0f,   // top left
                        6f, 242f, 0.0f,   //bottom left
                        65f, 252f, 0.0f,   //bottom right
                        70f, 260f, 0.0f }; //top right*/

                //Obstacles RIGHT
                float obstacle258coords[] = {
                        135f, 50f, 0.0f,   // top left
                        130f, 42f, 0.0f,   //bottom left
                        194f, 42f, 0.0f,   //bottom right
                        194f, 60f, 0.0f }; //top right

                float obstacle259coords[] = {
                        155f, 85f, 0.0f,   // top left
                        152f, 77f, 0.0f,   //bottom left
                        194f, 77f, 0.0f,   //bottom right
                        194f, 95f, 0.0f }; //top right

                float obstacle2510coords[] = {
                        167f, 120f, 0.0f,   // top left
                        166f, 112f, 0.0f,   //bottom left
                        194f, 112f, 0.0f,   //bottom right
                        194f, 122f, 0.0f }; //top right

                float obstacle2511coords[] = {
                        178f, 155f, 0.0f,   // top left
                        178f, 147f, 0.0f,   //bottom left
                        194f, 147f, 0.0f,   //bottom right
                        194f, 155f, 0.0f }; //top right

                float obstacle2512coords[] = {
                        166f, 190f, 0.0f,   // top left
                        167f, 182f, 0.0f,   //bottom left
                        194f, 180f, 0.0f,   //bottom right
                        194f, 190f, 0.0f }; //top right


                float obstacle2513coords[] = {
                        152f, 225f, 0.0f,   // top left
                        155f, 217f, 0.0f,   //bottom left
                        194f, 207f, 0.0f,   //bottom right
                        194f, 225f, 0.0f }; //top right*/

                float obstacle2514coords[] = {
                        135f, 260f, 0.0f,   // top left
                        140f, 252f, 0.0f,   //bottom left
                        194f, 242f, 0.0f,   //bottom right
                        194f, 260f, 0.0f }; //top right*/

                //OBSTACLE MIDDLE
                float obstacle2515coords[] = {
                        100, 166f, 0.0f,   // top
                        84f, 150f, 0.0f,   //left
                        100f, 134f, 0.0f,   //bottom
                        116f, 150f, 0.0f }; //right*/




                mObstacleCoords.add(obstacle251coords);
                mObstacleCoords.add(obstacle252coords);
                mObstacleCoords.add(obstacle253coords);
                mObstacleCoords.add(obstacle254coords);
                mObstacleCoords.add(obstacle255coords);
                mObstacleCoords.add(obstacle256coords);
                mObstacleCoords.add(obstacle257coords);

                mObstacleCoords.add(obstacle258coords);
                mObstacleCoords.add(obstacle259coords);
                mObstacleCoords.add(obstacle2510coords);
                mObstacleCoords.add(obstacle2511coords);
                mObstacleCoords.add(obstacle2512coords);
                mObstacleCoords.add(obstacle2513coords);
                mObstacleCoords.add(obstacle2514coords);

                mObstacleCoords.add(obstacle2515coords);

                //Targets

                float[] target251coords = CommonFunctions.createCircleCoords(160, 150, 8f);
                float[] target252coords = CommonFunctions.createCircleCoords(18,275,8f);

                mTargetCoords.add(target251coords);
                mTargetCoords.add(target252coords);

                //num of balls
                mNumOfBalls = 3;

                break;
        }
    }

    private void loadSet3Level(int levelIndex) {
        switch (levelIndex) {
            case 1:

                //top left
                float obstacle311coords[] = {
                        6f, 250f, 0.0f,   // top left
                        6f, 190f, 0.0f,   // bottom left
                        30f, 210f, 0.0f,   // bottom right
                        30f, 250f, 0.0f }; // top right


                //top middle
                float obstacle312coords[] = {
                        70f, 250f, 0.0f,   // top left
                        70f, 210f, 0.0f,   // bottom left
                        130f, 210f, 0.0f,   // bottom right
                        130f, 250f, 0.0f }; // top right

                //top right
                float obstacle313coords[] = {
                        170f, 250f, 0.0f,   // top left
                        170f, 210f, 0.0f,   // bottom left
                        194f, 190f, 0.0f,   // bottom right
                        194f, 250f, 0.0f }; // top right

                //middle left
                float obstacle314coords[] = {
                        50f, 180f, 0.0f,   // top
                        30f, 160f, 0.0f,   // top left
                        30f, 110f, 0.0f,   // bottom left
                        80f, 145f, 0.0f }; // right

                //middle right
                float obstacle315coords[] = {
                        150f, 180f, 0.0f,   // top
                        120f, 145f, 0.0f,   // left
                        170f, 110f, 0.0f,   // bottom right
                        170f, 160f, 0.0f }; // topright

                //middle center
                float obstacle316coords[] = {
                        88f, 116f, 0.0f,   // top left
                        40f, 80f, 0.0f,   // bottom left
                        160f, 80f, 0.0f,   // bottom right
                        112f, 116f, 0.0f }; // top right

                //bottom left
                float obstacle317coords[] = {
                        6f, 48f, 0.0f,   // top left
                        6f, 12f, 0.0f,   // bottom left
                        42f, 12f, 0.0f,   // bottom right
                        32f, 38f, 0.0f }; // top right

                //bottom right
                float obstacle318coords[] = {
                        168f, 38f, 0.0f,   // top left
                        158f, 12f, 0.0f,   // bottom left
                        194f, 12f, 0.0f,   // bottom right
                        194f, 48f, 0.0f }; // top right


                mObstacleCoords.add(obstacle311coords);
                mObstacleCoords.add(obstacle312coords);
                mObstacleCoords.add(obstacle313coords);
                mObstacleCoords.add(obstacle314coords);
                mObstacleCoords.add(obstacle315coords);
                mObstacleCoords.add(obstacle316coords);

                mObstacleCoords.add(obstacle317coords);
                mObstacleCoords.add(obstacle318coords);

                //Targets

                float[] target311coords = CommonFunctions.createCircleCoords(100,200,8f);
                float[] target312coords = CommonFunctions.createCircleCoords(100,130,8f);
                float[] target313coords = CommonFunctions.createCircleCoords(100,70,8f);

                mTargetCoords.add(target311coords);
                mTargetCoords.add(target312coords);
                mTargetCoords.add(target313coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 282, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;
                break;

            case 2:

                //bottom star
                float obstacle321coords[] = {
                        6f, 130f, 0.0f,   // top left
                        0f, 0f, 0.0f,   // bottom left
                        70f, 12f, 0.0f,   // bottom right
                        50f, 100f, 0.0f }; // top right


                float obstacle322coords[] = {
                        130f, 294f, 0.0f,   // top left
                        150f, 206f, 0.0f,   // bottom left
                        194f, 176f, 0.0f,   // bottom right
                        200f, 300f, 0.0f }; // top right


                mObstacleCoords.add(obstacle321coords);
                mObstacleCoords.add(obstacle322coords);

                //Targets

                float[] target321coords = CommonFunctions.createCircleCoords(16,136,8f);
                float[] target322coords = CommonFunctions.createCircleCoords(184,166,8f);
                float[] target323coords = CommonFunctions.createCircleCoords(75,200,8f);

                mTargetCoords.add(target321coords);
                mTargetCoords.add(target322coords);
                mTargetCoords.add(target323coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(170, 24, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 1;

                break;

            case 3:

                //bottom
                float obstacle331coords[] = {
                        52f, 6f, 0.0f,   // left
                        106f, -51f, 0.0f,   // bottom
                        160f, 6f, 0.0f,   // right
                        106f, 60f, 0.0f }; // top


                float obstacle332coords[] = {
                        140f, 86f, 0.0f,   // left
                        194f, 30f, 0.0f,   // bottom
                        248f, 86f, 0.0f,   // right
                        194f, 140f, 0.0f }; // top


                float obstacle333coords[] = {
                        154f, 180f, 0.0f,   // left
                        194f, 140f, 0.0f,   // bottom
                        234f, 180f, 0.0f,   // right
                        194f, 220f, 0.0f }; // top

                float obstacle334coords[] = {
                        154f, 260f, 0.0f,   // left
                        194f, 220f, 0.0f,   // bottom
                        234f, 260f, 0.0f,   // right
                        194f, 300f, 0.0f }; // top

                float obstacle335coords[] = {
                        -36f, 140f, 0.0f,   // left
                        6f, 100f, 0.0f,   // bottom
                        46f, 140f, 0.0f,   // right
                        6f, 180f, 0.0f }; // top


                float obstacle336coords[] = {
                        -36f, 220f, 0.0f,   // left
                        6f, 180f, 0.0f,   // bottom
                        46f, 220f, 0.0f,   // right
                        6f, 260f, 0.0f }; // top

                float obstacle337coords[] = {
                        -36f, 300f, 0.0f,   // left
                        6f, 260f, 0.0f,   // bottom
                        46f, 300f, 0.0f,   // right
                        6f, 340f, 0.0f }; // top

                float obstacle338coords[] = {
                        85f, 170f, 0.0f,   // left
                        115f, 140f, 0.0f,   // bottom
                        120f, 145f, 0.0f,   // right
                        90f, 175f, 0.0f }; // top


               // MovePath path321 = new MovePath();
               // path321.addMovement(new SingleMovement(new PointF(0.15f,0f), 100));
               // path321.addMovement(new SingleMovement(new PointF(-0.15f,0f), 100));

                //mMovingObstacleCoords.add(obstacle321coords);
                // mMovingObstaclePaths.add(path321);

                mObstacleCoords.add(obstacle331coords);
                mObstacleCoords.add(obstacle332coords);
                mObstacleCoords.add(obstacle333coords);
                mObstacleCoords.add(obstacle334coords);
                mObstacleCoords.add(obstacle335coords);
                mObstacleCoords.add(obstacle336coords);
                mObstacleCoords.add(obstacle337coords);
                mObstacleCoords.add(obstacle338coords);


                //Targets

                float[] target331coords = CommonFunctions.createCircleCoords(162,280,8f);
                float[] target332coords = CommonFunctions.createCircleCoords(110,170,8f);
                float[] target333coords = CommonFunctions.createCircleCoords(28,172,8f);

                mTargetCoords.add(target331coords);
                mTargetCoords.add(target332coords);
                mTargetCoords.add(target333coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(20, 26, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

                break;

            case 4:

                //TOP LEFT GROUP
                float obstacle341coords[] = {
                        31f, 269f, 0.0f,   // top left
                        31f, 175f, 0.0f,   // bottom left
                        46f, 175f, 0.0f,   // bottom right
                        46f, 269f, 0.0f }; // top right

                float obstacle342coords[] = {
                        46f, 251f, 0.0f,   // top left
                        46f, 193f, 0.0f,   // bottom left
                        61f, 193f, 0.0f,   // bottom right
                        61f, 251f, 0.0f }; // top right

                float obstacle343coords[] = {
                        61f, 233f, 0.0f,   // top left
                        61f, 211f, 0.0f,   // bottom left
                        76f, 211f, 0.0f,   // bottom right
                        76f, 233f, 0.0f }; // top right

                //TOP RIGHT GROUP
                float obstacle344coords[] = {
                        154f, 269f, 0.0f,   // top left
                        154f, 175f, 0.0f,   // bottom left
                        169f, 175f, 0.0f,   // bottom right
                        169f, 269f, 0.0f }; // top right

                float obstacle345coords[] = {
                        139f, 251f, 0.0f,   // top left
                        139f, 193f, 0.0f,   // bottom left
                        154f, 193f, 0.0f,   // bottom right
                        154f, 251f, 0.0f }; // top right

                float obstacle346coords[] = {
                        124f, 233f, 0.0f,   // top left
                        124f, 211f, 0.0f,   // bottom left
                        139f, 211f, 0.0f,   // bottom right
                        139f, 233f, 0.0f }; // top right

                //BOTTOM LEFT GROUP
                float obstacle347coords[] = {
                        61f, 125f, 0.0f,   // top left
                        61f, 37f, 0.0f,   // bottom left
                        76f, 37f, 0.0f,   // bottom right
                        76f, 125f, 0.0f }; // top right

                float obstacle348coords[] = {
                        46f, 107f, 0.0f,   // top left
                        46f, 55f, 0.0f,   // bottom left
                        61f, 55f, 0.0f,   // bottom right
                        61f, 107f, 0.0f }; // top right

                float obstacle349coords[] = {
                        31f, 89f, 0.0f,   // top left
                        31f, 73f, 0.0f,   // bottom left
                        46f, 73f, 0.0f,   // bottom right
                        46f, 89f, 0.0f }; // top right

                //BOTTOM RIGHT GROUP
                float obstacle3410coords[] = {
                        124f, 125f, 0.0f,   // top left
                        124f, 37f, 0.0f,   // bottom left
                        139f, 37f, 0.0f,   // bottom right
                        139f, 125f, 0.0f }; // top right

                float obstacle3411coords[] = {
                        139f, 107f, 0.0f,   // top left
                        139f, 55f, 0.0f,   // bottom left
                        154f, 55f, 0.0f,   // bottom right
                        154f, 107f, 0.0f }; // top right

                float obstacle3412coords[] = {
                        154f, 89f, 0.0f,   // top left
                        154f, 73f, 0.0f,   // bottom left
                        169f, 73f, 0.0f,   // bottom right
                        169f, 89f, 0.0f }; // top right


                mObstacleCoords.add(obstacle341coords);
                mObstacleCoords.add(obstacle342coords);
                mObstacleCoords.add(obstacle343coords);
                mObstacleCoords.add(obstacle344coords);
                mObstacleCoords.add(obstacle345coords);
                mObstacleCoords.add(obstacle346coords);
                mObstacleCoords.add(obstacle347coords);
                mObstacleCoords.add(obstacle348coords);
                mObstacleCoords.add(obstacle349coords);
                mObstacleCoords.add(obstacle3410coords);
                mObstacleCoords.add(obstacle3411coords);
                mObstacleCoords.add(obstacle3412coords);

                //Targets
                float[] target341coords = CommonFunctions.createCircleCoords(162,280,8f);
                float[] target342coords = CommonFunctions.createCircleCoords(69f,20f,8f);
                //float[] target333coords = CommonFunctions.createCircleCoords(28,172,8f);

                mTargetCoords.add(target341coords);
                mTargetCoords.add(target342coords);
               // mTargetCoords.add(target333coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 150, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

                break;

            case 5:

                //top
                float obstacle351coords[] = {
                        50f, 260f, 0.0f,   // top left
                        85f, 240f, 0.0f,   // bottom left
                        115f, 240f, 0.0f,   // bottom right
                        150f, 260f, 0.0f }; // top right


                //middle left
                float obstacle352coords[] = {
                        6f, 150f, 0.0f,   // top left
                        0f, 90f, 0.0f,   // bottom left
                        6f, 90f, 0.0f,   // bottom right
                        65f, 150f, 0.0f }; // top right

                //middle right
                float obstacle353coords[] = {
                        194f, 150f, 0.0f,   // top right
                        200f, 90f, 0.0f,   // bottom right
                        194f, 90f, 0.0f,   // bottom left
                        135f, 150f, 0.0f }; // top left

                //bottom left
                float obstacle354coords[] = {
                        -22f, 94f, 0.0f,   // top left
                        -22f, 12f, 0.0f,   // bottom left
                        0f, 0f, 0.0f,   // bottom right
                        40f, 12f, 0.0f }; // top right

                //bottom right
                float obstacle355coords[] = {
                        222f, 94f, 0.0f,   // top left
                        222f, 12f, 0.0f,   // bottom left
                        200f, 0f, 0.0f,   // bottom right
                        160f, 12f, 0.0f }; // top right


                MovePath path352 = new MovePath();
                path352.addMovement(new SingleMovement(new PointF(0f,-1f), 50));
                path352.addMovement(new SingleMovement(new PointF(0f,.25f), 200));

                MovePath path353 = new MovePath();
                path353.addMovement(new SingleMovement(new PointF(0f,-1f), 50));
                path353.addMovement(new SingleMovement(new PointF(0f,.25f), 200));

                MovePath path354 = new MovePath();
                path354.addMovement(new SingleMovement(new PointF(-.25f,0f), 25));
                path354.addMovement(new SingleMovement(new PointF(1f,0f), 50));
                path354.addMovement(new SingleMovement(new PointF(-.25f,0f), 175));

                MovePath path355 = new MovePath();
                path355.addMovement(new SingleMovement(new PointF(.25f,0f), 25));
                path355.addMovement(new SingleMovement(new PointF(-1f,0f), 50));
                path355.addMovement(new SingleMovement(new PointF(.25f,0f), 175));

                mMovingObstacleCoords.add(obstacle352coords);
                mMovingObstacleCoords.add(obstacle353coords);
                mMovingObstacleCoords.add(obstacle354coords);
                mMovingObstacleCoords.add(obstacle355coords);
                mMovingObstaclePaths.add(path352);
                mMovingObstaclePaths.add(path353);
                mMovingObstaclePaths.add(path354);
                mMovingObstaclePaths.add(path355);

                mObstacleCoords.add(obstacle351coords);

                //Targets

                float[] target351coords = CommonFunctions.createCircleCoords(100,270,8f);

                mTargetCoords.add(target351coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 24, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

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

    public float[] getBallInitialCoords() {
        return ballInitialCoords;
    }



}
