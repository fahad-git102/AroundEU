package com.fahadandroid.groupchat.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

public class DownloadPdf extends AsyncTask<String, Void, Void> {
    Context ctx;
    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = strings[1];
        String externalStorage = Environment.getExternalStorageDirectory().toString();
        File folder = new File(externalStorage, "PDF download");
        folder.mkdir();
        File pdfFile = new File(folder, fileName);
        try {
            pdfFile.createNewFile();
        }catch (Exception e){}
        FileDownloader.downloadFile(fileUrl, pdfFile);
        return null;
    }
}
