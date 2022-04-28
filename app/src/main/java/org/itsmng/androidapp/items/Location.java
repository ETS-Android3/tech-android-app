package org.itsmng.androidapp.items;

import org.itsmng.androidapp.common.Dropdowns;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class to regroup method common to Location management
 */
public class Location extends Dropdowns {

    private static final String LOCATION_INDEX_KEY = "id";
    private static final String LOCATION_VALUE_KEY = "name";

    /**
     * Call parent generateSpinnerArray with corresponding INDEX and VALUE KEYS
     *
     * @param restResponse : ITSMNG API Rest response
     *
     * @return super.generateSpinnerArray
     *
     * @throws JSONException When an error occur during parsing JSON
     */
    public ArrayList<String> generateSpinnerArray(JSONArray restResponse) throws JSONException {
        return super.generateSpinnerArray(restResponse, LOCATION_VALUE_KEY, LOCATION_INDEX_KEY);
    }

}
