package com.example.eli.myapplication.Resources;

/**
 * Created by Eli on 9/3/2016.
 */
public class StarRanges {

    private final int[][] levelStarRange = new int[5][3];

    public StarRanges(){

        levelStarRange[0] = new int[] {50000,86000,88000};
        levelStarRange[1] = new int[] {50000,67000,80000};
        levelStarRange[2] = new int[] {40000,60000,71000};
        levelStarRange[3] = new int[] {65000,77000,82000};
        levelStarRange[4] = new int[] {30000,48000,78000};

    }

    public int[] getRange(int level){
        level = level - 1;
        return levelStarRange[level];
    }
}
