package org.itsmng.androidapp.ui.allticket;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.common.Redirect;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.ui.ticket.TicketViewModel;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;

import org.json.JSONArray;
import org.json.JSONObject;

public class AllTicketFragment extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private AllTicketViewModel allTicketViewModel;

    private TableLayout ticketTable;
    private ProgressDialog tableProgessDiag;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allTicketViewModel =
                ViewModelProviders.of(this).get(AllTicketViewModel.class);

        ApiMgmt apiMgmt = new ApiMgmt(new ParametersMgmt(this.getContext()));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ALL_TICKETS).execute(apiMgmt);

        root = inflater.inflate(R.layout.fragment_allticket, container, false);

        ticketTable = root.findViewById(R.id.alltickets_table_layout);

        tableProgessDiag = ProgressDialog.show(container.getContext(), "",
                getResources().getString(R.string.ticket_loading_dialog), true);

        return root;
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){

        try{
            JSONArray ticketsArray = (JSONArray) restResponse;
            Ticket ticketProcessing = new Ticket(root, getContext());

            for (int i = 0; i < ticketsArray.length(); i++) {
                JSONObject ticketData = (JSONObject) ticketsArray.get(i);

                if(ticketProcessing.isCompliant(ticketData)){
                    TableRow rowData = new TableRow(getContext());
                    rowData.setLayoutParams(root.findViewById(R.id.allticket_table_header).getLayoutParams());

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

    @Override
    public void managePatchAsyncResponse(Object restResponse, String apiEndpoint) {

    }

    @Override
    public void managePostAsyncReponse(Object restResponse, String apiEndpoint) {

    }
}
