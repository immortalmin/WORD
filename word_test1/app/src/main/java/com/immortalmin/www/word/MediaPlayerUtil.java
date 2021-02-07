package com.immortalmin.www.word;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
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
    private FinishListener finishListener;
    private boolean listenerFlag = false;

    MediaPlayerUtil(Context context){
        mediaPlayer = new MediaPlayer();
        this.context = context;
    }

    /**
     * 重置音频
     * 需要绑定回调函数finish()
     * @param word 需要播放的单词
     * @param playFlag 准备好后是否播放
     * @return boolean
     */
    public Boolean reset(String word,boolean playFlag){
        word = formatWord(word);
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            String filePath = Environment.getExternalStorageDirectory()+"/WORD/word-audio/"+word+".mp3";
            File file = new File(filePath);
            if(file.exists()&&file.length()!=0){//音频文件存在并且大小不为0的话，直接从sd卡中读取音频
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                if(playFlag) mediaPlayer.start();
                if(finishListener!=null) finishListener.finish();
            }else{//如果不存在，就先下载网络音频，再从sd卡中读取音频
                String finalWord = word;
                new Thread(()->{
                    HttpGetContext httpGetContext = new HttpGetContext();
                    httpGetContext.saveMp3IntoSD(finalWord);
                    try {
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepare();
                        if(playFlag) mediaPlayer.start();
                        if(finishListener!=null) finishListener.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将单词中的缩写和符号转换成单词，可以获取更准确地音频
     * @param word 格式化前的单词
     * @return 格式化后的单词
     */
    public String formatWord(String word){
        //XXX:末尾的sth没有成功替换成something
        word = word.replaceAll("sb.","somebody").replaceAll("sb ","somebody ")
                .replaceAll("sth.","something").replaceAll("sth ","something ")
                .replaceAll("/"," or ");
        //处理没有英文字母、数字的字符串
        if(word.replaceAll("[^a-zA-Z0-9]","").length()==0){
            word="nothing";
        }
        return word;
    }

    public interface FinishListener{
        void finish();
    }

    public void setFinishListener(FinishListener finishListener) {
        listenerFlag = true;
        this.finishListener = finishListener;
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
