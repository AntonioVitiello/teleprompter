package com.av.teleprompter.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;
import com.av.teleprompter.data.loader.ScriptAsyncTaskLoader;
import com.av.teleprompter.test.SimpleIdlingResource;
import com.av.teleprompter.utils.ActionUtils;
import com.av.teleprompter.view.adapter.CustomCursorAdapter;
import com.av.teleprompter.view.adapter.ScriptAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Antonio Vitiello
 */
public class MainActivity extends BaseActivity implements ScriptAdapter.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SCRIPT_LOADER_ID = 0;
    private static final long IDLE_STATE_DELAY_MILLIS = 3000;

    @BindView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    // Just for Expresso tests, idling resources will be null in production
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private CustomCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int columns = getResources().getInteger(R.integer.grid_cols);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(SCRIPT_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(SCRIPT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ScriptAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }, IDLE_STATE_DELAY_MILLIS);

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @OnClick(R.id.add_fab)
    public void addScript() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickListener(int position) {
        ActionUtils.prePlay(mAdapter.getItem(position), this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = item.getGroupId();
        Script script = mAdapter.getItem(position);

        switch (item.getItemId()) {
            case CustomCursorAdapter.PLAY_MENU_ITEM_ID:
                ActionUtils.prePlay(script, this);
                return true;
            case CustomCursorAdapter.MODIFY_MENU_ITEM_ID:
                ActionUtils.modify(script, this);
                return true;
            case CustomCursorAdapter.DELETE_MENU_ITEM_ID:
                ActionUtils.delete(script, mToolbarLayout, this);
                // Restart the loader to re-query for all scripts after a deletion
                getSupportLoaderManager().restartLoader(SCRIPT_LOADER_ID, null, this);
                return true;
            case CustomCursorAdapter.SETTINGS_MENU_ITEM_ID:
                ActionUtils.settings(this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

}
