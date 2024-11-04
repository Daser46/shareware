package com.example.shareware;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.example.shareware.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://shareware-227a3-default-rtdb.firebaseio.com/");
    private FloatingActionButton addbtn;
    private Context context = this;
    private String user;
    private User currentUser;
    private ArrayList<Item> items;
    private Bitmap bitmap;
    private Item item;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedData data = new SharedData();

        addbtn = findViewById(R.id.addbtn);
        user = getIntent().getStringExtra("user");
//        items = new ArrayList<Item>();

        dbr.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                data.setUser(currentUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLocationPermissionGranted()){
                    replaceFragment(new AddFragment());
                    addbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                }else{
                    requestLocationPermission();
                }

            }
        });

        replaceFragment(new HomeFragment());
        binding.bottomnav.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.nav_home:
                    resetColor();
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.nav_maps:
                    resetColor();
                    MapFragment fragment = new MapFragment();
                    if(!isLocationPermissionGranted()){
                        requestLocationPermission();
                    }else{
                        replaceFragment(fragment);
                    }
                    break;
                case R.id.nav_profile:
                    resetColor();
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.nav_items:
                    resetColor();
                    replaceFragment(new ItemFragment());
                    break;
                case R.id.nav_plus:
                    if(isLocationPermissionGranted()){
                        replaceFragment(new AddFragment());
                        addbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                    }else{
                        requestLocationPermission();
                    }
                    break;
            }
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void resetColor(){
        addbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_main)));
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

    public void replaceFragmentWithBack(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void backToPrevFragment(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public boolean isLocationPermissionGranted(){
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    public void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
    }
//////////////////////////////additional methods to call upon////////////////////////////////////////
//    public void retrieveItems() {
//        dbr.child("items").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<Item> items = new ArrayList<>();
//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    item = childSnapshot.getValue(Item.class);
//                    items.add(item);
//                    Log.i("length", item.getDescription());
//                    Log.i("length", String.valueOf(items.size()));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    public void onDataLoaded(ArrayList<Item> items) {
//        // Update the items list in the activity
//        this.items = items;
//
//        // Update the adapter and notify it
//        ListViewAdapter listViewAdapter = new ListViewAdapter(Home.this, items);
//        listView = findViewById(R.id.listView);
//        listView.setAdapter(listViewAdapter);
//        listViewAdapter.notifyDataSetChanged();
//
//        // Log the size of the items list
//        Log.i("length", String.valueOf(items.size()));
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////



    public Bitmap getBitmap(){
        return this.bitmap;
    }

    public DatabaseReference getDbr(){
        return this.dbr;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public ArrayList<Item> getItems(){ return this.items;}
}