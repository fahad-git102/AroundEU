package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.NotificationsModel;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.Holder> {

    List<NotificationsModel> notificationsModelList;
    Context context;

    public NotificationsAdapter(List<NotificationsModel> notificationsModelList, Context context) {
        this.notificationsModelList = notificationsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvTitle.setText(notificationsModelList.get(position).getTitle());
        holder.tvMessage.setText(notificationsModelList.get(position).getMessage());
        holder.tvTime.setText(HelperClass.getFormattedDateTime(notificationsModelList
                .get(position).getTimeStamp(), "MMM dd, yyyy hh:mm a"));
    }

    @Override
    public int getItemCount() {
        return notificationsModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvMessage, tvTime;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
