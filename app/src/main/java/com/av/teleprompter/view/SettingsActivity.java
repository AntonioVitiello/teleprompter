package com.av.teleprompter.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.av.teleprompter.R;

/**
 * Created by Antonio Vitiello
 */
public class SettingsActivity extends BaseActivity {

    public static final int SETTINGS_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }

}