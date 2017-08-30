package com.example.pravesh.music009;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ottr006.musicjo.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MusicPlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageView btnPlay, btnForward, btnBackward, btnNext, btnPrevious, btnPlaylist,btnRepeat,btnShuffle;
    SeekBar  songProgressBar;
    private TextView songCurrentDurationLabel,songTitleLabel;
    private TextView songTotalDurationLabel;

    private  MediaPlayer mp;

    private Handler mHandler = new Handler();;
    private SongsManager plm;
    private Utilities utils;
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;
    private int currentSongIndex = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);


        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        btnForward = (ImageView) findViewById(R.id.btnForward);
        btnBackward = (ImageView) findViewById(R.id.btnBackward);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnPrevious = (ImageView) findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageView) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageView) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageView) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);


        mp = new MediaPlayer();
        plm = new SongsManager(MusicPlayerActivity.this);
        utils = new Utilities();


        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important



        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {

                this.songsList = plm.getPlayList();
            } else {
                requestPermission();
            }
        } else {


            this.songsList = plm.getPlayList();
        }


        playSong(0);


        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();

                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                }else{

                    if(mp!=null){
                        mp.start();
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });


        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int currentPosition = mp.getCurrentPosition();
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    mp.seekTo(mp.getDuration());
                }
            }
        });


        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int currentPosition = mp.getCurrentPosition();
                if(currentPosition - seekBackwardTime >= 0){
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    mp.seekTo(0);
                }

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{

                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{

                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{

                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{

                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();

                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });


        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");

            playSong(currentSongIndex);
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MusicPlayerActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayerActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MusicPlayerActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MusicPlayerActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    this.songsList = plm.getPlayList();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    /**
     * Function to play a song
     * @param songIndex - index of song
     * */
    public void  playSong(int songIndex){

        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();

            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            btnPlay.setImageResource(R.drawable.btn_pause);

            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));


            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

            songProgressBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mp.seekTo(currentPosition);

        updateProgressBar();
    }


    @Override
    public void onCompletion(MediaPlayer arg0) {

        if(isRepeat){

            playSong(currentSongIndex);
        } else if(isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }

}