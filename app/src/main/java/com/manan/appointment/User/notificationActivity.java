package com.manan.appointment.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.manan.appointment.R;

/**
 * Created by Manan on 8/4/2016.
 */
public class notificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_profile_layout);
        setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home)
        {
            back();
        }
        return super.onOptionsItemSelected(item);
    }

    private void back() {
        Intent intent=new Intent(this,ProfileSettingsActivity.class);
        startActivity(intent);
    }
}
