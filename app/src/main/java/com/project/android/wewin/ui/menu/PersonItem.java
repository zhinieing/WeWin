package com.project.android.wewin.ui.menu;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.wewin.R;
import com.project.android.wewin.utils.Util;

/**
 * Created by pengming on 20/12/2017.
 */

public class PersonItem extends DrawerItem<PersonItem.ViewHolder> {
    private Uri imageUri;
    private String username;

    public PersonItem(Uri imageUri, String username){
        this.imageUri = imageUri;
        this.username = username;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.person_item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        Util.loadCircleImage(imageUri, holder.imageView);

        holder.username.setText(username);
    }

    public class ViewHolder extends DrawerAdapter.ViewHolder {
        private ImageView imageView;
        private TextView username;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.side_person_img);
            username = itemView.findViewById(R.id.side_person_username);

        }
    }
}
