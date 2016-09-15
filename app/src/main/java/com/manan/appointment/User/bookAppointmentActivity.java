package com.manan.appointment.User;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.manan.appointment.HomepageAcivity;
import com.manan.appointment.R;
import com.manan.appointment.data.AppointmentInfo;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Manan on 7/13/2016.
 */
public class bookAppointmentActivity extends AppCompatActivity {

    // url to view appointment info from database
    private static String url_login = "http://176.32.230.13/iamcodemaster.com/Login/bookAppointment.php";

    GPSTracker gps;
    EditText date,time,postalCode;
    String DATE,TIME,REASON,POSTAL_CODE,DOCTOR;
    Spinner spinnerReason,spinnerDoctor;
    String[] reason,doctor;
    ProgressBar progressBar;
    int mMinute,mHour,mYear,mMonth,mDay;
    ArrayAdapter<String> adapter;
    double lat,longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.book_appt);
        setContentView(R.layout.book_appt_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSpinner();
        date=(EditText)findViewById(R.id.dateText);
        date.setTextIsSelectable(true);
        date.requestFocus();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        time=(EditText)findViewById(R.id.timeText);
        time.setTextIsSelectable(true);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });


        postalCode=(EditText)findViewById(R.id.postalCodeText);
        postalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                POSTAL_CODE=postalCode.getText().toString();
                new getDoctor().execute();
 //               spinnerDoctor.setAdapter(adapter);
            }
        });


             /* Current Location */
        gps=new GPSTracker(bookAppointmentActivity.this);
        if(!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
        else
        {
            lat=gps.getLatitude();
            longi=gps.getLongitude();
            new getPostalCode().execute();
        }
        if(lat==0.0 && longi==0.0)
            postalCode.setText("Cannot find location");
    }

    @Override
    protected void onResume() {
        GPSTracker loc=new GPSTracker(bookAppointmentActivity.this);
        Log.i("Set value here",loc.canGetLocation+","+loc.isGPSEnabled+","+loc.isNetworkEnabled);
        if(loc.canGetLocation())
        {
            lat=loc.getLatitude();
            longi=loc.getLongitude();
            new getPostalCode().execute();
        }
        super.onResume();
    }


    private int getPostalCode(String data)
    {
        int code=0;
        try
        {

            JSONObject json=new JSONObject(data);
            JSONArray array_json=json.getJSONArray("results");

            json=array_json.getJSONObject(0);
            array_json=json.getJSONArray("address_components");
            for(int i=0;i<array_json.length();i++)
            {
                json=array_json.getJSONObject(i);
//				System.out.println(json.toString());
                Log.i("address",json.toString());
                JSONArray array_json1=json.getJSONArray("types");
                for(int m=0;m<array_json1.length();m++)
                {
                    if(array_json1.get(m).equals("postal_code"))
                    {
                        code=Integer.parseInt(json.getString("short_name"));
                    }
                }
            }
            postalCode.setText(code+"");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return code;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    private void setSpinner() {
 //       new getDoctor().execute();
        String[] items = new String[] {"Sick", "Flu", "Muscle Pain","Eye Care"};
        String item[]=new String[]{"Select your doctor"};
        spinnerDoctor=(Spinner)findViewById(R.id.spinnerDoctor);
        spinnerReason = (Spinner) findViewById(R.id.spinnerReason);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(adapter);
    }


    private void setTime() {
        Calendar c = Calendar.getInstance();
        mMinute = c.get(Calendar.MINUTE);
        mHour = c.get(Calendar.HOUR);
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(hourOfDay==0 || hourOfDay>12)
                        {
                            if(minute<10)
                                time.setText(Math.abs(12-hourOfDay) + ":0" + minute+" PM");
                            else
                                time.setText(Math.abs(12-hourOfDay) +":"+ minute+" PM");
                        }
                        else
                        {
                            if(minute<10)
                            {
                                time.setText(hourOfDay + ":0" + minute+" AM");
                            }
                            else
                            {
                                time.setText(hourOfDay + ":" + minute+" AM");
                            }

                        }
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }


    private void setDate()
    {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date.setText(dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year);}
                }, mYear, mMonth, mDay);
        dpd.show();
    }


    private String convertDate(String date) {
        String dateString="";
        try {
            DateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            Date appt_date = format1.parse(date);
            DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            dateString = format2.format(appt_date);
        }
        catch(Exception e)
        {
            return "error";
        }
        return dateString;

    }

    private String convertTime(String time) {
        String timeString="";
        try
        {
            DateFormat format1 = new SimpleDateFormat("KK:mm a");
            Date start_time = format1.parse(time);
            DateFormat format2 = new SimpleDateFormat("HH:mm:ss");
            timeString = format2.format(start_time);
        }
        catch(Exception e)
        {
            return "error";
        }
        return timeString;
    }

    private boolean checkTimeConstraint(String time) throws ParseException
    {
        DateFormat format1=new SimpleDateFormat("HH:mm:ss");
        Date userTime=format1.parse(time);
        Date systemTime_end=format1.parse("17:00:00");
        Date systemTime_start=format1.parse("08:00:00");
        long seconds=(systemTime_end.getTime()-userTime.getTime())/100L;
        long seconds1=(userTime.getTime()-systemTime_start.getTime())/100L;
        if(seconds>=0 && seconds1>=0)
            return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try
        {
            if(item.getItemId()== android.R.id.home)
            {
                Log.i("back selected","true");
                back();
            }
            if (item.getItemId() == R.id.book_appt)
            {
                DATE=convertDate(date.getText().toString());
                TIME=convertTime(time.getText().toString());
                POSTAL_CODE=postalCode.getText().toString();
                REASON=spinnerReason.getSelectedItem().toString();
                DOCTOR=spinnerDoctor.getSelectedItem().toString();

                if(!checkEverything())
                {
                    new bookAppt().execute();
                }
                else
                {
                    throwDialog("All fields are mandatory. Cannot book appointment","error");
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkEverything() {
        if(DATE.contains("error") ||  TIME.equals("error") || POSTAL_CODE.equals(null) || DATE.equals("") || TIME.equals("") || POSTAL_CODE.equals(""))
        {
            return true;
        }
        else
            return false;
    }

    private void throwDialog(String msg,String title)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(bookAppointmentActivity.this);
        if(title.contains("error"))
        {
            builder1.setTitle("Error");
            builder1.setIcon(R.drawable.ic_action_error);
            builder1.setMessage(msg);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            refreshDisplay();
                            dialog.cancel();
                        }
                    });
        }
        else if(title.contains("success"))
        {
            builder1.setTitle("Success");
            builder1.setMessage(msg);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            homepage();
                            dialog.cancel();
                        }
                    });

        }
        else if(title.contains("Time issues"))
        {
            builder1.setTitle("Time Conflict");
            builder1.setIcon(R.drawable.ic_action_error);
            builder1.setMessage(msg);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            time.setText("");
                            time.requestFocus();
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void homepage()
    {
        Intent intent=new Intent(this,HomepageAcivity.class);
        startActivity(intent);
    }

    private void refreshDisplay()
    {
        date.setText("");
        time.setText("");
        postalCode.setText("");
    }


    private String[] getList(JSONArray jsonArray)
    {
        String item[]=new String[jsonArray.length()];
        try
        {
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject object=jsonArray.getJSONObject(i);
                item[i]=object.getString("name");
            }
        }
        catch(Exception e)
        {
            e.getMessage();
            return null;
        }

        return item;
    }

    class getDoctor extends AsyncTask<Void, Void, String>
    {
        private Exception exception;

        protected void onPreExecute() {
//            progressBar=(ProgressBar)findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {

                URL url = new URL("http://176.32.230.13/iamcodemaster.com/Login/getDoctor.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String,String> params = new HashMap<String,String>();
                params.put("postalcode",POSTAL_CODE);

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
            try {
                Log.i("doctor",response);
                if(response.contains("[]"))
                {
                    doctor=new String[]{"No doctors found nearby"};
                }
                else
                {
                    JSONArray jsonArray=new JSONArray(response);
                    doctor=getList(jsonArray);
                }

                adapter= new ArrayAdapter<String>(bookAppointmentActivity.this,
                        android.R.layout.simple_spinner_item, doctor);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDoctor.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class getPostalCode extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+longi+"&key=AIzaSyCGRMhsyJBKO8y8b5QcEXS3sMzKDo1klk8");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("GPSEnabled", lat + "," + longi);
            Log.i("Postal Code",getPostalCode(response)+"");
        }
    }



    class bookAppt extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar=(ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {


                if(!checkTimeConstraint(TIME))
                    return "Clinic not open";

                URL url = new URL(url_login);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String,String> params = new HashMap<String,String>();
                params.put("username", AppointmentInfo.username);
                params.put("date",DATE);
                params.put("time",TIME);
                params.put("name",DOCTOR);
                params.put("reason",REASON);

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
                response = "There was an Error";
            }
            progressBar.setVisibility(View.GONE);
            if(response.contains("Appointment requested"))
            {
                throwDialog("Appointment requested successfully. You can view status of your appointment","success");
            }
            else if(response.contains("Clinic not open"))
            {
                throwDialog("Time invalid. Appointment available only between 8am-5pm","error");
            }
            else if(response.contains("Error"))
            {
                throwDialog("Error booking appointment. Please try again","error");
            }
            else if(response.contains("Time issues"))
            {
                throwDialog("Appointment already exists at this time. Try again with other time","Time issues");
            }


            try {
                Log.i("Date",DATE+","+TIME+","+POSTAL_CODE+","+REASON+","+response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    private void back() {
        Intent intent=new Intent(this,HomepageAcivity.class);
        startActivity(intent);
    }
}
