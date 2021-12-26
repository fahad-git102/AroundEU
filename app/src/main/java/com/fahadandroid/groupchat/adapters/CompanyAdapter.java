package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.CompanyModel;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> {

    List<CompanyModel> companyModelList;
    Context context;

    public CompanyAdapter(List<CompanyModel> companyModelList, Context context) {
        this.companyModelList = companyModelList;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return companyModelList.size();
    }

    public class CompanyHolder extends RecyclerView.ViewHolder{

        TextView tvCompantName, tvLocation, tvSize, legal;

        public CompanyHolder(@NonNull View itemView) {
            super(itemView);
            tvCompantName = itemView.findViewById(R.id.tvCompanyName);
            tvLocation = itemView.findViewById(R.id.tvCompanyLocation);
            tvSize = itemView.findViewById(R.id.tvCompanySize);
            legal = itemView.findViewById(R.id.legal);
        }
    }
}
