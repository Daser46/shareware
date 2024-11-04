package com.example.shareware;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.shareware.databinding.FragmentMapBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap mMap;
    private FragmentMapBinding binding;
    private Sensor sensorMagnetic;
    private Sensor sensorAccelerometer;
    private SensorManager sensorManager;
    private DatabaseReference dbr;
    private Item item;
    private ArrayList<Item> items;
    private Home activity;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] rotationMatrix = new float[9];
    private float[] floatOrientation = new float[3];
    private ImageView arrow;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activity = (Home) getActivity();
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater,container,false);
        dbr = activity.getDbr();
        items = new ArrayList<>();

        arrow = binding.getRoot().findViewById(R.id.pointerNorth);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34,151);
        for(Item item: items){
            float lat = item.getLatitude();
            float lng = item.getLongitude();
            LatLng pickup = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(pickup).title(item.getId()));
        }
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this , sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this , sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //Sensor handling method which call in onResume method when changes happen in sensordata
    //im trying to direct my arrow to north always as compass.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //check the sensor type
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            floatGravity = sensorEvent.values;
        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            floatGeoMagnetic = sensorEvent.values;
        }
        SensorManager.getRotationMatrix(rotationMatrix, null, floatGravity, floatGeoMagnetic);
        SensorManager.getOrientation(rotationMatrix, floatOrientation);
        // rotating arrow for the opposite direction to the orientation
        arrow.setRotation((float) (-floatOrientation[0]*180/Math.PI));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void retrieveRequested(){
        dbr.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot childSnapshot: snapshot.getChildren()){
                    //Log.i("item Code : ", itemCode);
                    item = childSnapshot.getValue(Item.class);
                    if (item.getStatus().equals("requested") && item.getRequested().getUsername().equals(activity.getCurrentUser().getUsername())) {
                        items.add(item);
                    }


                    //  Log.i("length", item.getDescription());
                    // Log.i("length", String.valueOf(items.size()));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}