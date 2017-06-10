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

                        // set 1
                        if (currentLevel.startsWith("1.1")){
                            highScores.put("1.1",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.2")) {
                            highScores.put("1.2", currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.3")) {
                            highScores.put("1.3",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.4")) {
                            highScores.put("1.4",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("1.5")) {
                            highScores.put("1.5", currentLevel.substring(4));
                        }

                        // set 2
                          else if (currentLevel.startsWith("2.1")) {
                            highScores.put("2.1", currentLevel.substring(4));
                        } else if (currentLevel.startsWith("2.2")) {
                            highScores.put("2.2",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("2.3")) {
                            highScores.put("2.3",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("2.4")) {
                            highScores.put("2.4",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("2.5")) {
                            highScores.put("2.5",currentLevel.substring(4));
                        }

                        // set 3
                        else if (currentLevel.startsWith("3.1")) {
                            highScores.put("3.1", currentLevel.substring(4));
                        } else if (currentLevel.startsWith("3.2")) {
                            highScores.put("3.2",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("3.3")) {
                            highScores.put("3.3",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("3.4")) {
                            highScores.put("3.4",currentLevel.substring(4));
                        } else if (currentLevel.startsWith("3.5")) {
                            highScores.put("3.5",currentLevel.substring(4));
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

        String string11 = "&1.1:" + currentUserScores.get("1.1");
        String string12 = "&1.2:" + currentUserScores.get("1.2");
        String string13 = "&1.3:" + currentUserScores.get("1.3");
        String string14 = "&1.4:" + currentUserScores.get("1.4");
        String string15 = "&1.5:" + currentUserScores.get("1.5");

        String string21 = "&2.1:" + currentUserScores.get("2.1");
        String string22 = "&2.2:" + currentUserScores.get("2.2");
        String string23 = "&2.3:" + currentUserScores.get("2.3");
        String string24 = "&2.4:" + currentUserScores.get("2.4");
        String string25 = "&2.5:" + currentUserScores.get("2.5");

        String string31 = "&3.1:" + currentUserScores.get("3.1");
        String string32 = "&3.2:" + currentUserScores.get("3.2");
        String string33 = "&3.3:" + currentUserScores.get("3.3");
        String string34 = "&3.4:" + currentUserScores.get("3.4");
        String string35 = "&3.5:" + currentUserScores.get("3.5");

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.write(string11.getBytes());
            outputStream.write(string12.getBytes());
            outputStream.write(string13.getBytes());
            outputStream.write(string14.getBytes());
            outputStream.write(string15.getBytes());
            outputStream.write(string21.getBytes());
            outputStream.write(string22.getBytes());
            outputStream.write(string23.getBytes());
            outputStream.write(string24.getBytes());
            outputStream.write(string25.getBytes());
            outputStream.write(string31.getBytes());
            outputStream.write(string32.getBytes());
            outputStream.write(string33.getBytes());
            outputStream.write(string34.getBytes());
            outputStream.write(string35.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearScores(){


        String string = "*Eli";

        String string11 = "&1.1:" + "0";
        String string12 = "&1.2:" + "0";
        String string13 = "&1.3:" + "0";
        String string14 = "&1.4:" + "0";
        String string15 = "&1.5:" + "0";

        String string21 = "&2.1:" + "0";
        String string22 = "&2.2:" + "0";
        String string23 = "&2.3:" + "0";
        String string24 = "&2.4:" + "0";
        String string25 = "&2.5:" + "0";

        String string31 = "&3.1:" + "0";
        String string32 = "&3.2:" + "0";
        String string33 = "&3.3:" + "0";
        String string34 = "&3.4:" + "0";
        String string35 = "&3.5:" + "0";

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(storageLocation, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.write(string11.getBytes());
            outputStream.write(string12.getBytes());
            outputStream.write(string13.getBytes());
            outputStream.write(string14.getBytes());
            outputStream.write(string15.getBytes());
            outputStream.write(string21.getBytes());
            outputStream.write(string22.getBytes());
            outputStream.write(string23.getBytes());
            outputStream.write(string24.getBytes());
            outputStream.write(string25.getBytes());
            outputStream.write(string31.getBytes());
            outputStream.write(string32.getBytes());
            outputStream.write(string33.getBytes());
            outputStream.write(string34.getBytes());
            outputStream.write(string35.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
