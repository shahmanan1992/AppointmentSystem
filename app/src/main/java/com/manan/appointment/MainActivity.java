package com.manan.appointment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.manan.appointment.Doctor.doctorActivity;
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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int EDITOR_ACTIVITY_REQUEST = 1001;
    EditText et1,et2;

    // url to get login info from database
    private static String url_login = "http://176.32.230.13/iamcodemaster.com/Login/checkLogin.php";

    private String username="",pwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);
        et2.setTypeface(Typeface.DEFAULT);
        et2.setTransformationMethod(new PasswordTransformationMethod());

        login();
        register();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.aboutUs)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    private void register() {
        TextView register=(TextView)findViewById(R.id.newuser);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked","Clicked");
                username=et1.getText().toString();
                pwd=et2.getText().toString();
                new checkLogin().execute();
            }
        });

    }

    private void refreshDisplay() {
        EditText et1 = (EditText) findViewById(R.id.editText);
        EditText et2 = (EditText) findViewById(R.id.editText2);
        et1.setText("");
        et2.setText("");
        et1.requestFocus();
    }

    private void homepage() {
        Intent intent = new Intent(this, HomepageAcivity.class);
        startActivity(intent);
    }

    private void doctorHomepage()
    {
        Intent intent=new Intent(MainActivity.this,doctorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        super.onBackPressed();
    }

    class checkLogin extends AsyncTask<Void, Void, String> {

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
                params.put("username",username);
                params.put("password",pwd);

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
//        progressBar.setVisibility(View.GONE);
            try
            {
                Log.i("response",response);
                if(response.contains("0"))
                {
                    AppointmentInfo.username=username;
                    homepage();
                }
                else if((response.contains("Error while logging")) || response.contains("-1"))
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Email or Password is incorrect. Try again");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    refreshDisplay();
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else
                {
                    AppointmentInfo.username=username;
                    doctorHomepage();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}


