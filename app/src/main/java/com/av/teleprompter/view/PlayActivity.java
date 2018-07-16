package com.av.teleprompter.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.av.teleprompter.view.SettingsActivity.SETTINGS_REQUEST_CODE;

/**
 * Created by Antonio Vitiello
 */
public class PlayActivity extends BaseActivity {

    private static final long DEFAULT_SCROLL_DELAY_MILLIS = 500;
    private static final int HIDE_UI_DELAY = 300;
    public static String SCROLL_Y_KEY = "position_y";
    public static String SCRIPT_EXTRA_KEY = "script_key";

    @BindView(R.id.play_script)
    TextView mPlayScript;
    @BindView(R.id.play_scroll)
    ScrollView mScrollView;

    private Script mScript;
    private Handler mScrollHandler;
    private boolean mIsPlaying;
    private long mScrollDelayMillis = DEFAULT_SCROLL_DELAY_MILLIS;
    private int mScrollY;
    private Runnable mPlayRunnable;
    private final Handler mHideHandler = new Handler();


    private final Runnable mHideUiRunnable = () -> {
        // Delayed removal of status and navigation bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    };

    private final Runnable mShowUiRunnable = () -> {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        mScript = intent.getParcelableExtra(SCRIPT_EXTRA_KEY);
        mPlayScript.setText(mScript.getBody());
        // Set up the user interaction to manually show or hide the system UI.
        mPlayScript.setOnClickListener(view -> toggle());

        initPreferences();
        initScroller();

        // Postpone toggle() to indicate the user that UI controls are available
        new Handler().postDelayed(() -> toggle(), 1000);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void toggle() {
        if (mIsPlaying) {
            pause();
            showSystemUI();
        } else {
            hideSystemUI();
            play();
        }
    }

    private void hideSystemUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowUiRunnable);
        mHideHandler.postDelayed(mHideUiRunnable, HIDE_UI_DELAY);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHideUiRunnable);
        mHideHandler.postDelayed(mShowUiRunnable, HIDE_UI_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScrollView.scrollTo(0, mScrollY);
    }


    public void initScroller() {
        mScrollHandler = new Handler();
        mPlayRunnable = () -> {
            mScrollView.scrollTo(0, mScrollView.getScrollY() + 2);
            mScrollHandler.postDelayed(mPlayRunnable, mScrollDelayMillis);
        };
    }

    private void initPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String speedKey = getString(R.string.pref_speed_key);
        int speedDef = getResources().getInteger(R.integer.pref_scroll_speed_default);
        long speed = preferences.getInt(speedKey, speedDef);
        mScrollDelayMillis = 1000 / speed;

        int textColor = preferences.getInt(getString(R.string.pref_text_color_key), getResources().getColor(R.color.black));
        mPlayScript.setTextColor(textColor);

        int backgroundColor = preferences.getInt(getString(R.string.pref_background_color_key), getResources().getColor(R.color.white));
        mScrollView.getRootView().setBackgroundColor(backgroundColor);

        int textSize = preferences.getInt(getString(R.string.pref_text_size_key), getResources().getInteger(R.integer.pref_text_size_default));
        mPlayScript.setTextSize(textSize);

        String font = preferences.getString(getString(R.string.pref_font_key), getString(R.string.font_face_default));
        mPlayScript.setTypeface(Typeface.createFromAsset(getAssets(), String.format("fonts/%s.ttf", font)), 1);

        boolean isMirrored = preferences.getBoolean(getString(R.string.pref_mirror_key), false);
        mPlayScript.setScaleX(isMirrored ? -1 : 1);

    }

    private void pause() {
        mScrollHandler.removeCallbacksAndMessages(null);
        mIsPlaying = false;
    }

    private void play() {
        mScrollHandler.post(mPlayRunnable);
        mIsPlaying = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean consumed = true;
        switch (item.getItemId()) {
            case R.id.mi_play:
                if (mIsPlaying) {
                    pause();
                    item.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
                } else {
                    play();
                    item.setIcon(R.drawable.ic_pause_circle_outline_black_24dp);
                }
                break;
            case R.id.mi_settings:
                settings();
                break;
            default:
                Timber.d("Unable to manage menu action: %s", item);
                consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    private void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SCRIPT_EXTRA_KEY, mScript);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SETTINGS_REQUEST_CODE) {
            initPreferences();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(SCROLL_Y_KEY, mScrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollY = savedInstanceState.getInt(SCROLL_Y_KEY);
    }

}
