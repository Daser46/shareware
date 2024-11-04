package com.example.shareware;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shareware.databinding.FragmentAddBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {
    StorageReference storageRef;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<Intent> launcher;
    private Home activity;
    private FragmentAddBinding binding;
    private TextInputEditText itemNameText,categoryText, conditionText,descriptionText;
    private DatabaseReference dbr;
    private String item,category, condition,description;
    private ImageView mainImage,image1,image2,imageView;
    private Item items;
    private LocationManager locationManager;
    private float lattitude, longitude;
 //   private Button addbtn,cancelBtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
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
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if(data != null){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }

        });
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://shareware-227a3.appspot.com");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activity = (Home) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater,container,false);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        itemNameText = binding.getRoot().findViewById(R.id.itemName);
        categoryText = binding.getRoot().findViewById(R.id.category);
        conditionText = binding.getRoot().findViewById(R.id.status);
        descriptionText = binding.getRoot().findViewById(R.id.description);
        mainImage = binding.getRoot().findViewById(R.id.mainImage);
        image1 = binding.getRoot().findViewById(R.id.image1);
        image2 = binding.getRoot().findViewById(R.id.image2);

        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = mainImage;
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraLaunch();
                }else{
                    activity.requestCameraPermission();
                }
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = image1;
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraLaunch();
                }else{
                    activity.requestCameraPermission();
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = image2;
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    cameraLaunch();
                }else{
                    activity.requestCameraPermission();
                }

            }
        });

        binding.getRoot().findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        binding.getRoot().findViewById(R.id.clearBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Fragment fragment = new AddFragment();//rip optimization
                activity.replaceFragment(new AddFragment());
            }
        });

        return binding.getRoot();
    }

    public void addItem(){

        item = String.valueOf(itemNameText.getText()).trim();
        category = String.valueOf(categoryText.getText()).trim();
        condition = String.valueOf(conditionText.getText()).trim();
        description = String.valueOf(descriptionText.getText()).trim();

        dbr = activity.getDbr();
        DatabaseReference child = dbr.child("items").push();
        String key = child.getKey();



        if(!(item.isEmpty() || category.isEmpty() || condition.isEmpty() || description.isEmpty())) {
            child.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(key)) {
                        getCurrentLocation();
                        Bitmap primaryImage = ((BitmapDrawable) mainImage.getDrawable()).getBitmap();
                        Bitmap imageOne = ((BitmapDrawable) image1.getDrawable()).getBitmap();
                        Bitmap imageTwo = ((BitmapDrawable) image2.getDrawable()).getBitmap();
                        items = new Item(key, item, description, lattitude, longitude, condition, category, activity.getCurrentUser(), "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQCUdvoKQ1oJrXKFRMqFgdKlHDjJehVDuzrwA&usqp=CAU");
                        child.setValue(items);
                        uploadBitmapToFirebaseStorage(primaryImage,key,"primaryImage");
                        uploadBitmapToFirebaseStorage(imageOne,key,"image1");
                        uploadBitmapToFirebaseStorage(imageTwo,key,"image2");

                        Toast.makeText(getActivity(), "Successfully Added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed, Try again!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getActivity(),"Please fill fields", Toast.LENGTH_SHORT).show();

        }

    }

    public void cameraLaunch(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(intent);
    }

    private void uploadBitmapToFirebaseStorage(Bitmap bitmap,String key, String child) {
        // Generate a unique filename for the image
        String filename = key + child + ".jpg";

        // Create a reference to the location where you want to upload the image
        StorageReference imageRef = storageRef.child("images/" + filename);

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] imageData = output.toByteArray();

        // Upload the byte array to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);

        // Add a listener to monitor the upload progress and handle the completion
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    activity.getDbr().child("items").child(key).child(child).setValue(url);
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getActivity(),exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(getActivity(),"Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentLocation(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGps();
        }else{
            getLocation();
        }
    }

    public void getLocation(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(locationGps != null){
                lattitude = (float)locationGps.getLatitude();//type cast to float
                longitude = (float)locationGps.getLongitude();
            }


        }else{
            activity.requestLocationPermission();
        }
    }
    public void onGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Turn on GPS").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}