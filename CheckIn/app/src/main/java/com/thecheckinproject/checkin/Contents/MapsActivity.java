package com.thecheckinproject.checkin.Contents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thecheckinproject.checkin.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String provider=LocationManager.NETWORK_PROVIDER;
        try{
            Location location=    locationManager.getLastKnownLocation(provider);
    //        double lattitude=location.getLatitude();
     //       double longitude=location.getLongitude();

            double[] lattitude={19.0213,19.0390,19.0795};
            double[] longitude={72.8424,72.8619,72.8976};

            int d = 1500;
            Bitmap bm =BitmapFactory.decodeResource(getResources(),
                    R.mipmap.harvey);
            bm=getRoundedShape(bm);
            Canvas c = new Canvas(bm);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            c.drawCircle(d/2, d/2, d/2, p);
            BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);
            LatLng mainLocation=null;
            for(int i=0;i<lattitude.length;i++){
                mainLocation = new LatLng(lattitude[i],longitude[i]);
                mMap.addMarker(new MarkerOptions().icon(bmD).title(getAddress(lattitude[i],longitude[i])).position(mainLocation));

            }

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mainLocation).zoom(12).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (SecurityException e){
            Toast.makeText(getApplicationContext(),"Permissions denied by the user",Toast.LENGTH_SHORT).show();
        }

    }

    public  String getAddress( double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address>
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String mainAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String locality=address.getLocality();
                String city=address.getCountryName();
                String region_code=address.getCountryCode();
                String zipcode=address.getPostalCode();
                result.append(mainAddress+",");
                result.append(locality+",");
                result.append(city+","+ region_code+" ");
                result.append(zipcode);

            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 80;
        int targetHeight = 80;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

}
