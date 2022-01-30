package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.fahadandroid.groupchat.EditCompanyActivity;
import com.fahadandroid.groupchat.ManageRequestsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> {

    List<CompanyModel> companyModelList;
    Context context;
    DatabaseReference companiesRef;
    boolean fromAdmin;

    public CompanyAdapter(List<CompanyModel> companyModelList, Context context, boolean fromAdmin) {
        this.companyModelList = companyModelList;
        this.context = context;
        this.fromAdmin = fromAdmin;
        companiesRef = FirebaseDatabase.getInstance().getReference("companies");
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_list_item, parent, false);
        return new CompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyHolder holder, int position) {
        if (companyModelList.get(position).getFullLegalName()!=null&&!companyModelList.get(position).getFullLegalName().equals("")){
            holder.tvCompantName.setVisibility(View.VISIBLE);
            holder.legal.setVisibility(View.GONE);
            holder.tvCompantName.setText(companyModelList.get(position).getFullLegalName());
        }else {
            if (companyModelList.get(position).getLegalRepresentative()!=null){
                holder.tvCompantName.setVisibility(View.VISIBLE);
                holder.legal.setVisibility(View.VISIBLE);
                holder.tvCompantName.setText(companyModelList.get(position).getLegalRepresentative());
            }else {
                holder.tvCompantName.setVisibility(View.GONE);
                holder.legal.setVisibility(View.GONE);
            }
        }
        if (companyModelList.get(position).getSize()!=null){
            holder.tvSize.setText(companyModelList.get(position).getSize());
        }else {
            holder.tvSize.setText("");
        }
        if (companyModelList.get(position).getCity()!=null){
            holder.tvLocation.setText(companyModelList.get(position).getCity());
            if (companyModelList.get(position).getCountry()!=null){
                holder.tvLocation.setText(holder.tvLocation.getText().toString()+", "+ companyModelList.get(position).getCountry());
            }
        }
        if (fromAdmin){
            holder.btnMore.setVisibility(View.VISIBLE);
            PopupMenu dropDownMenu = new PopupMenu(context, holder.btnMore);
            final Menu menu = dropDownMenu.getMenu();
            menu.add(0, 0, 0, "Delete Company");
            menu.add(0, 1, 0, "Edit Company");
            dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 0:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete Company ?");
                            builder.setMessage("Are you sure you want to delete this company ?");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    companiesRef.child(companyModelList.get(position).getKey()).removeValue().
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialogInterface.dismiss();
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Company Deleted !", Toast.LENGTH_SHORT).show();
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
                            return true;

                        case 1:
                            Intent intent = new Intent(context, EditCompanyActivity.class);
                            intent.putExtra("company", companyModelList.get(position));
                            context.startActivity(intent);
                            return true;

                    }
                    return false;
                }
            });
            holder.btnMore.setOnTouchListener(dropDownMenu.getDragToOpenListener());
            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dropDownMenu.show();
                }
            });
        }else {
            holder.btnMore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return companyModelList.size();
    }

    public class CompanyHolder extends RecyclerView.ViewHolder{

        TextView tvCompantName, tvLocation, tvSize, legal;
        ImageButton btnMore;

        public CompanyHolder(@NonNull View itemView) {
            super(itemView);
            tvCompantName = itemView.findViewById(R.id.tvCompanyName);
            tvLocation = itemView.findViewById(R.id.tvCompanyLocation);
            tvSize = itemView.findViewById(R.id.tvCompanySize);
            legal = itemView.findViewById(R.id.legal);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
