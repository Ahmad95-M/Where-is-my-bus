package com.example.user.transport;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoutingListener, com.google.android.gms.location.LocationListener {

private GoogleMap mMap;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        LocationRequest mLocationRequest;
private Button mLogout,mRequest, mSettings;
private LatLng  pickupLocation;
private Boolean requestBol = false ;
private Marker pickupMarker;
    private Button mD2 ;

    protected LatLng start = new LatLng(32.036465,35.728084);
    protected LatLng end=new LatLng(31.973288,35.906955);
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
   //final int Location_Request_Code=1;
    private  String destination ,requestService;
    private  SupportMapFragment mapFragment;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_Code);
    }else {
        mapFragment.getMapAsync(this);

    }

        mLogout = (Button) findViewById(R.id.logout);
        mRequest = (Button) findViewById(R.id.request);
        mSettings = (Button) findViewById(R.id.Settings);
        mD2=(Button) findViewById(R.id.bus1);


        mLogout.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CustomerMapActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
        return;
        }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestBol){
                            requestBol= false;
                            geoQuery.removeAllListeners();
                            driverLocationRef.removeEventListener(driverLocationRefListener);



                            if (driverFoundID !=null){
                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                                    driverRef.setValue(true);
                                    driverFoundID=null;

                            }
                            driverFound =false;
                            radius =1;
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.removeLocation(userId);

                            if (pickupMarker !=null){ //diffrint from null if the marker doesn't exist the app will crash
                                    pickupMarker.remove();
                            }
                            if (mDriverMarker!=null){
                                    mDriverMarker.remove();
                            }

                            mRequest.setText("Call Transport ");


                    }else {
                            requestBol=true;

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                        pickupLocation= new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                        mRequest.setText("Getting your Bus ...");
                        getCloseestDriver();}
                }
        });
    mSettings.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CustomerMapActivity.this ,CustomerSettingsActivty.class);
            startActivity(intent);
            return;
        }
    });
    mD2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int buttonId= view.getId();
            GetRouting();
        }
    });

        /*PlaceAutocompleteFragment aceAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        aceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        destination= place.getName().toString();
                }

                @Override
                public void onError(Status status) {
                        // TODO: Handle the error.

                }
        });*/

        }



        private  int radius = 1;
        private  Boolean driverFound = false ;
        private  String driverFoundID;

        GeoQuery geoQuery ;
        private void  getCloseestDriver(){
                DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");

                GeoFire geoFire = new GeoFire(driverLocation);
                geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
                geoQuery.removeAllListeners();
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                               if (!driverFound && requestBol) {


                                   DatabaseReference mCustomerdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child(key);
                                   mCustomerdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                               Map <String ,Object> driverMap = ( Map <String ,Object>) dataSnapshot.getValue();
                                           if (driverFound){
                                           return;
                                           }
                                           if (driverMap.get("service").equals(requestService)){
                                               driverFound = true;
                                               driverFoundID = dataSnapshot.getKey();

                                               DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                               String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                               HashMap map = new HashMap();
                                               map.put("customerRideId",customerId);
                                               map.put("destination",destination);

                                               driverRef.updateChildren(map);


                                               getDriverLocation();
                                               mRequest.setText("Looking for Driver Location ...");
                                           }
                                           }
                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                       }
                                   });


                               }
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                                if (!driverFound){
                                        radius++;
                                        getCloseestDriver();
                                }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                });

        }
private Marker mDriverMarker;
        private  DatabaseReference driverLocationRef;
        private ValueEventListener driverLocationRefListener;
       private void  getDriverLocation(){
                driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("1");
               driverLocationRefListener=  driverLocationRef. addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                          if (dataSnapshot.exists() && requestBol){
                                  List<Object>map = (List<Object>) dataSnapshot.getValue();
                                  double locationLat=0;
                                  double locationLng=0;
                                  mRequest.setText("Driver Found");
                                  if (map.get(0) !=null){
                                          locationLat= Double.parseDouble(map.get(0).toString());

                                  }
                                  if (map.get(1) !=null){
                                          locationLng= Double.parseDouble(map.get(1).toString());
                                  }
                                  LatLng driverLatLng = new LatLng(locationLat,locationLng);
                                  if (mDriverMarker !=null){
                                          mDriverMarker.remove();
                                  }
                                  Location locl = new Location("");
                                  locl.setLatitude(pickupLocation.latitude);
                                  locl.setLongitude(pickupLocation.longitude);

                                  Location loc2 = new Location("");
                                  loc2.setLatitude(driverLatLng.latitude);
                                  loc2.setLongitude(driverLatLng.longitude);

                                  float distance = locl.distanceTo(loc2);
                                  if (distance<100){
                                          mRequest.setText("Driver is Here : ");

                                  }
                                  else
                                  {
                                          mRequest.setText("Driver Found : " + String.valueOf(distance));

                                  }


                                  mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus)));

                          }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
               });

       }

@Override
public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_Code);
    }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        }
protected synchronized void  buildGoogleApiClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient .connect();
        }




    private void GetRouting(){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start, end)
                .build();
        routing.execute();

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    final int Location_Request_Code=1;








@Override
public void onLocationChanged(Location location) {

        if (getApplicationContext() != null) {
                mLastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
}


@Override
public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_Code);
    }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

@Override
public void onConnectionSuspended(int i) {

        }

@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }


    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Location_Request_Code:{
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
@Override
protected void onStop() {
        super.onStop();

        }
        }

