package com.example.eli.myapplication.View;

/**
 * Created by Eli on 9/5/2016.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.example.eli.myapplication.Logic.Ball.MusicPlayerServiceConnection;
import com.example.eli.myapplication.Logic.MediaPlayerService;
import com.example.eli.myapplication.Resources.StarRanges;
import com.example.eli.myapplication.R;

import java.util.HashMap;




public class StartScreen extends ToolbarActivity {


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

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.LTGRAY);
        toolbar.setLogo(R.drawable.burgericon);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.overflow);
        toolbar.setOverflowIcon(drawable);

        //Add click listener
        TableLayout startScreen = (TableLayout) findViewById(R.id.startScreenLayout);
        startScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetSelect.class);
                startActivity(intent);
            }

        });

    }




}

