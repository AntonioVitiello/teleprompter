package com.av.teleprompter.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.utils.ActionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.av.teleprompter.view.EditActivity.MODIFY_REQUEST_CODE;
import static com.av.teleprompter.view.PlayActivity.SCRIPT_EXTRA_KEY;

public class PrePlayActivity extends BaseActivity {

    private static final int BACK_DELAY = 1000; // 1 second
    private static final String SCROLL_Y_KEY = "scroll_y";

    private Script mScript;
    private int mScrollY;

    @BindView(R.id.detail_script_body)
    TextView mBodyTextView;
    @BindView(R.id.detail_scroll)
    ScrollView mScrollView;
    @BindView(R.id.detail_main_layout)
    ConstraintLayout mDetailMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_play);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mScript = intent.getParcelableExtra(SCRIPT_EXTRA_KEY);

        updateView();

        initActionBar();

    }

    private void updateView() {
        setTitle(mScript.getTitle());
        mBodyTextView.setText(mScript.getBody());
        mBodyTextView.setContentDescription(mScript.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pre_play_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScrollView.scrollTo(0, mScrollY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean consumed = true;
        switch (item.getItemId()) {
            case R.id.mi_play:
                ActionUtils.play(mScript, this);
                break;
            case R.id.mi_modify:
                modify();
                break;
            case R.id.mi_delete:
                ActionUtils.delete(mScript, mDetailMainLayout, this);
                new Handler().postDelayed(() -> onBackPressed(), BACK_DELAY);
                break;
            case R.id.mi_settings:
                ActionUtils.settings(this);
                break;
            default:
                Timber.d("Unable to manage menu action: " + item.toString());
                consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    private void modify(){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(SCRIPT_EXTRA_KEY, mScript);
        startActivityForResult(intent, MODIFY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == MODIFY_REQUEST_CODE) {
            if (data.hasExtra(SCRIPT_EXTRA_KEY)){
                mScript = data.getParcelableExtra(SCRIPT_EXTRA_KEY);
                updateView();
            }
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