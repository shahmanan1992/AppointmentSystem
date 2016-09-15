package com.manan.appointment.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.manan.appointment.HomepageAcivity;
import com.manan.appointment.R;

/**
 * Created by Manan on 8/2/2016.
 */
public class ProfileSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile Settings");
        setContentView(R.layout.profile_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clickTextView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home)
        {
            back();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickTextView() {

        TextView edit_profile = (TextView)findViewById(R.id.editProfile);
        TextView notification = (TextView)findViewById(R.id.notification);

        edit_profile.setOnClickListener(this);
        notification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.editProfile: Intent intent=new Intent(this,profileUserActivity.class);
                                    startActivity(intent);
                                    break;
            case R.id.notification: intent=new Intent(this,notificationActivity.class);
                                    startActivity(intent);
                                    break;
        }
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    private void back() {
        Intent intent=new Intent(this, HomepageAcivity.class);
        startActivity(intent);
    }
}
