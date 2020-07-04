package com.sem.e_health2;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;

public class LodingDialog {
    private  Activity activity;
    private  AlertDialog dialog;
    LodingDialog(Activity MyActivity){
        activity=MyActivity;

    }
   void StartLodingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
       LayoutInflater inflater= activity.getLayoutInflater();
       builder.setView(inflater.inflate(R.layout.progress,null));
       builder.setCancelable(true);
       dialog=builder.create();
       dialog.show();
       dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
   }
   void DismissDialog(){

        dialog.dismiss();
   }
}
