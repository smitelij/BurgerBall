package com.example.eli.myapplication;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public final static String LEVEL_MESSAGE = "com.example.eli.myapplication.LEVEL_MESSAGE";
    public final static int START_LEVEL = 1;

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
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, OpenGLES20Activity.class);

        //default
        String levelMsg = "1.1";

        switch (view.getId()){

            case (R.id.level1):
                levelMsg = "1.1";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                break;

            case (R.id.level2):
                levelMsg = "1.2";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                break;

            case (R.id.level3):
                levelMsg = "1.3";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                break;

            case (R.id.level4):
                levelMsg = "1.4";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                break;

            case (R.id.level5):
                levelMsg = "1.5";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
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
                System.out.println("final score1: " + finalScore);
            }
        }

        System.out.println("final score2: " + finalScore);
        TextView scoreLabel = (TextView) findViewById(R.id.Level1ScoreText);
        scoreLabel.setText(finalScore + "");
        scoreLabel.setVisibility(View.VISIBLE);
    }


}

