package org.itsmng.androidapp.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.itsmng.androidapp.R;
import org.itsmng.androidapp.common.Items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Computer extends Items {

    /**
     * Abstract contructor
     *
     * @param root           : Current view
     * @param currentContext : Current context
     */
    public Computer(View root, Context currentContext) {
        super(root, currentContext);
    }

    /**
     * Manage computer list API
     *
     * @param computerInfos : JSON Object containing API Return
     *
     * @return : Table Row
     */
    public TableRow manageComputerList(JSONObject computerInfos) throws JSONException {
        TableRow computerRow = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.computer_list_row_template, null);

        TextView computerId = computerRow.findViewById(R.id.computer_id);
        computerId.setText(computerInfos.getString("id"));

        TextView computerName = computerRow.findViewById(R.id.computer_name);
        computerName.setText(computerInfos.getString("name"));

        TextView computerSerial = computerRow.findViewById(R.id.computer_serial);
        computerSerial.setText(computerInfos.getString("serial"));

        TextView computerLocations = computerRow.findViewById(R.id.computer_location);
        computerLocations.setText(computerInfos.getString("locations_id"));

        computerRow.setTag(computerInfos.getString("id"));

        return computerRow;
    }

}
