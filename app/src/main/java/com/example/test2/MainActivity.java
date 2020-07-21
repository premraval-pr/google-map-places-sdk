package com.example.test2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MapInterface,ListInterface, ListFragInterface {

    private static final int AUTOCOMPLETE_CODE = 999;
    MapsFragment mapsFragment;
    private boolean isLocationPermissionGranted;
    FusedLocationProviderClient flp;
    VolleyAPI volleyAPI;
    PlaceProvider placeProvider;
    PlacesClient placesClient;
    FloatingActionButton fab_info_close,fab_info_list;
    FrameLayout list_layout;
    ItemFragment itemFragment;
    boolean doubleBackToExitPressedOnce = false;
    MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeProvider = new PlaceProvider(this,android.R.layout.simple_dropdown_item_1line);
        volleyAPI = new VolleyAPI(this,placeProvider,this,this);
        mapsFragment = new MapsFragment(this,placeProvider);
        Places.initialize(getApplicationContext(),VolleyAPI.API_KEY);
        placesClient = Places.createClient(this);
        fab_info_close = findViewById(R.id.fab_info_close);
        fab_info_list = findViewById(R.id.fab_info_list);
        list_layout = findViewById(R.id.host_list_frag);
        myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(PlaceContent.ITEMS,this);
        itemFragment = new ItemFragment(this,myItemRecyclerViewAdapter);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_frag,mapsFragment)
                .commit();
    }

    void showUserLocation() {
        getUserLocationPermission();
        if (isLocationPermissionGranted)
            centerMapOnUserLocation();
        else
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
    }

    void getUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            isLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        isLocationPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if(isLocationPermissionGranted){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    void centerMapOnUserLocation() {
        FusedLocationProviderClient flp = new FusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapsFragment.getgMap().setMyLocationEnabled(true);
            Task<Location> locationTask = flp.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location lastKnownUserLocation = task.getResult();
                        if (lastKnownUserLocation != null) {
                            LatLng userLatLng = new LatLng(lastKnownUserLocation.getLatitude(), lastKnownUserLocation.getLongitude());
                            mapsFragment.getgMap().animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,16));
                        }
                        getLocationUpdates();
                    } else
                        Toast.makeText(MainActivity.this, "Unknown last user location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        //Slowest interval, app will receive power blame for this
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        //high priority
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Flexible priority depending on set intervals
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Depend on other apps requests only locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        flp = new FusedLocationProviderClient(this);
        //Remove onPause
        flp.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng updatedLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mapsFragment.getgMap().animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLatLng,16));
                }

            }
        }, null);
    }

    @Override
    public void mapSetupComplete() {
        showUserLocation();
    }

    @Override
    public void updateMarker() {
        if(placeProvider.getCount()>0) {
            String place_id = Objects.requireNonNull(placeProvider.getItem(0));
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
                            mapsFragment.addMarkers(fetchedPlace);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.host_info_frag,new InfoFragment(fetchedPlace),"INFO_TAG")
                                    .commit();
                            fab_info_close.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("PLACES EXCEPTION", "onFailure: " + e.getMessage());
                        }
                    });
        }
        else {
            mapsFragment.getgMap().clear();
            Fragment tempFrag = getSupportFragmentManager().findFragmentByTag("INFO_TAG");
            if(tempFrag!=null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(tempFrag)
                        .commit();
            }
            fab_info_close.setVisibility(View.GONE);
            fab_info_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void mapClicked(LatLng latLng) {
        volleyAPI.getPlaces(latLng.latitude,latLng.longitude);
    }

    public void startAutoCompleteActivity(View view) {
        LatLng center = mapsFragment.getgMap().getCameraPosition().target;
        double radius = mapsFragment.getgMap().getCameraPosition().zoom;
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.LAT_LNG,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.RATING,
                        Place.Field.WEBSITE_URI,
                        Place.Field.PHOTO_METADATAS))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountries(Arrays.asList("CA","US"))
                .setLocationBias(RectangularBounds.newInstance(toBounds(center,radius)))
                .build(this);

        startActivityForResult(intent,AUTOCOMPLETE_CODE);
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters){
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center,distanceFromCenterToCorner,225);
        LatLng northeastCorner = SphericalUtil.computeOffset(center,distanceFromCenterToCorner,45);
        return new LatLngBounds(southwestCorner,northeastCorner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = null;
                if (data != null) {
                    place = Autocomplete.getPlaceFromIntent(data);
                    mapsFragment.addMarkers(place);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.host_info_frag,new InfoFragment(place),"INFO_TAG")
                            .commit();
                    fab_info_close.setVisibility(View.VISIBLE);
                    fab_info_list.setVisibility(View.VISIBLE);
                }
                Log.d("SUCCESS", "onPlaceSelected: " + place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = null;
                if (data != null) {
                    status = Autocomplete.getStatusFromIntent(data);
                }
                Log.d("ERROR", "onError: " + status);
            }
        }
    }

    public void closeInfo(View view) {
        mapsFragment.getgMap().clear();
        Fragment tempFrag = getSupportFragmentManager().findFragmentByTag("INFO_TAG");
        if(tempFrag!=null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(tempFrag)
                    .commit();
        }
        fab_info_close.setVisibility(View.GONE);
        fab_info_list.setVisibility(View.GONE);
    }

    public void showList(View view) {
        LatLng tempLatLng = mapsFragment.getgMap().getCameraPosition().target;
        volleyAPI.getPlacesBySearch(tempLatLng.latitude,tempLatLng.longitude,InfoFragment.TEXT_TO_SEARCH,placesClient);
    }

    @Override
    public void closeClicked() {
        getSupportFragmentManager()
                    .beginTransaction()
                    .remove(itemFragment)
                    .commit();
        PlaceContent.ITEMS.clear();
        list_layout.setVisibility(View.GONE);
    }

    @Override
    public void listFilled() {
        mapsFragment.getgMap().clear();
        list_layout.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_list_frag,itemFragment)
                .commit();
        closeInfo(fab_info_list);
    }

    @Override
    public void dataSetChange() {
        mapsFragment.addAll();
        mapsFragment.getgMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mapsFragment.getgMap().getCameraPosition().target,12));
        myItemRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void placeClicked(Place place) {
        mapsFragment.addMarkers(place);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.host_info_frag,new InfoFragment(place),"INFO_TAG")
                .commit();
        fab_info_close.setVisibility(View.VISIBLE);
        fab_info_list.setVisibility(View.VISIBLE);
        closeClicked();
    }
}