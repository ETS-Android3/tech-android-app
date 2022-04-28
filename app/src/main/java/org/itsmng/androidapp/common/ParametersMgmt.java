package org.itsmng.androidapp.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Class used to manage Android parameters
 */
public class ParametersMgmt {

    // App context to get params
    private Context appContext = null;

    /**
     * Constructor
     *
     * @param currContext : Current app context
     */
    public ParametersMgmt(Context currContext){
        this.appContext = currContext;
    }

    /**
     * Get string param
     *
     * @param paramKey : Android app parameter key
     *
     * @return String : Parameter value
     */
    public String getStringParam(String paramKey){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        return sharedPreferences.getString(paramKey, null);
    }

    /**
     * Get bool param
     *
     * @param paramKey : Android app parameter key
     *
     * @return bool : Parameter value (false as default value)
     */
    public boolean getBoolParam(String paramKey){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        return sharedPreferences.getBoolean(paramKey, false);
    }

    /**
     * Get Int param
     *
     * @param paramKey : Android app parameter key
     *
     * @return int : Parameter value (false as default value)
     */
    public int getIntParam(String paramKey){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        return sharedPreferences.getInt(paramKey, 0);
    }

    /**
     *  Set string param in shared prefs
     *
     * @return Boolean : True if succeed else false
     */
    public boolean setStringParam(String paramKey, String paramValue){
        try{
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(appContext);
            SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
            sharedPrefsEditor.putString(paramKey, paramValue);
            sharedPrefsEditor.apply();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     *  Set boolean param in shared prefs
     *
     * @return Boolean : True if succeed else false
     */
    public boolean setIntParam(String paramKey, int paramValue){
        try{
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(appContext);
            SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
            sharedPrefsEditor.putInt(paramKey, paramValue);
            sharedPrefsEditor.apply();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
