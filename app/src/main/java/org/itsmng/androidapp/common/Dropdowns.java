package org.itsmng.androidapp.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Common class to manage most of the ITSMNG Dropdowns
 */
public abstract class Dropdowns {

    protected HashMap<String, Integer> dataMap = new HashMap<>();

    /**
     * Generate spinner array
     *
     * @param restResponse : ITSMNG API RestResponse
     * @param valueKey : Value (Name)
     * @param indexKey : Key Value (ID)
     *
     * @return Return ArrayList containing values, populate dataMap in background
     *
     * @throws JSONException When an error occur during parsing JSON
     */
    protected ArrayList<String> generateSpinnerArray(JSONArray restResponse, String valueKey, String indexKey) throws JSONException {
        ArrayList<String> spinnerValues = new ArrayList<>();

        for (int i = 0; i < restResponse.length(); i++) {
            JSONObject locationData = (JSONObject) restResponse.get(i);
            spinnerValues.add(locationData.getString(valueKey));
            linkSpinnerNameToID(
                    locationData.getString(valueKey),
                    locationData.getInt(indexKey)
            );
        }

        return spinnerValues;
    }

    /**
     * Add name to datamap with id in ITSMNG
     *
     * @param name : Name of the spinner value
     * @param id : Corresponding ID in ITSMNG
     */
    private void linkSpinnerNameToID(String name, int id){
        dataMap.put(name, id);
    }

    /**
     * Get DB ID of the value selected
     *
     * @param name : String selected value
     *
     * @return Item ID
     */
    public int getTextValueID(String name){
        return dataMap.getOrDefault(name, 0);
    }

}
