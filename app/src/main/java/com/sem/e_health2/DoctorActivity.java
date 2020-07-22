package com.sem.e_health2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class DoctorActivity extends AppCompatActivity implements ContactAdapter.ItemClickListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference delRf ;
    DatabaseReference Ref ;
    FloatingActionButton plus ;
    DatabaseReference docUsername;
    DatabaseReference docSubref;
    String docteurName ;
    FirebaseAuth mAuth;
    ContactAdapter Adapter ;
    EditText searchBar ;
    TextView tvdocusername;
    RelativeLayout rl;
    RecyclerView recyclerview ;
    List<Patient> listData= new ArrayList<>();
    public static SharedPreferences.Editor editor ;
    public static SharedPreferences prefs1;
     boolean firstStart;
     SharedPreferences prefs ;
    CircularProgressView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor1);
        changeStatusBarToWhite(this);
        recyclerview = findViewById(R.id.RC);
        enableSwipeToDeleteAndUndo();
        plus = findViewById(R.id.add);
        searchBar = findViewById(R.id.edt_search);
        Adapter = new ContactAdapter(this,listData) ;
        Adapter.setClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        tvdocusername = findViewById(R.id.txt_doctor_name);
        loader = findViewById(R.id.progress_view);
        rl = findViewById(R.id.rl);

        Ref = database.getReference("E-Health/Client name");

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerview.setAdapter(Adapter);
        recyclerview.setHasFixedSize(true);

        Intent intent = getIntent();
        Boolean share = intent.getBooleanExtra("share",false);
        String nurseName = intent.getStringExtra("user");
        String docName = intent.getStringExtra("doc");
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        if(share == true){
            SharedPreferences prefs2 = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor  editor2 = prefs2.edit();
            editor2.putBoolean("firstStart", false);
            editor2.apply();}
        DatabaseReference myRef1 = database.getReference("E-Health");
        firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            DatabaseReference Doc = myRef1.child("Nurses");
            Doc.child(Sub()).child("Username").setValue(nurseName);
            Doc.child(Sub()).child("UID").setValue(mAuth.getUid());
            Doc.child(Sub()).child("doctor").setValue(docName);
        }
        prefs1 = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs1.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
        docSubref = database.getReference("E-Health/Nurses/" + Sub() + "/doctor");
        docUsername = database.getReference("E-Health/Nurses/" + Sub() + "/Username");
        docSubref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docteurName = (String) dataSnapshot.getValue();
                delRf = database.getReference("E-Health/Doctors/"+docteurName);
                DatabaseReference myRef = database.getReference("E-Health/Doctors/"+docteurName+"/Clients");
                myRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Patient patient;
                        listData.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            patient = ds.getValue(Patient.class);
                            if (patient != null) {
                                listData.add(patient);

                            }

                        }
                        Adapter.notifyDataSetChanged();
                        rl.setVisibility(View.VISIBLE);
                        plus.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        docUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nurseName = (String) dataSnapshot.getValue();
                tvdocusername.setText("Nrs."+nurseName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });

        plus.setOnClickListener(v ->{

            Intent intent1 = new Intent(DoctorActivity.this,Addclient.class);

            intent1.putExtra("docid",docteurName);
            startActivity(intent1);



        });



    }
    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Patient> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (Patient s : listData) {
            //if the existing elements contains the search input
            if (s.getnamaLastName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        Adapter.filterList(filterdNames);
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {



                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(DoctorActivity.this)
                        // set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete")
                        .setIcon(R.drawable.delete)

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                final int position = viewHolder.getAdapterPosition();
                                if (position+1 == listData.size()){
                                    Toast.makeText(DoctorActivity.this, "Can't delete last Patient", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                                else {
                                    Adapter.removeItem(position, delRf);
                                    dialog.dismiss();
                                }
                            }

                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());


                            }
                        })
                        .create();
                myQuittingDialogBox.setCanceledOnTouchOutside(false);
                myQuittingDialogBox.show();


            }

        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerview);
    }

    public String Sub(){

        String filename = (mAuth.getCurrentUser().getEmail());
        int iend = filename.indexOf("@");

        String subString;
        if (iend != -1)
        {
            subString= filename.substring(0 , iend); //this will give abc
            return subString ;
        }
        return null ;
    }


    public static void changeStatusBarToWhite(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            activity.getWindow().setStatusBarColor(Color.rgb(255,255,255));

        }
    }

    public void onLoggedOut(View view) {
        editor.putBoolean("firstStart", true);
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(DoctorActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onHistoryItemClickListener(View view, int position) {
        Intent intent = new Intent(DoctorActivity.this,Addtest.class);
        Ref.setValue(listData.get(position).getName()+" "+listData.get(position).getLastName());
        intent.putExtra("name",listData.get(position).getName());
        intent.putExtra("lastname",listData.get(position).getLastName());
        intent.putExtra("docid",docteurName);
        startActivity(intent);

    }

    @Override
    public void onCallItemClickListener(View view, int position) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listData.get(position).getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
}
