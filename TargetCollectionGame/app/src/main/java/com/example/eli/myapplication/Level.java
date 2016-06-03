package com.example.eli.myapplication;

import java.util.ArrayList;

/**
 * Created by Eli on 6/2/2016.
 */
public class Level {

    ArrayList<Polygon> mObstacles = new ArrayList<>();
    ArrayList<Target> mTargets = new ArrayList<>();
    int mNumOfBalls;
    int levelIndex;

    public Level(int index){
        levelIndex = index;
        loadLevel();
    }

    private void loadLevel(){
        switch (levelIndex){

            //1
            case 1:
                //Obstacles
                float obstacle1coords[] = {
                    80f, 150f, 0.0f,   // top left
                    80f, 100f, 0.0f,   // bottom left
                    100f, 100f, 0.0f,   // bottom right
                    100f, 150f, 0.0f }; // top right


                float obstacle2coords[] = {
                    140f,  170f, 0.0f,   // top left
                    140f, 120f, 0.0f,   // bottom left
                    160f, 120f, 0.0f,   // bottom right
                    160f, 170f, 0.0f }; // top right

                int obstacleTexture = GameEngine.loadGLTexture(GameState.TEXTURE_WALL);

                Polygon obstacle1 = new Polygon(obstacle1coords, GameState.OBSTACLE_POLYGON, obstacleTexture);
                Polygon obstacle2 = new Polygon(obstacle2coords, GameState.OBSTACLE_POLYGON, obstacleTexture);

                mObstacles.add(obstacle1);
                mObstacles.add(obstacle2);

                //Targets
                int targetTexture = GameEngine.loadGLTexture(GameState.TEXTURE_TARGET);
                Target target1 = new Target(GameState.createCircleCoords(90,160,8f),8f, targetTexture);
                Target target2 = new Target(GameState.createCircleCoords(150,180,8f),8f, targetTexture);

                mTargets.add(target1);
                mTargets.add(target2);

                //num of balls
                mNumOfBalls = 3;

        }


    }

    public ArrayList<Polygon> getObstacles(){
        return mObstacles;
    }

    public ArrayList<Target> getTargets(){
        return mTargets;
    }

    public int getNumOfBalls(){
        return mNumOfBalls;
    }


}
