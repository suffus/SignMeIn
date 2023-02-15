package com.premiumlets.apps.signinapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static int REGISTER_DEVICE = 0;
    public final static int GENERATE_CODE = 1;
    public final static int USER_DETAILS = 2;
    public final static int LIST_USERS = 3;
    public final static int SET_USER_STATUS = 4;
    public final static int SET_METADATA = 5;
    public final static int GET_USER_DETAILS = 6;

    String location="home";
    boolean location_set_p = false;
    //AlertDialog locationDialog=null;
    //AlertDialog absenceDialog = null;

    DListValues locationDialog;
    DListValues absenceDialog;
    DListValues personListDialog;

    String absenceNames[] = new String[1];
    String absenceIDs[] = new String[1];

    Fragment mainFragment = null;

    public DaxtraKeysModel getModel() {
        return DaxtraKeysModel.getModel();
    }

    public void _debug( String s ) {
        Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaxtraKeysModel.getModel().setActivity( this );

        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if( !DaxtraKeysModel.getModel().isRegistered() ) {
            setFragment( new DaxtraRegistrationFragment() );
        } else {
            setFragment( new DaxtraNavigatorFragment() );
        }

        absenceDialog = new DListValues( this, "Reason for absence?", JListItem.builder) {
            @Override
            public void onSelect( ) {
                getModel().setUserStatus( getId( selectedIndex ), getId( selectedIndex ));
            }
        };

        locationDialog = new DListValues( this, "Where are you?", JListItem.builder) {
            @Override
            public void onSelect( ) {
                getModel().setUserStatus( "signin", getId( selectedIndex ));
            }

        };

        personListDialog = new DListValues( this, "People of Daxtra", PersonItem.builder) {
            @Override
            public void onSelect() {
                PersonDetailFragment p = PersonDetailFragment.thisFragment;
                p.setPerson((PersonItem) contents.get( selectedIndex ));

                setFragment( p, true );
                Toast.makeText(getApplicationContext(), "PING", Toast.LENGTH_LONG).show();

                //getModel().getUserDetails( getId( selectedIndex ));
            }
        };
    }

    public void setFragment( Fragment f ) {
        setFragment( f, false );
    }

    public void setFragment( Fragment f, boolean addToStack ) {
        LinearLayout fragContainer = (LinearLayout)findViewById( R.id.mainFragmentContainer );
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction ft = null;

        if( mainFragment == null ) {
            ft = fragMan.beginTransaction().add(R.id.mainFragmentContainer, f);
            //_debug(" Added New Fragment ");
        } else {
            ft = fragMan.beginTransaction().replace(R.id.mainFragmentContainer, f);
            //_debug(" Replaced Fragment ");
        }
        mainFragment = f;
        if( addToStack ) {
            ft.addToBackStack( null );
        }
        ft.commit();
    }

    public void setText( int id, String nm ) {
        TextView tv = findViewById( id );
        tv.setText( nm);
    }

    public void onEvent(int event, JSONObject node) {
        switch( event ) {
            case REGISTER_DEVICE:
                setFragment(new DaxtraNavigatorFragment());
                break;
            case GENERATE_CODE:
                break;
            case USER_DETAILS:
            case SET_METADATA:
                DaxtraNavigatorFragment.theNavigator.setUserData( node );
                JSONArray loc_arr = node.optJSONArray( "locations" );
                if( loc_arr != null ) {
                    locationDialog.setArray(loc_arr);
                }
                JSONArray ab_arr = node.optJSONArray( "absences" );
                if( ab_arr != null ) {
                    absenceDialog.setArray(ab_arr);
                }
                break;
            case SET_USER_STATUS:
                //Toast.makeText( this, node.toString(), Toast.LENGTH_LONG).show();
                DaxtraNavigatorFragment.theNavigator.setUserData( node );
                break;
            case GET_USER_DETAILS:
                try {
                    PersonDetailFragment.thisFragment.person.fromJSON(node);
                    PersonDetailFragment.thisFragment.showPerson();
                } catch( Exception e ) {
                    getModel()._error( e );
                }
                break;
            case LIST_USERS:
                JSONArray peopleArray = node.optJSONArray( "users" );
                personListDialog.setArray( peopleArray );
                personListDialog.setAdapter( new PersonListAdapter( this, personListDialog.contents ));
                personListDialog.show(); // now create view
                break;
       }
    }

    public void onEvent( int event, String msg ) {
        if( event == REGISTER_DEVICE ) {
            msg = "Device registered OK";
            LinearLayout fragContainer = (LinearLayout)findViewById( R.id.mainFragmentContainer );
            getSupportFragmentManager().beginTransaction().replace( fragContainer.getId(), new DaxtraNavigatorFragment()).commit();
        } else if( event == GENERATE_CODE ) {
            DaxtraNavigatorFragment.theNavigator.setCode( msg );
            return;
        } else if( event == USER_DETAILS ) {

            //    DaxtraNavigatorFragment.theNavigator.setUser( msg );
        }

        Toast.makeText( this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list_all) {

        } else if (id == R.id.status_signin) {
            locationDialog.show();
        } else if (id == R.id.status_outlunch) {
            getModel().setUserStatus("outlunch","anywhere");
        } else if (id == R.id.status_out) {
            getModel().setUserStatus("out", "home");
        } else if (id == R.id.status_traveling) {
            getModel().setUserStatus("traveling", "home");
        } else if (id == R.id.status_absent) {
            absenceDialog.show();
        } else if( id == android.R.id.home) {
            _debug( "Back Pressed" );
            onBackPressed();
            return true;
        } else {
            _debug("Unknown option");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
