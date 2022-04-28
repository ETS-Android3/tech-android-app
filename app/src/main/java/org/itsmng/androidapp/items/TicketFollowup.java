package org.itsmng.androidapp.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.common.Items;

import org.json.JSONException;
import org.json.JSONObject;

public class TicketFollowup extends Items {

    /**
     * @param root           : Current view
     * @param currentContext : Current context
     */
    public TicketFollowup(View root, Context currentContext) {
        super(root, currentContext);
    }

    /**
     * Manage Ticket followup data and format table and inflate row template
     *
     * @param followUpData Data returned by ITILFollowup API Endpoint
     *
     * @return Return followup row
     *
     * @throws JSONException When an error occur during parsing JSON
     */
    public LinearLayout manageTicketFollowup(JSONObject followUpData) throws JSONException {
        LinearLayout followUpRow = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ticket_followup_row_template, null);

        String fromText = mContext.getResources().getString(R.string.ticketfollowup_from);
        String atText = mContext.getResources().getString(R.string.ticketfollowup_at);

        TextView date = followUpRow.findViewById(R.id.ticketfollowup_date);
        date.setText(String.format("%s %s", atText, followUpData.getString("date")));

        TextView user = followUpRow.findViewById(R.id.ticketfollowup_user);
        user.setText(String.format("%s %s", fromText, followUpData.getString("users_id")));

        TextView description = followUpRow.findViewById(R.id.ticketfollowup_desc);
        description.setText(manageHtmlContent(followUpData.getString("content")));

        TextView isPrivate = followUpRow.findViewById(R.id.ticketfollowup_is_private);
        switch (followUpData.getString("is_private")){
            case "0":
                isPrivate.setBackgroundColor(mContext.getColor(R.color.colorGreen));
                break;
            case "1":
                isPrivate.setBackgroundColor(mContext.getColor(R.color.colorRed));
                break;
        }


        return followUpRow;
    }

    /**
     * Inflate create button to add followup
     *
     * @return Return inflated button
     */
    public Button inflateCreateFollowupButton(){
        return (Button) LayoutInflater.from(mContext).inflate(R.layout.ticket_followup_create_button_template, null);
    }

}
