package com.example.test2;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Objects;

public class InfoFragment extends Fragment {
    Place fetchedPlace;
    TextView title,phone,address,website;
    RatingBar ratingBar;
    PlacesClient placesClient;
    ImageView image;

    public InfoFragment(Place place){
        this.fetchedPlace = place;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        Places.initialize(Objects.requireNonNull(getContext()),VolleyAPI.API_KEY);
        placesClient = Places.createClient(getContext());
        title = view.findViewById(R.id.info_title);
        phone = view.findViewById(R.id.info_phone);
        address = view.findViewById(R.id.info_address);
        ratingBar = view.findViewById(R.id.info_rating);
        website = view.findViewById(R.id.info_website);
        image = view.findViewById(R.id.info_image);
        image.setClipToOutline(true);

        title.setText(fetchedPlace.getName());
        phone.setText(fetchedPlace.getPhoneNumber());
        address.setText(fetchedPlace.getAddress());

        if(fetchedPlace.getWebsiteUri()!=null){
            website.setText(Objects.requireNonNull(fetchedPlace.getWebsiteUri()).toString());
        }

        if(fetchedPlace.getRating()!=null) {
            ratingBar.setRating(Objects.requireNonNull(fetchedPlace.getRating()).floatValue());
        }

        if(fetchedPlace.getPhotoMetadatas()!=null){
            final FetchPhotoRequest fetchPhotoRequest = FetchPhotoRequest.builder(fetchedPlace.getPhotoMetadatas().get(0))
                    .setMaxHeight(400)
                    .build();
            placesClient.fetchPhoto(fetchPhotoRequest)
                    .addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                        @Override
                        public void onSuccess(FetchPhotoResponse photoResponse) {
                            Bitmap bitmap = photoResponse.getBitmap();
                            image.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("FETCH PHOTO EXCEPTION", "onFailure: " + e.getMessage());
                        }
                    });
        }
        return view;
    }
}