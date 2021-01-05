package com.immortalmin.www.word;

import android.media.MediaPlayer;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 单词音频播放工具类
 */
public class MediaPlayerUtil {
    private MediaPlayer mediaPlayer;

    MediaPlayerUtil(){
        mediaPlayer = new MediaPlayer();
    }
    public Boolean reset(String word){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            word = word.replaceAll("sb.","somebody").replaceAll("sb","somebody")
                    .replaceAll("sth.","something").replaceAll("sth","something")
                    .replaceAll("/"," or ");
            //处理没有因为字母、数字的字符串
            if(word.replaceAll("[^a-zA-Z0-9]","").length()==0){
                word="nothing";
            }
            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word.toLowerCase()));
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 停止播放
     */
    public void stop(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    /**
     * 开始播放（将会停止当前正在播放的音频）
     */
    public void start(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();
    }

    /**
     * 返回音频播放时间
     * @return
     */
    public int getDuration(){
        return mediaPlayer.getDuration();
    }

}
