package com.manan.appointment.User;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.manan.appointment.R;
import com.manan.appointment.data.AppointmentInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Manan on 7/15/2016.
 */
public class editViewAppointment extends AppCompatActivity {

    // url to delete appointment from database
    private static String url_login = "http://176.32.230.13/iamcodemaster.com/Login/deleteAppointment.php";

    TextView date,time,location,doctor,reason,clinic;
    String DATE,TIME,DOCTOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.editAppointment);
        setContentView(R.layout.view_appt_edit_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=this.getIntent();
        AppointmentInfo info=new AppointmentInfo();
        info.setDate(intent.getStringExtra("Date"));
        info.setTime(intent.getStringExtra("Time"));
        info.setLocation(intent.getStringExtra("Location"));
        info.setReason(intent.getStringExtra("Reason"));
        info.setDoctor(intent.getStringExtra("Doctor"));
        info.setClinicName(intent.getStringExtra("Clinic"));

        viewDisplay(info);
    }

    private void viewDisplay(AppointmentInfo info) {
        date=(TextView)findViewById(R.id.textView2);
        time=(TextView)findViewById(R.id.textView4);
        clinic=(TextView)findViewById(R.id.textView6);
        location=(TextView)findViewById(R.id.textView8);
        doctor=(TextView)findViewById(R.id.textView10);
        reason=(TextView)findViewById(R.id.textView12);



        // Set text to TextView
        date.setText(info.getDate());
        time.setText(info.getTime());
        location.setText(info.getLocation());
        reason.setText(info.getReason());
        doctor.setText(info.getDoctor());
        clinic.setText(info.getClinicName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_appt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if(item.getItemId()==R.id.delete_appt)
            {
                DATE=convertDate(date.getText().toString());
                TIME=convertTime(time.getText().toString());
//                DOCTOR=doctor.getText().toString();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(editViewAppointment.this);
                builder1.setTitle("Delete");
                builder1.setIcon(R.drawable.ic_action_delete);
                builder1.setMessage("Are you sure you want to delete?");


                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new deleteAppt().execute();
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

            if(item.getItemId()==android.R.id.home)
            {
                back();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }


    public void viewApptPage()
    {
        Intent intent=new Intent(this,viewAppointmentActivity.class);
        startActivity(intent);
    }


    private String convertDate(String date) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("MMMM dd, yyyy");
        Date appt_date = format1.parse(date);
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format2.format(appt_date);
        return dateString;
    }

    private String convertTime(String time) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("KK:mm a");
        Date start_time = format1.parse(time);
        DateFormat format2 = new SimpleDateFormat("HH:mm:ss");
        String timeString = format2.format(start_time);
        return timeString;
    }

    class deleteAppt extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
//            progressBar=(ProgressBar)findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {

                URL url = new URL(url_login);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String,String> params = new HashMap<String,String>();
                params.put("username", AppointmentInfo.username);
                params.put("date",DATE);
                params.put("time",TIME);
//                params.put("doctor",DOCTOR);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (String pair : params.keySet())
            {
                if(first)
                    first=false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(pair, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(params.get(pair), "UTF-8"));

            }

            return result.toString();
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            if(response.contains("Appointment deleted"))
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(editViewAppointment.this);
                builder1.setTitle("Success");
                builder1.setMessage("Appointment deleted successfully.");


                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                viewApptPage();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            else if(response.contains("Error"))
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(editViewAppointment.this);
                builder1.setMessage("Error deleting appointment. Try again in sometime");
                builder1.setIcon(R.drawable.ic_action_error);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

//            try {
//                Log.i("Date",DATE+","+TIME+","+LOCATION+","+REASON+","+response);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }


    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }


    private void back() {
        Intent intent=new Intent(this,viewAppointmentActivity.class);
        startActivity(intent);
        Log.i("Back pressed","Homepage");
    }
}
