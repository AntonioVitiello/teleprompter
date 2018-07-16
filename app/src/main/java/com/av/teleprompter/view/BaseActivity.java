package com.av.teleprompter.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.av.teleprompter.R;

/**
 * Created by Antonio Vitiello
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    public void navigateUpFromSameTask() {
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    public void startActivity(Class clazz) {
        Intent i = new Intent(this, clazz);
        startActivity(i);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
