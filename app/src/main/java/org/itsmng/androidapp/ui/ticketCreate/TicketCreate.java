package org.itsmng.androidapp.ui.ticketCreate;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TableRow;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;
import org.itsmng.androidapp.async.RestAsyncTask;
import org.itsmng.androidapp.common.Messages;
import org.itsmng.androidapp.common.ParametersMgmt;
import org.itsmng.androidapp.items.ITILCategory;
import org.itsmng.androidapp.items.Location;
import org.itsmng.androidapp.items.Ticket;
import org.itsmng.androidapp.ui.ticketDetails.TicketDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TicketCreate extends Fragment implements RestAsyncTask.RestAsyncResponse {

    private View root;
    private Location locationSpinner;
    private ITILCategory categorySpinner;
    private ProgressDialog dropdownsLoadingDialog;
    private ProgressDialog waitingDialog;
    private ApiMgmt apiMgmt;
    boolean is_complete = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.ticket_create_fragment, container, false);

        apiMgmt = new ApiMgmt(new ParametersMgmt(this.getContext()));
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_LOCATIONS).execute(apiMgmt);
        new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_GET_ITILCATEGORIES).execute(apiMgmt);

            Button submitButton = root.findViewById(R.id.createticket_submit_button);
            submitButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (createTicketAction(root)) {
                        waitingDialog = ProgressDialog.show(getContext(), "",
                                getResources().getString(R.string.app_data_update_create_loading), true);
                    }
                }
            });
            dropdownsLoadingDialog = ProgressDialog.show(getContext(), "",
                    getResources().getString(R.string.ticket_dropdown_loading), true);

        return root;
    }

    @Override
    public void manageGetAsyncResponse(Object restResponse, String apiEndpoint){
        JSONArray objectArray = (JSONArray) restResponse;

        try{
            ArrayList<String> spinnerValues = new ArrayList<>();
            Spinner formSpinner;
            if(apiEndpoint.equals(ApiEndpoint.API_GET_LOCATIONS)){
                locationSpinner = new Location();
                spinnerValues = locationSpinner.generateSpinnerArray(objectArray);
                formSpinner = root.findViewById(R.id.createticket_location_infos_field);
            }else{
                categorySpinner = new ITILCategory();
                spinnerValues = categorySpinner.generateSpinnerArray(objectArray);
                formSpinner = root.findViewById(R.id.createticket_category_infos_field);
            }
            String[] spinnerArray = new String[spinnerValues.size()];
            spinnerArray = spinnerValues.toArray(spinnerArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_item,
                    spinnerArray
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            formSpinner.setAdapter(adapter);

            dropdownsLoadingDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
            dropdownsLoadingDialog.dismiss();
        }

    }

    @Override
    public void managePatchAsyncResponse(Object restResponse, String apiEndpoint) {

    }

    @Override
    public void managePostAsyncReponse(Object restResponse, String apiEndpoint) {
        waitingDialog.dismiss();
        Messages.createSuccessDialog(getContext());

        EditText title = root.findViewById(R.id.createticket_title_infos_field);
        title.setText("");
        EditText content = root.findViewById(R.id.createticket_description_infos_field);
        content.setText("");
    }

    /**
     * Action when create ticket button pressed
     *
     * @param view : Current fragment view
     */
    private boolean createTicketAction(View view){

        int categoryId = -1;
        int locationId = -1;
        try{
            Ticket ticketManager = new Ticket(root, getContext());

            EditText title = view.findViewById(R.id.createticket_title_infos_field);
            Spinner category = view.findViewById(R.id.createticket_category_infos_field);
            Spinner location = view.findViewById(R.id.createticket_location_infos_field);
            EditText content = view.findViewById(R.id.createticket_description_infos_field);
            categoryId = categorySpinner.getTextValueID(category.getSelectedItem().toString());
            locationId = locationSpinner.getTextValueID(location.getSelectedItem().toString());
            HashMap<String, String> jsonContent = new HashMap<>();
            jsonContent.put("name", title.getText().toString());
            jsonContent.put("itilcategories_id", String.valueOf(categoryId));
            jsonContent.put("locations_id", String.valueOf(locationId));
            jsonContent.put("content", content.getText().toString());

            String jsonBody = ticketManager.generateJSONStringToInsert(jsonContent);

            new RestAsyncTask(this, this.getContext(), ApiEndpoint.API_ROOT_TICKET, null, RestAsyncTask.POST_REQUEST, jsonBody).execute(apiMgmt);
            return true;

        }catch (Exception e){
            //waitingDialog.dismiss();
            //Messages.createErrorDialog(getContext());
            //e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }

    }

}
