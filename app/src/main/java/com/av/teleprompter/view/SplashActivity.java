package com.av.teleprompter.view;

import android.os.Bundle;

/**
 * Created by Antonio Vitiello
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(MainActivity.class);
        finish();
    }

}
