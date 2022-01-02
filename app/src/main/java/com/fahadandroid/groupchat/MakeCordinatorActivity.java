
package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.CompanyAdapter;
import com.fahadandroid.groupchat.adapters.TeachersCordinatorAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeCordinatorActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerTeachers;
    SearchView searchView;
    FirebaseDatabase firebaseDatabase;
    List<UserModel> teachersModelList;
    List<String> teachersKey;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_cordinator);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        teachersModelList = new ArrayList<>();
        teachersKey = new ArrayList<>();
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerTeachers = findViewById(R.id.recycler_teachers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerTeachers.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerTeachers.getContext(),
                linearLayoutManager.getOrientation());
        recyclerTeachers.addItemDecoration(dividerItemDecoration);
        searchView = findViewById(R.id.searchView);
        getTeachers();

        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search teachers here...");

        if(!searchView.isFocused()) {
            searchView.clearFocus();
        }
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

    }

    private void search(String text){
        List<UserModel> teachersList = new ArrayList<>();
        for (UserModel teacher: teachersModelList){
            boolean firstNameMatch = false;
            boolean surNameMatch = false;
            if (teacher.getFirstName()!=null){
                if (teacher.getFirstName().toLowerCase().contains(text.toLowerCase())){
                    firstNameMatch=true;
                }
            }
            if (teacher.getSurName()!=null){
                if (teacher.getSurName().toLowerCase().contains(text.toLowerCase())){
                    surNameMatch=true;
                }
            }
            if (firstNameMatch||surNameMatch){
                teachersList.add(teacher);
                firstNameMatch = false;
                surNameMatch= false;
            }
            recyclerTeachers.setAdapter(null);
            TeachersCordinatorAdapter adapter = new TeachersCordinatorAdapter(teachersList, this);
            recyclerTeachers.setAdapter(adapter);

        }
    }

    private void getTeachers(){
        TeachersCordinatorAdapter adapter = new TeachersCordinatorAdapter(teachersModelList, this);
        recyclerTeachers.setAdapter(adapter);
        RecycleClick.addTo(recyclerTeachers).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakeCordinatorActivity.this);
                builder.setIcon(getResources().getDrawable(R.drawable.ic_coordinator));
                builder.setTitle("Make Co-ordinator");
                builder.setMessage("Are you sure you want to make "+ teachersModelList.get(i).getFirstName()+ " a Co-ordinator?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int iu) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("userType", "Cordinator");
                        usersRef.child(teachersModelList.get(i).getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MakeCordinatorActivity.this);
                                builder1.setTitle("Co-ordinator");
                                builder1.setIcon(getResources().getDrawable(R.drawable.check));
                                builder1.setMessage(teachersModelList.get(i).getFirstName()+" is a co-ordinator now.");
                                builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterfac, int i) {
                                        dialogInterfac.dismiss();
                                    }
                                });
                                AlertDialog alertDialog1 = builder1.create();
                                alertDialog1.show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUserType()!=null){
                        if(userModel.getUserType().equals("Teacher")){
                            teachersModelList.add(userModel);
                            teachersKey.add(userModel.getUid());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUserType()!=null){
                        if(userModel.getUserType().equals("Teacher")){
                            int index = teachersKey.indexOf(userModel.getUid());
                            teachersModelList.set(index, userModel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUserType()!=null){
                        if(userModel.getUserType().equals("Teacher")){
                            int index = teachersKey.indexOf(userModel.getUid());
                            teachersModelList.remove(index);
                            adapter.notifyDataSetChanged();
                        }
                    }
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