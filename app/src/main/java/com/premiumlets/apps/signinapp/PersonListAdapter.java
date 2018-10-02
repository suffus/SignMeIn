package com.premiumlets.apps.signinapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by steve on 29/03/18.
 */

public class PersonListAdapter extends ArrayAdapter {
    public PersonListAdapter(Context ctxt, List<JListItem> lst ) {
        super( ctxt, R.layout.person_list_item, lst );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PersonItem personItem = (PersonItem)getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.person_list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.list_person_name);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.list_person_status);
        ImageView ivIcon = (ImageView) convertView.findViewById( R.id.list_person_icon );
        // Populate the data into the template view using the data object
        tvName.setText( personItem.getName() );
        tvStatus.setText( personItem.getStatus().getStatusLine());
        ivIcon.setImageResource( personItem.getStatus().getIconResourceID() );

        // Return the completed view to render on screen
        return convertView;
    }
}

