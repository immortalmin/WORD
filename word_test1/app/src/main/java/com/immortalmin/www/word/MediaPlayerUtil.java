package com.immortalmin.www.word;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.concurrent.TimeoutException;

/**
 * 单词音频播放工具类
 */
public class MediaPlayerUtil {
    private MediaPlayer mediaPlayer;
    private Context context;

    MediaPlayerUtil(Context context){
        mediaPlayer = new MediaPlayer();
        this.context = context;
    }

    /**
     * 重置音频
     * @param word 需要播放的单词
     * @param playFlag 准备好后是否播放
     * @return boolean
     */
    public Boolean reset(String word,boolean playFlag){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            //XXX:末尾的sth没有成功替换成something
            word = word.replaceAll("sb.","somebody").replaceAll("sb ","somebody ")
                    .replaceAll("sth.","something").replaceAll("sth ","something ")
                    .replaceAll("/"," or ");
            //处理没有英文字母、数字的字符串
            if(word.replaceAll("[^a-zA-Z0-9]","").length()==0){
                word="nothing";
            }
            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?type=1&audio="+ URLEncoder.encode(word.toLowerCase()));
//            mediaPlayer.prepare();
            mediaPlayer.prepareAsync();
            //计时线程 如果1秒后还没有获取到单词的音频就提示获取失败
            Thread thread = new Thread(() -> {
                try{
                    Thread.sleep(2000);
                    Looper.prepare();
                    Toast.makeText(context,"不会读o(╥﹏╥)o TimeOut",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            thread.start();
            /**
             * 当播放失败时，也提醒失败
             */
            mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
                Toast.makeText(context,"不会读o(╥﹏╥)o Error",Toast.LENGTH_SHORT).show();
                return false;
            });
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                thread.interrupt();
                if(playFlag){
                    mediaPlayer.start();
                }
            });
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
