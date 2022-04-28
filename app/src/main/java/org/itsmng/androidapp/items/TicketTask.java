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

import java.time.LocalTime;

public class TicketTask extends Items {

    /**
     * @param root           : Current view
     * @param currentContext : Current context
     */
    public TicketTask(View root, Context currentContext) {
        super(root, currentContext);
    }

    /**
     * Manage Ticket Task data and format table and inflate row template
     *
     * @param taskData Data returned by TicketTask API Endpoint
     *
     * @return Return ticket task row
     *
     * @throws JSONException When an error occur during parsing JSON
     */
    public LinearLayout manageTicketTask(JSONObject taskData) throws JSONException {
        LinearLayout taskRow = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ticket_task_row_template, null);

        String fromText = mContext.getResources().getString(R.string.ticketfollowup_from);
        String atText = mContext.getResources().getString(R.string.ticketfollowup_at);

        TextView date = taskRow.findViewById(R.id.tickettask_date);
        date.setText(String.format("%s %s", atText, taskData.getString("date")));

        TextView user = taskRow.findViewById(R.id.tickettask_user);
        user.setText(String.format("%s %s", fromText, taskData.getString("users_id")));

        TextView description = taskRow.findViewById(R.id.tickettask_desc);
        description.setText(manageHtmlContent(taskData.getString("content")));

        TextView time = taskRow.findViewById(R.id.tickettask_time);
        time.setText(formatActionTimeFields(taskData.getString("actiontime")));

        TextView isPrivate = taskRow.findViewById(R.id.tickettask_is_private);
        switch (taskData.getString("is_private")){
            case "0":
                isPrivate.setBackgroundColor(mContext.getColor(R.color.colorGreen));
                break;
            case "1":
                isPrivate.setBackgroundColor(mContext.getColor(R.color.colorRed));
                break;
        }

        return taskRow;
    }

    /**
     * Inflate create button to add TicketTask
     *
     * @return Return inflated button
     */
    public Button inflateCreateTaskButton(){
        return (Button) LayoutInflater.from(mContext).inflate(R.layout.ticket_task_create_button_template, null);
    }

    /**
     * Return a complete string with task time
     *
     * @param secondsNumber : actiontime
     *
     * @return String, complete string
     */
    private String formatActionTimeFields(String secondsNumber){
        return String.format(
                "%s %s %s",
                mRoot.getResources().getString(R.string.tickettask_actiontime),
                secondsToHours(secondsNumber),
                mRoot.getResources().getString(R.string.tickettask_actiontime_unit)
        );
    }

    /**
     * Turn action time "time string" to hours for better displaying
     *
     * @param secondsNumber : Number of seconds
     *
     * @return String : time of the task
     */
    private String secondsToHours(String secondsNumber){
        try {
            float seconds = Float.parseFloat(secondsNumber);
            float hours = seconds / 3600;

            return String.valueOf(hours);
        }catch (Exception e){
            return secondsNumber;
        }
    }

    /**
     * Turn hours into actions time
     *
     * @param hoursNumber : Number of hours
     *
     * @return : Action time
     */
    public String hoursToSeconds(String hoursNumber){
        try {
            float hours = Float.parseFloat(hoursNumber);
            float actiontime = hours * 3600;

            return String.valueOf(actiontime);
        }catch (Exception e){
            return "0";
        }
    }


}
