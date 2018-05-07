package com.project.android.wewin.ui.adapter;

/**
 * @author pengming
 */
public interface ItemTouchHelperAdapter<T> {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDissmiss(int position);

    void onItemReset(int position, T t);
}
