package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.NewsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        String key = getIntent().getStringExtra("news");
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");
        text.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(text, Linkify.WEB_URLS);
        newsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    NewsModel newsModel = snapshot.getValue(NewsModel.class);
                    if (newsModel!=null){
                        newsModel.setKey(snapshot.getKey());
                        if (newsModel.getTitle()!=null){
                            title.setVisibility(View.VISIBLE);
                            title.setText(newsModel.getTitle());
                        }else {
                            title.setVisibility(View.GONE);
                        }
                        if (newsModel.getImageUrl()!=null){
                            Glide.with(NewsDetailActivity.this).load(newsModel.getImageUrl()).fitCenter().placeholder(R.drawable.default_image).into(image);
                        }
                        text.setText(newsModel.getDescription());
                        textDate.setText(HelperClass.getFormattedDateTime(newsModel.getTimeStamp(), "dd MMM, yyyy hh:mm a"));
                    }else {
                        Toast.makeText(NewsDetailActivity.this, "Data not found. This news must be deleted", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }
    }
}