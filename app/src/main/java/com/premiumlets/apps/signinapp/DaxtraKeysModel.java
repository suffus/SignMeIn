package com.premiumlets.apps.signinapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Created by galois on 2/22/18.
 */

public class DaxtraKeysModel {

    private MainActivity mActivity;
    private SharedPreferences mPreferences;
    private KeyPair mDeviceKeyPair;
    private boolean mDeviceSetup = false;
    private boolean mError = false;
    private Exception mCreateException = null;
    private Exception mServiceException = null;
    private String mDeviceName;
    private String mUserName;
    private static String apiServer="http://xtr1.daxtra.com/token/";



    static private DaxtraKeysModel theModel = new DaxtraKeysModel();
    static public DaxtraKeysModel getModel() {
        return theModel;
    }


    public DaxtraKeysModel( ) {

    }

    public void _error( Exception e ) {
        mActivity.onEvent( 500, e.toString());
    }

    public void setActivity( MainActivity a ) {
        mActivity = a;
        mPreferences = a.getSharedPreferences( "prefs", 0 );
        try {
            initState();
            } catch( Exception e ) {
            _error( e );
        }
    }

    public boolean isRegistered() {
        return mDeviceSetup;
    }

    public void registerDevice( String regCode ) {
        String uri;
        try {
            mDeviceKeyPair = generateKeyPair();
            saveKeyPair(mDeviceKeyPair);
            String pub64 = Base64.encodeToString(mDeviceKeyPair.getPublic().getEncoded(), Base64.DEFAULT);
            uri = apiServer + "regdev?domain=daxtra&public_key=" + URLEncoder.encode(pub64, "UTF-8") + "&activation_code=" + regCode;
        } catch( Exception e ) {
            _error( e );
            return;
        }

        Downloader req = new Downloader() {
            @Override
            public void onPostExecute( String result ) {
                try {
                    //RegisterActivity.this.getIntent().getStringExtra("");
                    JSONObject j0 = new JSONObject(result);
                    String regResult = j0.optString("result");
                    if (regResult.equals("success")) {
                        String deviceName = j0.optString("name");
                        mDeviceName = deviceName;
                        mDeviceSetup = true;

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putString( "pDeviceName", deviceName);
                        editor.putBoolean( "pIsSetup", true );
                        editor.commit();

                        mActivity.onEvent( 0, "" );
                        return;
                    } else {
                        mError = true;
                        mServiceException = new Exception( "Registration Result = "+regResult+"::"+result );
                        mActivity.onEvent( 500, mServiceException.toString() );
                        //Toast.makeText(mActivity, "Registration Failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    mError = true;
                    mServiceException = e;
                    _error( e );
                    //Toast.makeText(mActivity, "Registration Failed", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        };
        req.execute( uri );
    }

    public void saveKeyPair(KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());

        String pub64 = Base64.encodeToString(x509EncodedKeySpec.getEncoded(), Base64.DEFAULT);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("pPublicKey", pub64);
        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        String priv64 = Base64.encodeToString(pkcs8EncodedKeySpec.getEncoded(), Base64.DEFAULT);
        editor.putString("pPrivateKey",priv64);
        editor.commit();
    }

    public KeyPair loadKeyPair(String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        String pub64 = mPreferences.getString( "pPublicKey", "");
        byte[] encodedPublicKey = Base64.decode(pub64.getBytes(), Base64.DEFAULT);
        String priv64 = mPreferences.getString("pPrivateKey", "");
        byte[] encodedPrivateKey = Base64.decode( priv64.getBytes(), Base64.DEFAULT );

        // Generate KeyPair.

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        this.mDeviceKeyPair = new KeyPair( publicKey, privateKey );
        return this.mDeviceKeyPair;
    }

    private void verifyCodeResult( String res ) {

    }

    private String decryptStringRSA( String inString ) {
        return "";
    }

    private void _debug( String s ) {
        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT ).show();
    }

    public void getCode( ) {
        // first verify the device
        performVerifiedCall( "http://xtr1.daxtra.com/token/api?a=code&domain=daxtra",
                new Downloader() {
                    @Override
                    public void onPostExecute( String result ) {
                            //_debug("S2: "+result);
                        String showResult = "NX:ERROR:SHOULDNOTSEETHISEVER";
                        String resstr = "unknown";
                        try {
                                showResult = "ERROR";
                                JSONObject j0 = new JSONObject(result);
                                String code = j0.optString("encrypted_code").toString();
                                resstr = j0.optString("result").toString();
                                String message = j0.optString("message").toString();
                                if( code.length()>0) {
                                    Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");

                                    cipher.init(Cipher.DECRYPT_MODE, mDeviceKeyPair.getPrivate());
                                    byte[] code_bytes = Base64.decode(code, Base64.DEFAULT);
                                    byte[] decrypted_bytes = cipher.doFinal( code_bytes );
                                    String dCode = new String(decrypted_bytes, "UTF-8");
                                    /// rule is that everything after ":" is the code

                                    code = dCode.substring(dCode.indexOf(":")+1, dCode.length());
                                    if( code.length() < 4 ) {
                                        throw new RuntimeException( "No code found in server message." );
                                    }
                                } else {
                                    code = j0.optString("code").toString();
                                }
                                if (resstr.equals("success")) {
                                    showResult = "Code: " + code;
                                } else {
                                    showResult = "Error: " + message;
                                }
                            } catch( Exception e ) {
                                //_debug( "X2" );
                                _error( e );
                                return;
                            }
                            //_debug("X3");
                            mActivity.onEvent( 1, showResult );
                            return;
                        }
                    });
    }

    public void performVerifiedCall( final String url, final Downloader call ) {
        new Downloader() {
            @Override
            public void onPostExecute( String result ) {
                try {
                    String call_url = url;
                    JSONObject j0 = new JSONObject(result);
                    String v_code = j0.optString("verification_code");
                    Signature signature = Signature.getInstance("SHA1withRSA", "BC");
                    signature.initSign(mDeviceKeyPair.getPrivate(), new SecureRandom());
                    byte[] message = v_code.getBytes();
                    signature.update(message);
                    byte[] sigBytes = signature.sign();
                    String sig_64 = Base64.encodeToString(sigBytes, Base64.DEFAULT);
                    //_debug( "X1 ");
                    if( call_url.contains( "?") ) {
                        call_url += "&";
                    } else {
                        call_url += "?";
                    }
                    call_url += "key="+ URLEncoder.encode( getDeviceName(), "UTF-8") + "&verification_token=" + URLEncoder.encode( sig_64, "UTF-8" );
                    call.execute( call_url );
                } catch( Exception e ) {
                    _error( e );
                    return;
                }
            }
        }.execute( "http://xtr1.daxtra.com/token/verify_device?domain=daxtra&key=" + getDeviceName() );
    }

    public void getUserData( ) {
        callJSONAPI( MainActivity.USER_DETAILS,  "user_details" );
    }

    public void getUserDetails( String u ) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("user", u );
            callJSONAPI( MainActivity.GET_USER_DETAILS, "get_user_details", params );
        } catch( Exception e ) {
            _error( e );
        }
    }


