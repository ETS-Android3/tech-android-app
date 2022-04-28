package org.itsmng.androidapp.items;

import android.content.Context;
import android.view.View;

import org.itsmng.androidapp.common.Items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TicketUser extends Items {

    /**
     * @param root           : Current view
     * @param currentContext : Current context
     */
    public TicketUser(View root, Context currentContext) {
        super(root, currentContext);
    }

    /**
     * Retrieve ticket user for a specific ticket id
     *
     * @param ticketUserData : ticketUser rest API Output
     * @param ticketId : ticket id as String
     */
    public JSONObject getTicketUsers(JSONArray ticketUserData, String ticketId) throws JSONException {
        for (int i = 0; i < ticketUserData.length(); i++) {
            JSONObject ticketUserRow = (JSONObject) ticketUserData.get(i);

            if(ticketId.equals(ticketUserRow.getString("tickets_id"))){
                return ticketUserRow;
            }
        }
        return null;
    }
}
