package com.example.test2;

import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlaceProvider extends ArrayAdapter<String> {
    public List<String> id;

    public PlaceProvider(@NonNull Context context, int resource) {
        super(context, resource);
        id = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return id.size();
    }

    @Override
    public void add(@Nullable String object) {
        id.add(object);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return id.get(position);
    }

    public void clearData(){id.clear();}

    public List<String> getIdList() {
        return id;
    }
}
