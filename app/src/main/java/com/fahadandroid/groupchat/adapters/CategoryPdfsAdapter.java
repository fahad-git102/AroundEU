package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CategoryPdfsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CategoryPdfsAdapter extends RecyclerView.Adapter<CategoryPdfsAdapter.CatHolder> {

    List<CategoryPdfsModel> list;
    Context context;
    FirebaseAuth mAuth;

    public CategoryPdfsAdapter(List<CategoryPdfsModel> list, Context context) {
        this.list = list;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdfs_list_item, parent, false);
        return new CatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatHolder holder, int position) {
        holder.tvName.setText("Doc "+position);
        if (EUGroupChat.currentUser!=null){
            if (EUGroupChat.currentUser.isAdmin()){
                holder.btnDelete.setVisibility(View.VISIBLE);
            }else {
                holder.btnDelete.setVisibility(View.GONE);
            }
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete file?");
                builder.setIcon(context.getResources().getDrawable(R.drawable.delete));
                builder.setMessage("Are you sure you want to delete this file ?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoriesRef.child(list.get(position).getCategoryId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "File deleted !", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CatHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageButton btnDelete;

        public CatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
