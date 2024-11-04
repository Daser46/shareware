package com.example.shareware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareware.databinding.FragmentEditDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditDetailsFragment extends Fragment {
    private DatabaseReference oldNode;
    private FirebaseAuth uAuth = FirebaseAuth.getInstance();
    private Home activity;
    FragmentEditDetailsBinding binding;
    Button cancel;
    Button edit;
    SharedData data;
    TextInputEditText username, firstname, lastname, email, address, mobile;
    String usernameText, firstnameText, lastnameText, emailText, addressText, mobileText;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditDetailsFragment newInstance(String param1, String param2) {
        EditDetailsFragment fragment = new EditDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activity = (Home) getActivity();
        }
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
        binding = FragmentEditDetailsBinding.inflate(inflater,container,false);

        data = new SharedData();
        edit = (Button) binding.getRoot().findViewById(R.id.done);
        cancel = (Button) binding.getRoot().findViewById(R.id.cancel);

        //take a copy of node of the current user
        oldNode = FirebaseDatabase.getInstance().getReference("users/"+data.getCurrentUser().getUsername());


        username = binding.getRoot().findViewById(R.id.username);
        firstname = binding.getRoot().findViewById(R.id.firstname);
        lastname = binding.getRoot().findViewById(R.id.lastname);
        email = binding.getRoot().findViewById(R.id.email);
        address = binding.getRoot().findViewById(R.id.address);
        mobile = binding.getRoot().findViewById(R.id.mobile);

        username.setText(data.getCurrentUser().getUsername());
        firstname.setText(data.getCurrentUser().getFirstname());
        lastname.setText(data.getCurrentUser().getLastname());
        email.setText(data.getCurrentUser().getEmail());
        address.setText(data.getCurrentUser().getAddress());
        mobile.setText(data.getCurrentUser().getMobile());


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.replaceFragment(new ProfileFragment());
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDetails();
                activity.replaceFragment(new ProfileFragment());
            }
        });
        return binding.getRoot();
    }



    public void changeDetails(){
        usernameText = username.getText().toString().trim();
        firstnameText = firstname.getText().toString().trim();
        lastnameText = lastname.getText().toString().trim();
        emailText = email.getText().toString().trim();
        addressText = address.getText().toString().trim();
        mobileText = mobile.getText().toString().trim();

        User updatedUser = new User(usernameText, data.getCurrentUser().getID(), data.getCurrentUser().getPassword(), firstnameText, lastnameText, mobileText, emailText);
        updatedUser.setAddress(addressText);
        //validation
        if(usernameText.isEmpty()){
            return;
        }
        else if(firstnameText.isEmpty()){
            return;
        }
        else if(lastnameText.isEmpty()){
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return;
        }else if(mobileText.length() <= 0 || !Patterns.PHONE.matcher(mobileText).matches()) {
            return;
        }else if(!(emailText.isEmpty() || data.getCurrentUser().getEmail().equals(emailText))){

            //this is kinda hackish way, that I'm authenticating user and get a firebase user, In real world should ask for sign in again or re-auth
            // for changing sensitive data and gonna need one more UI, I'm sorry for doing this :(
            uAuth.signInWithEmailAndPassword(data.getCurrentUser().getEmail(), data.getCurrentUser().getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = uAuth.getCurrentUser();
                        user.updateEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if(!(usernameText.isEmpty() || data.getCurrentUser().getUsername().equals(usernameText))){
                                        upDateUser(updatedUser);
                                    }else{
                                        oldNode.setValue(updatedUser);

                                    }
                                } else {
                                    Toast.makeText(activity,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                    }
                }
            });
            // this is the correct way I think, but should take user inputs and assigned them to credentials. And also parse firebase user for the fragment, activity or shared data when u signed in for the first time.

//            AuthCredential credential = EmailAuthProvider.getCredential(data.getCurrentUser().getEmail(), data.getCurrentUser().getPassword());
//            FirebaseUser user = uAuth.getCurrentUser();
//            assert user != null;
//
//
//            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        user.updateEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    if(!(usernameText.isEmpty() || data.getCurrentUser().getUsername().equals(usernameText))){
//                                        upDateUser(updatedUser);
//                                    }else{
//                                        oldNode.setValue(updatedUser);
//
//                                    }
//                                } else {
//                                    Toast.makeText(activity,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(activity,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }else if(!(usernameText.isEmpty() || data.getCurrentUser().getUsername().equals(usernameText))){
            upDateUser(updatedUser);
        }else{
            oldNode.setValue(updatedUser);
        }
        data.setUser(updatedUser);

    }




    public void  upDateUser(User user){
        oldNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Set the new node with the updated username
                if (!snapshot.hasChild(user.getUsername())) {
                    DatabaseReference newDbRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUsername());
                    newDbRef.setValue(user);

                    // Delete the old node
                    oldNode.removeValue();
                }else{
                    Toast.makeText(activity,"Username Taken", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here
                Toast.makeText(activity,error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}