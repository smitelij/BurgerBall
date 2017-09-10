package com.example.eli.myapplication.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.eli.myapplication.Controller.GameEngine;
import com.example.eli.myapplication.Logic.HighScoreController;
import com.example.eli.myapplication.Logic.OptionsController;
import com.example.eli.myapplication.R;
import com.example.eli.myapplication.Resources.StarRanges;

import java.util.HashMap;

public class MainActivity extends BackgroundMusicActivity implements View.OnFocusChangeListener{

    private Context context;
    public final static String LEVEL_MESSAGE = "com.example.eli.myapplication.LEVEL_MESSAGE";
    public final static int START_LEVEL = 1;
    public final static String STORAGE_LOCATION = "target.txt";
    public final String currentUser = "Eli";
    public String currentSet;
    public String currentLevel;
    private HashMap currentUserScores;
    private StarRanges starRangeData = new StarRanges();
    private HighScoreController highScoreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        Intent intent = getIntent();
        currentSet = intent.getStringExtra(SetSelect.SET_SELECT_MESSAGE);
        //loadSet(levelString);

        setContentView(R.layout.activity_main);

        //Load image
        //ImageView mainLogo = (ImageView) findViewById(R.id.mainLogo);
        //mainLogo.setImageResource(R.drawable.burgerball6);

        //Load chapter images
        ImageView chapterImage = (ImageView) findViewById(R.id.chapterImage);
        ImageView chapterTextureImage = (ImageView) findViewById(R.id.chapterTextureImage);

        if (currentSet.compareTo("1")==0){
            chapterImage.setImageResource(R.drawable.chapter1new);
            chapterTextureImage.setImageResource(R.drawable.chapter1previewtexture);
            setButtons(1);
            updateStarColors(1);
        } else if (currentSet.compareTo("2")==0){
            chapterImage.setImageResource(R.drawable.chapter2new);
            chapterTextureImage.setImageResource(R.drawable.chapter2previewtexture);
            setButtons(2);
            updateStarColors(2);
        } else if (currentSet.compareTo("3")==0){
            chapterImage.setImageResource(R.drawable.chapter3new);
            chapterTextureImage.setImageResource(R.drawable.chapter3previewtexture);
            setButtons(3);
            updateStarColors(3);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.LTGRAY);
        toolbar.setLogo(R.drawable.burgericon);

        initializeButtons();

