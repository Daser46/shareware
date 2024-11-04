package com.example.shareware;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class App extends AppCompatActivity {
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://shareware-227a3-default-rtdb.firebaseio.com/");
    FirebaseAuth uAuth = FirebaseAuth.getInstance();
    Button loginbtn, regbtn, login, register;
    private TextInputEditText username,password,newUsername,newPassword,firstname,lastname,mobile,email;
   // final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        regbtn = (Button) findViewById(R.id.regbtn);
        login = (Button) findViewById(R.id.loginbtn);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister(v);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authentication();
            }
        });

    }
    public void moveToRegister(View v){
        setContentView(R.layout.activity_reg);
        //move back to login screen
        loginbtn = (Button) findViewById(R.id.loginbutton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLogin(v);
            }
        });

        //process registration
        register = findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registration(view)){
                    Toast.makeText(App.this, "Now Login !", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(App.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void moveToLogin(View v){
        setContentView(R.layout.activity_app);
        regbtn = (Button) findViewById(R.id.regbtn);
        login = (Button) findViewById(R.id.loginbtn);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister(v);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authentication();
            }
        });
    }

    /**
     * @author s92062870@ousl.lk
     * login using email or username and create a session
     * @return void
     */
    public void authentication(){
        username = findViewById(R.id.logUserName);
        password = findViewById(R.id.logPassword);

        String user = username.getText().toString().trim();
        String userPW = password.getText().toString();

        if(user.isEmpty()){
            username.setError("empty fields");

        }else if(userPW.isEmpty()){
            password.setError("empty fields");
        }
        //Authentication via email
        else if(Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            uAuth.signInWithEmailAndPassword(user, userPW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(App.this, MainActivity.class);
                        intent.putExtra("auth", true);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(App.this, "Invalid Login!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            dbr.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User currentUser = snapshot.getValue(User.class);
                        if(currentUser.getPassword().equals(userPW)){
                            Intent intent = new Intent(App.this, MainActivity.class);
                            intent.putExtra("auth", true);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(App.this, "Invalid Login!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(App.this, "Invalid Login!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * @author s92062870@ousl.lk
     * do the registration of user
     * @param v Take View to navigate back to login
     * @return true on successful registration
     */

    public boolean registration(View v){
        newUsername = findViewById(R.id.logUserName);
        newPassword = findViewById(R.id.logPassword);
        firstname = findViewById(R.id.regFirstname);
        lastname = findViewById(R.id.regLastname);
        mobile = findViewById(R.id.regNumber);
        email = findViewById(R.id.emailinput);

        String uName = String.valueOf(newUsername.getText()).trim();
        String pw = String.valueOf(newPassword.getText());
        String fname = String.valueOf(firstname.getText()).trim();
        String lname = String.valueOf(lastname.getText()).trim();
        String phone = String.valueOf(mobile.getText()).trim();
        String uemail = String.valueOf(email.getText()).trim();

        if(uName.isEmpty()){
            newUsername.setError("empty username");
            newUsername.requestFocus();
            return false;
        }
        else if(pw.isEmpty() || pw.length() < 6){
            newPassword.setError("password is too short");
            newPassword.requestFocus();
            return false;
        }
        else if(fname.isEmpty()){
            firstname.setError("empty field");
            firstname.requestFocus();
            return false;
        }
        else if(lname.isEmpty()){
            lastname.setError("empty field");
            lastname.requestFocus();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()){
            email.setError("invalid email");
            email.requestFocus();
            return false;
        }else if(phone.length() <= 0 || !Patterns.PHONE.matcher(phone).matches()){
            mobile.setError("invalid phone");
            mobile.requestFocus();
            return false;
        }else{
            dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(uName)){
                        uAuth.createUserWithEmailAndPassword(uemail,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String id = task.getResult().getUser().getUid();
                                    User user = new User(uName, id, pw, fname, lname, phone, uemail);
                                    dbr.child("users").child(uName).setValue(user);
                                    status = true;
                                    moveToLogin(v);
                                }else{
                                    Toast.makeText(App.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        Toast.makeText(App.this,"Username Taken", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            return status;
        }

    }

}