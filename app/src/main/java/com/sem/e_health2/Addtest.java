package com.sem.e_health2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static com.sem.e_health2.DoctorActivity.changeStatusBarToWhite;

public class Addtest extends AppCompatActivity {

    private static final String TAG = "Addtest";
    List<Test> testList = new ArrayList<>();
    RecAdapter adapter ;
    RecyclerView recyclerview ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference tempRef;
    DatabaseReference glucRef;
    DatabaseReference hardbeatsRef;
    DatabaseReference emgRef;
    DatabaseReference patientRef;
    String name ;
    String lastname ;
    String docID ;
    String temp ;
    String emg ;
    String hartbeats ;
    String glucose ;
    String finalDate;
    DatabaseReference testRef ;
    LodingDialog loadingDialog;
    MutableLiveData<Boolean> ok = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_tests);
        changeStatusBarToWhite(this);
        recyclerview = findViewById(R.id.RC1);
        enableSwipeToDeleteAndUndo();
         loadingDialog =new LodingDialog(Addtest.this);

        adapter = new RecAdapter(this,testList);
       ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);


       findViewById(R.id.img_back).setOnClickListener((v)->{
           finish();
       });
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        lastname = intent.getStringExtra("lastname");
        docID = intent.getStringExtra("docid");

        tempRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Temp");
        hardbeatsRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Heart Beats");
        emgRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/EMG");
        glucRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Glucose");
        patientRef = database.getReference("E-Health/Client live test ");

        DatabaseReference myRef = database.getReference("E-Health/Doctors/"+docID+"/Clients TESTS");
        testRef = myRef.child(name+" "+lastname+" TESTS");
        Date c = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dateformatted = dateFormat.format(date);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        finalDate = formattedDate +" "+dateformatted ;
        FloatingActionButton fab = findViewById(R.id.fab);
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ok.setValue(false);
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue().toString());
                if(dataSnapshot.hasChild(name+" "+lastname)) {
                    if (dataSnapshot.hasChild(name+" "+lastname+"/Temp") && dataSnapshot.hasChild(name+" "+lastname+"/EMG") &&
                            dataSnapshot.hasChild(name+" "+lastname+"/Glucose") && dataSnapshot.hasChild(name+" "+lastname+"/Heart Beats")) {
                        Log.d(TAG, "onDataChange: data received");
                        for (DataSnapshot data : dataSnapshot.child(name+" "+lastname).getChildren()) {
                            Log.d(TAG, "onDataChange: " + data.toString());
                            Log.d(TAG, "onDataChange:value: " + data.getValue().toString());
                            if (data.getKey().equals("EMG"))
                                emg = data.getValue().toString();
                            if (data.getKey().equals("Glucose"))
                                glucose = data.getValue().toString();
                            if (data.getKey().equals("Heart Beats"))
                                hartbeats = data.getValue().toString();
                            if (data.getKey().equals("Temp"))
                                temp = data.getValue().toString();
                        }
                    ok.setValue(true);



                    } else {
                        Log.d(TAG, "onDataChange: something went wrong");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference notifRef = database.getReference("E-Health/Doctors/"+docID);
        DatabaseReference notifyGlucosepRef = notifRef.child("NotificationGlucose");
        DatabaseReference notifyHeartbeatsRef = notifRef.child("NotificationHeartbeats");
        DatabaseReference notifyTempRef = notifRef.child("NotificationTemp");

        fab.setOnClickListener(v ->{
            loadingDialog.StartLodingDialog();
            ok.observe(this, aBoolean -> {
                if(aBoolean) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            loadingDialog.DismissDialog();;
                            Test test = new Test();
                            test.setTime(finalDate);
                            test.setTemp(temp);
                            test.setEmg(emg);
                            test.setGlucose(glucose);
                            test.setHartbeats(hartbeats);
                            testRef.child(finalDate).setValue(test);
                            if (Double.valueOf(test.getGlucose()) < 80 ){notifyGlucosepRef.child("Value").setValue("The Glucose of "+name+" "+lastname+" is Low");}
                            else if (Double.valueOf(test.getGlucose()) > 160){notifyGlucosepRef.child("Value").setValue("The Glucose of "+name+" "+lastname+" is High");}

                            if (Double.valueOf(test.getHartbeats()) < 60 ){notifyHeartbeatsRef.child("Value").setValue("The Heartbeats of "+name+" "+lastname+" is Low");}
                            else if (Double.valueOf(test.getHartbeats()) > 100){notifyHeartbeatsRef.child("Value").setValue("The Heartbeats of "+name+" "+lastname+" is High");}

                            if (Double.valueOf(test.getTemp()) < 35 ){notifyTempRef.child("Value").setValue("The Temperature of "+name+" "+lastname+" is Low");}
                            else if (Double.valueOf(test.getTemp()) > 38.9){notifyTempRef.child("Value").setValue("The temperature of "+name+" "+lastname+" is High");}
                            recreate();
                        }
                    }, 5000);


                }

            });

        });

        testRef.addValueEventListener(vel);
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {



                    AlertDialog myQuittingDialogBox = new AlertDialog.Builder(Addtest.this)
                            // set message, title, and icon
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")
                            .setIcon(R.drawable.delete)

                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final int position = viewHolder.getAdapterPosition();
                                    if (position+1 == testList.size()){
                                        Toast.makeText(Addtest.this, "Can't delete last test", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                    else{
                                    adapter.removeItem(position,testRef);
                                    dialog.dismiss();}
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
    ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Test test ;
            testList.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                test = ds.getValue(Test.class);
                if (test != null) {
                    testList.add(test);

                }

            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void getPatientTests(){
        Log.d(TAG, "onTestClicked: ");
        loadingDialog.StartLodingDialog();

        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue().toString());
             if(dataSnapshot.hasChild(name+" "+lastname)) {
                 if (dataSnapshot.hasChild(name+" "+lastname+"/Temp") && dataSnapshot.hasChild(name+" "+lastname+"/EMG") &&
                         dataSnapshot.hasChild(name+" "+lastname+"/Glucose") && dataSnapshot.hasChild(name+" "+lastname+"/Heart Beats")) {
                     Log.d(TAG, "onDataChange: data received");
                     loadingDialog.DismissDialog();



                 } else {
                     Log.d(TAG, "onDataChange: something went wrong");
                 }
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
