package com.example.teachcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_profile){

            Intent profileIntent = new Intent (MainActivity.this, UserProfile.class);
            startActivity(profileIntent);
            //startActivity(new intent(getApplicationContext(), UserProfile.class));

        }

//        if(id == R.id.action_settings){
//
//            Intent profileIntent = new Intent (MainActivity.this, UserProfile.class)
//            startActivity(profileIntent);
//
//
//        }
//
//        if(id == R.id.action_home){
//
//            Intent profileIntent = new Intent (MainActivity.this, UserProfile.class)
//            startActivity(profileIntent);
//
//        }
//
//        if(id == R.id.action_progress){
//
//            Intent profileIntent = new Intent (MainActivity.this, UserProfile.class)
//            startActivity(profileIntent);
//
//        }


        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut(); // user logout
        startActivity(new Intent(getApplicationContext(), Login.class));  // start Login Activity
        finish();
    }
}
