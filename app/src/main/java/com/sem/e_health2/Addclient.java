package com.sem.e_health2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sem.e_health2.DoctorActivity.changeStatusBarToWhite;

public class Addclient extends AppCompatActivity {

    private static final String TAG = "Addclient";
    EditText tnom ;
    EditText tprenom;
    EditText ttelephone;
    EditText tage;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth;
    DatabaseReference clienRef ;
    private StorageReference mStorageRef ;

    private static final int CAMERA_REQUEST = 1888;
    private CircleImageView imgProfile;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Bitmap photo ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        changeStatusBarToWhite(this);
        Intent intent = getIntent();
        String docID = intent.getStringExtra("docid");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference Ref = database.getReference("E-Health/Doctors/"+docID);
        clienRef = Ref.child("Clients");
        tnom = findViewById(R.id.name);
        tprenom = findViewById(R.id.lastname);
        ttelephone = findViewById(R.id.phone);
        tage =findViewById(R.id.tele);
        imgProfile =findViewById(R.id.img_profile);
        photo= BitmapFactory.decodeResource(imgProfile.getResources(),
                R.drawable.man_ic);
        findViewById(R.id.addc).setOnClickListener((v) ->{
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) { showDialog();}

          else  if (tnom.getText().toString().length() == 0 ){   tnom.setError("Name is required!");}
          else if(  tnom.getText().toString().length() <5 ){   tnom.setError("name should be more than 5 characters!");}
            else if (tprenom.getText().toString().length() == 0  ){   tprenom.setError("Last Name  is required!");}else if(tprenom.getText().toString().length() < 5){   tprenom.setError("name should be more than 5 characters!");}
            else if (tage.getText().toString().length() == 0 ){   tage.setError("Age is required!");}else if (Integer.parseInt(tage.getText().toString()) < 1 || Integer.parseInt(tage.getText().toString()) > 99){   tage.setError("Age Should Be between [1..99]");}
             else if (ttelephone.getText().toString().length() == 0 )  {   ttelephone.setError("Phone number is required!");}else if ( ttelephone.getText().toString().length()<8){ttelephone.setError("Phone Number Should Be Consists Of eight numbers");}

             else {

                    Patient et = new Patient();
                    et.setName(tnom.getText().toString());
                    et.setLastName(tprenom.getText().toString());
                    et.setPhone(ttelephone.getText().toString());
                    et.setAge(tage.getText().toString());
                    uploadImage(et) ;
                    startActivity(new Intent(Addclient.this,DoctorActivity.class));
                    finish();
                    }
        });



        findViewById(R.id.img_back).setOnClickListener((v) ->{
            finish();
        });


    }

    void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null);

        Button acceptButton = view.findViewById(R.id.acceptButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //  Log.e(TAG, "onClick: cancel button" );
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        alertDialog.show();
    }
    public void onChoosePhoto(View view) {
        Log.d(TAG, "onChoosePhoto: ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
             photo = (Bitmap) data.getExtras().get("data");
            imgProfile.setImageBitmap(photo);


        }
    }
    private void uploadImage( Patient patient) {
        String Photoname = UUID.randomUUID().toString();
        final StorageReference riversRef = mStorageRef.child("images/" + Photoname);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG,20,baos);
        byte[] bitmapData = baos.toByteArray();
        UploadTask uploadTask = riversRef.putBytes(bitmapData);
        uploadTask
                .addOnSuccessListener(taskSnapshot ->
                        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("urlPhotoTest",uri.toString());
                    patient.setImageUri(uri.toString());
                    clienRef.child(patient.getName()+" "+ patient.getLastName()).setValue(patient);


                    }));



    }


}
