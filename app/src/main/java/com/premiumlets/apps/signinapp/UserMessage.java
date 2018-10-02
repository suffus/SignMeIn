package com.premiumlets.apps.signinapp;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by steve on 4/5/18.
 */

public class UserMessage extends JListItem {
    PersonItem sender;
    String theMessage;
    Date receivedDate;
    String messageId;

    public PersonItem getSender() {
        return sender;
    }

    public void setSender(PersonItem sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return theMessage;
    }

    public void setMessage(String theMessage) {
        this.theMessage = theMessage;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    UserMessage( PersonItem sender ) {
        this.sender = sender;

    }


    public JListItem fromJSON( JSONObject j0 ) {
        sender = new PersonItem();
        JSONObject s0 = j0.optJSONObject( "sender" );
        sender.fromJSON( s0 );
        setMessage( j0.optString( "message" ));
        setMessageId( j0.optString( "id"));
        return this;
    }
}
