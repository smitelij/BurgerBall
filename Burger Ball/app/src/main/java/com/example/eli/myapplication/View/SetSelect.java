package com.example.eli.myapplication.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eli.myapplication.Logic.Ball.MusicPlayerServiceConnection;
import com.example.eli.myapplication.Logic.HighScoreController;
import com.example.eli.myapplication.Logic.MediaPlayerService;
import com.example.eli.myapplication.Logic.OptionsController;
import com.example.eli.myapplication.R;
import com.example.eli.myapplication.Resources.StarRanges;

import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by Eli on 9/5/2016.
 */
public class SetSelect extends ToolbarActivity {


/**
 * Created by Eli on 9/5/2016.
 */



    private Context context;
    public final static String SET_SELECT_MESSAGE = "com.example.eli.myapplication.SET_SELECT_MESSAGE";
    public final static String HISCORE_STORAGE_LOCATION = "target.txt";
    public final String currentUser = "Eli";
    public String setSelection;
    private HashMap currentUserScores;
    private StarRanges starRangeData = new StarRanges();
    private HighScoreController highScoreController;
    private OptionsController optionsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_select);

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.LTGRAY);
        toolbar.setLogo(R.drawable.burgericon);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.overflow);
        toolbar.setOverflowIcon(drawable);


        //setButtons();

        loadScoresAndUpdateDisplays();
    }
