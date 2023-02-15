package com.premiumlets.apps.signinapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve on 02/03/18.
 */

public class DListValues {
    JSONConstructor<JListItem> builder;
    List<JListItem> contents;
    ArrayAdapter listViewAdapter;
    ListView listView;

    int selectedIndex;

    AlertDialog dialog = null;
    Activity activity = null;
    int nItems;
    String title;

    DListValues( Activity a, String t, JSONConstructor bld ) {
        activity = a;
        title = t;
        contents = new ArrayList<JListItem>();
        builder = bld;
        nItems = 0;
    }

    public void add( JSONObject obj  ) {
        if( obj==null) {
            return;
        }
        JListItem tNew = builder.getNew();
        tNew.fromJSON( obj );
        contents.add( tNew );
    }

    public void add( JListItem item ) {
        contents.add( item );
    }

    public void setArray( JSONArray jar ) {
        contents.clear();
        for( int i=0; i<jar.length(); ++i ) {
            add( jar.optJSONObject( i ) );
        }
    }
    public Object[] getContents() { return contents.toArray( ); }

    public String [] getNames() {
        String[] rv = new String[ contents.size() ];
        for( int i=0; i<contents.size(); ++i ) {
            rv[i] = contents.get(i).toString();
        }
        return rv;
    }

    public void setAdapter( ArrayAdapter adapter ) {
        listViewAdapter = adapter;
        if( listView != null ) {
            listView.setAdapter( listViewAdapter );
        }

    }

    public ArrayAdapter getAdapter() {
        return listViewAdapter;
    }

    public JListItem get( int n ) { return contents.get( n ); }

    public String getId( int n ) {return ((JSONConstructor<JListItem>)contents.get( n )).getId(); }

    public void onSelect() {

    }

    public void createDialog( ) {
        LinearLayout layout = new LinearLayout(activity);

        //Setup Layout Attributes
        LinearLayout.LayoutParams paramsLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(paramsLL);
        layout.setOrientation(LinearLayout.VERTICAL);



        listView = new ListView( activity );
        ListView.LayoutParams paramsLV = new ListView.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        listView.setLayoutParams( paramsLV );
        layout.addView( listView );

        dialog = new AlertDialog.Builder( activity ).create();
        dialog.setView( layout );
        dialog.setTitle(title);

        if( listViewAdapter == null ) {
            listViewAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, getNames());
        }

        setAdapter( listViewAdapter );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
                onSelect( );
                dialog.dismiss();
            }
        });
    }

    public void show() {
        if( dialog == null ) {
            createDialog( );
        }
        dialog.show();
    }
}
