package com.sem.e_health2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static com.sem.e_health2.DoctorActivity.changeStatusBarToWhite;

public class Addtest extends AppCompatActivity {
    List<Test> testList = new ArrayList<>();
    RecAdapter adapter ;
    RecyclerView recyclerview ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference tempRef;
    DatabaseReference glucRef;
    DatabaseReference hardbeatsRef;
    DatabaseReference emgRef;
    String temp ;
    String emg ;
    String hartbeats ;
    String glucose ;
    String finalDate;
    DatabaseReference testRef ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_tests);
        changeStatusBarToWhite(this);
        recyclerview = findViewById(R.id.RC1);
        enableSwipeToDeleteAndUndo();

        adapter = new RecAdapter(this,testList);
       ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);


       findViewById(R.id.img_back).setOnClickListener((v)->{
           finish();
       });
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String lastname = intent.getStringExtra("lastname");
        String docID = intent.getStringExtra("docid");

        tempRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Temp");
        hardbeatsRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Heart Beats");
        emgRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/EMG");
        glucRef = database.getReference("E-Health/Client live test /"+name+" "+lastname+"/Glucose");

        DatabaseReference myRef = database.getReference("E-Health/Doctors/"+docID+"/Clients TESTS");
        testRef = myRef.child(name+" "+lastname+" TESTS");
        hardbeatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hartbeats = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        glucRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                glucose = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        emgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emg = (String) dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temp = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Date c = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dateformatted = dateFormat.format(date);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        finalDate = formattedDate +" "+dateformatted ;


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->{
            Test test = new Test();
            test.setTime(finalDate);
            test.setTemp(temp);
            test.setEmg(emg);
            test.setGlucose(glucose);
            test.setHartbeats(hartbeats);
            testRef.child(finalDate).setValue(test);
            recreate();







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
                            .setIcon(R.drawable.delet1)

                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final int position = viewHolder.getAdapterPosition();
                                    adapter.removeItem(position,testRef);
                                    dialog.dismiss();
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

}
