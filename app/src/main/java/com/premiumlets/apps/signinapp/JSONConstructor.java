package com.premiumlets.apps.signinapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carolynwright on 03/03/2018.
 */

public interface JSONConstructor<T> {
    public T fromJSON(JSONObject j);
    public T getNew();
    public JSONObject toJSON() throws JSONException;
    String getId();
}
