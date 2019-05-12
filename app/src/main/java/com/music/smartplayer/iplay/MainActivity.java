package com.music.smartplayer.iplay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // when the user clicks on the screen then the voice receiver gets activated
    private RelativeLayout parentRelativeLayout;

    // Speech Recognizer
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    // used to check various states
    private String keeper ="", mode = "ON";

    private ImageView playPauseBtn, nextBtn, previousBtn;
    private TextView songNameTxt;

    // for logo
    private ImageView logoImg;

    private RelativeLayout lowerRelativeLayout;

    private Button voiceEnableBtn;


    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAudioPermission();

        // initializing variables
        playPauseBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        logoImg = findViewById(R.id.logo);

        lowerRelativeLayout = findViewById(R.id.lower_relative_layout);
        voiceEnableBtn = findViewById(R.id.voice_enable_btn);
        songNameTxt = findViewById(R.id.song_name);


        parentRelativeLayout = findViewById(R.id.parent_relative_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        // getting the details from intent snd start mediaPlayer
        receiveAndStartPlaying();

        // setting the logo in imageView
        logoImg.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                // when found something the keeper will get the first string
                if (matchesFound != null){
                    keeper = matchesFound.get(0);
                    Toast.makeText(MainActivity.this, "Result: "+keeper, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        // function when touching the screen
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // for the two actions ACTON UP and ACTION DOWN
                switch(motionEvent.getAction()){
                    // for long press of screen
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    // when the screen is left
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });


        // function of voice btn
        voiceEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("ON")){
                    mode = "OFF" ;
                    voiceEnableBtn.setText("Voice Enable - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else {
                    mode = "ON" ;
                    voiceEnableBtn.setText("Voice Enable - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    // getting the details from intent snd start mediaPlayer
    private void receiveAndStartPlaying(){
        if (myMediaPlayer != null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        // receiving intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        myMediaPlayer.start();

    }

    // to check for the permissions of audio record
    private void checkAudioPermission(){
        // if above android marsh mellow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                startActivity(intent);
                finish();
            }
        }

    }
}
