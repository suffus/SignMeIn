package com.premiumlets.apps.signinapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carolynwright on 03/03/2018.
 */

public class JListItem implements JSONConstructor<JListItem> {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String id;
    private String name;
    private String icon;

    JListItem() {
        this.id="";
        this.name="";
        this.icon="";
    }

    JListItem( String id, String name, String icon ) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public JListItem getNew() {
        return new JListItem();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JListItem jListItem = (JListItem) o;

        return getId().equals(jListItem.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    public JListItem fromJSON(JSONObject j0) {
        setName( j0.optString("name") );
        setId( j0.optString( "id" ));
        setIcon( j0.optString("icon"));
        return this;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject rv = new JSONObject();
        rv.put( "name", getName());
        rv.put( "id", getId() );
        if( getIcon().length() > 0 ) {
            rv.put("icon", getIcon());
        }
        return rv;
    }

    static JListItem builder = new JListItem("", "", "");

}
