package com.music.smartplayer.iplay;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private String[] allItems;
    private ListView mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSongsList = findViewById(R.id.songsList);

        externalStragePermission();
    }



    public void externalStragePermission(){
        // copied from github

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displaySongs();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        /* ... */
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }

                }).check();
    }

    // function to read files from phone
    public ArrayList<File> readSongs (File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        // if the file is a directory and not hidden
        for (File individualFile : allFiles){
            if (individualFile.isDirectory() && !individualFile.isHidden()){
                arrayList.addAll(readSongs(individualFile));
            }
            else {
                if (individualFile.getName().endsWith(".mp3")
                        || individualFile.getName().endsWith(".aac")
                        || individualFile.getName().endsWith(".wav")
                        ||individualFile.getName().endsWith(".wma")){
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }

    // to display somg list
    private void displaySongs(){
        final ArrayList<File> audioSongs = readSongs(Environment.getExternalStorageDirectory());

        allItems =  new String[audioSongs.size()];
        for (int songCounter=0; songCounter<audioSongs.size(); songCounter++){
            allItems[songCounter] = audioSongs.get(songCounter).getName();
        }

        // adding the names to the list so as to display
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, allItems);
        mSongsList.setAdapter(arrayAdapter);

        // function when a user clicks on the list item
        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get the name first then send the name info wth intent
                String songName = mSongsList.getItemAtPosition(i).toString();
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);

            }
        });
    }
}