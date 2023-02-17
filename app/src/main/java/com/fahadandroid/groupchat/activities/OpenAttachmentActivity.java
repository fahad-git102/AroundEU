package com.fahadandroid.groupchat.activities;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.io.File;
import java.util.List;

public class OpenAttachmentActivity extends AppCompatActivity implements View.OnClickListener, FetchListener {

    PlayerView playerView;
    SimpleExoPlayer player;
    ImageView imgView;
    ImageButton goBack;
    Request request;
    String url;
    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
    ImageButton btnDownload ;
    boolean isVideo = false;
    Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_attachment);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        playerView = findViewById(R.id.video_player_view);
        btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(this);
        imgView = findViewById(R.id.imageView);
        isVideo = getIntent().getBooleanExtra("isVideo", false);
        boolean isProfilePic = getIntent().getBooleanExtra("profile_pic", false);
        if (isProfilePic){
            btnDownload.setVisibility(View.GONE);
        }
        url = getIntent().getStringExtra("url");
        if (isVideo){
            playerView.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.GONE);
            initializePlayer(url);
        }else {
            playerView.setVisibility(View.GONE);
            imgView.setVisibility(View.VISIBLE);
            Glide.with(this).load(url).placeholder(R.drawable.default_image).fitCenter().into(imgView);
        }
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableLogging(true)
                .setDownloadConcurrentLimit(1)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player!=null){
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (player!=null){
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack) {
            finish();
        }else if(view.getId()==R.id.btnDownload){
            downloadFile(url);
        }
    }

    private void initializePlayer(String url){
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videouri = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(player);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);

        } catch (Exception e) {

        }
    }

    private void downloadFile(String url){
        String extension = null;
        if (isVideo){
            extension = ".mp4";
        }else {
            extension = ".jpg";
        }

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

    private void showNotification(int id){
        try {
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
        }catch (Exception e){
            Toast.makeText(this, "Downloading started...", Toast.LENGTH_SHORT).show();
        }
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
        try {
            if (builder!=null){
                builder.setContentTitle("Download completed !");
                builder.setContentText("File is downloaded successfully in your external storage.");
                builder.setProgress(0,0, false);
                mNotificationManager.notify(download.getId(), builder.build());
            }
        }catch (Exception e){}
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
        try {
            int progress = download.getProgress();
            if (builder!=null){
                builder.setProgress(100,progress,false);
                mNotificationManager.notify(download.getId(), builder.build());
            }
        }catch (Exception e){

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
}