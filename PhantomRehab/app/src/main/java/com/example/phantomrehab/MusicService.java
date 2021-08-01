package com.example.phantomrehab;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        play();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pause();
    }

    public void play(){
        if (player == null){
            player = MediaPlayer.create(this,R.raw.bgm_dreamy_piano);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.setLooping(true);
        player.start();
    }

    public void pause(){
        if (player != null){
            player.pause();
        }
    }

    public void stop (View v){
        stopPlayer();
    }

    private void stopPlayer(){
        if (player != null){
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onStop(){
//        super.onStop();
//        stopPlayer();
//    }

}

