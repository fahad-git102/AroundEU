package com.fahadandroid.groupchat.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader {
    private static final int MEGABYTE = 1024*1024;
    public static void downloadFile(String fileUrl, File directory){
        try{
            URL url = new URL(fileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = httpURLConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer))>0){
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (MalformedURLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
