package com.example.eli.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eli.myapplication.Model.HighScoreController;
import com.example.eli.myapplication.R;
import com.example.eli.myapplication.Model.StarRanges;

import java.util.HashMap;

/**
 * Created by Eli on 9/5/2016.
 */
public class SetSelect extends AppCompatActivity{


/**
 * Created by Eli on 9/5/2016.
 */



    private Context context;
    public final static String SET_SELECT_MESSAGE = "com.example.eli.myapplication.SET_SELECT_MESSAGE";
    public final static String STORAGE_LOCATION = "target.txt";
    public final String currentUser = "Eli";
    public String setSelection;
    private HashMap currentUserScores;
    private StarRanges starRangeData = new StarRanges();
    private HighScoreController highScoreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_select);

        ImageView mainLogo = (ImageView) findViewById(R.id.mainLogo);
        mainLogo.setImageResource(R.drawable.chapterselect);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.LTGRAY);
        toolbar.setLogo(R.drawable.burgericon);

        context = getApplicationContext();
        highScoreController = new HighScoreController(context,STORAGE_LOCATION);
        currentUserScores = highScoreController.getUserScores(currentUser);
        int totalStarsChapter1 = countTotalStars(1);
        int totalStarsChapter2 = countTotalStars(2);
        int totalStarsChapter3 = countTotalStars(3);

        TextView totalStarsChapter1text = (TextView) findViewById(R.id.totalStars1Text);
        TextView totalStarsChapter2text = (TextView) findViewById(R.id.totalStars2Text);
        TextView totalStarsChapter3text = (TextView) findViewById(R.id.totalStars3Text);


        if (totalStarsChapter1 > 0){
            totalStarsChapter1text.setText("Total stars: x" + totalStarsChapter1);
        } else {
            totalStarsChapter1text.setVisibility(View.INVISIBLE);
        }

        if (totalStarsChapter2 > 0){
            totalStarsChapter2text.setText("Total stars: x" + totalStarsChapter2);
        } else {
            if (totalStarsChapter1 < 12) {
                totalStarsChapter2text.setText("12 stars needed in chapter 1 to unlock");
            } else {
                totalStarsChapter2text.setVisibility(View.INVISIBLE);
            }
        }

        if (totalStarsChapter3 > 0){
            totalStarsChapter3text.setText("Total stars: x" + totalStarsChapter3);
        } else if ((totalStarsChapter2 > 0) && (totalStarsChapter2 < 12)){
            totalStarsChapter3text.setText("12 stars needed in chapter 2 to unlock");
        } else {
            totalStarsChapter3text.setVisibility(View.INVISIBLE);
        }



        context = getApplicationContext();

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
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_scores) {
            //clearScores();
        }

        return super.onOptionsItemSelected(item);
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

        int[] levelRange = starRangeData.getRange(1);
        String scoreString = (String) currentUserScores.get(chapter + ".1");
        int score = Integer.parseInt(scoreString);
        totalStars = compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(2);
        scoreString = (String) currentUserScores.get(chapter + ".2");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(3);
        scoreString = (String) currentUserScores.get(chapter + ".3");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(4);
        scoreString = (String) currentUserScores.get(chapter + ".4");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        levelRange = starRangeData.getRange(5);
        scoreString = (String) currentUserScores.get(chapter + ".5");
        score = Integer.parseInt(scoreString);
        totalStars = totalStars + compareScoreToRange(score, levelRange);

        return totalStars;
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

