package com.premiumlets.apps.signinapp;

import com.premiumlets.apps.signinapp.JListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by carolynwright on 05/03/2018.
 */

public class PersonItem extends JListItem {

    static JListItem builder = new PersonItem();


    class PersonStatus {
        public String getStatus() {
            return vStatus;
        }

        public void setStatus(String vStatus) {
            this.vStatus = vStatus;
        }

        private String vStatus;

        public String getLocation() {
            return vLocation;
        }

        public void setLocation(String vLocation) {
            this.vLocation = vLocation;
        }

        public Date getRecordDate() {
            return vRecordDate;
        }

        public void setRecordDate(Date vRecordDate) {
            this.vRecordDate = vRecordDate;
        }

        private String vLocation;
        private Date vRecordDate;

        public int getIconResourceID() {
            return vIconResourceID;
        }

        public void setIconResourceID(int vIconResourceID) {
            this.vIconResourceID = vIconResourceID;
        }

        private int vIconResourceID=R.drawable.ic_icon_status_unknown;
        PersonStatus() {

        }
        public PersonStatus fromJSON( JSONObject j0 ) {
            String stat = j0.optString( "status", "unkown");
            setStatus( stat );
            String loc = j0.optString( "location", "unknown");
            setLocation( loc );
            return this;
        }
        public String getStatusLine() {
            String statusLine = "";
            vIconResourceID = R.drawable.ic_icon_status_unknown;
            if( getStatus().equals("working") ) {
                statusLine = "Working in " + getLocation();
                vIconResourceID = R.drawable.img_satus_working;
            }  else if( getStatus().equals("out") ) {
                statusLine = "Out";
                vIconResourceID = R.drawable.icon_sleep;
            } else if( getStatus().equals("absent") ) {
                statusLine = getLocation();
                vIconResourceID = R.drawable.icon_out;
                if( getLocation().equals("sick")) {
                    vIconResourceID = R.drawable.icon_sick;
                } else if( getLocation().equals("holiday")) {
                    vIconResourceID = R.drawable.icon_holiday;
                } else if( getLocation().equals("traveling")) {
                    vIconResourceID = R.drawable.icon_travel;
                }
            } else if( getStatus().equals("outlunch") ) {
                statusLine = "On a break";
                vIconResourceID = R.drawable.icon_break;
            } else if( getStatus().equals("unknown")) {
                statusLine = "Not signed in";
            }
            return statusLine;
        }
        public boolean isWorkingStatus() {
            if( getStatus().equals("working")) {
                return true;
            }
            return false;
        }
    }

    class PersonContact {
        String vEmail;
        TelephoneNumber[] vTelephones = new TelephoneNumber[0];

        class TelephoneNumber {
            String vProt;
            String vNumber;
            String vExtension;
            String vType;

            public String getType() {
                return vType;
            }

            public void setType(String vType) {
                this.vType = vType;
            }

            public String getProt() {
                return vProt;
            }

            public void setProt(String vProt) {
                this.vProt = vProt;
            }

            public String getNumber() {
                return vNumber;
            }

            public void setNumber(String vNumber) {
                this.vNumber = vNumber;
            }

            public String getExtension() {
                return vExtension;
            }

            public void setExtension(String vExtension) {
                this.vExtension = vExtension;
            }

            TelephoneNumber() {
                setProt("unknown");
            }

            TelephoneNumber fromJSON( JSONObject j0 ) {
                setType( j0.optString( "type", "unkown"));
                setNumber( j0.optString( "number", "unknown"));
                setExtension( j0.optString( "extension", ""));
                setProt( j0.optString( "protocol", "tel"));
                return this;
            }
        }

        public String getEmail() {
            return vEmail;
        }

        public void setEmail(String vEmail) {
            if( vEmail != null ) {
                this.vEmail = vEmail;
            }
        }


        PersonContact() {

        }

        public TelephoneNumber getTelephoneByType( String t ) {
            if( vTelephones == null ) {
                return null;
            }
            for( int i = 0; i < vTelephones.length; ++i ) {
                if( vTelephones[i].getType().equals(t )) {
                    return vTelephones[i];
                }
            }
            return null;
        }

        public PersonContact fromJSON( JSONObject j0 ) {
            setEmail( j0.optString( "email", ""));
            JSONArray telArray = j0.optJSONArray( "telephones");
            if( telArray != null ) {
                vTelephones = new TelephoneNumber[telArray.length()];
                for( int i=0; i < telArray.length(); ++i ) {
                    TelephoneNumber num = new TelephoneNumber();
                    num.fromJSON(telArray.optJSONObject(i));
                    vTelephones[i] = num;
                }
            }
            return this;

        }

    }

    private PersonStatus vStatus = new PersonStatus();
    private PersonContact vContactDetails = new PersonContact();
    private String vOffice;
    private String vJobTitle;
    private int vPersonID;


    public String getJobTitle() {
        return vJobTitle;
    }

    public void setJobTitle(String vJobTitle) {
        this.vJobTitle = vJobTitle;
    }

    public String getOffice() {
        return vOffice;
    }

    public void setOffice(String vOffice) {
        if( vOffice != null ) {
            this.vOffice = vOffice;
        }
    }

    public String getPersonName() {
        return getName();
    }

    public void setPersonName(String vPersonName) {
        setName( vPersonName );
    }

    public PersonStatus getStatus() {
        return vStatus;
    }

    public void setStatus(PersonStatus vStatus) {
        this.vStatus = vStatus;
    }

    public PersonContact getContactDetails() {
        return vContactDetails;
    }

    public void setContactDetails(PersonContact vContactDetails) {
        this.vContactDetails = vContactDetails;
    }

    public int getPersonID() {
        return vPersonID;
    }

    public void setPersonID(int vPersonID) {
        if( vPersonID > 0 ) {
            this.vPersonID = vPersonID;
        }
    }

    public PersonItem() {

    }

    public JListItem getNew() {
        return new PersonItem();
    }

    public PersonItem fromJSON( JSONObject j0 ) {
        getContactDetails().fromJSON( j0 );
        getStatus().fromJSON( j0 );
        setName( j0.optString("name") );
        setId( j0.optString( "id" ));
        setIcon( j0.optString("icon"));
        setPersonID( j0.optInt( "userid"));
        setOffice( j0.optString( "department"));
        setJobTitle( j0.optString( "job_title"));

        return this;
    }
}
