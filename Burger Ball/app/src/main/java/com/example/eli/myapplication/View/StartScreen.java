package com.example.eli.myapplication.View;

/**
 * Created by Eli on 9/5/2016.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.eli.myapplication.Logic.Ball.MusicPlayerServiceConnection;
import com.example.eli.myapplication.Logic.MediaPlayerService;
import com.example.eli.myapplication.Resources.StarRanges;
import com.example.eli.myapplication.R;

import java.util.HashMap;




public class StartScreen extends BackgroundMusicActivity {

    /*
    public final static String START_SCREEN_MESSAGE = "com.example.eli.myapplication.START_SCREEN_MESSAGE";
    public final static String STORAGE_LOCATION = "target.txt";
    public final String currentUser = "Eli";
    public String currentLevel;
    private HashMap currentUserScores;
    private StarRanges starRangeData = new StarRanges();
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Attach to layout
        setContentView(R.layout.start_screen);

        //Load image
        ImageView mainLogo = (ImageView) findViewById(R.id.mainLogo);
        mainLogo.setImageResource(R.drawable.burgerball6);

        //Load image
        ImageView burgerLogo = (ImageView) findViewById(R.id.burgerLogo);
        burgerLogo.setImageResource(R.drawable.burgersmall);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.LTGRAY);
        toolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
            //clearScores();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        //Intent intent = new Intent(this, OpenGLES20Activity.class);

        System.out.println("sending message........");

        switch (view.getId()) {

            case (R.id.playGameButton):
                Intent intent = new Intent(this, SetSelect.class);
                startActivity(intent);
                break;

            case (R.id.optionsButton):
                Intent optionIntent = new Intent(this, OptionsScreen.class);
                startActivity(optionIntent);
                break;
        }

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
*/



}

