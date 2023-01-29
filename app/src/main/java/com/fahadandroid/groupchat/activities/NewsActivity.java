package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.NewsAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.NewsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerNews;
    DatabaseReference newsRef;
    List<NewsModel> newsModelList;
    NewsAdapter adapter;
    List<String> newsKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        goBack = findViewById(R.id.goBack);
        newsModelList = new ArrayList<>();
        newsKeys = new ArrayList<>();
        newsRef = FirebaseDatabase.getInstance().getReference("news");
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerNews = findViewById(R.id.recycler_news);
        recyclerNews.setLayoutManager(new LinearLayoutManager(this));
        getNews();
    }
    private void getNews(){
        adapter = new NewsAdapter(newsModelList, this);
        recyclerNews.setAdapter(adapter);
        RecycleClick.addTo(recyclerNews).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                intent.putExtra("news", newsModelList.get(i).getKey());
                startActivity(intent);
            }
        });

        RecycleClick.addTo(recyclerNews).setOnItemLongClickListener(new RecycleClick.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int i, View view) {

                if (EUGroupChat.currentUser.isAdmin()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                    builder.setTitle("Delete News");
                    builder.setMessage("Are you sure you wnat to delete this news ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int iaa) {
                            newsRef.child(newsModelList.get(i).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(NewsActivity.this, "News deleted successfully !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                return false;
            }
        });

        newsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    NewsModel newsModel = snapshot.getValue(NewsModel.class);
                    newsModel.setKey(snapshot.getKey());
                    if (EUGroupChat.currentUser.getSelectedCountry()!=null){
                        if (newsModel.getCountry()!=null&&newsModel.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())){
                            newsModelList.add(0, newsModel);
                            newsKeys.add(0, snapshot.getKey());
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        newsModelList.add(0, newsModel);
                        newsKeys.add(0, snapshot.getKey());
                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    NewsModel newsModel = snapshot.getValue(NewsModel.class);
                    newsModel.setKey(snapshot.getKey());
                    if (EUGroupChat.currentUser.getSelectedCountry()!=null) {
                        if (newsModel.getCountry() != null && newsModel.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())) {
                            int index = newsKeys.indexOf(snapshot.getKey());
                            newsModelList.set(index, newsModel);
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        int index = newsKeys.indexOf(snapshot.getKey());
                        newsModelList.set(index, newsModel);
                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    int index = newsKeys.indexOf(snapshot.getKey());
                    newsModelList.remove(index);
                    newsKeys.remove(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}