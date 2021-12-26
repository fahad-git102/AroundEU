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

import com.fahadandroid.groupchat.ManageRequestsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.CountryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.Holder> {
    List<CountryModel> countries;
    Context context;
    boolean fromAdmin;
    DatabaseReference countriesRef;

    public CountriesAdapter(List<CountryModel> countries, Context context, boolean fromAdmin) {
        this.countries = countries;
        this.context = context;
        this.fromAdmin = fromAdmin;
        countriesRef = FirebaseDatabase.getInstance().getReference("countries");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_select_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvName.setText(countries.get(position).getCountryName());
        if (position==countries.size()-1){
            holder.vv.setVisibility(View.GONE);
        }else {
            holder.vv.setVisibility(View.VISIBLE);
        }
        if (fromAdmin){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Country ?");
                    builder.setMessage("Are you sure you want to delete this country ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("deleted", true);
                            countriesRef.child(countries.get(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Deleted Successfully !", Toast.LENGTH_SHORT).show();
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
        }else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView tvName ;
        View vv;
        ImageButton btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            vv = itemView.findViewById(R.id.vv);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
