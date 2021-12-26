package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.CoordinatorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CoordinatorsAdapter extends RecyclerView.Adapter<CoordinatorsAdapter.Holder> {

    List<CoordinatorModel> coordinatorModelList;
    Context context;
    boolean fromAdmin;

    public CoordinatorsAdapter(List<CoordinatorModel> coordinatorModelList, Context context, boolean fromAdmin) {
        this.coordinatorModelList = coordinatorModelList;
        this.context = context;
        this.fromAdmin = fromAdmin;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvText.setText(coordinatorModelList.get(position).getText());
        String text = "<u>"+coordinatorModelList.get(position).getPhone()+"</u>";
        holder.tvPhone.setText(Html.fromHtml(text));
        holder.tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+coordinatorModelList.get(position).getPhone()));
                context.startActivity(intent);
            }
        });
        if (fromAdmin){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Contact ?");
                    builder.setMessage("Are you sure you want to delete this coordinator's contact ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("coordinators");
                            ref.child(coordinatorModelList.get(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    coordinatorModelList.remove(position);
                                    notifyDataSetChanged();
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
    }

    @Override
    public int getItemCount() {
        return coordinatorModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView tvText, tvPhone;
        ImageButton btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
