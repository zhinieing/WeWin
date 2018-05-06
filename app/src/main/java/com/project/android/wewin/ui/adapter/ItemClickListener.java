package com.project.android.wewin.ui.adapter;

/**
 * @author pengming
 */
public interface ItemClickListener<T> extends BaseViewAdapter.Presenter {

    void onItemClick(T t);
}
