package com.example.pneumoniadetect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class NewRecordActivity extends AppCompatActivity {
    public static final String TAG = "";
    TextView patientid, patientname, patientage, address, phone, symptoms;
    RadioButton radioBtn;
    RadioGroup gender;
    Button submit, btnCam, btnUpld;
    ImageView imgview;
    public static final int PICK_IMAGE = 1;

    FirebaseFirestore Pneumonia_db = FirebaseFirestore.getInstance();
    public FirebaseAuth mAuth;

    Calendar c = Calendar.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_activtiy);
        patientid = findViewById(R.id.patientId);
        patientname = findViewById(R.id.patientName);
        patientage = findViewById(R.id.patientAge);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phno);
        submit = findViewById(R.id.button);
        imgview = findViewById(R.id.imageView);
        btnCam = findViewById(R.id.camera);
        btnUpld = findViewById(R.id.upload);
        gender = findViewById(R.id.gender);
        symptoms = findViewById(R.id.symptoms);



        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        btnUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100) {
                //capture image
                Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                Bitmap resized = Bitmap.createScaledBitmap(captureImage, 512, 512, false);
                imgview.setImageBitmap(resized);

                //imgview.setImageBitmap(captureImage);
            } else if (requestCode == PICK_IMAGE && data != null) {
                Uri uri = data.getData();
                Bitmap bit_img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Bitmap resized = Bitmap.createScaledBitmap(bit_img, 512, 512, false);
                imgview.setImageBitmap(resized);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void submit(View view) {
        String pId = patientid.getText().toString();
        String pName = patientname.getText().toString();
        String pAge = patientage.getText().toString();
        String pAdd = address.getText().toString();
        String pPhno = phone.getText().toString();
        String symtm = symptoms.getText().toString();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());


        if (imgview == null) {
            Toast.makeText(NewRecordActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectId = gender.getCheckedRadioButtonId();
        radioBtn = findViewById(selectId);

        if (TextUtils.isEmpty(pId)) {
            patientid.setError("Enter patient id");
            return;
        }
        if (TextUtils.isEmpty(pName)) {
            patientname.setError("Enter patient name");
            return;
        }
        if (TextUtils.isEmpty(pAge)) {
            patientage.setError("Enter patient age");
            return;
        }
        if (TextUtils.isEmpty(pAdd)) {
            address.setError("Enter Address");
            return;
        }
        if (phone.length() < 10 || phone.length() > 10) {
            phone.setError("Enter valid phone no");
            return;
        }
        if (TextUtils.isEmpty(symtm)) {
            symptoms.setError("Enter symptoms");
            return;
        }


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
try {
    Map<String, Object> patient = new HashMap<>();
    patient.put("Patient_Id", pId);
    patient.put("Patient_Name", pName);
    patient.put("Patient_Age", pAge);
    patient.put("Gender", radioBtn.getText().toString());
    patient.put("Patient_Address", pAdd);
    patient.put("Patient_Phno", pPhno);
    patient.put("Symptoms", symtm);
   // patient.put("X-Ray", imgview);
    patient.put("userId", userId);
    patient.put("Date", formattedDate);


    Pneumonia_db.collection("patientDetails").document(pId).set(patient)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + aVoid);
                    startActivity(new Intent(NewRecordActivity.this, MainActivity.class));
                    Toast.makeText(NewRecordActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                }

            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(NewRecordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewRecordActivity.this, MainActivity.class));

                }
            });
}catch(Exception e){
    Toast.makeText(NewRecordActivity.this, "Exception"+e, Toast.LENGTH_LONG).show();
        }

    }
}



