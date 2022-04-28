package org.itsmng.androidapp.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiAuthentication;

/**
 * Task to manage Login AsyncTask.
 */
public class LoginAsyncTask extends AsyncTask<ApiAuthentication, Void, Boolean> {

    private LoginAsyncResponse mDelegate;
    private Context mContext;

    /**
     * Constructor
     */
    public LoginAsyncTask(LoginAsyncResponse delegate, Context currentContext){
        this.mContext = currentContext;
        this.mDelegate = delegate;
    }

    protected Boolean doInBackground(ApiAuthentication... apiAuth) {
        try
        {
            ApiAuthentication taskObject = apiAuth[0];
            if(!taskObject.loginToItsmngAPI()
                    || !taskObject.getItsmngID()){
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        mDelegate.manageLoginResponse(result);
    }

    /**
     * Implement interface to manage ASync to view
     */
    public interface LoginAsyncResponse {
        /**
         * Method to add in order to manage Login action
         *
         * @param authStatus : True if auth succeed else false
         */
        void manageLoginResponse( boolean authStatus );
    }
}
