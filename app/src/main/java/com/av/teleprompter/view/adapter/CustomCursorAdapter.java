package com.av.teleprompter.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Antonio Vitiello
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.CustomViewHolder> {

    public static final int PLAY_MENU_ITEM_ID = 0;
    public static final int MODIFY_MENU_ITEM_ID = 1;
    public static final int DELETE_MENU_ITEM_ID = 2;
    public static final int SETTINGS_MENU_ITEM_ID = 3;

    private ScriptAdapter.OnClickListener mClickListener;
    private Cursor mCursor;


    public CustomCursorAdapter(ScriptAdapter.OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.script_item, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        Script script = Script.from(mCursor);
        viewHolder.bind(script);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    public Script getItem(int position) {
        Script script = null;
        if (mCursor != null && mCursor.moveToPosition(position)) {
            script = Script.from(mCursor);
        }

        return script;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = cursor;

        //check if this is a valid cursor, then update the cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }

        return oldCursor;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.script_title)
        TextView title;
        @BindView(R.id.script_body)
        TextView body;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Script script) {
            title.setText(script.getTitle());
            body.setText(script.getBody());
            itemView.setContentDescription(script.getTitle());
            itemView.setTag(script.getId());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickListener.onClickListener(position);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();
            menu.setHeaderTitle(title.getText());
            //       groupId,       itemId,    order,  title
            menu.add(position, PLAY_MENU_ITEM_ID, 0, R.string.play_menu_item);
            menu.add(position, MODIFY_MENU_ITEM_ID, 1, R.string.modify_menu_item);
            menu.add(position, DELETE_MENU_ITEM_ID, 2, R.string.delete_menu_item);
            menu.add(position, SETTINGS_MENU_ITEM_ID, 3, R.string.settings_menu_item);
        }

    }

}
