package com.example.eli.myapplication.Logic;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eli on 8/19/2016.
 */
public class OptionsController {

    String storageLocation;
    Context context;

    public OptionsController(Context context, String storageLocation){
        this.storageLocation = storageLocation;
        this.context = context;
    }

    public HashMap<String, String> getOptions(){

        System.out.println("options file parse.");

        HashMap optionMap = new HashMap();

        //defaults
        optionMap.put("override_set_filter","0");
        optionMap.put("muteMusic","0");
        optionMap.put("muteSound","0");

        try {

            FileInputStream fis = context.openFileInput(storageLocation);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String result = sb.toString();

            System.out.println("RESULT: " + result);

            String[] allOptions = result.split("\\&");

            for (int index = 1; index < allOptions.length; index++ ) {

                String currentOption = allOptions[index];

                int separatorIndex = currentOption.indexOf("=");
                String optionTitle = currentOption.substring(0,separatorIndex);
                String optionValue = currentOption.substring(separatorIndex + 1);
                System.out.println("option title:" + optionTitle);
                System.out.println("option value:" + optionValue);

                optionMap.put(optionTitle,optionValue);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionMap;

    }

    public void resetOptions(){

        String optOverrideSetFilter = "&override_set_filter=" + "0";
        String optMuteMusic = "&muteMusic=" + "0";
        String optMuteSound = "&muteSound=" + "0";

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);

            outputStream.write(optOverrideSetFilter.getBytes());
            outputStream.write(optMuteMusic.getBytes());
            outputStream.write(optMuteSound.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOptions(HashMap<String,String> optionsMap){

        FileOutputStream outputStream;

        try {

            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);

            for(Map.Entry<String, String> entry: optionsMap.entrySet()) {
                String optionTitle = entry.getKey();
                String optionValue = entry.getValue();
                String fullOption = "&" + optionTitle + "=" + optionValue;
                outputStream.write(fullOption.getBytes());
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getOption(String optionTitle) {
        HashMap<String,String> optionMap = getOptions();
        String optionValue = optionMap.get(optionTitle);
        if (optionValue == null) {
            return false;
        }

        if (optionValue.equals("1")) {
            return true;
        }

        return false;
    }


}
