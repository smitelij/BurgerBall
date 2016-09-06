package com.example.eli.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_select);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            case (R.id.set4button):
                setSelection = "4";
                intent.putExtra(SET_SELECT_MESSAGE, setSelection);
                break;

            case (R.id.set5button):
                setSelection = "5";
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




}

