package com.sem.e_health2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.sem.e_health2.DoctorActivity.changeStatusBarToWhite;

public class Registre extends AppCompatActivity {
    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    ImageView BtRegistre ;
    EditText user ;
    EditText email ;
    EditText password ;
    EditText confirmPassword;
    TextView login ;
    String item ;
    public static NiceSpinner niceSpinner;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        changeStatusBarToWhite(this);
        login = findViewById(R.id.tc_login);
        mAuth = FirebaseAuth.getInstance();
        BtRegistre = findViewById(R.id.imageView3);
        email = findViewById(R.id.edt_email2);
        password = findViewById(R.id.edt_password2);
        confirmPassword =findViewById(R.id.edt_password_confirm);
        user= findViewById(R.id.edt_username2);
        List<String> spinnerArray =  new ArrayList<>();

        dbRef = database.getReference("E-Health/Doctors");
        dbRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        niceSpinner = findViewById(R.id.nice_spinner);
                        for(DataSnapshot data : dataSnapshot.getChildren()) {

                            spinnerArray.add(data.getKey());

                        }
                        niceSpinner.attachDataSource(spinnerArray);
                        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                            @Override
                            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                                // This example uses String, but your type can be any
                                item = parent.getItemAtPosition(position).toString();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // on error
                    }
                });




        login.setOnClickListener((v -> finish()));
        findViewById(R.id.img_back_sign_up).setOnClickListener((v -> finish()));

        BtRegistre.setOnClickListener(view -> SignUP());

    }


    private void updateUI(FirebaseUser currentUser) {


        if(currentUser != null){


            Intent intent = new Intent(Registre.this,DoctorActivity.class);
            intent.putExtra("user",user.getText().toString());
            intent.putExtra("doc",item);
            startActivity(intent);
        }

    }
    public void SignUP(){


        String Spassword =  password.getText().toString() ;
        String Sconfirmpassword = confirmPassword.getText().toString() ;

        if ( !Sconfirmpassword.equals( Spassword)){

            confirmPassword.setError("Passwords do not match !");


        }
        else if (user.getText().toString().length() == 0) {   user.setError("UserName is required!");}
        else if (email.getText().toString().length() == 0){   email.setError("Email is required!");}
        else if (password.getText().toString().length() == 0) {   password.setError("Password is required!");}
        else {

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);


                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registre.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    });
        }




    }







}
