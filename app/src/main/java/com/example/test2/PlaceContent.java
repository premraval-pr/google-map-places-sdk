package com.example.test2;

import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaceContent {

    public static final List<Place> ITEMS = new ArrayList<>();


    public static void addItem(Place item) {
        ITEMS.add(item);
    }

}