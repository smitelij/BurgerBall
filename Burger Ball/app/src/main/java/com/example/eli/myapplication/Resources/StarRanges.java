package com.example.eli.myapplication.Resources;

/**
 * Created by Eli on 9/3/2016.
 */
public class StarRanges {

    private final int[][][] levelStarRange = new int[3][5][3];

    public StarRanges(){

        levelStarRange[0][0] = new int[] {50000,86000,88000};
        levelStarRange[0][1] = new int[] {50000,78000,91000};
        levelStarRange[0][2] = new int[] {65000,77000,82000};
        levelStarRange[0][3] = new int[] {60000,78000,79000};
        levelStarRange[0][4] = new int[] {25000,48000,54000};

        levelStarRange[1][0] = new int[] {20000,40000,60000};
        levelStarRange[1][1] = new int[] {40000,60000,70000};
        levelStarRange[1][2] = new int[] {40000,60000,71000};
        levelStarRange[1][3] = new int[] {30000,48000,78000};
        levelStarRange[1][4] = new int[] {40000,62000,67000};

        levelStarRange[2][0] = new int[] {66000,71000,71500};
        levelStarRange[2][1] = new int[] {50000,76000,80000};
        levelStarRange[2][2] = new int[] {60000,73000,75000};
        levelStarRange[2][3] = new int[] {30000,50000,60000};
        levelStarRange[2][4] = new int[] {66000,72000,76000};

    }

    public int[] getRange(int chapter, int level){
        chapter = chapter -1;
        level = level - 1;
        return levelStarRange[chapter][level];
    }
}
