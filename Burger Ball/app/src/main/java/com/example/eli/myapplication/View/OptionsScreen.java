package com.example.eli.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.example.eli.myapplication.Logic.Ball.MusicPlayerServiceConnection;
import com.example.eli.myapplication.Logic.HighScoreController;
import com.example.eli.myapplication.Logic.MediaPlayerService;
import com.example.eli.myapplication.Logic.OptionsController;
import com.example.eli.myapplication.R;

import java.util.HashMap;

public class OptionsScreen extends BackgroundMusicActivity {

    public final static String STORAGE_LOCATION = "options.txt";
    private Context context;
    private OptionsController optionsController;
    private HashMap<String,String> optionsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_options_screen);


        context = getApplicationContext();
        optionsController = new OptionsController(context,STORAGE_LOCATION);
        optionsMap = optionsController.getOptions();
        if (optionsMap.entrySet().size() == 0) {
            optionsController.resetOptions();
            optionsMap = optionsController.getOptions();
        }

        //Override set filter
        CheckBox overrideSetFilterCB = (CheckBox) findViewById(R.id.overridesetfilter);
        String overrideSetFilter = optionsMap.get("override_set_filter");
        overrideSetFilterCB.setChecked(overrideSetFilter.equals("1"));

        //mute music
        CheckBox muteMusicCB = (CheckBox) findViewById(R.id.mutemusic);
        String muteMusic = optionsMap.get("muteMusic");
        muteMusicCB.setChecked(muteMusic.equals("1"));

        //mute sound
        CheckBox muteSoundCB = (CheckBox) findViewById(R.id.mutesound);
        String muteSound = optionsMap.get("muteSound");
        muteSoundCB.setChecked(muteSound.equals("1"));


    }

    public void sendMessage(View view) {

        switch (view.getId()) {

            case (R.id.backButton):
                finish();
                break;
        }
    }

    @Override
    public void onDestroy() {

        updateOptionsMap();
        optionsController.updateOptions(optionsMap);

        super.onDestroy();
    }

    private void updateOptionsMap() {

        //Override set filter
        CheckBox overrideSetFilterCB = (CheckBox) findViewById(R.id.overridesetfilter);
        if (overrideSetFilterCB.isChecked()) {
            optionsMap.put("override_set_filter","1");
        } else {
            optionsMap.put("override_set_filter","0");
        }

        CheckBox muteMusicCB = (CheckBox) findViewById(R.id.mutemusic);
        String muteMusicPrevious = optionsMap.get("muteMusic");
        if (muteMusicCB.isChecked()) {
            if (muteMusicPrevious.equals("0")) {
                stopMusic();
            }
            optionsMap.put("muteMusic","1");
        } else {
            if (muteMusicPrevious.equals("1")) {
                restartMusic();
            }
            optionsMap.put("muteMusic","0");
        }

        CheckBox muteSoundCB = (CheckBox) findViewById(R.id.mutesound);
        if (muteSoundCB.isChecked()) {
            optionsMap.put("muteSound","1");
        } else {
            optionsMap.put("muteSound","0");
        }
    }
}
