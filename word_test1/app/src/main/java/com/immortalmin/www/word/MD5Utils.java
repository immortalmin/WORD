package com.immortalmin.www.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 */
public class MD5Utils {
    //加密字符串
    public String getMD5Code(String info){
        try {
            MessageDigest md5=MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("utf-8"));
            byte[]encryption=md5.digest();
            StringBuffer stringBuffer=new StringBuffer();
            for(int i=0;i<encryption.length;i++){
                if(Integer.toHexString(0xff &encryption[i]).length()==1){
                    stringBuffer.append("0").append(Integer.toHexString(0xff&encryption[i]));
                }else {
                    stringBuffer.append(Integer.toHexString(0xff&encryption[i]));
                }
            }
            return stringBuffer.toString();
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }
    //加密文件
    public static String md5ForFile(File file){
        int buffersize = 1024;
        FileInputStream fis = null;
        DigestInputStream dis = null;

        try {
            //创建MD5转换器和文件流
            MessageDigest messageDigest =MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis,messageDigest);

            byte[] buffer = new byte[buffersize];
            //DigestInputStream实际上在流处理文件时就在内部就进行了一定的处理
            while (dis.read(buffer) > 0);

            //通过DigestInputStream对象得到一个最终的MessageDigest对象。
            messageDigest = dis.getMessageDigest();

            // 通过messageDigest拿到结果，也是字节数组，包含16个元素
            byte[] array = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            StringBuilder hex = new StringBuilder(array.length * 2);
            for (byte b : array) {
                if ((b & 0xFF) < 0x10){
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
