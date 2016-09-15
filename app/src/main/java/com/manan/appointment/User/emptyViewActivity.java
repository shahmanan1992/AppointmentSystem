package com.manan.appointment.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.manan.appointment.HomepageAcivity;
import com.manan.appointment.R;

/**
 * Created by Manan on 7/27/2016.
 */
public class emptyViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptyview_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text=(TextView)findViewById(R.id.emptyView);
        text.setText("No current appointments");
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
        Intent intent=new Intent(this,HomepageAcivity.class);
        startActivity(intent);
        Log.i("Back pressed","Homepage");
    }
}
