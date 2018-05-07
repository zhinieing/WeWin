package com.project.android.wewin.ui.adapter;

import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.local.db.entity.GroupInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pengming
 */
public class SimpleItemTouchHelperCallback<T> extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    private ItemDissmissListener listener;
    private List<T> lists = new ArrayList<>();
    private boolean delete = true;


    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter<T> adapter, ItemDissmissListener listener) {
        mAdapter = adapter;
        this.listener = listener;
    }


    public void set(List<T> data) {
        lists.clear();
        lists.addAll(data);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags, swipeFlags;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;
        } else {
            swipeFlags = ItemTouchHelper.LEFT;
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        Collections.swap(lists, viewHolder.getAdapterPosition(),target.getAdapterPosition());
        listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        delete = true;
        mAdapter.onItemDissmiss(position);

        Snackbar.make(viewHolder.itemView, R.string.delete_group_success, Snackbar.LENGTH_LONG)
                .setAction(R.string.reset, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete = false;
                        mAdapter.onItemReset(position, lists.get(position));
                    }
                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (delete) {
                    lists.remove(position);
                    listener.onItemDismiss(position);
                }

            }
        }).show();
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

}
