package com.finitegames.oasisAlpha;

//Imports
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class functions{
    static SQLiteDatabase oasisDatabase = SQLiteDatabase.openOrCreateDatabase("rems",null);
    Cursor curse;

    public static void updateColor(){
        oasisDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS params(BackGround VARACHAR(255), Color VARCHAR(255));"
        );
    }

    public SQLiteDatabase getOasisDatabase() {
        return oasisDatabase;
    }

    //Check for user mobile on the database before opening the game.
    //Using json api written on the database.
    public static boolean verify_User(String Url_string) throws IOException, JSONException {
        HttpURLConnection urlConnection;
        URL url = new URL(Url_string);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /*milliseconds*/);
        urlConnection.setConnectTimeout(15000 /*milliseconds*/);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader bfr = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while((line = bfr.readLine()) != null){
            sb.append(line).append("\n");
        }
        bfr.close();
        String jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return true; //new JSONObject(jsonString);
    }

}
