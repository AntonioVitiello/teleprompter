package com.av.teleprompter.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.av.teleprompter.R;
import com.av.teleprompter.data.Script;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.ViewHolder> {

    public static final int PLAY_MENU_ITEM_ID = 0;
    public static final int MODIFY_MENU_ITEM_ID = 1;
    public static final int DELETE_MENU_ITEM_ID = 2;
    public static final int SETTINGS_MENU_ITEM_ID = 3;

    private List<Script> mScripts = new ArrayList<>();
    private OnClickListener mClickListener;

    public ScriptAdapter(OnClickListener listener) {
        mClickListener = listener;
    }

    public void updateData(List<Script> scripts) {
        mScripts.clear();
        mScripts.addAll(scripts);
        notifyDataSetChanged();
    }

    public void delete(Script script) {
        mScripts.remove(script);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.script_item, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Script script = mScripts.get(position);
        holder.bind(script);
    }

    @Override
    public int getItemCount() {
        return mScripts.size();
    }

    public Script getItem(int position) {
        return mScripts.get(position);
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        @BindView(R.id.script_title)
        TextView title;
        @BindView(R.id.script_body)
        TextView desc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Script script) {
            title.setText(script.getTitle());
            desc.setText(script.getBody());
            itemView.setContentDescription(script.getTitle());
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
