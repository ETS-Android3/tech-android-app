package org.itsmng.androidapp.api.mgmt;

import android.util.JsonReader;

import org.itsmng.androidapp.common.ParametersMgmt;

import org.json.JSONObject;

public class ApiAuthentication extends ApiMgmt {

    // Param manager
    protected ParametersMgmt paramMgmt;

    // API Endpoints
    private final String INIT_SESSION = "initSession";
    private final String KILL_SESSION = "killSession";
    private final String FULL_SESSION = "getFullSession";

    // Session token return
    static public final String SESSION_TOKEN_KEY = "session_token";
    // Itsmng Connected user ID
    static public final String SESSION_ITSMNG_ID = "glpiID";

    // Auth status
    public boolean authStatus = false;

    /**
     *  Base constructor to extend ApiMgmt
     */
    public ApiAuthentication(ParametersMgmt paramMgmt){
        super(paramMgmt);
        this.paramMgmt = paramMgmt;
    }

    /**
     *  Login to ITSMNG API using app Param
     */
    public boolean loginToItsmngAPI(){
        try{
            JSONObject authResponse = (JSONObject) get(INIT_SESSION);
            setApiSessionToken(authResponse.getString(SESSION_TOKEN_KEY));
            return this.paramMgmt.setStringParam(SESSION_TOKEN_KEY, authResponse.getString(SESSION_TOKEN_KEY));
        }catch(Exception e){
            return false;
        }
    }

    /**
     *  Get current user ID
     */
    public boolean getItsmngID(){
        try{
            JSONObject authResponse = (JSONObject) get(FULL_SESSION);
            return this.paramMgmt.setIntParam(SESSION_ITSMNG_ID, authResponse.getJSONObject("session").getInt(SESSION_ITSMNG_ID));
        }catch(Exception e){
            return false;
        }
    }

    /**
     *  Logout from ITSMNG API using sessionIdentifier
     */
    public void logoutFromItsmngAPI(){
        get(KILL_SESSION);
    }

}
