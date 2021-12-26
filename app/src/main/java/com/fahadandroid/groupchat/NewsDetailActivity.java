package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.NewsModel;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    ImageView image;
    TextView text, textDate, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        image = findViewById(R.id.imageView);
        text = findViewById(R.id.text);
        textDate = findViewById(R.id.textDate);
        title = findViewById(R.id.title);
        NewsModel newsModel = getIntent().getParcelableExtra("news");
        if (newsModel!=null){
            if (newsModel.getTitle()!=null){
                title.setVisibility(View.VISIBLE);
                title.setText(newsModel.getTitle());
            }else {
                title.setVisibility(View.GONE);
            }
            if (newsModel.getImageUrl()!=null){
                Glide.with(this).load(newsModel.getImageUrl()).placeholder(R.drawable.default_image).into(image);
            }
            text.setText(newsModel.getDescription());
            textDate.setText(HelperClass.getFormattedDateTime(newsModel.getTimeStamp(), "dd MMM, yyyy hh:mm a"));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }
    }
}