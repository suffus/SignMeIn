package com.premiumlets.apps.signinapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

/**
 * Created by steve on 2/22/18.
 */

public class DaxtraNavigatorFragment extends android.support.v4.app.Fragment {

    static DaxtraNavigatorFragment theNavigator;
    public final static long CODE_TIMEOUT_MILLIS = 180000;

    PersonItem person;
    CountDownTimer ticker;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    ImageButton btnCode;
    ImageButton btnList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        theNavigator = this;
        View v = inflater.inflate(R.layout.content_main, container, false);
        MainActivity vActivity = (MainActivity) getActivity();

        vActivity._debug( "Create Fragment");

        Toolbar toolbar = vActivity.findViewById( R.id.toolbar);
        drawer = (DrawerLayout) vActivity.findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                vActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.btnCode = (ImageButton) v.findViewById(R.id.codeButton);
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DaxtraKeysModel.getModel().getCode();
                //Toast.makeText(getActivity(), "Code requested", Toast.LENGTH_SHORT).show();
            }
        });
        btnCode.setEnabled( false );
        btnCode.setClickable( false );
        this.btnList = (ImageButton) v.findViewById( R.id.listButton);
        btnList.setOnClickListener( new View.OnClickListener(  ) {
            @Override
            public void onClick( View v ) {
                DaxtraKeysModel.getModel().getUserList();
            }
        } );


        btnList.setEnabled( false );
        btnList.setClickable( false );

        DaxtraKeysModel.getModel().getMetaData();

        vActivity.getSupportActionBar().setTitle( "The Daxtra App");
        drawer.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled( true );
        return v;
    }

    @Override
    public void onDestroyView() {
        toggle.setDrawerIndicatorEnabled( false );
        toggle.setHomeAsUpIndicator(0);
        drawer.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED );
        super.onDestroyView();
    }

    private void setTimeout( int pct ) {
        ProgressBar b = getActivity().findViewById( R.id.codeTimeoutBar );
        if( b!=null ) {
            b.setProgress(pct);
        }
    }

    public void setCode( String code ) {
        final TextView tv = getActivity().findViewById( R.id.greeting );
        tv.setText(code);

        if( ticker != null ) {
            ticker.cancel();
        }
        ticker = new CountDownTimer( CODE_TIMEOUT_MILLIS, 250) {
            public void onTick( long millis ) {
                int pct = (int)(millis*100/CODE_TIMEOUT_MILLIS);
                setTimeout( pct );
            }
            public void onFinish() {
                tv.setText("Code Expired");
                ticker = null;
            }
        }.start();
        setTimeout( 100 );
    }


    public void setUserData( JSONObject node ) {
        //Toast.makeText( getActivity(), "The node: "+node.toString(), Toast.LENGTH_LONG).show();
        person = new PersonItem();
        person.fromJSON(node);
        setUserData(person);
    }

    public void setUserData( PersonItem p ) {
        person = p;
        TextView personNameView = getActivity().findViewById( R.id.personName );
        personNameView.setText( person.getPersonName() );
        TextView personStatusView = getActivity().findViewById( R.id.personStatus );
        String statusLine = person.getStatus().getStatusLine();
        personStatusView.setText( statusLine );
        TextView greetingView = getActivity().findViewById(R.id.greeting);

        if( ticker == null ) {
            greetingView.setText("Welcome to Daxtra!");
        }
        ImageView statusIconView = getActivity().findViewById( R.id.statusIconLarge );
        statusIconView.setImageResource( person.getStatus().getIconResourceID() );
        if( ! person.getStatus().getStatus().equals("unknown") ) {
            btnCode.setEnabled( true );
            btnCode.setClickable( true );
            btnList.setEnabled( true );
            btnList.setClickable( true );
        } else {
            btnCode.setEnabled( false );
            btnCode.setClickable( false );
            btnList.setEnabled( false );
            btnList.setClickable( false );
            greetingView.setText( "Please Sign In!" );
        }
    }

}