    public void setUserStatus( String status, String location ) {
        try {
            HashMap<String, String> params = new HashMap<String,String>();
            params.put("status", status);
            params.put("location",location);
            callJSONAPI(MainActivity.SET_USER_STATUS, "set_status", params);
        } catch( Exception e ) {
            _error(e);
        }
    }

    public void getUserList() {
        callJSONAPI( MainActivity.LIST_USERS, "list_users");
    }

    public void getMetaData() {
        callJSONAPI( MainActivity.SET_METADATA, "get_metadata");
    }

    public void callJSONAPI( final int eventResult, String method ) {
        callJSONAPI( eventResult, method, null);
    }


    public void callJSONAPI( final int eventResult, String method, Map<String, String> params ) {
        String apiCall = "http://xtr1.daxtra.com/token/api?domain=daxtra&a="+method;
        if( params != null ) {
            for( String param : params.keySet() ) {
                try {
                    apiCall += "&" + URLEncoder.encode( param, "UTF-8") + "=" + URLEncoder.encode( params.get( param ), "UTF-8");
                } catch( Exception e ) {
                    _error( e );
                }
            }
        }
        performVerifiedCall( apiCall, new Downloader() {
            @Override
            public void onPostExecute( String result ) {
                try {
                    mActivity.onEvent(eventResult, new JSONObject(result));
                } catch( Exception e ) {
                    _error( e );
                }
            }
        });
    }

    private void initState( ) throws Exception {
        SharedPreferences prefs = mActivity.getSharedPreferences("prefs", 0);
        mPreferences = prefs;
        mDeviceSetup = prefs.getBoolean("pIsSetup", false);
        mDeviceName = prefs.getString("pDeviceName", "");

        if( mDeviceSetup ) {
            try {
                loadKeyPair("RSA");
            } catch (Exception e) {
                throw e;
            }
        }
    }

    private String getDeviceName() {
        return mDeviceName;
    }


    private KeyPair generateKeyPair( ) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom rand = new SecureRandom();
            keyGen.initialize(2048, rand);
            mDeviceKeyPair = keyGen.generateKeyPair();
            saveKeyPair(mDeviceKeyPair);
            return mDeviceKeyPair;
        } catch( Exception e ) {
            _error( e );
        }
        return null;
    }

}
