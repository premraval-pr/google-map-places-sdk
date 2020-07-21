package com.example.test2;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VolleyAPI {
    public static final String Places_Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String Places_Text_Url = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    public static final String API_KEY = "YOUR_API";
    Context context;
    PlaceProvider placeProvider;
    MapInterface mapInterface;
    ListInterface listInterface;

    public VolleyAPI(Context context, PlaceProvider placeProvider,MapInterface mapInterface,ListInterface listInterface){
        this.context = context;
        this.placeProvider = placeProvider;
        this.mapInterface = mapInterface;
        this.listInterface = listInterface;
    }

    public void getPlaces(double lat,double lon){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Places_Url +
                "?key=" + API_KEY +
                "&location=" + lat + "," + lon +
                "&radius=20";
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
                                if(jsonObject.has("place_id") && placeProvider.getCount()==0 && !types.contains("political") && !types.contains("route")) {
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

    public void getPlacesBySearch(double lat, double lon, String textQuery, final PlacesClient placesClient){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Places_Text_Url +
                "?key=" + API_KEY +
                "&query=" + textQuery +
                "&location=" + lat + "," + lon +
                "&radius=100";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("mssg", "onResponse: " + response.toString());
                            JSONArray results = response.getJSONArray("results");
                            PlaceContent.ITEMS.clear();
                            listInterface.listFilled();
                            for(int i =0;i<results.length();i++){


                                JSONObject jsonObject = results.getJSONObject(i);
                                List<String> types = new ArrayList<>();
                                if(jsonObject.has("types")) {
                                    JSONArray typesArray = jsonObject.getJSONArray("types");
                                    for(int j=0;j<typesArray.length();j++){
                                        types.add(typesArray.getString(j));
                                    }
                                }
                                if(jsonObject.has("place_id") && !types.contains("political") && !types.contains("route")) {
                                    String place_id = jsonObject.getString("place_id");
                                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                                            Place.Field.NAME,
                                            Place.Field.ADDRESS,
                                            Place.Field.LAT_LNG,
                                            Place.Field.PHONE_NUMBER,
                                            Place.Field.RATING,
                                            Place.Field.WEBSITE_URI,
                                            Place.Field.PHOTO_METADATAS);

                                    FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(place_id, placeFields);

                                    placesClient.fetchPlace(placeRequest)
                                            .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                                @Override
                                                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                                    Place fetchedPlace = fetchPlaceResponse.getPlace();
                                                    Log.d("PLACES SUCCESS", "onSuccess: " + fetchedPlace);
                                                    PlaceContent.addItem(fetchedPlace);
                                                    listInterface.dataSetChange();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("PLACES EXCEPTION", "onFailure: " + e.getMessage());
                                                }
                                            });
                                }
                            }
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
