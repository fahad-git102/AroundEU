package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeachersCordinatorAdapter extends RecyclerView.Adapter<TeachersCordinatorAdapter.THolder> {

    List<UserModel> list;
    Context context;

    public TeachersCordinatorAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public THolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teachers_cordinators_item, parent, false);
        return new THolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull THolder holder, int position) {
        if (list.get(position).getProfileUrl()!=null){
            Glide.with(context).load(list.get(position).getProfileUrl()).fitCenter().placeholder(R.drawable.default_image).into(holder.profilePic);
        }
        holder.tvTeachersName.setText(list.get(position).getFirstName()+" "+list.get(position).getSurName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class THolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView tvTeachersName;

        public THolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            tvTeachersName = itemView.findViewById(R.id.tvTeachersName);
        }
    }
}
