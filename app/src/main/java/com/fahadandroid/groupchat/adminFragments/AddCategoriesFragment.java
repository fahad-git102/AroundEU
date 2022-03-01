package com.fahadandroid.groupchat.adminFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.PdfsListActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.StringSelectAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddCategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;

    public AddCategoriesFragment() {
        // Required empty public constructor
    }

    public static AddCategoriesFragment newInstance(String param1, String param2) {
        AddCategoriesFragment fragment = new AddCategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_categories, container, false);
        recyclerView= view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<String> stringList = new ArrayList<>();
        stringList.add("General Information");
        stringList.add("Excursions");
        stringList.add("Useful Information");
        StringSelectAdapter stringSelectAdapter = new StringSelectAdapter(stringList, requireContext());
        recyclerView.setAdapter(stringSelectAdapter);
        RecycleClick.addTo(recyclerView).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(requireContext(), PdfsListActivity.class);
                intent.putExtra("type", stringList.get(i));
                startActivity(intent);
            }
        });
        return view;
    }
}