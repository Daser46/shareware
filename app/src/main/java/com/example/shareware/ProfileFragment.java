package com.example.shareware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.shareware.databinding.FragmentProfileBinding;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private Home activity;
    FragmentProfileBinding binding;
    private TextView username, fname, lname, email, address, mobile;
    private Button logout, editBtn, settingsBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        SharedData data = new SharedData();
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        username = binding.getRoot().findViewById(R.id.usernameText);
        fname = binding.getRoot().findViewById(R.id.fnameText);
        lname = binding.getRoot().findViewById(R.id.lnameText);
        email = binding.getRoot().findViewById(R.id.emailText);
        mobile = binding.getRoot().findViewById(R.id.mobileText);
        editBtn = (Button) binding.getRoot().findViewById(R.id.editBtn);
        settingsBtn = (Button) binding.getRoot().findViewById(R.id.settingsBtn);
        logout = (Button) binding.getRoot().findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), App.class);
                startActivity(intent);
            }
        });
        username.setText(data.getCurrentUser().getUsername());
        fname.setText(data.getCurrentUser().getFirstname());
        lname.setText(data.getCurrentUser().getLastname());
        email.setText(data.getCurrentUser().getEmail());
        mobile.setText(data.getCurrentUser().getMobile());


        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDetailsFragment fragment = new EditDetailsFragment();
                activity.replaceFragment(fragment);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment fragment = new SettingsFragment();
                activity.replaceFragment(fragment);
            }
        });
    }


}


