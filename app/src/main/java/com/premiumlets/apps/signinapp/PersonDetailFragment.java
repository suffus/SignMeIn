package com.premiumlets.apps.signinapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static com.premiumlets.apps.signinapp.DaxtraKeysModel.getModel;

/**
 * Created by galois on 3/9/18.
 */

public class PersonDetailFragment extends Fragment {

    PersonItem person = new PersonItem();
    View thisView;
    boolean details_requested = false;

    DListValues phoneDialog;

    static PersonDetailFragment thisFragment = new PersonDetailFragment();

    public PersonDetailFragment() {
        super();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_detail, container, false);
        thisView = v;
        thisFragment = this;
        //person = new PersonItem();
        // Activity vActivity = getActivity();
        final MainActivity vActivity = (MainActivity)getActivity();
        ActionBar ab = vActivity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled( true );
        ab.setHomeButtonEnabled( true );

        ab.setTitle( "Detailed Person View");

        ImageButton btnEmail = (ImageButton) v.findViewById(R.id.pd_btn_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailIntent();
                //Toast.makeText(getActivity(), "Code requested", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnPhone = (ImageButton) v.findViewById( R.id.pd_btn_phone);
        btnPhone.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( phoneDialog != null ) {
                    phoneDialog.show();
                }
            }
        });

        if( person != null ) {
            showPerson();
        }

        if( (!details_requested) && person != null ) {
            getModel().getUserDetails(person.getId());
            details_requested = true;
        }

        return v;
    }

    @Override
    public void onSaveInstanceState( Bundle state ) {
        state.putString( "PersonId", person.getId());
        state.putString( "PersonName", person.getPersonName());
        state.putString( "PersonJobTitle", )
    }

    @Override
    public void onDestroyView() {
        phoneDialog = null;
        person = null;
        details_requested = false;
        super.onDestroyView();
    }

    public void setPerson( PersonItem person ) {
        this.person = person;
        //details_requested = false;
        if( thisView != null && person != null ) {
            showPerson();
        }
    }

    public void showPerson(){
        TextView personNameView = thisView.findViewById( R.id.pd_name );
        TextView personStatusView = thisView.findViewById( R.id.pd_statusline );
        TextView personTitleView = thisView.findViewById( R.id.pd_jobtitle );
        TextView cookieView = thisView.findViewById( R.id.pd_dot_cookie );
        ImageView iconView = thisView.findViewById( R.id.pd_image );

        personNameView.setText( person.getPersonName());
        personTitleView.setText( person.getJobTitle());
        personStatusView.setText( person.getStatus().getStatusLine());
        cookieView.setText( "Hello World!!!");
        iconView.setImageResource( person.getStatus().getIconResourceID() );

        if( details_requested ) {
            if( person.getContactDetails().vTelephones.length > 0 ) {
                phoneDialog = new DListValues( getActivity(), "Phone numbers", JListItem.builder ) {
                    @Override
                    public void onSelect(  ) {
                        //getModel()._error( new Exception("selected" ));
                       // makePhoneCall( "07912326620" );
                                makePhoneCall(get(selectedIndex).getName());
                    }
                };
                phoneDialog.contents.clear();
                PersonItem.PersonContact.TelephoneNumber[] arr = person.getContactDetails().vTelephones;
                for( int i = 0; i < arr.length; ++i ) {
                    phoneDialog.add( new JListItem(arr[i].getNumber(), arr[i].getNumber(), ""));
                }
            }
        }
    }

    public void sendEmailIntent() {

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setData(Uri.parse("mailto:"+person.getContactDetails().getEmail())); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);

    }

    public void makePhoneCall( String num ) {
        if(false && ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            getModel()._error( new Exception( "Cannot call phone for this"));
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData( Uri.parse( "tel:"+num));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        }
    }

}
