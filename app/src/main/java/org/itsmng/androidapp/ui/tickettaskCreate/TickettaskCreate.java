package org.itsmng.androidapp.ui.tickettaskCreate;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.Messages;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.TicketFollowup;
import org.itsmng.androidapp.items.TicketTask;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;

import org.json.JSONException;

import java.util.HashMap;

public class TickettaskCreate extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private TickettaskCreateViewModel tickettaskCreateViewModel;

    private ProgressDialog waitingDialog;
    private View root;
    private ApiMgmt apiMgmt;

    private String ticketId = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tickettaskCreateViewModel =
                ViewModelProviders.of(this).get(TickettaskCreateViewModel.class);

        try{
            Bundle arguments = getArguments();
            assert arguments != null;
            ticketId = arguments.getString(TicketDetails.ID_KEY);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        apiMgmt = new ApiMgmt(new ParametersMgmt(getContext()));
        root = inflater.inflate(R.layout.tickettask_create_fragment, container, false);

        Spinner spinner = root.findViewById(R.id.tickettask_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tickettask_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner tickettaskPrivate = root.findViewById(R.id.tickettask_create_private);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.yes_no_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tickettaskPrivate.setAdapter(adapter);

        final Button addTask = root.findViewById(R.id.tickettask_create_submit);
        addTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText content = root.findViewById(R.id.tickettask_create_content);
                Spinner time = root.findViewById(R.id.tickettask_time);
                Spinner priv = root.findViewById(R.id.tickettask_create_private);
                addTicketTask(content.getText().toString(), time.getSelectedItem().toString(), priv.getSelectedItemPosition());
            }
        });

        return root;
    }

    private void addTicketTask(String content, String time, int priv){
        try{
            waitingDialog = ProgressDialog.show(getContext(), "",
                    getResources().getString(R.string.app_data_update_create_loading), true);

            TicketTask tTask = new TicketTask(root,getContext());

            HashMap<String, String> jsonContent = new HashMap<>();
            jsonContent.put("content", content);
            jsonContent.put("actiontime", tTask.hoursToSeconds(time));
            jsonContent.put("state", "3");
            jsonContent.put("tickets_id", ticketId);
            jsonContent.put("is_private", String.valueOf(priv));

            String jsonBody = tTask.generateJSONStringToInsert(jsonContent);

            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_ROOT_TICKET_TASKS, null, RestAsyncTask.POST_REQUEST, jsonBody).execute(apiMgmt);
        }catch (JSONException e){
            waitingDialog.dismiss();
            AlertDialog errorMessage = Messages.createErrorDialog(getContext());
            errorMessage.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Bundle arguments = new Bundle();
                    arguments.putString( TicketDetails.ID_KEY , ticketId);
                    Redirect.redirectToAnotherFragmentWithArgs(new TicketDetails(), getFragmentManager(), R.id.nav_host_fragment, arguments);
                }
            });
        }
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){

    }

    @Override
    public void managePatchAsyncResponse(Object restResponse, String apiEndpoint) {

    }

    @Override
    public void managePostAsyncReponse(Object restResponse, String apiEndpoint) {
        waitingDialog.dismiss();
        AlertDialog successMessage = Messages.createSuccessDialog(getContext());
        successMessage.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Bundle arguments = new Bundle();
                arguments.putString( TicketDetails.ID_KEY , ticketId);
                Redirect.redirectToAnotherFragmentWithArgs(new TicketDetails(), getFragmentManager(), R.id.nav_host_fragment, arguments);
            }
        });
    }
}
