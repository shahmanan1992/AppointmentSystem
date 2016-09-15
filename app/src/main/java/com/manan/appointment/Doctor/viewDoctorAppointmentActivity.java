package com.manan.appointment.Doctor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.manan.appointment.CustomListAdapter;
import com.manan.appointment.HomepageAcivity;
import com.manan.appointment.R;
import com.manan.appointment.User.editViewAppointment;
import com.manan.appointment.User.emptyViewActivity;
import com.manan.appointment.data.AppointmentInfo;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Manan on 8/1/2016.
 */
public class viewDoctorAppointmentActivity extends AppCompatActivity {

    private static String url_login = "http://176.32.230.13/iamcodemaster.com/Login/getDoctorAppointments.php";

    ArrayList<AppointmentInfo> results=new ArrayList<AppointmentInfo>();

    private List<AppointmentInfo> list_items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.view_appt);
        setContentView(R.layout.doctor_view_appt_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadData();

    }

    private void loadData() {
        new getDoctorAppointment().execute();
    }

    private ArrayList<AppointmentInfo> getListData(JSONArray json,String status) {
        TreeMap<Date,AppointmentInfo> sortAppointment=new TreeMap<Date,AppointmentInfo>();
        ArrayList<AppointmentInfo> results = new ArrayList<AppointmentInfo>();
        try
        {
            for(int i=0;i<json.length();i++)
            {
                JSONObject json_object=json.getJSONObject(i);
                AppointmentInfo newsData = new AppointmentInfo();
                if(json_object.getString("status").equals(status))
                {
                    newsData.setStatus(json_object.getString("status"));
                    newsData.setDate(convertDate(json_object.getString("date")));
                    newsData.setPatientName(json_object.getString("patient"));
                    newsData.setTime(convertTime(json_object.getString("time")));
                    newsData.setPatientContact(json_object.getString("patient_contact"));
                    newsData.setReason(json_object.getString("reason"));
                    sortAppointment.put(parseDate(json_object.getString("date"),json_object.getString("time")),newsData);
                }
            }

            if(sortAppointment.size()==0)
            {
                AppointmentInfo newsData = new AppointmentInfo();
                newsData.setDate("");
                newsData.setPatientName("No "+status+" appointments");
                newsData.setTime("");
                newsData.setPatientContact("");
                newsData.setReason("");
                results.add(newsData);
            }
            else
            {
                for(Date d:sortAppointment.keySet())
                {
                    results.add(sortAppointment.get(d));
                }
            }


        }
        catch(Exception e)
        {
            e.getMessage();
        }

        return results;
    }

    private Date parseDate(String date,String time) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date appt_date = format1.parse(date+" "+time);
        return appt_date;
    }

    private String convertDate(String date) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date appt_date = format1.parse(date);
        DateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy");
        String dateString = format2.format(appt_date);
        return dateString;
    }

    private String convertTime(String time) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        Date start_time = format1.parse(time);
        DateFormat format2 = new SimpleDateFormat("KK:mm a");
        String timeString = format2.format(start_time);
        return timeString;
    }


    class getDoctorAppointment extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
//        progressBar.setVisibility(View.VISIBLE);
//        responseView.setText("");
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
                params.put("username",AppointmentInfo.username);


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
//        progressBar.setVisibility(View.GONE);

            try {
                if(response.contains("No records") || response.contains("error"))
                {
                    Intent intent=new Intent(viewDoctorAppointmentActivity.this,emptyViewActivity.class);
                    startActivity(intent);
                }
                else {

                    JSONArray json = new JSONArray(response);

                    results = getListData(json,"requested");
                    final ListView lv1 = (ListView) findViewById(R.id.listRequestedItems);
                    lv1.setAdapter(new doctorCustomListAdapter(viewDoctorAppointmentActivity.this, results));
                    Utility.setListViewHeightBasedOnChildren(lv1);
                    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            Object o = lv1.getItemAtPosition(position);
                            AppointmentInfo apptData = (AppointmentInfo) o;
//                Toast.makeText(getApplicationContext(), apptData.getLocation(), Toast.LENGTH_LONG).show();
                            if(!apptData.getDate().isEmpty())
                            {
                                Intent intent = new Intent(viewDoctorAppointmentActivity.this, editDoctorViewAppointment.class);
                                intent.putExtra("Status",apptData.getStatus());
                                intent.putExtra("Date", apptData.getDate());
                                intent.putExtra("Time", apptData.getTime());
                                intent.putExtra("Patient", apptData.getPatientName());
                                intent.putExtra("Contact", apptData.getPatientContact());
                                intent.putExtra("Reason", apptData.getReason());
                                startActivityForResult(intent, 1001);
                            }
                        }
                    });


                    results = getListData(json,"confirmed");
                    final ListView lv2 = (ListView) findViewById(R.id.listConfirmedItems);
                    lv2.setAdapter(new doctorCustomListAdapter(viewDoctorAppointmentActivity.this, results));
                    Utility.setListViewHeightBasedOnChildren(lv2);
                    lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            Object o = lv2.getItemAtPosition(position);
                            AppointmentInfo apptData = (AppointmentInfo) o;
                            if(!apptData.getDate().isEmpty())
                            {
                                Intent intent = new Intent(viewDoctorAppointmentActivity.this, editDoctorViewAppointment.class);
                                intent.putExtra("Status",apptData.getStatus());
                                intent.putExtra("Date", apptData.getDate());
                                intent.putExtra("Time", apptData.getTime());
                                intent.putExtra("Patient", apptData.getPatientName());
                                intent.putExtra("Contact", apptData.getPatientContact());
                                intent.putExtra("Reason", apptData.getReason());
                                startActivityForResult(intent, 1001);
                            }
//                Toast.makeText(getApplicationContext(), apptData.getLocation(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            back();
        }
        return super.onOptionsItemSelected(item);
    }

    private void back() {
        Intent intent=new Intent(this,doctorActivity.class);
        startActivity(intent);
        Log.i("Back pressed","Homepage");
    }

}
