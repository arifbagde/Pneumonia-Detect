package com.example.pneumoniadetect;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    TextView textView2;
    Button addNew;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;


    private static final int STORAGE_PER_CODE = 1;
    private static final int CAM_PER_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2 = findViewById(R.id.textView2);
        addNew = findViewById(R.id.addNew);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        checkPermission(Manifest.permission.CAMERA, CAM_PER_CODE);

       DocumentReference docRef = fStore.collection("users").document(userId);
       docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                textView2.setText("Welcome "+ documentSnapshot.getString("firstName"));
            }
        });


    }




    private void checkPermission(String permission, int code) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, code);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                    return;
            }

            case 2:{
                if(!(grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permission denied to Camera Access", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void addNew(View view){
        startActivity(new Intent(MainActivity.this, NewRecordActivity.class));
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PER_CODE);
    }

    public void prevRec(View view){
        startActivity(new Intent(MainActivity.this, PrevRecordActivity.class));
    }
}