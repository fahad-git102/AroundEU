package com.fahadandroid.groupchat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder> {

    List<PlacesModel> placesModelList;
    Context context;
    boolean delete = false;
    DatabaseReference placesRef;

    public PlacesAdapter(List<PlacesModel> placesModelList, Context context, boolean delete) {
        this.placesModelList = placesModelList;
        this.context = context;
        this.delete = delete;
        placesRef = FirebaseDatabase.getInstance().getReference("places");
    }

    @NonNull
    @Override
    public PlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.places_layout, parent, false);
        return new PlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesHolder holder, int position) {
        if (placesModelList.get(position).getImageUrl()!=null){
            Glide.with(context).load(placesModelList.get(position).getImageUrl()).fitCenter().placeholder(R.drawable.default_image).into(holder.image);
//            Picasso.get().load(placesModelList.get(position).getImageUrl()).resize(140,140).placeholder(R.drawable.default_image).into(holder.image);
        }
        holder.tvText.setText(placesModelList.get(position).getDescription());
        if (placesModelList.get(position).getCategory()!=null){
            holder.tvCategory.setText(placesModelList.get(position).getCategory());
        }else {
            holder.tvCategory.setText("");
        }
        for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
            if (EUGroupChat.userModelList.get(i).getUid()!=null){
                if (EUGroupChat.userModelList.get(i).getUid().equals(placesModelList.get(position).getUid())){
                    holder.tvUserName.setText(EUGroupChat.userModelList.get(i).getFirstName()+" "+EUGroupChat.userModelList.get(i).getSurName());
                }
            }
        }
        if (delete){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.VISIBLE);
            if (placesModelList.get(position).getStatus()!=null){
                holder.tvStatus.setText(placesModelList.get(position).getStatus());
            }else {
                holder.tvStatus.setText("None");
            }
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete ?");
                    builder.setMessage("Are you sure you want to delete this place ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            placesRef.child(placesModelList.get(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Deleted successfully !", Toast.LENGTH_SHORT).show();
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
        return placesModelList.size();
    }

    public class PlacesHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView tvText, tvUserName, tvCategory, tvStatus;
        ImageButton btnDelete;

        public PlacesHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tvText = itemView.findViewById(R.id.tvText);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvUserName = itemView.findViewById(R.id.tvUsername);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
