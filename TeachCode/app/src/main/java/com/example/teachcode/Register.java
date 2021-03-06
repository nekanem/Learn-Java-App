package com.example.teachcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.opencensus.tags.Tag;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mfullNameRegEditText, memailRegEditText, mpasswordRegEditText, mphoneRegEditText;
    Button mRegisterBtn;
    TextView mcreateRegText;
    FirebaseAuth firebaseAuth;  // provide by firebase
    ProgressBar progressRegBar;
    FirebaseFirestore firestore;
    String userID;
    CheckBox mcheckBoxStudent, mcheckBoxTeacher;
    private static final String TAG = "Register";
    String userType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mfullNameRegEditText = findViewById(R.id.fullNameRegEditText);
        memailRegEditText = findViewById(R.id.emailRegEditText);
        mpasswordRegEditText = findViewById(R.id.passwordRegEditText);
        mphoneRegEditText = findViewById(R.id.phoneRegEditText);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mcreateRegText = findViewById(R.id.createRegText);
        mcheckBoxStudent = findViewById(R.id.checkBoxStudent);
        mcheckBoxTeacher = findViewById(R.id.checkBoxTeacher);

        // instantiate the fire base stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressRegBar = findViewById(R.id.progressRegBar);


        // user already has an account
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // direct to main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // user does not have an account and need to register
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = memailRegEditText.getText().toString().trim();
                String password = mpasswordRegEditText.getText().toString().trim();
                final String fullName = mfullNameRegEditText.getText().toString();
                final String phone = mphoneRegEditText.getText().toString();
                final boolean studentUser = mcheckBoxStudent.isChecked();
                final boolean teacherUser = mcheckBoxTeacher.isChecked();
                String userType = "";

                // if email or password is empty
                if (TextUtils.isEmpty(email)) {
                    memailRegEditText.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mpasswordRegEditText.setError("Password is required.");
                    return;
                }
                // password length must be greater than 6
                if (password.length() < 6) {
                    mpasswordRegEditText.setError("Password must at least 6 characters.");
                    return;
                }

                if (studentUser && teacherUser) {
                    Toast.makeText(Register.this, "Please select only one role", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!studentUser && !teacherUser) {
                    Toast.makeText(Register.this, "Please select only one role", Toast.LENGTH_LONG).show();
                    return;
                }
                if(studentUser){
                    userType="student";
                }
                if(teacherUser){
                    userType="teacher";
                }

                progressRegBar.setVisibility(View.VISIBLE);

                // register the user in firebaseno

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if task is successful -> we success create a user
                        if (task.isSuccessful()) {
                            // verify email sending link
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification link has been sent to your email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "sendEmailVerification - onFailure: failed sending verify link. " + e.toString());
                                }
                            });
                            Toast.makeText(Register.this, "User successfully created.", Toast.LENGTH_SHORT).show();


                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("users").document(userID);
                            Map<String, Object> userDB = new HashMap<>();
                            if (studentUser) {
                                // store user's data into fire store database


                                userDB.put("fullName", fullName);
                                userDB.put("email", email);
                                userDB.put("phone", phone);
                                userDB.put("userType", "student");


                                documentReference.set(userDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "documentRef - onSuccess: user Profile is created for " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "documentRef - onFailure: failed creating profile " + e.toString());
                                    }
                                });
                            } else if (teacherUser) {
                                // store user's data into fire store database

                                userDB.put("fullName", fullName);
                                userDB.put("email", email);
                                userDB.put("phone", phone);
                                userDB.put("userType", "teacher");

                                documentReference.set(userDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "documentRef - onSuccess: user Profile is created for " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "documentRef - onFailure: failed creating profile " + e.toString());
                                    }
                                });

                            }


                            // direct to main activity
                            progressRegBar.setVisibility(View.VISIBLE);
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        } else {
                            Toast.makeText(Register.this, "Error. Register new user failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressRegBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // already a user option. go straight to login
        mcreateRegText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressRegBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
}