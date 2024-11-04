package com.example.shareware;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shareware.databinding.FragmentHomeBinding;
import com.example.shareware.databinding.FragmentItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {
    private FragmentItemBinding binding;
    private Home activity;
    private CardView cardView;
    private ListView listView;
    private DatabaseReference dbr;
    private Item item;
    private ArrayList<Item> items;
    private Button requested, posted;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemFragment() {
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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
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
        binding = FragmentItemBinding.inflate(inflater,container,false);
        dbr = activity.getDbr();
        items = new ArrayList<Item>();// i have seen in new updated ArryaList<>(); can be instanciate like this
        requested = binding.getRoot().findViewById(R.id.requested);
        posted = binding.getRoot().findViewById(R.id.posted);
        listView = binding.getRoot().findViewById(R.id.list);
        retrieveRequested();


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveRequested();
            }
        });
        posted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrievePosted();
            }
        });
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
                Log.i("Array length", String.valueOf(items.size()));
                ListAdapter listAdapter = new ListAdapter(getActivity(), items); // change here
                listView.setAdapter(listAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retrievePosted(){
        dbr.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot childSnapshot: snapshot.getChildren()){
                    //Log.i("item Code : ", itemCode);
                    item = childSnapshot.getValue(Item.class);
                    if(item.getOwner().getUsername().equals(activity.getCurrentUser().getUsername())) {
                        items.add(item);

                    }

                    //  Log.i("length", item.getDescription());
                    // Log.i("length", String.valueOf(items.size()));
                }
                Log.i("Array length", String.valueOf(items.size()));
                ListAdapter listAdapter = new ListAdapter(getActivity(), items); //change here
                listView.setAdapter(listAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}