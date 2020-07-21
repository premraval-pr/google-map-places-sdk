package com.example.test2;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.Objects;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Place> mValues;
    ListFragInterface listFragInterface;

    public MyItemRecyclerViewAdapter(List<Place> items, ListFragInterface listFragInterface) {
        mValues = items;
        this.listFragInterface = listFragInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(holder.mItem.getName());
        holder.mAddressView.setText(holder.mItem.getAddress());
        holder.mPhoneView.setText(holder.mItem.getPhoneNumber());

        if(holder.mItem.getRating()!=null){
            holder.mRatingBar.setRating(holder.mItem.getRating().floatValue());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFragInterface.placeClicked(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mAddressView;
        public final TextView mPhoneView;
        public final RatingBar mRatingBar;
        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.tv_list_title);
            mAddressView = view.findViewById(R.id.tv_list_address);
            mPhoneView = view.findViewById(R.id.tv_list_phone);
            mRatingBar = view.findViewById(R.id.rb_list_rating);
        }

    }
}