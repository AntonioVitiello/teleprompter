package com.av.teleprompter.data;


import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.av.teleprompter.R;

/**
 * Created by Antonio Vitiello
 */
public class Script implements Parcelable {
    private String mTitle;
    private String mBody;
    private long mId;

    public Script(String title, String body, long id) {
        mTitle = title;
        mBody = body;
        mId = id;
    }

    public static Script from(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(ScriptContract.ScriptEntry.TITLE));
        String body = cursor.getString(cursor.getColumnIndex(ScriptContract.ScriptEntry.BODY));
        long id = cursor.getLong(cursor.getColumnIndex(ScriptContract.ScriptEntry._ID));
        return new Script(title, body, id);
    }

    public static Script deafult(Context context) {
        String title = context.getString(R.string.app_name);
        String body = context.getString(R.string.script_body);
        return new Script(title, body, 1);
    }

    protected Script(Parcel in) {
        mTitle = in.readString();
        mBody = in.readString();
        mId = in.readLong();
    }

    public static final Creator<Script> CREATOR = new Creator<Script>() {
        @Override
        public Script createFromParcel(Parcel in) {
            return new Script(in);
        }

        @Override
        public Script[] newArray(int size) {
            return new Script[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public long getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "Script{" +
                "mTitle='" + mTitle + '\'' +
                ", mBody='" + mBody + '\'' +
                ", mId=" + mId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mBody);
        dest.writeLong(mId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Script))
            return false;

        Script s = (Script) o;
        if (mId != s.mId)
            return false;
        if (!(mTitle == null ? s.mTitle == null : mTitle.equals(s.mTitle)))
            return false;
        if (!(mBody == null ? s.mBody == null : mBody.equals(s.mBody)))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (mTitle == null ? 0 : mTitle.hashCode());
        hashCode = 31 * hashCode + (mBody == null ? 0 : mBody.hashCode());
        hashCode = 31 * hashCode + (int) mId;
        return hashCode;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setBody(String body) {
        mBody = body;
    }

}