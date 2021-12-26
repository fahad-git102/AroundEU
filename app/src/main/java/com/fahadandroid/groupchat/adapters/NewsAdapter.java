package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.NewsModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewVHolder> {

    List<NewsModel> newsModelList;
    Context context;

    public NewsAdapter(List<NewsModel> newsModelList, Context context) {
        this.newsModelList = newsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_layout, parent, false);
        return new NewVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewVHolder holder, int position) {
        Picasso.get().load(newsModelList.get(position).getImageUrl()).fit().placeholder(R.drawable.default_image).into(holder.image);
        holder.tvText.setText(newsModelList.get(position).getDescription());
        if (newsModelList.get(position).getTitle()!=null){
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(newsModelList.get(position).getTitle());
        }else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        holder.tvDate.setText(HelperClass.getFormattedDateTime(newsModelList.get(position).getTimeStamp(), "dd MMM, yyyy hh:mm a"));
    }

    @Override
    public int getItemCount() {
        return newsModelList.size();
    }

    public class NewVHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView tvText, tvDate, tvTitle;
        public NewVHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tvText = itemView.findViewById(R.id.tvText);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
