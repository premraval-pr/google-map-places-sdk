package com.example.test2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    FloatingActionButton fab_list_close;
    ListInterface listInterface;
    MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    private ItemFragment() {
    }

    public ItemFragment(ListInterface listInterface,MyItemRecyclerViewAdapter myItemRecyclerViewAdapter) {
        this.listInterface = listInterface;
        this.myItemRecyclerViewAdapter = myItemRecyclerViewAdapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        fab_list_close = view.findViewById(R.id.fab_list_close);

        fab_list_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listInterface.closeClicked();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.list_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myItemRecyclerViewAdapter);

        return view;
    }
}