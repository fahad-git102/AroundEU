package com.fahadandroid.groupchat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.CountryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.BHolder> {

    List<BusinessList> businessLists;
    CountryModel selectedCountry;
    Context context;
    boolean showMenu=false;
    DatabaseReference businessListRef;

    public BusinessListAdapter(List<BusinessList> businessLists, Context context, boolean showMenu) {
        this.businessLists = businessLists;
        this.context = context;
        this.showMenu = showMenu;
        businessListRef = FirebaseDatabase.getInstance().getReference("businessList");
    }

    @NonNull
    @Override
    public BHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.string_list_item, parent, false);
        return new BHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BHolder holder, int position) {
        holder.text.setText(businessLists.get(position).getName());

        if (businessLists.get(position).getCountryId()!=null){
            holder.tvCountry.setVisibility(View.VISIBLE);
            for (int i = 0; i< EUGroupChat.countryModelList.size(); i++){
                if (businessLists.get(position).getCountryId().equals(EUGroupChat.countryModelList.get(i).getKey())){
                    holder.tvCountry.setText(EUGroupChat.countryModelList.get(i).getCountryName());
                }
            }
        }else {
            holder.tvCountry.setVisibility(View.GONE);
        }

        if (showMenu){
            holder.btnMore.setVisibility(View.VISIBLE);
        }

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(businessLists.get(position).getName());
                builder.setMessage("Select your action for this business list");
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view1 = LayoutInflater.from(context).inflate(R.layout.add_string_data_dialog, null);
                        EditText etName = view1.findViewById(R.id.etName);
                        Button btnSave = view1.findViewById(R.id.btnSave);
                        Spinner spinner = view1.findViewById(R.id.spinner);
                        etName.setText(businessLists.get(position).getName());
                        spinner.setVisibility(View.VISIBLE);
                        builder.setView(view1);
                        AlertDialog alertDialog = builder.create();
                        if (EUGroupChat.countryModelList.size()>0){
                            int index = -1;
                            for (int b = 0; b<EUGroupChat.countryModelList.size(); b++){
                                if (EUGroupChat.countryModelList.get(b).getKey().equals(businessLists.get(position).getCountryId())){
                                    selectedCountry = EUGroupChat.countryModelList.get(b);
                                    index = b;
                                }
                            }
                            ArrayAdapter<CountryModel> adapter =
                                    new ArrayAdapter<>(context.getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, EUGroupChat.countryModelList);
                            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                            spinner.setAdapter(adapter);
                            spinner.setSelection(index, true);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    selectedCountry = EUGroupChat.countryModelList.get(i);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String name = etName.getText().toString();
                                if (!name.isEmpty()&&selectedCountry!=null){
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("name", name);
                                    map.put("countryId", selectedCountry.getKey());
                                    businessListRef.child(businessLists.get(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            alertDialog.dismiss();
                                            Toast.makeText(context, "Business List Updated !", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    if (TextUtils.isEmpty(name)){
                                        etName.setError("Name Required");
                                    }
                                    if (selectedCountry==null){
                                        Toast.makeText(context, "Please select a country", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });

                        alertDialog.show();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        dialogInterface.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Delete");
                        builder1.setMessage("Are you sure you want to delete "+businessLists.get(position).getName() + " ?");
                        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int d) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("deleted", true);
                                businessListRef.child(businessLists.get(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Deleted successfully !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return businessLists.size();
    }

    public class BHolder extends RecyclerView.ViewHolder{
        TextView text, tvCountry;
        ImageButton btnMore;
        public BHolder(@NonNull View itemView) {
            super(itemView);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            text = itemView.findViewById(R.id.text);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
