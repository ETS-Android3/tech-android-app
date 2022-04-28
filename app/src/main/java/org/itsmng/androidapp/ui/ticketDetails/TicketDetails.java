package org.itsmng.androidapp.ui.ticketDetails;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.Messages;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.ITILCategory;
import org.itsmng.androidapp.items.Location;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.items.TicketFollowup;
import org.itsmng.androidapp.items.TicketTask;
import org.itsmng.androidapp.ui.followupCreate.FollowupCreate;
import org.itsmng.androidapp.ui.tickettaskCreate.TickettaskCreate;
import com.google.android.material.tabs.TabLayout;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TicketDetails extends Fragment implements RestAsyncTask.RestAsyncResponse {

    public static String ID_KEY = "ticket_id";

    private String ticketId = "";

    private View root;

    private Location locationData;
    private ITILCategory categoryData;

    private ArrayList<String> locationSpinnerList;
    private ArrayList<String> categorySpinnerList;

    private boolean categoryLoaded = false;
    private boolean locationLoaded = false;
    private boolean ticketLoaded = false;

    private Ticket ticketManager;

    private ProgressDialog loadingDialog;
    private ProgressDialog waitingDialog;

    private ApiMgmt apiMgmt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.ticket_details_fragment, container, false);
        ticketManager = new Ticket(root, getContext());

        try{
            Bundle arguments = getArguments();
            assert arguments != null;
            ticketId = arguments.getString(ID_KEY);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        TabLayout tabLayout = root.findViewById(R.id.ticketdetails_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TableLayout tableLay = root.findViewById(R.id.ticketdetails_table_layout);
                    tableLay.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    TableLayout tableLay = root.findViewById(R.id.ticketfollowup_table_layout);
                    tableLay.setVisibility(View.VISIBLE);
                } else {
                    TableLayout tableLay = root.findViewById(R.id.tickettask_table_layout);
                    tableLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TableLayout tableLay = root.findViewById(R.id.ticketdetails_table_layout);
                    tableLay.setVisibility(View.INVISIBLE);
                } else if (tab.getPosition() == 1) {
                    TableLayout tableLay = root.findViewById(R.id.ticketfollowup_table_layout);
                    tableLay.setVisibility(View.INVISIBLE);
                } else {
                    TableLayout tableLay = root.findViewById(R.id.tickettask_table_layout);
                    tableLay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button submitButton = root.findViewById(R.id.ticketdetails_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                int locationId = -1;
                int categoryId = -1;

                try{
                    Spinner category = root.findViewById(R.id.ticketdetails_category_value);
                    Spinner location = root.findViewById(R.id.ticketdetails_location_value);
                    locationId = locationData.getTextValueID(location.getSelectedItem().toString());
                    categoryId = categoryData.getTextValueID(category.getSelectedItem().toString());


                    builder.setMessage(getResources().getString(R.string.sure_dialog_ticket));

                    builder.setPositiveButton(getResources().getString(R.string.spinner_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            waitingDialog = ProgressDialog.show(getContext(), "",
                                    getResources().getString(R.string.app_data_update_create_loading), true);
                            updateTicketAction(root);
                            //HERE
                        }
                    });

                    builder.setNegativeButton(getResources().getString(R.string.spinner_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }catch(Exception e) {
                    if (categoryId == -1)
                        builder.setMessage(getResources().getString(R.string.error_field_category));
                    else
                        builder.setMessage(getResources().getString(R.string.error_field_location));
                    builder.setNegativeButton(getResources().getString(R.string.dialog_validate), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                /*/
                /*/

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        apiMgmt = new ApiMgmt(new ParametersMgmt(this.getContext()));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_LOCATIONS).execute(apiMgmt);
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ITILCATEGORIES).execute(apiMgmt);
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKET_FOLLOWUPS, ticketId).execute(apiMgmt);
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKET_TASKS, ticketId).execute(apiMgmt);

        loadingDialog = ProgressDialog.show(getContext(), "",
                getResources().getString(R.string.ticket_loading_dialog), true);

        return root;
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint) {
        try{

            JSONArray objectArray = null;

            if(apiEndpoint.equals(ApiEndpoint.API_GET_TICKETS_DETAILS)){
                processTicketDetails((JSONObject) restResponse);
            }else{
                objectArray = (JSONArray) restResponse;
            }

            if(apiEndpoint.equals(ApiEndpoint.API_GET_TICKET_USERS)){
                JSONObject userAssignedJson = ((JSONArray) restResponse).getJSONObject(0);
                ticketManager.updateTextViewObject(R.id.ticketdetails_assignee_value,userAssignedJson.getString("users_id"));
            }

            if(apiEndpoint.equals(ApiEndpoint.API_GET_LOCATIONS)){
                locationData = new Location();
                locationSpinnerList = locationData.generateSpinnerArray(objectArray);
                locationLoaded = true;
            }else if(apiEndpoint.equals(ApiEndpoint.API_GET_ITILCATEGORIES)){
                categoryData = new ITILCategory();
                categorySpinnerList = categoryData.generateSpinnerArray(objectArray);
                categoryLoaded = true;
            }

            if(apiEndpoint.equals(ApiEndpoint.API_GET_TICKET_FOLLOWUPS)){
                processFollowupDetails(objectArray);
            }else if(apiEndpoint.equals(ApiEndpoint.API_GET_TICKET_TASKS)){
                processTicketTaskDetails(objectArray);
            }

            if(categoryLoaded && locationLoaded && !ticketLoaded){
                ticketLoaded = true;
                new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKETS_DETAILS, ticketId).execute(apiMgmt);
                loadingDialog.dismiss();
            }

        }catch (Exception e){
            e.printStackTrace();
            loadingDialog.dismiss();
        }
    }

    @Override
    public void managePatchAsyncResponse(Object restResponse, String apiEndpoint) {
        waitingDialog.dismiss();
        Messages.createSuccessDialog(getContext());
    }

    @Override
    public void managePostAsyncReponse(Object restResponse, String apiEndpoint) {
        // Empty
    }

    private void processTicketDetails(JSONObject ticketData) throws JSONException {
        // Form data
        ticketManager.updateTextViewObject(R.id.ticketdetails_date_value,ticketData.getString("date"));
        ticketManager.getPriorityInformations(R.id.ticketdetails_priority_value,ticketData.getString("priority"));
        ticketManager.updateTextViewObject(R.id.ticketdetails_requester_value,ticketData.getString("users_id_recipient"));
        ticketManager.updateEditViewObject(R.id.ticketdetails_title_value,ticketData.getString("name"));
        ticketManager.updateEditViewObject(R.id.ticketdetails_desc_value, ticketManager.manageHtmlContent(ticketData.getString("content")));

        // Spinners
        Spinner catSpinner = root.findViewById(R.id.ticketdetails_category_value);
        ticketManager.updateSpinner(catSpinner, categorySpinnerList, ticketData.getString("itilcategories_id"));

        Spinner locSpinner = root.findViewById(R.id.ticketdetails_location_value);
        ticketManager.updateSpinner(locSpinner, locationSpinnerList, ticketData.getString("locations_id"));

        Spinner ticketStatusSpinner = root.findViewById(R.id.ticketdetails_status_value);
        ticketManager.updateSpinner(
                ticketStatusSpinner,
                ticketManager.getTicketStatusArray(),
                ticketManager.getStatusNameFromID(ticketData.getString("status"))
        );

        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKET_USERS, ticketData.getString("id")).execute(apiMgmt);
    }

    /**
     * Action when update ticket button pressed
     *
     * @param view : Current fragment view
     */
    private void updateTicketAction(View view){
        try{
            Ticket ticketManager = new Ticket(root, getContext());

            EditText title = view.findViewById(R.id.ticketdetails_title_value);
            Spinner category = view.findViewById(R.id.ticketdetails_category_value);
            Spinner location = view.findViewById(R.id.ticketdetails_location_value);
            EditText content = view.findViewById(R.id.ticketdetails_desc_value);
            Spinner status = view.findViewById(R.id.ticketdetails_status_value);
            int categoryId = categoryData.getTextValueID(category.getSelectedItem().toString());
            int locationId = locationData.getTextValueID(location.getSelectedItem().toString());
            int statusId = ticketManager.getTicketStatusId(status.getSelectedItem().toString());
            HashMap<String, String> jsonContent = new HashMap<>();
            jsonContent.put("name", title.getText().toString());
            jsonContent.put("itilcategories_id", String.valueOf(categoryId));
            jsonContent.put("locations_id", String.valueOf(locationId));
            jsonContent.put("content", content.getText().toString());
            jsonContent.put("status", String.valueOf(statusId));

            String jsonBody = ticketManager.generateJSONStringToInsert(jsonContent);


            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_UPDATE_TICKET, ticketId, RestAsyncTask.PATCH_REQUEST, jsonBody).execute(apiMgmt);

        }catch (JSONException e){
            waitingDialog.dismiss();
            Messages.createErrorDialog(getContext());
            e.printStackTrace();
        }

    }

    private void processFollowupDetails(JSONArray followupsData) throws  JSONException{
        TableLayout baseLayout = root.findViewById(R.id.ticketfollowup_table_layout);
        TicketFollowup tFollow = new TicketFollowup(root, getContext());
        for (int i = 0; i < followupsData.length(); i++) {
            JSONObject followupJsonObj = (JSONObject) followupsData.get(i);
            baseLayout.addView(tFollow.manageTicketFollowup(followupJsonObj));
        }
        baseLayout.addView(tFollow.inflateCreateFollowupButton());
        // Add create action
        Button submitButton = baseLayout.findViewById(R.id.ticketfollowup_create_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FollowupCreate followupCreate = new FollowupCreate();
                Bundle arguments = new Bundle();
                arguments.putString( TicketDetails.ID_KEY , ticketId);
                Redirect.redirectToAnotherFragmentWithArgs(followupCreate, getFragmentManager(), R.id.nav_host_fragment, arguments);
            }
        });

    }

    private void processTicketTaskDetails(JSONArray taskData) throws  JSONException{
        TableLayout baseLayout = root.findViewById(R.id.tickettask_table_layout);
        TicketTask tTask = new TicketTask(root, getContext());
        for (int i = 0; i < taskData.length(); i++) {
            JSONObject taskJson = (JSONObject) taskData.get(i);
            baseLayout.addView(tTask.manageTicketTask(taskJson));
        }
        baseLayout.addView(tTask.inflateCreateTaskButton());
        // Add create action
        Button submitButton = baseLayout.findViewById(R.id.tickettask_create_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TickettaskCreate ticketaskCreate = new TickettaskCreate();
                Bundle arguments = new Bundle();
                arguments.putString( TicketDetails.ID_KEY , ticketId);
                Redirect.redirectToAnotherFragmentWithArgs(ticketaskCreate, getFragmentManager(), R.id.nav_host_fragment, arguments);
            }
        });
    }
}
