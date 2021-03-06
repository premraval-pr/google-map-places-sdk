package com.example.test2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {

    GoogleMap gMap;
    MapInterface mapInterface;
    PlaceProvider placeProvider;

    public GoogleMap getgMap() {
        return gMap;
    }

    public MapsFragment(MapInterface mapInterface,PlaceProvider placeProvider){
        this.mapInterface = mapInterface;
        this.placeProvider = placeProvider;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap = googleMap;
            mapInterface.mapSetupComplete();

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mapInterface.mapClicked(latLng);
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void addMarkers(Place place){
        gMap.clear();
        gMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName())).showInfoWindow();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),16));
    }

    public void addAll(){
        for(Place place: PlaceContent.ITEMS){
            gMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName()));
        }
    }
}