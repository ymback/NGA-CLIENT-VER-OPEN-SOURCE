package sp.phone.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import gov.anzong.androidnga.R;

public class LocationUpdater implements LocationListener {
    private final LocationManager locationManager;
    private final Context c;


    public LocationUpdater(LocationManager locationManager, Context c) {
        super();
        this.locationManager = locationManager;
        this.c = c;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(c, R.string.get_location_success, Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);
        ActivityUtil.updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