/*
    @TargetApi(16)
    private void setButtons() {

        Button set1button = (Button) findViewById(R.id.set1button);
        Button set2button = (Button) findViewById(R.id.set2button);
        Button set3button = (Button) findViewById(R.id.set3button);

        Drawable button1 = null;
        Drawable button2 = null;
        Drawable button3 = null;

        button1 = ContextCompat.getDrawable(context,R.drawable.buttonset);
        button2 = ContextCompat.getDrawable(context,R.drawable.buttonset);
        button3 = ContextCompat.getDrawable(context,R.drawable.buttonset);

        set1button.setBackground(button1);
        set2button.setBackground(button2);
        set3button.setBackground(button3);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        loadScoresAndUpdateDisplays();
    }

    @TargetApi(16)
    private void loadScoresAndUpdateDisplays() {
        context = getApplicationContext();
        highScoreController = new HighScoreController(context, HISCORE_STORAGE_LOCATION);
        currentUserScores = highScoreController.getUserScores(currentUser);

        optionsController = new OptionsController(context, OPTIONS_STORAGE_LOCATION);
        boolean isSetOverrideEnabled = optionsController.getOption("override_set_filter");

        int totalStarsChapter1 = countTotalStars(1);
        int totalStarsChapter2 = countTotalStars(2);
        int totalStarsChapter3 = countTotalStars(3);

        int totalScore = countTotalScore();
        String totalScoreFormatted = NumberFormat.getIntegerInstance().format(totalScore);

        TextView totalStarsChapter1text = (TextView) findViewById(R.id.totalStars1Text);
        TextView totalStarsChapter2text = (TextView) findViewById(R.id.totalStars2Text);
        TextView totalStarsChapter3text = (TextView) findViewById(R.id.totalStars3Text);
        TextView totalStarsTotalText = (TextView) findViewById(R.id.totalStarsTotalText);
        TextView totalStarsTotal = (TextView) findViewById(R.id.totalStarsTotal);
        TextView totalScoreTotal = (TextView) findViewById(R.id.totalScoreTotal);

        TextView set2locked = (TextView) findViewById(R.id.set2locked);
        TextView set3locked = (TextView) findViewById(R.id.set3locked);

        ImageView blueStarImage = (ImageView) findViewById(R.id.bluestarimage);
        ImageView redStarImage = (ImageView) findViewById(R.id.redstarimage);
        ImageView greenStarImage = (ImageView) findViewById(R.id.greenstarimage);
        ImageView blackStarImage = (ImageView) findViewById(R.id.blackstarimage);

        Button set2button = (Button) findViewById(R.id.set2button);
        Button set3button = (Button) findViewById(R.id.set3button);

        Drawable button2 = null;
        Drawable button3 = null;

        button2 = ContextCompat.getDrawable(context,R.drawable.buttonset);
        button3 = ContextCompat.getDrawable(context,R.drawable.buttonset);

        int lockedSetBackground = Color.parseColor("#21181d");
        int lockedSetText = Color.parseColor("#5B5B5B");

        //---------------------------
        // SET 1
        //
        if (totalStarsChapter1 > 0){
            redStarImage.setVisibility(View.VISIBLE);
            totalStarsChapter1text.setVisibility(View.VISIBLE);
            totalStarsChapter1text.setText("= " + totalStarsChapter1);
        } else {
            redStarImage.setVisibility(View.INVISIBLE);
            totalStarsChapter1text.setVisibility(View.INVISIBLE);
        }

        //------------------------
        // SET 2
        //
        //If set 2 is UNLOCKED
        if (totalStarsChapter1 >= 12 || isSetOverrideEnabled) {
            set2button.setTextColor(Color.WHITE);
            set2button.setBackground(button2);
            set2button.setClickable(true);
            set2locked.setVisibility(View.INVISIBLE);

            //If stars have been earned
            if (totalStarsChapter2 > 0) {
                greenStarImage.setVisibility(View.VISIBLE);
                totalStarsChapter2text.setVisibility(View.VISIBLE);
                totalStarsChapter2text.setText("= " + totalStarsChapter2);
            } else {
                greenStarImage.setVisibility(View.INVISIBLE);
                totalStarsChapter2text.setVisibility(View.INVISIBLE);
            }

            //If set 2 is LOCKED
        } else {
            greenStarImage.setVisibility(View.GONE);
            totalStarsChapter2text.setVisibility(View.GONE);
            set2locked.setVisibility(View.VISIBLE);
            set2button.setTextColor(lockedSetText);
            set2button.setBackgroundColor(lockedSetBackground);
            set2button.setClickable(false);
        }

        //----------------------
        // SET 3
        //
        //If set 3 is UNLOCKED
        if (totalStarsChapter2 >= 12 || isSetOverrideEnabled) {
            set3button.setTextColor(Color.WHITE);
            set3button.setBackground(button3);
            set3button.setClickable(true);
            set3locked.setVisibility(View.INVISIBLE);

            //If stars have been earned
            if (totalStarsChapter3 > 0) {
                blueStarImage.setVisibility(View.VISIBLE);
                totalStarsChapter3text.setVisibility(View.VISIBLE);
                totalStarsChapter3text.setText("= " + totalStarsChapter3);
            } else {
                blueStarImage.setVisibility(View.INVISIBLE);
                totalStarsChapter3text.setVisibility(View.INVISIBLE);
            }

            //If set 3 is LOCKED
        } else {
            set3locked.setVisibility(View.VISIBLE);
            totalStarsChapter3text.setVisibility(View.GONE);
            blueStarImage.setVisibility(View.GONE);
            set3button.setBackgroundColor(lockedSetBackground);
            set3button.setTextColor(lockedSetText);
            set3button.setClickable(false);
        }

        //ALL
        if (totalStarsChapter1 > 0) {
            totalStarsTotalText.setVisibility(View.VISIBLE);
            totalStarsTotal.setVisibility(View.VISIBLE);
            blackStarImage.setVisibility(View.VISIBLE);
            totalScoreTotal.setVisibility(View.VISIBLE);
            totalStarsTotal.setText("= " + (totalStarsChapter1 + totalStarsChapter2 + totalStarsChapter3));
            totalScoreTotal.setText(totalScoreFormatted);
        } else {
            totalStarsTotalText.setVisibility(View.GONE);
            totalStarsTotal.setVisibility(View.GONE);
            blackStarImage.setVisibility(View.GONE);
            totalScoreTotal.setVisibility(View.GONE);
        }


        context = getApplicationContext();
    }

    public void sendMessage(View view) {
        //Intent intent = new Intent(this, OpenGLES20Activity.class);

        System.out.println("sending message...");

        Intent intent = new Intent(this, MainActivity.class);

        switch (view.getId()){

            case (R.id.set1button):
                setSelection = "1";
                intent.putExtra(SET_SELECT_MESSAGE, setSelection);
                break;

            case (R.id.set2button):
                setSelection = "2";
                intent.putExtra(SET_SELECT_MESSAGE, setSelection);
                break;

            case (R.id.set3button):
                setSelection = "3";
                intent.putExtra(SET_SELECT_MESSAGE, setSelection);
                break;

        }

        System.out.println("TRYING TO START MAIN ACTIVITY");

        startActivity(intent);

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int finalScore = 0;

        if (requestCode == START_LEVEL) {

            if (resultCode == RESULT_OK){
                if (data.hasExtra(GameEngine.END_LEVEL_SCORE)){
                    finalScore = data.getExtras().getInt(GameEngine.END_LEVEL_SCORE);
                }
                String currentLevelHighScoreString = (String) currentUserScores.get(setSelection);
                int currentLevelHighScore = Integer.parseInt(currentLevelHighScoreString);
                if (finalScore > currentLevelHighScore){
                    String newHighScore = Integer.toString(finalScore);
                    currentUserScores.put(setSelection, newHighScore);
                    updateScoreDisplays();
                    updateStarsEarned();
                    updateHighScoreFile();
                }
            }
        }

    }
    */

    public int countTotalStars(int chapter){
        int totalStars;

        int[] levelRange = starRangeData.getRange(chapter, 1);
        String scoreString = (String) currentUserScores.get(chapter + ".1");
        int score = Integer.parseInt(scoreString);
        totalStars = compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(chapter, 2);
        scoreString = (String) currentUserScores.get(chapter + ".2");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(chapter, 3);
        scoreString = (String) currentUserScores.get(chapter + ".3");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(chapter, 4);
        scoreString = (String) currentUserScores.get(chapter + ".4");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(chapter, 5);
        scoreString = (String) currentUserScores.get(chapter + ".5");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        return totalStars;
    }

    public int countTotalScore() {
        int totalScore = 0;
        int currentScore = 0;


        for (int i = 1; i <= 3; i++) {

            String scoreString = (String) currentUserScores.get(i + ".1");
            currentScore = Integer.parseInt(scoreString);
            totalScore+= currentScore;

            scoreString = (String) currentUserScores.get(i + ".2");
            currentScore = Integer.parseInt(scoreString);
            totalScore+= currentScore;

            scoreString = (String) currentUserScores.get(i + ".3");
            currentScore = Integer.parseInt(scoreString);
            totalScore+= currentScore;

            scoreString = (String) currentUserScores.get(i + ".4");
            currentScore = Integer.parseInt(scoreString);
            totalScore+= currentScore;

            scoreString = (String) currentUserScores.get(i + ".5");
            currentScore = Integer.parseInt(scoreString);
            totalScore+= currentScore;
        }
        return totalScore;
    }

    private int compareScoreToRange(int score, int[] range){
        int rating = 0;

        if (score>0){
            rating=1;
        } if (score > range[0]){
            rating = 2;
        } if (score > range[1]){
            rating = 3;
        } if (score > range[2]){
            rating = 4;
        }

        return rating;
    }


}

