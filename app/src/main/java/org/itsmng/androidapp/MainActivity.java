package org.itsmng.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.itsmng.androidapp.api.mgmt.ApiAuthentication;
import org.itsmng.androidapp.async.LoginAsyncTask;
import org.itsmng.androidapp.common.Messages;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements LoginAsyncTask.LoginAsyncResponse {

    private ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginDialog = ProgressDialog.show(this, "",
                getResources().getString(R.string.mainactivity_wait_login), true);

        // Param manager
        ParametersMgmt paramMgmt = new ParametersMgmt(this.getApplicationContext());

        // Get app params and trigger API init
        ApiAuthentication itsmngAuth = new ApiAuthentication(paramMgmt);

        // Trigger login to API
        new LoginAsyncTask(this, this).execute(itsmngAuth);
    }

    @Override
    public void manageLoginResponse(boolean authStatus) {
        loginDialog.dismiss();
        if(!authStatus){
            Messages.createCustomDialog(
                    this,
                    this.getResources().getString(R.string.login_failed_title),
                    this.getResources().getString(R.string.login_failed_description)
            ).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
