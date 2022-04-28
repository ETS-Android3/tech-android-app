package org.itsmng.androidapp.ui.followupCreate;

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
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.Messages;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.items.TicketFollowup;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FollowupCreate extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private FollowupCreateViewModel followupCreateViewModel;

    private ProgressDialog waitingDialog;
    private View root;
    private ApiMgmt apiMgmt;

    private String ticketId = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        followupCreateViewModel =
                ViewModelProviders.of(this).get(FollowupCreateViewModel.class);

        try{
            Bundle arguments = getArguments();
            assert arguments != null;
            ticketId = arguments.getString(TicketDetails.ID_KEY);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        apiMgmt = new ApiMgmt(new ParametersMgmt(getContext()));
        root = inflater.inflate(R.layout.followup_create_fragment, container, false);

        Spinner followupPrivate = root.findViewById(R.id.followup_create_private);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.yes_no_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        followupPrivate.setAdapter(adapter);

        final Button addFollowup = root.findViewById(R.id.followup_create_submit);
        addFollowup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText content = root.findViewById(R.id.followup_create_content);
                Spinner priv = root.findViewById(R.id.followup_create_private);
                addFollowup(content.getText().toString(), priv.getSelectedItemPosition());
            }
        });

        return root;
    }

    private void addFollowup(String content, int priv){
        try{
            waitingDialog = ProgressDialog.show(getContext(), "",
                    getResources().getString(R.string.app_data_update_create_loading), true);

            TicketFollowup tFollowup = new TicketFollowup(root,getContext());

            HashMap<String, String> jsonContent = new HashMap<>();
            jsonContent.put("content", content);
            jsonContent.put("items_id", ticketId);
            jsonContent.put("itemtype", "Ticket");
            jsonContent.put("is_private", String.valueOf(priv));

            String jsonBody = tFollowup.generateJSONStringToInsert(jsonContent);

            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_ROOT_TICKET_FOLLOWUPS, null, RestAsyncTask.POST_REQUEST, jsonBody).execute(apiMgmt);
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
