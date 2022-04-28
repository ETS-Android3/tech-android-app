package org.itsmng.androidapp.ui.computerList;

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
import org.itsmng.androidapp.items.TicketTask;
import org.itsmng.androidapp.ui.computerDetails.ComputerDetailsFragment;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;

import org.json.JSONArray;
import org.json.JSONObject;

public class ComputerListFragment extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private ComputerListViewModel computerListViewModel;

    private TableLayout ticketTable;
    private ProgressDialog tableProgessDiag;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        computerListViewModel =
                ViewModelProviders.of(this).get(ComputerListViewModel.class);

        ApiMgmt apiMgmt = new ApiMgmt(new ParametersMgmt(this.getContext()));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ALL_COMPUTERS).execute(apiMgmt);

        root = inflater.inflate(R.layout.fragment_computerlist, container, false);

        tableProgessDiag = ProgressDialog.show(container.getContext(), "",
                getResources().getString(R.string.computer_loading_dialog), true);

        return root;
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){
        JSONArray objectArray = (JSONArray) restResponse;

        try{
            Computer computerObject = new Computer(root, getContext());
            TableLayout baseLayout = root.findViewById(R.id.computers_table_layout);

            for (int i = 0; i < objectArray.length(); i++) {
                JSONObject ticketData = (JSONObject) objectArray.get(i);
                TableRow currRow = computerObject.manageComputerList(ticketData);
                baseLayout.addView(currRow);

                currRow.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ComputerDetailsFragment cDetails = new ComputerDetailsFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString( ComputerDetailsFragment.ID_KEY , v.getTag().toString());
                        Redirect.redirectToAnotherFragmentWithArgs(cDetails, getFragmentManager(), R.id.nav_host_fragment, arguments);
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
