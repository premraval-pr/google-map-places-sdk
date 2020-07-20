package com.example.test2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VolleyAPI {
    public static final String Places_Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String API_KEY = "AIzaSyDuCQwL2Occ0DlgmKtkAZsSvQfyYpIgff0";
    public double radius = 20;
    Context context;
    PlaceProvider placeProvider;
    MapInterface mapInterface;

    public VolleyAPI(Context context, PlaceProvider placeProvider,MapInterface mapInterface){
        this.context = context;
        this.placeProvider = placeProvider;
        this.mapInterface = mapInterface;
    }

    public void getPlaces(double lat,double lon){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Places_Url +
                "?key=" + API_KEY +
                "&location=" + lat + "," + lon +
                "&radius=" + radius;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("mssg", "onResponse: " + response.toString());
                            JSONArray results = response.getJSONArray("results");
                            placeProvider.clearData();
                            for(int i =0;i<results.length();i++){
                                JSONObject jsonObject = results.getJSONObject(i);
                                List<String> types = new ArrayList<>();
                                if(jsonObject.has("types")) {
                                    JSONArray typesArray = jsonObject.getJSONArray("types");
                                    for(int j=0;j<typesArray.length();j++){
                                        types.add(typesArray.getString(j));
                                    }
                                }
                                if(jsonObject.has("place_id") && !types.contains("political") && placeProvider.getCount()==0) {
                                    placeProvider.add(jsonObject.getString("place_id"));
                                }
                                placeProvider.notifyDataSetChanged();
                            }
                            mapInterface.updateMarker();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("mssg", "onErrorResponse: " + error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }
}
