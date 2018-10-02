package com.premiumlets.apps.signinapp;

/**
 * Created by galois on 2/22/18.
 */

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Steve on 1/26/2016.
 */
public class Downloader extends AsyncTask<String,Void,String> {
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 50000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            //Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, len);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        final int CHUNK = 8192;
        byte[] buffer = new byte[CHUNK];
        ArrayList<byte[]> bset = new ArrayList<>(5);
        int tlen = 0;
        int offset = 0;
        while( true ) {
            int nb = stream.read(buffer, offset, CHUNK - offset);
            if (nb > 0) {
                tlen += nb;
                offset = CHUNK - offset - nb;
                if (offset == 0) {
                    bset.add(buffer);
                    buffer = new byte[CHUNK];
                } else {
                    offset = CHUNK - offset;
                }
            } else {
                bset.add( buffer );
                break;
            }
        }
        byte rv[] = new byte[tlen];
        for( int i=0, ptr=0; i<bset.size(); ++i ) {
            buffer = bset.get( i );
            for( int j=0; j<CHUNK && ptr < tlen; ) {
                rv[ptr++] = buffer[j++];
            }
        }
        return new String( rv, "UTF-8");
    }

    @Override
    protected String doInBackground( String... urls ) {
        try {
            return downloadUrl(urls[0]);
        } catch (Exception e) {
            return "{\"fail\":\""+e.toString()+"\"}";
        }
    }


}
