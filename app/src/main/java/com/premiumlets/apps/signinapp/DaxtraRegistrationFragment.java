package com.premiumlets.apps.signinapp;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * Created by steve on 2/23/18.
 */

public class DaxtraRegistrationFragment extends Fragment {

    public void onCreate() {

    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_device, container, false);

        EditText txt = (EditText) v.findViewById( R.id.registrationCode );
        txt.addTextChangedListener( new TextWatcher() {
            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count) {
                Button btn = (Button) getActivity().findViewById( R.id.btnRegister );
                if( s.length() == 6 ) {
                    btn.setEnabled( true );
                } else {
                    btn.setEnabled( false );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
        });

        Button btn = (Button)v.findViewById( R.id.btnRegister );
        btn.setEnabled( false );
        btn.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick( View v ) {
                EditText txt = getActivity().findViewById( R.id.registrationCode );
                String code = txt.getText().toString();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
                DaxtraKeysModel.getModel().registerDevice( code );
            }
        });

        return v;

    }
}