        highScoreController = new HighScoreController(context,STORAGE_LOCATION);
        currentUserScores = highScoreController.getUserScores(currentUser);
        updateScoreDisplays();
        updateStarsEarned();
    }

    @TargetApi(16)
    private void setButtons(int chapter) {
        Button level1button = (Button) findViewById(R.id.level1button);
        Button level2button = (Button) findViewById(R.id.level2button);
        Button level3button = (Button) findViewById(R.id.level3button);
        Button level4button = (Button) findViewById(R.id.level4button);
        Button level5button = (Button) findViewById(R.id.level5button);

        Drawable button1 = null;
        Drawable button2 = null;
        Drawable button3 = null;
        Drawable button4 = null;
        Drawable button5 = null;

        switch (chapter) {
            case 1:
                button1 = ContextCompat.getDrawable(context,R.drawable.button1);
                button2 = ContextCompat.getDrawable(context,R.drawable.button1);
                button3 = ContextCompat.getDrawable(context,R.drawable.button1);
                button4 = ContextCompat.getDrawable(context,R.drawable.button1);
                button5 = ContextCompat.getDrawable(context,R.drawable.button1);
                break;

            case 2:
                button1 = ContextCompat.getDrawable(context,R.drawable.button2);
                button2 = ContextCompat.getDrawable(context,R.drawable.button2);
                button3 = ContextCompat.getDrawable(context,R.drawable.button2);
                button4 = ContextCompat.getDrawable(context,R.drawable.button2);
                button5 = ContextCompat.getDrawable(context,R.drawable.button2);
                break;

            case 3:
                button1 = ContextCompat.getDrawable(context,R.drawable.button3);
                button2 = ContextCompat.getDrawable(context,R.drawable.button3);
                button3 = ContextCompat.getDrawable(context,R.drawable.button3);
                button4 = ContextCompat.getDrawable(context,R.drawable.button3);
                button5 = ContextCompat.getDrawable(context,R.drawable.button3);
                break;
        }

        level1button.setBackground(button1);
        level2button.setBackground(button2);
        level3button.setBackground(button3);
        level4button.setBackground(button4);
        level5button.setBackground(button5);
    }

    private void updateStarColors(int chapter) {
        RatingBar ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        RatingBar ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        RatingBar ratingBar4 = (RatingBar) findViewById(R.id.ratingBar4);
        RatingBar ratingBar5 = (RatingBar) findViewById(R.id.ratingBar5);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //setTitle(" ");

        return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button1, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_scores) {
            highScoreController.clearScores();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        handleChangeEvent(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int finalScore = 0;

        if (requestCode == START_LEVEL) {

            if (resultCode == RESULT_OK){
                if (data.hasExtra(GameEngine.END_LEVEL_SCORE)){
                    finalScore = data.getExtras().getInt(GameEngine.END_LEVEL_SCORE);
                }
                String currentLevelHighScoreString = (String) currentUserScores.get(currentLevel);
                int currentLevelHighScore = Integer.parseInt(currentLevelHighScoreString);
                if (finalScore > currentLevelHighScore){
                    String newHighScore = Integer.toString(finalScore);
                    currentUserScores.put(currentLevel, newHighScore);
                    updateScoreDisplays();
                    updateStarsEarned();
                    highScoreController.updateHighScoreFile(currentUserScores);
                }
            }
        }

        //bindMPservice();

    }


    private void updateScoreDisplays(){
        String score1 = (String) currentUserScores.get(currentSet + ".1");
        String score2 = (String) currentUserScores.get(currentSet + ".2");
        String score3 = (String) currentUserScores.get(currentSet + ".3");
        String score4 = (String) currentUserScores.get(currentSet + ".4");
        String score5 = (String) currentUserScores.get(currentSet + ".5");

        if (!(score1.equals("0"))){
            TextView scoreLabel1 = (TextView) findViewById(R.id.Level1ScoreText);
            scoreLabel1.setText(score1);
            scoreLabel1.setVisibility(View.VISIBLE);
        }
        if (!(score2.equals("0"))){
            TextView scoreLabel2 = (TextView) findViewById(R.id.Level2ScoreText);
            scoreLabel2.setText(score2);
            scoreLabel2.setVisibility(View.VISIBLE);
        }
        if (!(score3.equals("0"))){
            TextView scoreLabel3 = (TextView) findViewById(R.id.Level3ScoreText);
            scoreLabel3.setText(score3);
            scoreLabel3.setVisibility(View.VISIBLE);
        }
        if (!(score4.equals("0"))){
            TextView scoreLabel4 = (TextView) findViewById(R.id.Level4ScoreText);
            scoreLabel4.setText(score4);
            scoreLabel4.setVisibility(View.VISIBLE);
        }
        if (!(score5.equals("0"))){
            TextView scoreLabel5 = (TextView) findViewById(R.id.Level5ScoreText);
            scoreLabel5.setText(score5);
            scoreLabel5.setVisibility(View.VISIBLE);
        }
    }


    private void updateStarsEarned(){
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        int[] levelRange = starRangeData.getRange(1);
        String scoreString = (String) currentUserScores.get(currentSet + ".1");
        int score = Integer.parseInt(scoreString);
        int rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        levelRange = starRangeData.getRange(2);
        scoreString = (String) currentUserScores.get(currentSet + ".2");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar3);
        levelRange = starRangeData.getRange(3);
        scoreString = (String) currentUserScores.get(currentSet + ".3");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar4);
        levelRange = starRangeData.getRange(4);
        scoreString = (String) currentUserScores.get(currentSet + ".4");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar5);
        levelRange = starRangeData.getRange(5);
        scoreString = (String) currentUserScores.get(currentSet + ".5");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating); // ?? wtf?

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

    private void initializeButtons() {
        Button button1 = (Button) findViewById(R.id.level1button);
        button1.requestFocus();
        button1.setOnFocusChangeListener(this);
        Button button2 = (Button) findViewById(R.id.level2button);
        button2.setOnFocusChangeListener(this);
        Button button3 = (Button) findViewById(R.id.level3button);
        button3.setOnFocusChangeListener(this);
        Button button4 = (Button) findViewById(R.id.level4button);
        button4.setOnFocusChangeListener(this);
        Button button5 = (Button) findViewById(R.id.level5button);
        button5.setOnFocusChangeListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (!hasFocus) {
            return;
        }

        handleChangeEvent(v);
    }

    private void handleChangeEvent(View view) {
        Intent intent = new Intent(this, OpenGLES20Activity.class);

        switch (view.getId()){

            case (R.id.level1button):
                currentLevel = currentSet + ".1";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level2button):
                currentLevel = currentSet + ".2";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level3button):
                currentLevel = currentSet + ".3";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level4button):
                currentLevel = currentSet + ".4";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level5button):
                currentLevel = currentSet + ".5";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

        }

        boolean muteSound = shouldMuteSound();
        intent.putExtra("muteSound", muteSound);
        startActivityForResult(intent, START_LEVEL);
    }

    private boolean shouldMuteSound() {
        OptionsController optionsController = new OptionsController(this, OPTIONS_STORAGE_LOCATION);
        boolean shouldMuteSound = optionsController.getOption("muteSound");
        return shouldMuteSound;
    }

}

