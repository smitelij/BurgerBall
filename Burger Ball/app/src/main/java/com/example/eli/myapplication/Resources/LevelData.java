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
                //top
                float obstacle141coords[] = {
                        50f, 260f, 0.0f,   // top left
                        85f, 240f, 0.0f,   // bottom left
                        115f, 240f, 0.0f,   // bottom right
                        150f, 260f, 0.0f }; // top right


                //middle left
                float obstacle142coords[] = {
                        6f, 150f, 0.0f,   // top left
                        0f, 90f, 0.0f,   // bottom left
                        6f, 90f, 0.0f,   // bottom right
                        65f, 150f, 0.0f }; // top right

                //middle right
                float obstacle143coords[] = {
                        194f, 150f, 0.0f,   // top right
                        200f, 90f, 0.0f,   // bottom right
                        194f, 90f, 0.0f,   // bottom left
                        135f, 150f, 0.0f }; // top left

                //bottom left
                float obstacle144coords[] = {
                        -22f, 94f, 0.0f,   // top left
                        -22f, 12f, 0.0f,   // bottom left
                        0f, 0f, 0.0f,   // bottom right
                        40f, 12f, 0.0f }; // top right

                //bottom right
                float obstacle145coords[] = {
                        222f, 94f, 0.0f,   // top left
                        222f, 12f, 0.0f,   // bottom left
                        200f, 0f, 0.0f,   // bottom right
                        160f, 12f, 0.0f }; // top right


                MovePath path142 = new MovePath();
                path142.addMovement(new SingleMovement(new PointF(0f,-1f), 50));
                path142.addMovement(new SingleMovement(new PointF(0f,.25f), 200));

                MovePath path143 = new MovePath();
                path143.addMovement(new SingleMovement(new PointF(0f,-1f), 50));
                path143.addMovement(new SingleMovement(new PointF(0f,.25f), 200));

                MovePath path144 = new MovePath();
                path144.addMovement(new SingleMovement(new PointF(-.25f,0f), 25));
                path144.addMovement(new SingleMovement(new PointF(1f,0f), 50));
                path144.addMovement(new SingleMovement(new PointF(-.25f,0f), 175));

                MovePath path145 = new MovePath();
                path145.addMovement(new SingleMovement(new PointF(.25f,0f), 25));
                path145.addMovement(new SingleMovement(new PointF(-1f,0f), 50));
                path145.addMovement(new SingleMovement(new PointF(.25f,0f), 175));

                mMovingObstacleCoords.add(obstacle142coords);
                mMovingObstacleCoords.add(obstacle143coords);
                mMovingObstacleCoords.add(obstacle144coords);
                mMovingObstacleCoords.add(obstacle145coords);
                mMovingObstaclePaths.add(path142);
                mMovingObstaclePaths.add(path143);
                mMovingObstaclePaths.add(path144);
                mMovingObstaclePaths.add(path145);

                mObstacleCoords.add(obstacle141coords);

                //Targets

                float[] target141coords = CommonFunctions.createCircleCoords(100,270,8f);

                mTargetCoords.add(target141coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 24, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

                break;


            //Level 5 Data
            case 5:
                //Obstacles
                float obstacle151coords[] = {
                        44f, 120f, 0.0f,   // top left
                        44f, 60f, 0.0f,   // bottom left
                        64f, 60f, 0.0f,   // bottom right
                        64f, 120f, 0.0f }; // top right


                float obstacle152coords[] = {
                        135f,  180f, 0.0f,   // top left
                        135f, 120, 0.0f,   // bottom left
                        155f, 120f, 0.0f,   // bottom right
                        155f, 180f, 0.0f }; // top right

                float obstacle153coords[] = {
                        80f,  250f, 0.0f,   // top left
                        80f, 190f, 0.0f,   // bottom left
                        100f, 190f, 0.0f,   // bottom right
                        100f, 250f, 0.0f }; // top right


                mObstacleCoords.add(obstacle151coords);
                mObstacleCoords.add(obstacle152coords);
                mObstacleCoords.add(obstacle153coords);

                //Targets

                float[] target151coords = CommonFunctions.createCircleCoords(22, 110, 8f);
                float[] target152coords = CommonFunctions.createCircleCoords(90,260,8f);
                float[] target153coords = CommonFunctions.createCircleCoords(165,150,8f);

                mTargetCoords.add(target151coords);
                mTargetCoords.add(target152coords);
                mTargetCoords.add(target153coords);

                //num of balls
                mNumOfBalls = 2;

                break;

        }


    }

    private void loadSet2Level(int levelIndex) {
        switch (levelIndex) {
            case 1:
                //top left
                float obstacle211coords[] = {
                        6f, 250f, 0.0f,   // top left
                        6f, 190f, 0.0f,   // bottom left
                        30f, 210f, 0.0f,   // bottom right
                        30f, 250f, 0.0f }; // top right


                //top middle
                float obstacle212coords[] = {
                        70f, 250f, 0.0f,   // top left
                        70f, 210f, 0.0f,   // bottom left
                        130f, 210f, 0.0f,   // bottom right
                        130f, 250f, 0.0f }; // top right

                //top right
                float obstacle213coords[] = {
                        170f, 250f, 0.0f,   // top left
                        170f, 210f, 0.0f,   // bottom left
                        194f, 190f, 0.0f,   // bottom right
                        194f, 250f, 0.0f }; // top right

                //middle left
                float obstacle214coords[] = {
                        50f, 180f, 0.0f,   // top
                        30f, 160f, 0.0f,   // top left
                        30f, 110f, 0.0f,   // bottom left
                        80f, 145f, 0.0f }; // right

                //middle right
                float obstacle215coords[] = {
                        150f, 180f, 0.0f,   // top
                        120f, 145f, 0.0f,   // left
                        170f, 110f, 0.0f,   // bottom right
                        170f, 160f, 0.0f }; // topright

                //middle center
                float obstacle216coords[] = {
                        88f, 116f, 0.0f,   // top left
                        40f, 80f, 0.0f,   // bottom left
                        160f, 80f, 0.0f,   // bottom right
                        112f, 116f, 0.0f }; // top right

                //bottom left
                float obstacle217coords[] = {
                        6f, 48f, 0.0f,   // top left
                        6f, 12f, 0.0f,   // bottom left
                        42f, 12f, 0.0f,   // bottom right
                        32f, 38f, 0.0f }; // top right

                //bottom right
                float obstacle218coords[] = {
                        168f, 38f, 0.0f,   // top left
                        158f, 12f, 0.0f,   // bottom left
                        194f, 12f, 0.0f,   // bottom right
                        194f, 48f, 0.0f }; // top right


                mObstacleCoords.add(obstacle211coords);
                mObstacleCoords.add(obstacle212coords);
                mObstacleCoords.add(obstacle213coords);
                mObstacleCoords.add(obstacle214coords);
                mObstacleCoords.add(obstacle215coords);
                mObstacleCoords.add(obstacle216coords);

                mObstacleCoords.add(obstacle217coords);
                mObstacleCoords.add(obstacle218coords);

                //Targets

                float[] target211coords = CommonFunctions.createCircleCoords(100,200,8f);
                float[] target212coords = CommonFunctions.createCircleCoords(100,130,8f);
                float[] target213coords = CommonFunctions.createCircleCoords(100,70,8f);

                mTargetCoords.add(target211coords);
                mTargetCoords.add(target212coords);
                mTargetCoords.add(target213coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 282, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;
                break;


            case 2:
                //bottom
                float obstacle221coords[] = {
                        52f, 6f, 0.0f,   // left
                        106f, -51f, 0.0f,   // bottom
                        160f, 6f, 0.0f,   // right
                        106f, 60f, 0.0f }; // top


                float obstacle222coords[] = {
                        140f, 86f, 0.0f,   // left
                        194f, 30f, 0.0f,   // bottom
                        248f, 86f, 0.0f,   // right
                        194f, 140f, 0.0f }; // top


                float obstacle223coords[] = {
                        154f, 180f, 0.0f,   // left
                        194f, 140f, 0.0f,   // bottom
                        234f, 180f, 0.0f,   // right
                        194f, 220f, 0.0f }; // top

                float obstacle224coords[] = {
                        154f, 260f, 0.0f,   // left
                        194f, 220f, 0.0f,   // bottom
                        234f, 260f, 0.0f,   // right
                        194f, 300f, 0.0f }; // top

                float obstacle225coords[] = {
                        -36f, 140f, 0.0f,   // left
                        6f, 100f, 0.0f,   // bottom
                        46f, 140f, 0.0f,   // right
                        6f, 180f, 0.0f }; // top


                float obstacle226coords[] = {
                        -36f, 220f, 0.0f,   // left
                        6f, 180f, 0.0f,   // bottom
                        46f, 220f, 0.0f,   // right
                        6f, 260f, 0.0f }; // top

                float obstacle227coords[] = {
                        -36f, 300f, 0.0f,   // left
                        6f, 260f, 0.0f,   // bottom
                        46f, 300f, 0.0f,   // right
                        6f, 340f, 0.0f }; // top

                float obstacle228coords[] = {
                        85f, 170f, 0.0f,   // left
                        115f, 140f, 0.0f,   // bottom
                        120f, 145f, 0.0f,   // right
                        90f, 175f, 0.0f }; // top


                // MovePath path321 = new MovePath();
                // path321.addMovement(new SingleMovement(new PointF(0.15f,0f), 100));
                // path321.addMovement(new SingleMovement(new PointF(-0.15f,0f), 100));

                //mMovingObstacleCoords.add(obstacle321coords);
                // mMovingObstaclePaths.add(path321);

                mObstacleCoords.add(obstacle221coords);
                mObstacleCoords.add(obstacle222coords);
                mObstacleCoords.add(obstacle223coords);
                mObstacleCoords.add(obstacle224coords);
                mObstacleCoords.add(obstacle225coords);
                mObstacleCoords.add(obstacle226coords);
                mObstacleCoords.add(obstacle227coords);
                mObstacleCoords.add(obstacle228coords);


                //Targets

                float[] target221coords = CommonFunctions.createCircleCoords(162,280,8f);
                float[] target222coords = CommonFunctions.createCircleCoords(110,170,8f);
                float[] target223coords = CommonFunctions.createCircleCoords(28,172,8f);

                mTargetCoords.add(target221coords);
                mTargetCoords.add(target222coords);
                mTargetCoords.add(target223coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(20, 26, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

                break;


            case 3:
                //Obstacles
                float obstacle231coords[] = {
                        0f, 120f, 0.0f,   // top left
                        0f, 50f, 0.0f,   // bottom left
                        75f, 50f, 0.0f,   // bottom right
                        30f, 120f, 0.0f }; // top right


                float obstacle232coords[] = {
                        170f, 120f, 0.0f,   // top left
                        125f, 50f, 0.0f,   // bottom left
                        200f, 50f, 0.0f,   // bottom right
                        200f, 120f, 0.0f }; // top right

                float obstacle233coords[] = {
                        87f, 220f, 0f,     // top
                        47f, 200f, 0.0f,   // top left
                        87f, 140f, 0.0f,   // bottom
                        127f, 200f, 0.0f,}; // top right

                MovePath path = new MovePath();
                path.addMovement(new SingleMovement(new PointF(0.5f,0f), 100));
                path.addMovement(new SingleMovement(new PointF(-0.5f, 0f), 100));


                mObstacleCoords.add(obstacle231coords);
                mObstacleCoords.add(obstacle232coords);
                mMovingObstacleCoords.add(obstacle233coords);
                mMovingObstaclePaths.add(path);

                //Targets

                float[] target231coords = CommonFunctions.createCircleCoords(100,235,8f);
                float[] target232coords = CommonFunctions.createCircleCoords(15,128,8f);
                float[] target233coords = CommonFunctions.createCircleCoords(185,128,8f);

                mTargetCoords.add(target231coords);
                mTargetCoords.add(target232coords);
                mTargetCoords.add(target233coords);

                //num of balls
                mNumOfBalls = 2;

                break;


            case 4:
                //Obstacles
                float obstacle241coords[] = {
                        6f, 80f, 0.0f,   // top left
                        6f, 60f, 0.0f,   // bottom left
                        50f, 60f, 0.0f,   // bottom right
                        50f, 80f, 0.0f }; // top right


                float obstacle242coords[] = {
                        130f, 140f, 0.0f,   // top left
                        130f, 115f, 0.0f,   // bottom left
                        210f, 115f, 0.0f,   // bottom right
                        210f, 140f, 0.0f }; // top right

                float obstacle243coords[] = {
                        40f, 250f, 0.0f,   // top left
                        50f, 220f, 0.0f,   // bottom left
                        80f, 220f, 0.0f,   // bottom right
                        90f, 250f, 0.0f }; // top right

                float obstacle244coords[] = {
                        6f, 150f, 0.0f,   // top left
                        6f, 130f, 0.0f,   // bottom left
                        75f, 130f, 0.0f,   // bottom right
                        75f, 150f, 0.0f }; // top right

                float obstacle245coords[] = {
                        130f, 85f, 0.0f,   // top left
                        130f, 0f, 0.0f,   // bottom left
                        220f, 0f, 0.0f,   // bottom right
                        220f, 85f, 0.0f }; // top right



                MovePath path241 = new MovePath();
                path241.addMovement(new SingleMovement(new PointF(0f,-0.25f), 170));
                path241.addMovement(new SingleMovement(new PointF(0f, 0.25f), 170));

                MovePath path242 = new MovePath();
                path242.addMovement(new SingleMovement(new PointF(0f,-0.25f), 170));
                path242.addMovement(new SingleMovement(new PointF(0f, 0.25f), 170));

                mObstacleCoords.add(obstacle242coords);
                mObstacleCoords.add(obstacle243coords);
                mObstacleCoords.add(obstacle245coords);

                mMovingObstacleCoords.add(obstacle241coords);
                mMovingObstacleCoords.add(obstacle244coords);

                mMovingObstaclePaths.add(path241);
                mMovingObstaclePaths.add(path242);

                //Targets

                float[] target241coords = CommonFunctions.createCircleCoords(60,260,8f);
                float[] target242coords = CommonFunctions.createCircleCoords(185,100,8f);

                mTargetCoords.add(target241coords);
                mTargetCoords.add(target242coords);

                //num of balls
                mNumOfBalls = 3;

                break;


            case 5:
                //Obstacles
                float obstacle261coords[] = {
                        6f, 30f, 0.0f,   // left
                        36f, 12f, 0.0f,   // bottom
                        66f, 30f, 0.0f,   // right
                        36f, 48f, 0.0f }; // top

                //Obstacles
                float obstacle262coords[] = {
                        86f, 130f, 0.0f,   // left
                        116f, 112f, 0.0f,   // bottom
                        146f, 130f, 0.0f,   // right
                        116f, 148f, 0.0f }; // top

                //Obstacles
                float obstacle263coords[] = {
                        6f, 180f, 0.0f,   // left
                        36f, 162f, 0.0f,   // bottom
                        66f, 180f, 0.0f,   // right
                        36f, 198f, 0.0f }; // top

                float obstacle264coords[] = {
                        126f, 195f, 0.0f,   // left
                        156f, 177f, 0.0f,   // bottom
                        186f, 195f, 0.0f,   // right
                        156f, 213f, 0.0f }; // top

                float obstacle265coords[] = {
                        61f, 232f, 0.0f,   // left
                        91f, 214f, 0.0f,   // bottom
                        121f, 232f, 0.0f,   // right
                        91f, 250f, 0.0f }; // top

                MovePath path261 = new MovePath();
                path261.addMovement(new SingleMovement(new PointF(0.3f,0.5f), 100));
                path261.addMovement(new SingleMovement(new PointF(-0.3f, -0.5f), 100));

                MovePath path262 = new MovePath();
                path262.addMovement(new SingleMovement(new PointF(0.4f,-0.4f), 100));
                path262.addMovement(new SingleMovement(new PointF(-0.4f, 0.4f), 100));

                MovePath path263 = new MovePath();
                path263.addMovement(new SingleMovement(new PointF(0.2f,-0.6f), 100));
                path263.addMovement(new SingleMovement(new PointF(-0.2f, 0.6f), 100));

                MovePath path264 = new MovePath();
                path264.addMovement(new SingleMovement(new PointF(-0.2f,-0.6f), 100));
                path264.addMovement(new SingleMovement(new PointF(0.2f, 0.6f), 100));

                MovePath path265 = new MovePath();
                path265.addMovement(new SingleMovement(new PointF(-0.6f,-0.2f), 100));
                path265.addMovement(new SingleMovement(new PointF(0.6f, 0.2f), 100));

                mMovingObstacleCoords.add(obstacle261coords);
                mMovingObstacleCoords.add(obstacle262coords);
                mMovingObstacleCoords.add(obstacle263coords);
                mMovingObstacleCoords.add(obstacle264coords);
                mMovingObstacleCoords.add(obstacle265coords);

                mMovingObstaclePaths.add(path261);
                mMovingObstaclePaths.add(path262);
                mMovingObstaclePaths.add(path263);
                mMovingObstaclePaths.add(path264);
                mMovingObstaclePaths.add(path265);

                //Targets

                float[] target261coords = CommonFunctions.createCircleCoords(185, 283, 8f);
                float[] target262coords = CommonFunctions.createCircleCoords(20,242,8f);

                mTargetCoords.add(target261coords);
                mTargetCoords.add(target262coords);

                //num of balls
                mNumOfBalls = 3;

                break;

        }
    }

    private void loadSet3Level(int levelIndex) {
        switch (levelIndex) {
            case 1:
                //bottom star
                float obstacle311coords[] = {
                        6f, 130f, 0.0f,   // top left
                        0f, 0f, 0.0f,   // bottom left
                        70f, 12f, 0.0f,   // bottom right
                        50f, 100f, 0.0f }; // top right


                float obstacle312coords[] = {
                        130f, 294f, 0.0f,   // top left
                        150f, 206f, 0.0f,   // bottom left
                        194f, 176f, 0.0f,   // bottom right
                        200f, 300f, 0.0f }; // top right


                mObstacleCoords.add(obstacle311coords);
                mObstacleCoords.add(obstacle312coords);

                //Targets

                float[] target311coords = CommonFunctions.createCircleCoords(16,136,8f);
                float[] target312coords = CommonFunctions.createCircleCoords(184,166,8f);
                float[] target313coords = CommonFunctions.createCircleCoords(75,200,8f);

                mTargetCoords.add(target311coords);
                mTargetCoords.add(target312coords);
                mTargetCoords.add(target313coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(170, 24, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 1;

                break;


            case 2:
                //TOP LEFT GROUP
                float obstacle321coords[] = {
                        31f, 269f, 0.0f,   // top left
                        31f, 175f, 0.0f,   // bottom left
                        46f, 175f, 0.0f,   // bottom right
                        46f, 269f, 0.0f }; // top right

                float obstacle322coords[] = {
                        46f, 251f, 0.0f,   // top left
                        46f, 193f, 0.0f,   // bottom left
                        61f, 193f, 0.0f,   // bottom right
                        61f, 251f, 0.0f }; // top right

                float obstacle323coords[] = {
                        61f, 233f, 0.0f,   // top left
                        61f, 211f, 0.0f,   // bottom left
                        76f, 211f, 0.0f,   // bottom right
                        76f, 233f, 0.0f }; // top right

                //TOP RIGHT GROUP
                float obstacle324coords[] = {
                        154f, 269f, 0.0f,   // top left
                        154f, 175f, 0.0f,   // bottom left
                        169f, 175f, 0.0f,   // bottom right
                        169f, 269f, 0.0f }; // top right

                float obstacle325coords[] = {
                        139f, 251f, 0.0f,   // top left
                        139f, 193f, 0.0f,   // bottom left
                        154f, 193f, 0.0f,   // bottom right
                        154f, 251f, 0.0f }; // top right

                float obstacle326coords[] = {
                        124f, 233f, 0.0f,   // top left
                        124f, 211f, 0.0f,   // bottom left
                        139f, 211f, 0.0f,   // bottom right
                        139f, 233f, 0.0f }; // top right

                //BOTTOM LEFT GROUP
                float obstacle327coords[] = {
                        61f, 125f, 0.0f,   // top left
                        61f, 37f, 0.0f,   // bottom left
                        76f, 37f, 0.0f,   // bottom right
                        76f, 125f, 0.0f }; // top right

                float obstacle328coords[] = {
                        46f, 107f, 0.0f,   // top left
                        46f, 55f, 0.0f,   // bottom left
                        61f, 55f, 0.0f,   // bottom right
                        61f, 107f, 0.0f }; // top right

                float obstacle329coords[] = {
                        31f, 89f, 0.0f,   // top left
                        31f, 73f, 0.0f,   // bottom left
                        46f, 73f, 0.0f,   // bottom right
                        46f, 89f, 0.0f }; // top right

                //BOTTOM RIGHT GROUP
                float obstacle3210coords[] = {
                        124f, 125f, 0.0f,   // top left
                        124f, 37f, 0.0f,   // bottom left
                        139f, 37f, 0.0f,   // bottom right
                        139f, 125f, 0.0f }; // top right

                float obstacle3211coords[] = {
                        139f, 107f, 0.0f,   // top left
                        139f, 55f, 0.0f,   // bottom left
                        154f, 55f, 0.0f,   // bottom right
                        154f, 107f, 0.0f }; // top right

                float obstacle3212coords[] = {
                        154f, 89f, 0.0f,   // top left
                        154f, 73f, 0.0f,   // bottom left
                        169f, 73f, 0.0f,   // bottom right
                        169f, 89f, 0.0f }; // top right


                mObstacleCoords.add(obstacle321coords);
                mObstacleCoords.add(obstacle322coords);
                mObstacleCoords.add(obstacle323coords);
                mObstacleCoords.add(obstacle324coords);
                mObstacleCoords.add(obstacle325coords);
                mObstacleCoords.add(obstacle326coords);
                mObstacleCoords.add(obstacle327coords);
                mObstacleCoords.add(obstacle328coords);
                mObstacleCoords.add(obstacle329coords);
                mObstacleCoords.add(obstacle3210coords);
                mObstacleCoords.add(obstacle3211coords);
                mObstacleCoords.add(obstacle3212coords);

                //Targets
                float[] target321coords = CommonFunctions.createCircleCoords(162,280,8f);
                float[] target322coords = CommonFunctions.createCircleCoords(69f,20f,8f);
                //float[] target333coords = CommonFunctions.createCircleCoords(28,172,8f);

                mTargetCoords.add(target321coords);
                mTargetCoords.add(target322coords);
                // mTargetCoords.add(target333coords);

                ballInitialCoords = CommonFunctions.createCircleCoords(100, 150, GameState.ballRadius);

                //num of balls
                mNumOfBalls = 2;

                break;


            case 3:
                //Obstacles
                float obstacle331coords[] = {
                        80f, 140f, 0.0f,   // top left
                        80f, 95f, 0.0f,   // bottom left
                        130f, 95f, 0.0f,   // bottom right
                        130f, 105f, 0.0f }; // top right


                float obstacle332coords[] = {
                        0f,  170f, 0.0f,   // top left
                        0f, 0, 0.0f,   // bottom left
                        60f, 0f, 0.0f,   // bottom right
                        0f, 204f, 0.0f }; // top right

                float obstacle333coords[] = {
                        130f,  170f, 0.0f,   // top left
                        190f, 6f, 0.0f,   // bottom left
                        200f, 6f, 0.0f,   // bottom right
                        200f, 170f, 0.0f }; // top right

                MovePath path331 = new MovePath();
                path331.addMovement(new SingleMovement(new PointF(-.4f,0f), 170));
                path331.addMovement(new SingleMovement(new PointF(.4f, 0f), 170));

                MovePath path332 = new MovePath();
                path332.addMovement(new SingleMovement(new PointF(-0.2f,0f), 85));
                path332.addMovement(new SingleMovement(new PointF(0.2f, 0f), 85));

                mMovingObstacleCoords.add(obstacle331coords);
                mMovingObstacleCoords.add(obstacle332coords);

                mMovingObstaclePaths.add(path331);
                mMovingObstaclePaths.add(path332);

                mObstacleCoords.add(obstacle333coords);

                //Targets

                float[] target331coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target332coords = CommonFunctions.createCircleCoords(130,150,8f);

                mTargetCoords.add(target331coords);
                mTargetCoords.add(target332coords);

                //num of balls
                mNumOfBalls = 2;

                break;


            case 4:
                //Obstacles
                float obstacle341coords[] = {
                        40f, 0f, 0.0f,   // top left
                        40f, -10f, 0.0f,   // bottom left
                        75f, 20f, 0.0f,   // bottom right
                        75f, 30f, 0.0f }; // top right


                float obstacle342coords[] = {
                        125f,  60f, 0.0f,   // top left
                        125f, 50, 0.0f,   // bottom left
                        160f, 20f, 0.0f,   // bottom right
                        160f, 30f, 0.0f }; // top right

                float obstacle343coords[] = {
                        60f,  170f, 0.0f,   // top left
                        60f, 160f, 0.0f,   // bottom left
                        135f, 160f, 0.0f,   // bottom right
                        135f, 170f, 0.0f }; // top right

                MovePath path341 = new MovePath();
                path341.addMovement(new SingleMovement(new PointF(0f,0.8f), 550));
                path341.addMovement(new SingleMovement(new PointF(-1.0f, 0f), 100));
                path341.addMovement(new SingleMovement(new PointF(0f, -0.8f), 550));
                path341.addMovement(new SingleMovement(new PointF(1.0f, 0f), 100));

                MovePath path342 = new MovePath();
                path342.addMovement(new SingleMovement(new PointF(0f,0.8f), 512));
                path342.addMovement(new SingleMovement(new PointF(1.0f, 0f), 100));
                path342.addMovement(new SingleMovement(new PointF(0f, -0.8f), 550));
                path342.addMovement(new SingleMovement(new PointF(-1.0f, 0f), 100));
                path342.addMovement(new SingleMovement(new PointF(0f,0.8f), 38));

                mMovingObstacleCoords.add(obstacle341coords);
                mMovingObstacleCoords.add(obstacle342coords);

                mMovingObstaclePaths.add(path341);
                mMovingObstaclePaths.add(path342);

                mObstacleCoords.add(obstacle343coords);

                //Targets

                float[] target341coords = CommonFunctions.createCircleCoords(185, 260, 8f);
                float[] target342coords = CommonFunctions.createCircleCoords(10,260,8f);

                mTargetCoords.add(target341coords);
                mTargetCoords.add(target342coords);

                //num of balls
                mNumOfBalls = 3;


                //
                break;


            case 5:
                //Obstacles LEFT
                float obstacle351coords[] = {
                        6f, 60f, 0.0f,   // top left
                        6f, 42f, 0.0f,   //bottom left
                        70f, 42f, 0.0f,   //bottom right
                        65f, 50f, 0.0f }; //top right

                float obstacle352coords[] = {
                        6f, 95f, 0.0f,   // top left
                        6f, 77f, 0.0f,   //bottom left
                        48f, 77f, 0.0f,   //bottom right
                        45f, 85f, 0.0f }; //top right

                float obstacle353coords[] = {
                        6f, 122f, 0.0f,   // top left
                        6f, 112f, 0.0f,   //bottom left
                        34f, 112f, 0.0f,   //bottom right
                        33f, 120f, 0.0f }; //top right

                float obstacle354coords[] = {
                        6f, 155f, 0.0f,   // top left
                        6f, 147f, 0.0f,   //bottom left
                        22f, 147f, 0.0f,   //bottom right
                        22f, 155f, 0.0f }; //top right

                float obstacle355coords[] = {
                        6f, 190f, 0.0f,   // top left
                        6f, 180f, 0.0f,   //bottom left
                        33f, 182f, 0.0f,   //bottom right
                        34f, 190f, 0.0f }; //top right

                float obstacle356coords[] = {
                        6f, 225f, 0.0f,   // top left
                        6f, 207f, 0.0f,   //bottom left
                        45f, 217f, 0.0f,   //bottom right
                        48f, 225f, 0.0f }; //top right*/

                float obstacle357coords[] = {
                        6f, 260f, 0.0f,   // top left
                        6f, 242f, 0.0f,   //bottom left
                        65f, 252f, 0.0f,   //bottom right
                        70f, 260f, 0.0f }; //top right*/

                //Obstacles RIGHT
                float obstacle358coords[] = {
                        135f, 50f, 0.0f,   // top left
                        130f, 42f, 0.0f,   //bottom left
                        194f, 42f, 0.0f,   //bottom right
                        194f, 60f, 0.0f }; //top right

                float obstacle359coords[] = {
                        155f, 85f, 0.0f,   // top left
                        152f, 77f, 0.0f,   //bottom left
                        194f, 77f, 0.0f,   //bottom right
                        194f, 95f, 0.0f }; //top right

                float obstacle3510coords[] = {
                        167f, 120f, 0.0f,   // top left
                        166f, 112f, 0.0f,   //bottom left
                        194f, 112f, 0.0f,   //bottom right
                        194f, 122f, 0.0f }; //top right

                float obstacle3511coords[] = {
                        178f, 155f, 0.0f,   // top left
                        178f, 147f, 0.0f,   //bottom left
                        194f, 147f, 0.0f,   //bottom right
                        194f, 155f, 0.0f }; //top right

                float obstacle3512coords[] = {
                        166f, 190f, 0.0f,   // top left
                        167f, 182f, 0.0f,   //bottom left
                        194f, 180f, 0.0f,   //bottom right
                        194f, 190f, 0.0f }; //top right


                float obstacle3513coords[] = {
                        152f, 225f, 0.0f,   // top left
                        155f, 217f, 0.0f,   //bottom left
                        194f, 207f, 0.0f,   //bottom right
                        194f, 225f, 0.0f }; //top right*/

                float obstacle3514coords[] = {
                        135f, 260f, 0.0f,   // top left
                        140f, 252f, 0.0f,   //bottom left
                        194f, 242f, 0.0f,   //bottom right
                        194f, 260f, 0.0f }; //top right*/

                //OBSTACLE MIDDLE
                float obstacle3515coords[] = {
                        100, 166f, 0.0f,   // top
                        84f, 150f, 0.0f,   //left
                        100f, 134f, 0.0f,   //bottom
                        116f, 150f, 0.0f }; //right*/




                mObstacleCoords.add(obstacle351coords);
                mObstacleCoords.add(obstacle352coords);
                mObstacleCoords.add(obstacle353coords);
                mObstacleCoords.add(obstacle354coords);
                mObstacleCoords.add(obstacle355coords);
                mObstacleCoords.add(obstacle356coords);
                mObstacleCoords.add(obstacle357coords);

                mObstacleCoords.add(obstacle358coords);
                mObstacleCoords.add(obstacle359coords);
                mObstacleCoords.add(obstacle3510coords);
                mObstacleCoords.add(obstacle3511coords);
                mObstacleCoords.add(obstacle3512coords);
                mObstacleCoords.add(obstacle3513coords);
                mObstacleCoords.add(obstacle3514coords);

                mObstacleCoords.add(obstacle3515coords);

                //Targets

                float[] target351coords = CommonFunctions.createCircleCoords(160, 150, 8f);
                float[] target352coords = CommonFunctions.createCircleCoords(18,275,8f);

                mTargetCoords.add(target351coords);
                mTargetCoords.add(target352coords);

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

    public float[] getBallInitialCoords() {
        return ballInitialCoords;
    }



}
