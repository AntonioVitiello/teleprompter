package com.av.teleprompter.view;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.av.teleprompter.R;
import com.av.teleprompter.application.TeleApplication;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.ScriptContract;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Antonio Vitiello
 */
public class EditActivity extends BaseActivity implements View.OnClickListener {

    public static final int MODIFY_REQUEST_CODE = 1;

    private Tracker mTracker;
    private Script mScript;
    private int mResult;

    @BindView(R.id.addTitle)
    EditText mTitleView;
    @BindView(R.id.addScript)
    EditText mBodyView;
    @BindView(R.id.edit_container)
    ConstraintLayout mEditContainer;
    @BindView(R.id.button_save)
    TextView mButtonSave;
    @BindView(R.id.button_cancel)
    TextView mButtonCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        initActionBar();
        TeleApplication application = (TeleApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Intent intent = getIntent();
        mScript = intent.getParcelableExtra(PlayActivity.SCRIPT_EXTRA_KEY);
        if (mScript != null) {
            mTitleView.setText(mScript.getTitle());
            mBodyView.setText(mScript.getBody());
        }

        mButtonSave.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);

        mTitleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveButton();
            }
        });

        mBodyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveButton();
            }
        });

        checkSaveButton();
    }

    private void checkSaveButton() {
        mButtonSave.setEnabled(mBodyView.getText().toString().length() > 0 && mTitleView.getText().toString().length() > 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(EditActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.tracker_action))
                .setAction(getString(R.string.tracker_share))
                .build());
    }

    private void addNewScript() {

        if (validate()) {
            String title = mTitleView.getText().toString();
            String body = mBodyView.getText().toString();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ScriptContract.ScriptEntry.TITLE, title);
            contentValues.put(ScriptContract.ScriptEntry.BODY, body);
            Uri uri = getContentResolver().insert(ScriptContract.ScriptEntry.CONTENT_URI, contentValues);
            mScript = new Script(title, body, ContentUris.parseId(uri));
        }
    }

    private void insertScript() {

        if (validate()) {
            String title = mTitleView.getText().toString();
            String body = mBodyView.getText().toString();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ScriptContract.ScriptEntry.TITLE, title);
            contentValues.put(ScriptContract.ScriptEntry.BODY, body);
            getContentResolver().update(ScriptContract.ScriptEntry.CONTENT_URI,
                    contentValues,
                    ScriptContract.ScriptEntry._ID + " = ?",
                    new String[]{Long.toString(mScript.getId())});
            mScript.setTitle(title);
            mScript.setBody(body);
        }
    }

    private boolean validate() {

        boolean isValid = true;
        int errorMessageId = -1;

        if (TextUtils.isEmpty(mTitleView.getText())) {
            isValid = false;
            errorMessageId = R.string.error_add_title;
            mTitleView.requestFocus();
        } else if (TextUtils.isEmpty(mBodyView.getText())) {
            isValid = false;
            errorMessageId = R.string.error_add_body;
            mBodyView.requestFocus();
        }

        if (!isValid) {
            Snackbar snackbar = Snackbar.make(mEditContainer, getString(errorMessageId), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return isValid;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_save:
                if (mScript == null || mScript.getId() == 0) {
                    addNewScript();
                } else {
                    insertScript();
                }
                mResult = RESULT_OK;
                break;
            case R.id.button_cancel:
            default:
                mResult = RESULT_CANCELED;
        }

        hideKeyboard();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra(PlayActivity.SCRIPT_EXTRA_KEY, mScript);
        setResult(mResult, data);
        super.finish();
    }

}
