package com.example.shareware;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareware.databinding.FragmentItemViewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemViewFragment extends Fragment {
    private Home activity;
    private FragmentItemViewBinding binding;
    private Button back,request;
    private ImageView image1,image2,primaryImage,displayImage;
    private TextView username,itemName,itemCode,status,category,description,condition,location;
    private Bitmap bitmap1,bitmap2,primaryBitmap;
    private DatabaseReference dbr;
    private SharedData data;


    ImageView active;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemViewFragment newInstance(String param1, String param2) {
        ItemViewFragment fragment = new ItemViewFragment();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activity = (Home) getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemViewBinding.inflate(inflater,container,false);

        image1 = binding.getRoot().findViewById(R.id.image2);
        image2 = binding.getRoot().findViewById(R.id.image3);
        primaryImage = binding.getRoot().findViewById(R.id.mainImage);
        displayImage = binding.getRoot().findViewById(R.id.displayImage);

        username = binding.getRoot().findViewById(R.id.ownerName);
        itemCode = binding.getRoot().findViewById(R.id.itemCode);
        itemName = binding.getRoot().findViewById(R.id.itemName);
        description = binding.getRoot().findViewById(R.id.descriptionText);
        location = binding.getRoot().findViewById(R.id.locationText);
        condition = binding.getRoot().findViewById(R.id.condition);
        category = binding.getRoot().findViewById(R.id.categoryText);
        status = binding.getRoot().findViewById(R.id.statusText);

        request = binding.getRoot().findViewById(R.id.reqButton);

        dbr = activity.getDbr();
        data = new SharedData();
        Item item = data.getOnViewItem();
        dbr.child("items").child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item currentItem = snapshot.getValue(Item.class);
                setImageFromUrl(image1, currentItem.getImage1());
                setImageFromUrl(image2,currentItem.getImage2());
                setImageFromUrl(primaryImage, currentItem.getPrimaryImage());
                username.setText(currentItem.getOwner().getUsername());
                itemName.setText(currentItem.getName());
                itemCode.setText(currentItem.getId());
                description.setText(currentItem.getDescription());
                condition.setText(currentItem.getCondition());
                category.setText(currentItem.getCategory());
                status.setText(currentItem.getStatus());

                String loc = String.valueOf((int) currentItem.getLatitude()) + " " + String.valueOf((int) currentItem.getLongitude());
                location.setText(loc);
                Bitmap image = ((BitmapDrawable) primaryImage.getDrawable()).getBitmap();
                displayImage.setImageBitmap(image);
                if(currentItem.getStatus().equals("available") && !currentItem.getOwner().getUsername().equals(activity.getCurrentUser().getUsername())){
                    request.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentItem.setStatus("requested");
                            currentItem.setRequested(activity.getCurrentUser());
                            dbr.child("items").child(currentItem.getId()).setValue(currentItem);
                            status.setText(currentItem.getStatus());
                        }
                    });
                }else{
                    Toast.makeText(getActivity(),"You can't request this item", Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final float spToPxScaleFactor = getResources().getDisplayMetrics().scaledDensity;
        final int ACTIVE_WIDTH = (int) (120 * spToPxScaleFactor) ;
        final int ACTIVE_HEIGHT = (int) (94 * spToPxScaleFactor);
        final int WIDTH = (int) (107 * spToPxScaleFactor);
        final int HEIGHT = (int) (84 * spToPxScaleFactor);
        active = primaryImage;

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable) image1.getDrawable()).getBitmap();
                displayImage.setImageBitmap(image);
//                ViewGroup.LayoutParams alayoutParams = image1.getLayoutParams();
//                alayoutParams.width = ACTIVE_WIDTH;
//                alayoutParams.height = ACTIVE_HEIGHT;
//                image1.setLayoutParams(alayoutParams);
//                ViewGroup.LayoutParams layoutParams = image1.getLayoutParams();
//                layoutParams.width = WIDTH;
//                layoutParams.height = HEIGHT;
//                active.setLayoutParams(layoutParams);
//                active = image1;
            }

        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable) image2.getDrawable()).getBitmap();
                displayImage.setImageBitmap(image);
//                ViewGroup.LayoutParams alayoutParams = image2.getLayoutParams();
//                alayoutParams.width = ACTIVE_WIDTH;
//                alayoutParams.height = ACTIVE_HEIGHT;
//                image1.setLayoutParams(alayoutParams);
//                ViewGroup.LayoutParams layoutParams = image2.getLayoutParams();
//                layoutParams.width = WIDTH;
//                layoutParams.height = HEIGHT;
//                active.setLayoutParams(layoutParams);
//                active = image2;
            }

        });

        primaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable) primaryImage.getDrawable()).getBitmap();
                displayImage.setImageBitmap(image);
//                ViewGroup.LayoutParams alayoutParams = primaryImage.getLayoutParams();
//                alayoutParams.width = ACTIVE_WIDTH;
//                alayoutParams.height = ACTIVE_HEIGHT;
//                image1.setLayoutParams(alayoutParams);
//                ViewGroup.LayoutParams layoutParams = primaryImage.getLayoutParams();
//                layoutParams.width = WIDTH;
//                layoutParams.height = HEIGHT;
//                active.setLayoutParams(layoutParams);
//                active = primaryImage;
            }

        });



        back = (Button) binding.getRoot().findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.backToPrevFragment();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public void setImageFromUrl(ImageView imageView, String url){
        //primaryImage
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String imageUrl = url;

        Callable<Bitmap> loadImageCallable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        };
        Future<Bitmap> future = executor.submit(loadImageCallable); // a blocker for other threaders until loadImageCallable execute completely
        try {
            Bitmap bitmap = future.get(); // This blocks until the task is complete
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();

    }
}

