package com.example.test2;

import com.google.android.gms.maps.model.LatLng;

public interface MapInterface {
    void mapSetupComplete();
    void updateMarker();
    void mapClicked(LatLng latLng);
}
