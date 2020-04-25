package com.immortalmin.www.word;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.net.URLEncoder;

public class MusicService extends Service {

    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }

    }
    //歌曲路径
    private String []musicDir = new String[]{
            "http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode("one"),
            "http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode("two"),
            "http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode("three"),
            "http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode("four"),
//            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Jameston Thieves Krumm - Unusual Suspects.mp3",
//            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Max Vangeli Flatdisk - Blow This Club (Extended Mix).mp3",
//            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Kingsfoil - Grapevine Valentine.mp3",
//            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/VANTAGE    - ∞【 Rediscover 】∞.mp3"
    };
    //哪一首歌曲
    private int musicIndex = 1;
    //MediaPlayer-----Idle 状态(闲置状态)
    public static MediaPlayer mp = new MediaPlayer();

    public MusicService(){

        try {
            musicIndex = 1;
            //MediaPlayer-----Initialized 状态(初始化状态)
            mp.setDataSource(musicDir[musicIndex]);
            //MediaPlayer-----Prepared 状态(准备状态)
            mp.prepare();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("hint", "can't get to the song");
            e.printStackTrace();
        }
    }

    public void playOrPause() {
        // TODO Auto-generated method stub
        if (mp.isPlaying()) {
            mp.pause();
        }else {
            mp.start();
        }
    }

    public void stop(){
        if (mp != null) {
            mp.stop();
            try {
                mp.prepare();
                mp.seekTo(0);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public void nextMusic(){
        if (mp != null && musicIndex < 3) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex+1]);
                musicIndex++;
                mp.prepare();
                //MediaPlayer-----Started状态
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("hint", "Can't jump next music");
                e.printStackTrace();
            }
        }
    }

    public void preMusic(){
        if (mp != null && musicIndex > 0) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[musicIndex-1]);
                musicIndex--;
                mp.prepare();
                mp.seekTo(0);
                mp.start();
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("hint", "Can't jump next music");
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}


