package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class LoadPdfActivity extends AppCompatActivity implements FetchListener {

    PDFView pdfView;
    ImageButton goBack;
    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
    Request request;
    Fetch fetch;
    ImageButton btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pdf);
        pdfView = findViewById(R.id.idPDFView);
        goBack = findViewById(R.id.goBack);
        btnDownload = findViewById(R.id.btnDownload);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String pdfURL = getIntent().getStringExtra("url");
        Toast.makeText(this, "Loading... Please wait.. ", Toast.LENGTH_SHORT).show();
        new RetrivePDFfromUrl().execute(pdfURL);
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableLogging(true)
                .setDownloadConcurrentLimit(1)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(this);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(pdfURL);
            }
        });
    }

    private void showNotification(int id){
        builder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Download Started...");
        bigText.setBigContentTitle("Download Started");
        bigText.setSummaryText("Text in detail");

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("Downloading Started...");
        builder.setContentText("File is being downloaded in your external memory.");
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(bigText);
        builder.setProgress(100,0,false);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mNotificationManager.notify(id, builder.build());
    }

    @Override
    public void onAdded(@NonNull Download download) {
        showNotification(download.getId());
    }

    @Override
    public void onCancelled(@NonNull Download download) {

    }

    @Override
    public void onCompleted(@NonNull Download download) {
        Toast.makeText(this, "Downloaded successfully !", Toast.LENGTH_SHORT).show();

        if (builder!=null){
            builder.setContentTitle("Download completed !");
            builder.setContentText("File is downloaded successfully in your external storage.");
            builder.setProgress(0,0, false);
            mNotificationManager.notify(download.getId(), builder.build());
        }
    }

    @Override
    public void onDeleted(@NonNull Download download) {

    }

    @Override
    public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

    }

    @Override
    public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {

    }

    @Override
    public void onPaused(@NonNull Download download) {

    }

    @Override
    public void onProgress(@NonNull Download download, long l, long l1) {
        int progress = download.getProgress();
        if (builder!=null){
            builder.setProgress(100,progress,false);
            mNotificationManager.notify(download.getId(), builder.build());
        }
    }

    @Override
    public void onQueued(@NonNull Download download, boolean b) {

    }

    @Override
    public void onRemoved(@NonNull Download download) {

    }

    @Override
    public void onResumed(@NonNull Download download) {

    }

    @Override
    public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {

    }

    @Override
    public void onWaitingNetwork(@NonNull Download download) {

    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream).load();
        }
    }

    private void downloadFile(String url){
        String extension = ".pdf";

        File folder = new File(Environment.getExternalStorageDirectory()+ "/Download");
        if (!folder.exists()){
            folder.mkdir();
        }
        File f = new File(folder, "AroundEU");
        File childF = new File(f, "file_"+System.currentTimeMillis());
        request = new Request(url, childF.getPath()+extension);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

        fetch.enqueue(request, updatedRequest -> {
            Toast.makeText(this, "Downloading started...", Toast.LENGTH_SHORT).show();
        }, error -> {
            Toast.makeText(this, "Download failed.  "+error.name(), Toast.LENGTH_SHORT).show();
        });

    }

}