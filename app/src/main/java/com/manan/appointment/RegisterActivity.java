package com.manan.appointment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.manan.appointment.data.AppointmentInfo;
import com.manan.appointment.data.emailValidate;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Manan on 7/26/2016.
 */
public class RegisterActivity extends AppCompatActivity{

    EditText fullName,userName,pwd,confirmpwd,email,contact;
    String FULLNAME,USERNAME,PWD,EMAIL,CONTACT,CONFIRMPWD;
//    ProgressBar progressBar;

    // url to register user
    private static String url_login = "http://176.32.230.13/iamcodemaster.com/Login/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        setTitle("Register");
        fullName=(EditText)findViewById(R.id.fullname);
        email=(EditText)findViewById(R.id.emailRegister);
        contact=(EditText)findViewById(R.id.contact);
        userName=(EditText)findViewById(R.id.username);
        pwd=(EditText)findViewById(R.id.pwd);
        confirmpwd=(EditText)findViewById(R.id.confirmpwd);

        setDefaultFont();
        registerUser();
    }

    private void setDefaultFont() {
        pwd.setTypeface(Typeface.DEFAULT);
        pwd.setTransformationMethod(new PasswordTransformationMethod());
        confirmpwd.setTypeface(Typeface.DEFAULT);
        confirmpwd.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void registerUser() {
        Button register=(Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FULLNAME=fullName.getText().toString();
                EMAIL=email.getText().toString();
                CONTACT=contact.getText().toString();
                USERNAME=userName.getText().toString();
                PWD=pwd.getText().toString();
                CONFIRMPWD=confirmpwd.getText().toString();


                /* Error while registering (Missing fields) */
                if(checkConstraint()== 1)
                {
                    Log.i("error",FULLNAME+","+CONTACT+","+EMAIL+","+USERNAME+","+PWD+","+CONFIRMPWD);
                    alertDialog("Cannot register user. All fields are mandatory.","error");
                }
                /* Password and Confirm password does not match */
                else if(checkConstraint()== 2)
                {
                    alertDialog("Confirm Password does not match. Both passwords must be same","error");
                }
                /* Email Address invalid */
                else if(checkConstraint() == 3)
                {
                    alertDialog("Email Address not valid","error");
                }
                /* Contact Number validation */
                else if(checkConstraint() == 4)
                {
                    alertDialog("Contact number length must be 10","error");
                }
                /* Registration successful */
                else
                {
                    new register().execute();
                }

            }
        });
    }

    private void alertDialog(final String show,String title)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
        if(title.contains("error")) {
            builder1.setTitle("Error");
            builder1.setIcon(R.drawable.ic_action_error);
            builder1.setMessage(show);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(show.contains("passwords"))
                            {
                                pwd.setText("");
                                confirmpwd.setText("");
                                pwd.requestFocus();
                            }
                            if(show.contains("Email"))
                            {
                                email.setText("");
                                email.requestFocus();
                            }
                            if(show.contains("Username"))
                            {
                                userName.setText("");
                                userName.requestFocus();
                            }
                            dialog.cancel();
                        }
                    });
        }
        else {
            builder1.setTitle("Success");
            builder1.setMessage(show);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
        }

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private int checkConstraint()
    {
        int value=-1;
        try
        {
            if(FULLNAME.equals("") || USERNAME.equals("") || PWD.equals("") || CONFIRMPWD.equals("")
                    || EMAIL.equals("") || CONTACT.equals(""))
            {
                value = 1;
            }
            else if(!PWD.equals(CONFIRMPWD))
            {
                value = 2;
            }
            else if(!emailValidate.validate(EMAIL))
            {
                value = 3;
            }
            else if(CONTACT.length()!=10 || CONTACT.contains("[a-z]"))
            {
                value = 4;
            }
        }
        catch(Exception e)
        {
            value = 0;
        }
        return value;

    }



    class register extends AsyncTask<Void, Void, String> {

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
                params.put("username", USERNAME);
                params.put("fullname",FULLNAME);
                params.put("password",PWD);
                params.put("email",EMAIL);
                params.put("contact",CONTACT);

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
 //           progressBar.setVisibility(View.GONE);
            if(response.contains("username already exists"))
            {
                alertDialog("Username already exists. Please try again","error");
            }
            else if(response.contains("Error"))
            {
                alertDialog(response,"error");
            }
            else
            {
                alertDialog("Registration Successful","success");
            }

            try {
//                Log.i("Date",DATE+","+TIME+","+POSTAL_CODE+","+REASON+","+response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
