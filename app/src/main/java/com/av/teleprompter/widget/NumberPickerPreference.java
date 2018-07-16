package com.av.teleprompter.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.av.teleprompter.R;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 */
public class NumberPickerPreference extends Preference implements
        Preference.OnPreferenceClickListener, DialogInterface.OnClickListener, View.OnClickListener {

    private final boolean DEFAULT_BIND_SUMMARY = true;
    private final int DEFAULT_MAX = 100;
    private final int DEFAULT_MIN = 1;
    private final int DEFAULT_STEP = 1;
    private final int DEFAULT_CURRENT = 50;
    private final String DEFAULT_TITLE = "Choose a number";

    private AlertDialog mDialog;
    private NumberPicker mPicker;
    private String mTitle;
    private boolean mBindSummary = DEFAULT_BIND_SUMMARY;
    private int mMax = DEFAULT_MAX;
    private int mMin = DEFAULT_MIN;
    private int mStep = DEFAULT_STEP;
    private int mCurrentValue = mMin;
    private String[] mValues;
    private int[] mNumberPickerAttrs = R.styleable.NumberPickerPreferenceAttrs;


    public NumberPickerPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setOnPreferenceClickListener(this);

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    mNumberPickerAttrs,
                    0, 0);
            readCustomProperties(typedArray);
        }

        int title = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "title", -1);
        mTitle = title != -1 ? context.getString(title) : DEFAULT_TITLE;

        updateView();
    }

    private void readCustomProperties(TypedArray typedArray) {
        try {
            mMin = typedArray.getInteger(R.styleable.NumberPickerPreferenceAttrs_minValue, DEFAULT_MIN);
            mMax = typedArray.getInteger(R.styleable.NumberPickerPreferenceAttrs_maxValue, DEFAULT_MAX);
            mStep = typedArray.getInteger(R.styleable.NumberPickerPreferenceAttrs_stepValue, DEFAULT_STEP);
            mCurrentValue = typedArray.getInteger(R.styleable.NumberPickerPreferenceAttrs_startValue, DEFAULT_CURRENT);
            mBindSummary = typedArray.getBoolean(R.styleable.NumberPickerPreferenceAttrs_bindSummary, DEFAULT_BIND_SUMMARY);

            if (mMax < mMin) {
                throw new AssertionError("max value must be > min value");
            }
            if (mStep <= 0) {
                throw new AssertionError("step value must be > 0");
            }
        } finally {
            typedArray.recycle();
        }
    }

    private void updateView() {
        //do nothing
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(mMin);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }

        Timber.d("NumberPickerPreference: mCurrentValue = %d", mCurrentValue);
        if (mBindSummary) {
            setSummary(Integer.toString(mCurrentValue));
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current setting value
        myState.value = mCurrentValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mPicker.setValue(myState.value);
        mCurrentValue = myState.value;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        showDialog();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showDialog() {
        if (mDialog == null) {

            View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.view_number_picker_dialog, null);

            // build NumberPicker
            mPicker = view.findViewById(R.id.number_picker);
            if (mStep > 1) {
                mValues = new String[(mMax - mMin) / mStep + 1];
                for (int i = 0; i < mValues.length; i++) {
                    mValues[i] = Integer.toString(mMin + mStep * i);
                }
                mPicker.setMinValue(0);
                mPicker.setMaxValue((mMax - mMin) / mStep);
                mPicker.setDisplayedValues(mValues);
            } else {
                mPicker.setMaxValue(mMax);
                mPicker.setMinValue(mMin);
                mPicker.setValue(mCurrentValue);
            }

            // build save button
            Button saveButton = view.findViewById(R.id.btn_save);
            saveButton.setOnClickListener(this);

            // build dialog
            mDialog = new AlertDialog.Builder(getContext())
                    .setTitle(mTitle)
                    .setView(view)
                    .setCancelable(true)
                    .create();

            mDialog.setCanceledOnTouchOutside(true);
        }

        mDialog.show();
    }

    private void save(int value) {
        if (mBindSummary) {
            setSummary(Integer.toString(value));
        }
        if (getOnPreferenceChangeListener() != null) {
            getOnPreferenceChangeListener().onPreferenceChange(this, value);
        }
        persistInt(value);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (AlertDialog.BUTTON_POSITIVE == which) {
            save(getValue());
        }
    }

    public int getValue() {
        if (mStep == 1) {
            return mPicker.getValue();
        } else {
            return Integer.parseInt(mValues[mPicker.getValue()]);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        save(getValue());
        mDialog.dismiss();
    }

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}