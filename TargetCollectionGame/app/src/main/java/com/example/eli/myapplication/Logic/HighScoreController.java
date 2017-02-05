package com.example.eli.myapplication.Logic;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Eli on 8/19/2016.
 */
public class HighScoreController {

    String storageLocation;
    Context context;

    public HighScoreController(Context context, String storageLocation){
        this.storageLocation = storageLocation;
        this.context = context;
    }

    public HashMap<String, String> getUserScores(String currentUser){

        System.out.println("high score file parse.");

        HashMap highScores = new HashMap();

        //defaults
        for (int i=1; i<=3; i++){
            for (int j=1; j<=5; j++){
                highScores.put(i + "." + j, "0");
            }
        }

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

            String[] allUserScores = result.split("\\*");

            for (String currentUserScore : allUserScores){

                System.out.println("currentUserScore: " + currentUserScore);

                if (currentUserScore.startsWith(currentUser)){
                    String[] allLevelScores = currentUserScore.split("\\&");

                    for (String currentLevel : allLevelScores){
                        System.out.println("setSelection: " + currentLevel);

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

    public void updateHighScoreFile(HashMap<String,String> currentUserScores){

        //TODO change this name to be dynamic
        String string = "*Eli";
        String string1 = "&1.1:" + currentUserScores.get("1.1");
        String string2 = "&1.2:" + currentUserScores.get("1.2");
        String string3 = "&1.3:" + currentUserScores.get("1.3");
        String string4 = "&1.4:" + currentUserScores.get("1.4");
        String string5 = "&1.5:" + currentUserScores.get("1.5");

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);
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

    public void clearScores(){


        String string = "*Eli";

        String string1 = "&1.1:" + "0";
        String string2 = "&1.2:" + "0";
        String string3 = "&1.3:" + "0";
        String string4 = "&1.4:" + "0";
        String string5 = "&1.5:" + "0";

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);
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
