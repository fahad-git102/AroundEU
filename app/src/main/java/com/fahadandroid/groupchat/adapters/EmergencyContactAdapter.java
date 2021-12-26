package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.emergency.EmergencyNumber;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.ManageRequestsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.EmergencyContact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.EHolder> {

    List<EmergencyContact> emergencyNumberList;
    Context context;
    boolean fromAdmin = false;

    public EmergencyContactAdapter(List<EmergencyContact> emergencyNumberList, Context context, boolean fromAdmin) {
        this.emergencyNumberList = emergencyNumberList;
        this.context = context;
        this.fromAdmin = fromAdmin;
    }

    @NonNull
    @Override
    public EHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_contact_item, null);
        return new EHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EHolder holder, int position) {
        holder.tvContact.setText(emergencyNumberList.get(position).getName()+" - "+emergencyNumberList.get(position).getNumber());
        holder.tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+emergencyNumberList.get(position).getNumber()));
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
                    builder.setMessage("Are you sure you want to delete this contact ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("emergencyContacts");
                            ref.child(emergencyNumberList.get(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    emergencyNumberList.remove(position);
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
        return emergencyNumberList.size();
    }

    public class EHolder extends RecyclerView.ViewHolder{

        TextView tvContact;
        ImageButton btnDelete;

        public EHolder(@NonNull View itemView) {
            super(itemView);
            tvContact = itemView.findViewById(R.id.tvContact);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
