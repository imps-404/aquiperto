package com.example.firebase1;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mMapView;
    String fullAddress = "";
    Double lat, lng;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button btn;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loadAddress();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView2);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


        btn = findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        MapActivity.this,
                        SearchShopActivity.class
                );
                startActivity(i);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    public void loadAddress()
    {
        fullAddress = getIntent().getStringExtra("ADDRESS");

    }
    public void parseAddress(String address)
    {
        //chop the first part
        String[] latlong =  address.split(":");
        String remaining = latlong[1];

         latlong = remaining.split("\\(");
        remaining = latlong[0];

        latlong = remaining.split(",");
        remaining = latlong[0];

        lat = Double.parseDouble(latlong[0]);
        lng = Double.parseDouble(latlong[1]);

        Log.wtf("zzzz", "address"+ address);
        Log.wtf("zzzz", "parse"+ remaining);
        Log.wtf("zzzz", "latlng"+ lat+", "+lng);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {




        //use your google api key to create a GeoApiContext instance
        GeoApiContext context = new GeoApiContext.Builder().apiKey("xxxxx").build();


        //this will get geolocation details via address

            GeocodingResult[] results2 = new GeocodingResult[0];
            try {
                results2 = GeocodingApi.geocode(context, fullAddress).await();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(results2[0]);
            Log.wtf("zzzz", "results0"+ results2[0]);

            parseAddress(results2[0].toString());

           // String s= results2[0].toString();
           // char[] charArray = s.toCharArray();
           // String lat=s, lng=s;
            //lat.replaceAll("(\\\\d+).+","$1");
            //lng.replaceAll("(\\\\d+).+","$2");



        /*
        ArrayList<LatLng> points = new ArrayList<>();
        Matcher m = Pattern.compile("Geometry: \\((-?\\d+\\.\\d+),(-?\\d+\\.\\d+)\\)").matcher(s);
        while (m.find()) {
            points.add(new LatLng(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))));
        }*/
        //Log.wtf("zzzz", "zzzz"+ points.toString());

        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng shop = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(shop)
                .title("Aquiperto"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(shop));
        displayCurrentLocation(googleMap, shop);

    }
    public  void displayCurrentLocation(GoogleMap googleMap, LatLng latLng) {

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(latLng).
                zoom(50 ).
                bearing(0).
                build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
