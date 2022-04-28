package org.itsmng.androidapp.common;

import android.app.AlertDialog;
import android.content.Context;

import org.itsmng.androidapp.R;

public class Messages {

    /**
     * Create a default success dialog
     *
     * @param currContext : Current context
     *
     * @return AlertDialog builder with default success message
     */
    public static AlertDialog createSuccessDialog(Context currContext){
        return createCustomDialog(currContext, currContext.getResources().getString(R.string.success_title), currContext.getResources().getString(R.string.success_description));
    }

    /**
     * Create a default error dialog
     *
     * @param currContext : Current context
     *
     * @return AlertDialog builder with default error message
     */
    public static AlertDialog createErrorDialog(Context currContext){
        return createCustomDialog(currContext, currContext.getResources().getString(R.string.error_title), currContext.getResources().getString(R.string.error_description));
    }

    /**
     * Create a default error dialog
     *
     * @param currContext : Current context
     * @param title : Title of the dialog
     * @param message :  Message of the dialog
     *
     * @return AlertDialog builder with asked title and message
     */
    public static AlertDialog createCustomDialog(Context currContext, String title, String message){
        return new AlertDialog.Builder(currContext)
                .setTitle(title)
                .setMessage(message)
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.dialog_validate, null)
                .show();
    }

}
