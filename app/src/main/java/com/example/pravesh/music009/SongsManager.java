package com.example.pravesh.music009;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    Cursor cursor;
    Context context;

    public SongsManager(Context context){

        this.context = context;
    }

    public ArrayList<HashMap<String, String>> getPlayList(){

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";



        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        ContentResolver contentResolver = context.getContentResolver();
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection, selection,null,null);


        while(cursor.moveToNext()) {
            HashMap<String, String> song = new HashMap<String, String>();
            song.put("songID",cursor.getString(0));
            song.put("songTitle",cursor.getString(2));
            song.put("songPath",cursor.getString(3));
            songsList.add(song);
        }

        return songsList;
    }

}
