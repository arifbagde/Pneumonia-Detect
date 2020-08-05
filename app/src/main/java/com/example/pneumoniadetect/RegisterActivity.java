package com.example.pneumoniadetect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String  TAG = "";
    TextView signIn2;
    EditText firstName, lastName, email, password, confirm_password, phoneNo;
    Button registerUser;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String userID;

    FirebaseFirestore Pneumonia_db = FirebaseFirestore.getInstance();



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.Phone);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.progressBar);
        registerUser = findViewById(R.id.registerUser);
        signIn2 = findViewById(R.id.signIn2);

        mAuth = FirebaseAuth.getInstance();



        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Authenticate user
                final String Email = email.getText().toString();
                final String Password = password.getText().toString();
                String Fname = firstName.getText().toString();
                String Lname = lastName.getText().toString();


                if(TextUtils.isEmpty(Fname)){
                    firstName.setError("Enter First Name");
                    return;
                }

                if(TextUtils.isEmpty(Lname)){
                    lastName.setError("Enter Last Name");
                    return;
                }

                if(TextUtils.isEmpty(Email)){
                    email.setError("Enter Email id");
                    return;
                }

                if(TextUtils.isEmpty(Password) & password.length() < 8){
                    password.setError("Password must have at-least 8 Characters");
                    return;
                }

                if (!password.getText().toString().equals(confirm_password.getText().toString())) {
                    password.setError("Password should be same");
                    confirm_password.setError("Password should be same");
                    return;
                }
                if(phoneNo.length()<10){
                    phoneNo.setError("Enter valid phone no.");
                    return;
                }


                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();

                            Map<String, Object> new_user = new HashMap<>();
                            new_user.put("userId", userID);
                            new_user.put("firstName", firstName.getText().toString());
                            new_user.put("lasName", lastName.getText().toString());
                            new_user.put("phoneNo", phoneNo.getText().toString());
                            new_user.put("email", email.getText().toString());
                            new_user.put("password", password.getText().toString());

                            Pneumonia_db.collection("users").document(userID).set(new_user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + aVoid);
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        signIn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

