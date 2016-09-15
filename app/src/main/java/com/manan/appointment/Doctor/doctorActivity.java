package com.manan.appointment.Doctor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manan.appointment.MainActivity;
import com.manan.appointment.R;
import com.manan.appointment.User.bookAppointmentActivity;
import com.manan.appointment.User.viewAppointmentActivity;

/**
 * Created by Manan on 7/31/2016.
 */
public class doctorActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_homepage_layout);

                /* Method for handling all label clicks */
        labelClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            popupAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    private void popupAlert() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(doctorActivity.this);
        builder1.setTitle("Logout");
        builder1.setIcon(R.drawable.ic_action_logout);
        builder1.setMessage("Do you want to logout?");


        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent=new Intent(doctorActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void labelClick() {
        RelativeLayout first=(RelativeLayout)findViewById(R.id.doc_firstView);
        RelativeLayout second=(RelativeLayout)findViewById(R.id.doc_secondView);
        RelativeLayout third=(RelativeLayout)findViewById(R.id.doc_thirdView);
        RelativeLayout fourth=(RelativeLayout)findViewById(R.id.doc_fourthView);

        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.doc_firstView:
//                TextView tv = (TextView) v;
                Intent intent=new Intent(this,bookAppointmentActivity.class);
                startActivity(intent);
                Log.i("First View","Edit working days/time");
                break;
            case R.id.doc_secondView:
//                tv = (TextView) v;
                intent=new Intent(this,viewDoctorAppointmentActivity.class);
                startActivity(intent);
                Log.i("Second View","view appointment");
                break;
            case R.id.doc_thirdView:
//                tv = (TextView) v;
                Log.i("Third View","profile settings");
                break;
            case R.id.doc_fourthView:
//                tv = (TextView) v;
                Log.i("Fourth View","get reports");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        popupAlert();
        Log.i("Back pressed","Homepage");
    }

}
