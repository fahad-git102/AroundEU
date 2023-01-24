package com.fahadandroid.groupchat.helpers;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fahadandroid.groupchat.adapters.ChatRecyclerAdapter;

public class MyScrollToBottomObserver extends RecyclerView.AdapterDataObserver {
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    ChatRecyclerAdapter adapter;
    public MyScrollToBottomObserver(RecyclerView recyclerView, ChatRecyclerAdapter adapter,
                                    LinearLayoutManager manager) {
        this.adapter = adapter;
        this.manager = manager;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int count = adapter.getItemCount();
        int lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        boolean loading = lastVisiblePosition == -1;
        boolean atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1;
        if (loading || atBottom) {
            recyclerView.scrollToPosition(positionStart);
        }
    }
}
