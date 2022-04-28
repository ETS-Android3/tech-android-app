package org.itsmng.androidapp.items;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.common.Items;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to regroup method common to ticket management
 */
public class Ticket extends Items {

    /**
     * Constructor
     *
     * @param root : root view of the fragment
     * @param currentContext : current fragment context
     */
    public Ticket(View root, Context currentContext){
        super(root, currentContext);
    }

    /**
     * Manage JSON Object returned by API to create Row
     * @param currentRow : Table row we want to inflate
     * @param ticketData : JSONObject with current data
     *
     * @return TableRow : Table row with ticket informations
     *
     * @throws JSONException : If JSON not valid or value not present
     */
    public TableRow manageTicketData(TableRow currentRow, JSONObject ticketData) throws JSONException {

        TextView idTextView = getTextViewObject(ticketData.getString("id"), mRoot.findViewById(R.id.ticket_id).getLayoutParams());
        currentRow.addView(getPriorityInformations(idTextView, ticketData.getString("priority"), false));
        currentRow.addView(getTextViewObject(ticketData.getString("name"), mRoot.findViewById(R.id.ticket_title).getLayoutParams()));
        currentRow.addView(getTextViewObject(ticketData.getString("itilcategories_id"), mRoot.findViewById(R.id.ticket_category).getLayoutParams()));
        currentRow.addView(getTextViewObject(ticketData.getString("locations_id"), mRoot.findViewById(R.id.ticket_location).getLayoutParams()));

        currentRow.setTag(ticketData.getString("id"));

        return currentRow;
    }

    /**
     * Return if a ticket is compliant
     *
     * @param ticketData : JSON Data of the ticket
     *
     * @return True if compliant, false if not
     */
    public boolean isCompliant(JSONObject ticketData) throws JSONException {
        if(ticketData.getInt("status") == 5 || ticketData.getInt("status") == 6){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Get priority color depending on number from REST Api.
     *
     * @param tView : TextView containing priority
     * @param priority : Priority String
     *
     * @return Corrected textview
     */
    private TextView getPriorityInformations(TextView tView, String priority, boolean setText){
        int priorityInt = Integer.parseInt(priority);

        switch (priorityInt){
            case 2:
                if (setText) tView.setText(R.string.ticket_priority_low);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityLow));
                break;
            case 3:
                if (setText) tView.setText(R.string.ticket_priority_medium);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityMedium));
                break;
            case 4:
                if (setText) tView.setText(R.string.ticket_priority_high);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityHigh));
                break;
            case 5:
                if (setText) tView.setText(R.string.ticket_priority_veryhigh);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityVeryHigh));
                break;
            case 6:
                if (setText) tView.setText(R.string.ticket_priority_major);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityMajor));
                break;
            default:
                if (setText) tView.setText(R.string.ticket_priority_verylow);
                tView.setBackgroundColor(mContext.getResources().getColor(R.color.priorityVeryLow));
                break;
        }

        return tView;
    }

    /**
     * Get priority color depending on number from REST Api.
     *
     * @param tViewID : TextView ID
     * @param priority : Priority string
     *
     * @return Updated textview
     */
    public TextView getPriorityInformations(int tViewID, String priority){
        return getPriorityInformations((TextView) mRoot.findViewById(tViewID), priority, true);
    }

    /**
     * From status name get status id
     *
     * @param statusName : Name of the status
     * @return statusId
     */
    public int getTicketStatusId(String statusName){
        if(statusName.equals(mContext.getResources().getString(R.string.ticket_status_new))){
            return 1;
        }else if(statusName.equals(mContext.getResources().getString(R.string.ticket_status_assigned))){
            return 2;
        }else if(statusName.equals(mContext.getResources().getString(R.string.ticket_status_planned))){
            return 3;
        }else if(statusName.equals(mContext.getResources().getString(R.string.ticket_status_pending))){
            return 4;
        }else if(statusName.equals(mContext.getResources().getString(R.string.ticket_status_solved))){
            return 5;
        }else{
            return 6;
        }
    }

    /**
     * Return array of ITSMNG Ticket status
     */
    public String[] getTicketStatusArray(){
        return new String[]{
            mContext.getResources().getString(R.string.ticket_status_new),
            mContext.getResources().getString(R.string.ticket_status_assigned),
            mContext.getResources().getString(R.string.ticket_status_planned),
            mContext.getResources().getString(R.string.ticket_status_pending),
            mContext.getResources().getString(R.string.ticket_status_solved),
            mContext.getResources().getString(R.string.ticket_status_closed),
        };
    }

    /**
     * Return status name from string
     *
     * @return Status pretty name
     */
    public String getStatusNameFromID(String statusId){
        int statusInt = Integer.parseInt(statusId);

        switch (statusInt){
            case 2:
                return mContext.getResources().getString(R.string.ticket_status_assigned);
            case 3:
                return mContext.getResources().getString(R.string.ticket_status_planned);
            case 4:
                return mContext.getResources().getString(R.string.ticket_status_pending);
            case 5:
                return mContext.getResources().getString(R.string.ticket_status_solved);
            case 6:
                return mContext.getResources().getString(R.string.ticket_status_closed);
            default:
                return mContext.getResources().getString(R.string.ticket_status_new);
        }
    }

}
