package com.manan.appointment.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manan.appointment.R;
import com.manan.appointment.data.AppointmentInfo;
import com.manan.appointment.data.emailValidate;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Manan on 8/4/2016.
 */
public class profileUserActivity extends AppCompatActivity {

    // url to save user profile in database
    private static String save_profile = "http://176.32.230.13/iamcodemaster.com/Login/updateUserProfile.php";
    private static String getUserData = "http://176.32.230.13/iamcodemaster.com/Login/getUserData.php";

    String FULLNAME, USERNAME, PASSWORD, EMAIL, NUMBER,backup_email;
    EditText fullname, username, pwd, email, number;
    ProgressBar progressBar;
    ImageView profilePhoto;
    String imgDecodableString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_profile_layout);
        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullname = (EditText) findViewById(R.id.profile_fullname);
        username = (EditText) findViewById(R.id.profile_username);
        pwd = (EditText) findViewById(R.id.profile_password);
        email = (EditText) findViewById(R.id.profile_email);
        number = (EditText) findViewById(R.id.profile_number);

        profilePhoto= (ImageView)findViewById(R.id.user_profile_photo);
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throwDialog("Sucess","Upload Photo");
            }
        });

        new getUserData().execute();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            back();
        }

        if (item.getItemId() == R.id.saveprofile) {
            FULLNAME=fullname.getText().toString();
            USERNAME=username.getText().toString();
            EMAIL=email.getText().toString();
            PASSWORD=pwd.getText().toString();
            NUMBER=number.getText().toString();
            new updateUserProfile().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void back() {
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        startActivity(intent);
    }


    class getUserData extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
//            progressBar = (ProgressBar) findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            try {

                URL url = new URL(getUserData);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", AppointmentInfo.username);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            try {
                if (response == null) {
                    response = "There was an Error";
                }
                if (response.contains("Error")) {
                    throwDialog("Error","Error getting user data. try again later");
                }
                else {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.i("response",response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject obj=jsonArray.getJSONObject(i);
                        pwd.setText(obj.getString("pwd"));
                        username.setText(AppointmentInfo.username);
                        email.setText(obj.getString("email"));
                        number.setText(obj.getString("contact"));
                        fullname.setText(obj.getString("name"));

                    }
                    backup_email=email.getText().toString();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void throwDialog(final String problem, final String msg)
    {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(profileUserActivity.this);
        builder1.setCancelable(false);
        if(problem.contains("Error"))
        {
            builder1.setTitle("Error");
            builder1.setIcon(R.drawable.ic_action_error);
            builder1.setMessage(msg);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(problem.contains("Email"))
                            {
                                email.setText(backup_email);
                            }
                            else
                            {
                                Intent intent = new Intent(profileUserActivity.this, ProfileSettingsActivity.class);
                                startActivity(intent);
                            }

                            dialog.cancel();
                        }
                    });
        }
        else
        {

            String[] myItems = {"Take photo", "Choose from gallery"};
            final ArrayList<String> items=new ArrayList<String>();
            for(String s:myItems)
                items.add(s);

            builder1.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.view_appt_layout, null);
            builder1.setView(convertView);
            builder1.setTitle("Upload Photo");
            ListView list = (ListView) convertView.findViewById(R.id.listItems);
            list.setDivider(null);
            list.setDividerHeight(0);
            list.setAdapter(new CustomListAdapter(profileUserActivity.this, items));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                    if(items.get(position).contains("Take photo"))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    }
                    else
                    {
                        // Create intent to Open Image applications like Gallery, Google Photos
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, 1);

                    }

                }
            });
        }

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                profilePhoto.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }



    class CustomListAdapter extends BaseAdapter {
        private ArrayList<String> listData;
        private LayoutInflater layoutInflater;

        public CustomListAdapter(Context aContext, ArrayList<String> listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(aContext);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.photo_upload_layout, null);
                holder = new ViewHolder();
                holder.photo = (TextView) convertView.findViewById(R.id.profile_photo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.photo.setText(listData.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView photo;
        }
    }

    class updateUserProfile extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
//            progressBar = (ProgressBar) findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try
            {

                if(!emailValidate.validate(EMAIL))
                {
                    return "Email Error";
                }

                URL url = new URL(save_profile);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", USERNAME);
                params.put("fullname", FULLNAME);
                params.put("email", EMAIL);
                params.put("pwd", PASSWORD);
                params.put("contact", NUMBER);
                params.put("name",AppointmentInfo.username);

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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "There was an Error";
            }
            //           progressBar.setVisibility(View.GONE);
            if(response.contains("Email Error"))
            {
                throwDialog(response,"Enter a valid email address");
            }
            else if (response.contains("Error")) {
                Toast.makeText(getApplication().getBaseContext(), "Error updating user profile. Try again", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplication().getBaseContext(), "Saved changes", Toast.LENGTH_LONG).show();
            }

        }
    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String pair : params.keySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(pair), "UTF-8"));
        }

        return result.toString();
    }
}
