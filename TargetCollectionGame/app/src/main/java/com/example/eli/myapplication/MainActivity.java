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

public class MainActivity extends AppCompatActivity {

    public final static String LEVEL_MESSAGE = "com.example.eli.myapplication.LEVEL_MESSAGE";

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
        String levelMsg;

        switch (view.getId()){

            case (R.id.level1):
                levelMsg = "level1";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                startActivity(intent);
                break;

            case (R.id.level2):
                levelMsg = "level2";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                startActivity(intent);
                break;

            case (R.id.level3):
                levelMsg = "level3";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                startActivity(intent);
                break;

            case (R.id.level4):
                levelMsg = "level4";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                startActivity(intent);
                break;

            case (R.id.level5):
                levelMsg = "level5";
                intent.putExtra(LEVEL_MESSAGE, levelMsg);
                startActivity(intent);
                break;

        }
    }

}

