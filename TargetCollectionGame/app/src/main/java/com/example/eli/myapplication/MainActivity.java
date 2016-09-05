package com.example.eli.myapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Context context;
    public final static String LEVEL_MESSAGE = "com.example.eli.myapplication.LEVEL_MESSAGE";
    public final static int START_LEVEL = 1;
    public final static String STORAGE_LOCATION = "target.txt";
    public final String currentUser = "Eli";
    public String currentLevel;
    private HashMap currentUserScores;
    private StarRanges starRangeData = new StarRanges();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        context = getApplicationContext();
        currentUserScores = HighScoreFileParse(context, currentUser);
        updateScoreDisplays();
        updateStarsEarned();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle(" ");

        return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_scores) {
            clearScores();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, OpenGLES20Activity.class);

        switch (view.getId()){

            case (R.id.level1):
                currentLevel = "1.1";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level2):
                currentLevel = "1.2";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level3):
                currentLevel = "1.3";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level4):
                currentLevel = "1.4";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

            case (R.id.level5):
                currentLevel = "1.5";
                intent.putExtra(LEVEL_MESSAGE, currentLevel);
                break;

        }

        startActivityForResult(intent, START_LEVEL);

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
                    updateHighScoreFile();
                }
            }
        }

    }

    //TODO put this in a separate file for better for better cohesion
    public HashMap<String, String> HighScoreFileParse(Context context, String currentUser){

        System.out.println("high score file parse.");

        HashMap highScores = new HashMap();

        try {

            FileInputStream fis = context.openFileInput(STORAGE_LOCATION);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String result = sb.toString();

            System.out.println("RESULT: " + result);

            String[] allUserScores = result.split("\\*");

            for (String currentUserScore : allUserScores){

                System.out.println("currentUserScore: " + currentUserScore);

                if (currentUserScore.startsWith(currentUser)){
                    String[] allLevelScores = currentUserScore.split("\\&");

                    for (String currentLevel : allLevelScores){
                        System.out.println("currentLevel: " + currentLevel);

                        if (currentLevel.startsWith("1.1")){
                            highScores.put("1.1",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.2")) {
                            highScores.put("1.2", currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.3")) {
                            highScores.put("1.3",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.4")) {
                            highScores.put("1.4",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.5")) {
                            highScores.put("1.5",currentLevel.substring(4));
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return highScores;

    }

    private void updateScoreDisplays(){
        String score1 = (String) currentUserScores.get("1.1");
        String score2 = (String) currentUserScores.get("1.2");
        String score3 = (String) currentUserScores.get("1.3");
        String score4 = (String) currentUserScores.get("1.4");
        String score5 = (String) currentUserScores.get("1.5");

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

    //TODO put this in a separate file for better cohesion
    private void updateHighScoreFile(){

        String string = "*Eli";
        String string1 = "&1.1:" + currentUserScores.get("1.1");
        String string2 = "&1.2:" + currentUserScores.get("1.2");
        String string3 = "&1.3:" + currentUserScores.get("1.3");
        String string4 = "&1.4:" + currentUserScores.get("1.4");
        String string5 = "&1.5:" + currentUserScores.get("1.5");

        //String string1 = "&1.1:" + "0";
        //String string2 = "&1.2:" + "0";
        //String string3 = "&1.3:" + "0";
        //String string4 = "&1.4:" + "0";
        //String string5 = "&1.5:" + "0";

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(STORAGE_LOCATION, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.write(string1.getBytes());
            outputStream.write(string2.getBytes());
            outputStream.write(string3.getBytes());
            outputStream.write(string4.getBytes());
            outputStream.write(string5.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStarsEarned(){
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        int[] levelRange = starRangeData.getRange(1);
        String scoreString = (String) currentUserScores.get("1.1");
        int score = Integer.parseInt(scoreString);
        int rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        levelRange = starRangeData.getRange(2);
        scoreString = (String) currentUserScores.get("1.2");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar3);
        levelRange = starRangeData.getRange(3);
        scoreString = (String) currentUserScores.get("1.3");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar4);
        levelRange = starRangeData.getRange(4);
        scoreString = (String) currentUserScores.get("1.4");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar5);
        levelRange = starRangeData.getRange(5);
        scoreString = (String) currentUserScores.get("1.5");
        score = Integer.parseInt(scoreString);
        rating = compareScoreToRange(score, levelRange);
        ratingBar.setRating(rating);

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

    private void clearScores(){


        String string = "*Eli";

        String string1 = "&1.1:" + "0";
        String string2 = "&1.2:" + "0";
        String string3 = "&1.3:" + "0";
        String string4 = "&1.4:" + "0";
        String string5 = "&1.5:" + "0";

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(STORAGE_LOCATION, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.write(string1.getBytes());
            outputStream.write(string2.getBytes());
            outputStream.write(string3.getBytes());
            outputStream.write(string4.getBytes());
            outputStream.write(string5.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

