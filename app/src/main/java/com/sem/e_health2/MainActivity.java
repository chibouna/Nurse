package com.sem.e_health2;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.sem.e_health2.DoctorActivity.changeStatusBarToWhite;
import static com.sem.e_health2.DoctorActivity.prefs1;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView registre ;
    ImageView BtLogin ;
    EditText email ;
    EditText password ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeStatusBarToWhite(this);


        mAuth = FirebaseAuth.getInstance();
        BtLogin = findViewById(R.id.imageView33);
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
                BtLogin.setOnClickListener(view -> Login());

        registre = findViewById(R.id.tc_registre);
        registre.setOnClickListener(ls);
    }




    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    View.OnClickListener ls = v -> startActivity(new Intent(MainActivity.this,Registre.class));
    private void updateUI(FirebaseUser currentUser) {


        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this,DoctorActivity.class);
            intent.putExtra("share", true);
            startActivity(intent);
        }

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
         alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        alertDialog.show();
    }
    public void Login(){       ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) { showDialog();}

      else  if (email.getText().toString().length() == 0){   email.setError("Email is required!");}
        else if (password.getText().toString().length() == 0) {   password.setError("Password is required!");}
        else {


            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        }
    }


}
