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

        CheckBox overrideSetFilterCB = (CheckBox) findViewById(R.id.overridesetfilter);

        String overrideSetFilter = optionsMap.get("override_set_filter");
        overrideSetFilterCB.setChecked(overrideSetFilter.equals("1"));
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
        CheckBox overrideSetFilterCB = (CheckBox) findViewById(R.id.overridesetfilter);
        if (overrideSetFilterCB.isChecked()) {
            optionsMap.put("override_set_filter","1");
        } else {
            optionsMap.put("override_set_filter","0");
        }
    }
}
