package com.example.android.lagdev;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.lagdev.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

/**
 * Created by sodiqOladeni on 27/08/2017.
 */
public class ProfileAdapter extends ArrayAdapter<Profile> {
    Activity context;

    public ProfileAdapter(Activity context, ArrayList<Profile> profile) {
        super(context, 0, profile);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Profile currentProfile = getItem(position);
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.profileIcon = (CircleImageView) itemView.findViewById(R.id.profile_icon);
            //holder.gitUrl = (TextView) itemView.findViewById(R.id.txt_git_url);
            holder.userName = (TextView) itemView.findViewById(R.id.txt_user_name);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        Picasso.with(context).load(currentProfile.getMProfileResourceId()).resize(100, 100).placeholder(R.drawable.avatar).into(holder.profileIcon);
        //holder.gitUrl.setText(currentProfile.getMGitUrl());
        holder.userName.setText(currentProfile.getMUserName());

        return itemView;
    }

    private class ViewHolder {
        //TextView gitUrl;
        TextView userName;
        CircleImageView profileIcon;
    }


}

