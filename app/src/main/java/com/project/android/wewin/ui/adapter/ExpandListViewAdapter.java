package com.project.android.wewin.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.utils.Util;

import java.util.List;

/**
 * Created by pengming on 26/12/2017.
 */

public class ExpandListViewAdapter extends BaseExpandableListAdapter {
    private List<Class> mListData;
    private LayoutInflater mInflate;
    private Context context;

    public ExpandListViewAdapter(List<Class> mListData, Context context) {
        this.mListData = mListData;
        this.context = context;
        this.mInflate = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListData.get(groupPosition).getGroupInfos().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FirstHolder holder = null;
        if (convertView == null) {
            holder = new FirstHolder();
            convertView = mInflate.inflate(R.layout.item_expand_lv_first, parent, false);
            holder.leftTv = convertView.findViewById(R.id.first_left_tv);
            holder.rightTv = convertView.findViewById(R.id.first_right_tv);

            convertView.setTag(holder);
        } else {
            holder = (FirstHolder) convertView.getTag();
        }
        holder.leftTv.setText(mListData.get(groupPosition).getClassName());
        holder.rightTv.setText(mListData.get(groupPosition).getTeacherSize() + "/" + mListData.get(groupPosition).getStudentSize());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CustomExpandableListView lv = ((CustomExpandableListView) convertView);
        if (convertView == null) {
            lv = new CustomExpandableListView(context);
        }

        SecondAdapter secondAdapter = new SecondAdapter(context, mListData.get(groupPosition).getGroupInfos());

        lv.setAdapter(secondAdapter);
        return lv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    /*
  *   第二层的适配器
  * */
    class SecondAdapter extends BaseExpandableListAdapter {
        Context context;
        LayoutInflater inflater;
        List<GroupInfo> listSecondModel;

        public SecondAdapter(Context context,List<GroupInfo> listSecondModel) {
            this.context = context;
            this.listSecondModel = listSecondModel;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {

            return listSecondModel == null ? 0 : listSecondModel.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return listSecondModel.get(groupPosition).getGroupMembers() == null ? 0 : listSecondModel.get(groupPosition).getGroupMembers().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listSecondModel.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return listSecondModel.get(groupPosition).getGroupMembers().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            SecondHolder holder = null;
            if (convertView == null) {
                holder = new SecondHolder();
                convertView = mInflate.inflate(R.layout.item_expand_lv_second, parent, false);
                holder.leftTv = convertView.findViewById(R.id.second_left_tv);
                holder.rightTv = convertView.findViewById(R.id.second_right_tv);

                convertView.setTag(holder);
            } else {
                holder = (SecondHolder) convertView.getTag();
            }
            holder.leftTv.setText(listSecondModel.get(groupPosition).getGroupName());
            holder.rightTv.setText(listSecondModel.get(groupPosition).getMemberSize() + "");

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ThirdHolder holder = null;
            if (convertView == null) {
                holder = new ThirdHolder();
                convertView = mInflate.inflate(R.layout.item_expand_lv_third, parent, false);
                holder.iv = convertView.findViewById(R.id.third_iv);
                holder.tv = convertView.findViewById(R.id.third_tv);

                convertView.setTag(holder);
            } else {
                holder = (ThirdHolder) convertView.getTag();
            }

            String imgUrl = listSecondModel.get(groupPosition).getGroupMembers().get(childPosition).getUserPhoto();
            if (imgUrl != null) {
                Util.loadCircleImage(Uri.parse(imgUrl), holder.iv);
            } else {
                Util.loadCircleImage(Uri.parse(""), holder.iv);
            }

            holder.tv.setText(listSecondModel.get(groupPosition).getGroupMembers().get(childPosition).getUserName());

            return convertView;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    class FirstHolder {
        TextView leftTv;
        TextView rightTv;

    }

    class SecondHolder {
        TextView leftTv;
        TextView rightTv;

    }

    class ThirdHolder{
        ImageView iv;
        TextView tv;
    }
}
