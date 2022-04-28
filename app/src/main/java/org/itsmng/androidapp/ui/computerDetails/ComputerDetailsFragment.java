package org.itsmng.androidapp.ui.computerDetails;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.Computer;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ComputerDetailsFragment extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private ComputerDetailsViewModel computerListViewModel;

    private String computerId = "";

    public static String ID_KEY = "computers_id";

    private ApiMgmt apiMgmt;

    private ProgressDialog tableProgessDiag;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        computerListViewModel =
                ViewModelProviders.of(this).get(ComputerDetailsViewModel.class);

        try{
            Bundle arguments = getArguments();
            assert arguments != null;
            computerId = arguments.getString(ID_KEY);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        root = inflater.inflate(R.layout.computer_details_fragment, container, false);

        TabLayout tabLayout = root.findViewById(R.id.computerdetails_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TableLayout tableLay = root.findViewById(R.id.computerdetails_table_layout);
                    tableLay.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    TableLayout tableLay = root.findViewById(R.id.computertickets_table_layout);
                    tableLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TableLayout tableLay = root.findViewById(R.id.computerdetails_table_layout);
                    tableLay.setVisibility(View.INVISIBLE);
                } else if (tab.getPosition() == 1) {
                    TableLayout tableLay = root.findViewById(R.id.computertickets_table_layout);
                    tableLay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        apiMgmt = new ApiMgmt(new ParametersMgmt(this.getContext()));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ONE_COMPUTER, computerId).execute(apiMgmt);
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ITEM_TICKETS, computerId).execute(apiMgmt);

        tableProgessDiag = ProgressDialog.show(container.getContext(), "",
                getResources().getString(R.string.computer_loading_dialog), true);

        return root;
    }

    private void processComputerDetails(JSONObject computerData) throws JSONException {
        Computer computerManager = new Computer(root, getContext());

        computerManager.updateTextViewObject(R.id.computerdetails_name_value,computerData.getString("name"));
        computerManager.updateTextViewObject(R.id.computerdetails_user_value,computerData.getString("users_id"));
        computerManager.updateTextViewObject(R.id.computerdetails_manager_value,computerData.getString("users_id_tech"));
        computerManager.updateTextViewObject(R.id.computerdetails_location_value,computerData.getString("locations_id"));
        computerManager.updateTextViewObject(R.id.computerdetails_serial_value,computerData.getString("serial"));
        computerManager.updateEditViewObject(R.id.computerdetails_inventory_value,computerData.getString("otherserial"));
        computerManager.updateEditViewObject(R.id.computerdetails_source_value, computerData.getString("autoupdatesystems_id"));
    }

    private void processLinkedItems(JSONArray linkedTickets) throws JSONException{

        for (int i = 0; i < linkedTickets.length(); i++) {
            JSONObject linkedTicket = (JSONObject) linkedTickets.get(i);
            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKETS_DETAILS, linkedTicket.getString("tickets_id")).execute(apiMgmt);
        }

    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){

        try{

            if(apiEndpoint.equals(ApiEndpoint.API_GET_ONE_COMPUTER)){
                processComputerDetails((JSONObject) restResponse);
            }

            if(apiEndpoint.equals(ApiEndpoint.API_GET_ITEM_TICKETS)){
                processLinkedItems((JSONArray) restResponse);
            }

            if(apiEndpoint.equals(ApiEndpoint.API_GET_TICKETS_DETAILS)){
                Ticket ticketManager = new Ticket(root, getContext());
                TableRow rowData = new TableRow(getContext());
                TableLayout computerTickets = root.findViewById(R.id.computertickets_table_layout);

                computerTickets.addView(
                        ticketManager.manageTicketData(rowData, (JSONObject) restResponse)
                );

                rowData.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        TicketDetails tDetails = new TicketDetails();
                        Bundle arguments = new Bundle();
                        arguments.putString( TicketDetails.ID_KEY , v.getTag().toString());
                        Redirect.redirectToAnotherFragmentWithArgs(tDetails, getFragmentManager(), R.id.nav_host_fragment, arguments);
                    }
                });

            }

            tableProgessDiag.dismiss();
        }catch (Exception e){
            e.printStackTrace();
            tableProgessDiag.dismiss();
        }
    }

    @Override
    public void managePatchAsyncResponse(Object restResponse, String apiEndpoint) {

    }

    @Override
    public void managePostAsyncReponse(Object restResponse, String apiEndpoint) {

    }
}
