package org.itsmng.androidapp.ui.ticket;

import android.app.ProgressDialog;
import android.icu.text.Edits;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.FractionRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.itsmng.androidapp.HomeActivity;
import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiAuthentication;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.items.TicketUser;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import java.util.zip.Inflater;

public class TicketFragment extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private TicketViewModel ticketViewModel;

    private ApiMgmt apiMgmt;
    private String sessionIdStr;

    private TableLayout ticketTable;
    private ProgressDialog tableProgessDiag;

    private View root;

    private boolean areTicketProcessed = false;
    private JSONArray ticketsArray;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ticketViewModel =
                ViewModelProviders.of(this).get(TicketViewModel.class);

        ParametersMgmt paramMgmt = new ParametersMgmt(this.getContext());
        apiMgmt = new ApiMgmt(paramMgmt);
        sessionIdStr = Integer.toString(paramMgmt.getIntParam(ApiAuthentication.SESSION_ITSMNG_ID));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_MY_TICKETS, sessionIdStr).execute(apiMgmt);

        root = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketTable = root.findViewById(R.id.mytickets_table_layout);

        tableProgessDiag = ProgressDialog.show(container.getContext(), "",
                getResources().getString(R.string.ticket_loading_dialog), true);

        return root;
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){

        if(apiEndpoint.equals(ApiEndpoint.API_GET_MY_TICKETS) && !areTicketProcessed){
            areTicketProcessed = true;
            ticketsArray = (JSONArray) restResponse;
            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_TICKET_ASSIGNED, String.valueOf(apiMgmt.apiUserID)).execute(apiMgmt);
        }else{
            try{
                Ticket ticketProcessing = new Ticket(root, getContext());

                for (int i = 0; i < ticketsArray.length(); i++) {
                    JSONObject ticketData = (JSONObject) ticketsArray.get(i);

                    TicketUser tUser = new TicketUser(root, getContext());
                    JSONObject tUserInfos = tUser.getTicketUsers((JSONArray) restResponse, ticketData.getString("id"));

                    if(ticketProcessing.isCompliant(ticketData) && tUserInfos != null){

                        TableRow rowData = new TableRow(getContext());
                        rowData.setLayoutParams(root.findViewById(R.id.myticket_table_header).getLayoutParams());

                        ticketTable.addView(
                                ticketProcessing.manageTicketData(rowData, ticketData),
                                ticketTable.getLayoutParams()
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

                }
            }catch (Exception e){
                e.printStackTrace();
            }

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
