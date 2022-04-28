package org.itsmng.androidapp.common;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.itsmng.androidapp.R;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Basic class containing most of the basic method to manage fields / android widget / ITSMNG Common specificities
 */
public abstract class Items {

    protected View mRoot;
    protected Context mContext;

    /**
     * Abstract contructor
     *
     * @param root : Current view
     * @param currentContext : Current context
     */
    protected Items(View root, Context currentContext){
        this.mRoot = root;
        this.mContext = currentContext;
    }

    /**
     * Will escape and format HTML Field for display
     *
     * @return Escaped and formatted edit text
     */
    public String manageHtmlContent(String textToFormat){
        String escapedTest = StringEscapeUtils.unescapeHtml4(textToFormat);
        Spanned formattedHtml = Html.fromHtml(escapedTest, Html.FROM_HTML_MODE_COMPACT);
        return formattedHtml.toString();
    }

    /**
     * Create and return TextView object containing ticket info
     *
     * @param text : Text to show
     * @param tViewLP : TextView param layout
     *
     * @return TextView object
     */
    protected TextView getTextViewObject(String text, ViewGroup.LayoutParams tViewLP){
        TextView tView = new TextView(mContext);
        tView.setLayoutParams(tViewLP);
        tView.setGravity(Gravity.CENTER);

        if(text.equals("0")){
            tView.setText(R.string.ticket_empty_fields);
        }else{
            tView.setText(text);
        }

        return tView;
    }

    /**
     * Update textview object depending on its ID
     *
     * @param textViewID : TestView ID
     * @param text : Text to set
     *
     * @return Updated textview
     */
    public TextView updateTextViewObject(int textViewID, String text){
        TextView updateTextView = mRoot.findViewById(textViewID);
        updateTextView.setText(text);
        return updateTextView;
    }

    /**
     * Update editview object depending on its ID
     *
     * @param  editViewID : Editview ID
     * @param text : Text to set
     *
     * @return Updated textview
     */
    public TextView updateEditViewObject(int editViewID, String text){
        TextView updateTextView = mRoot.findViewById(editViewID);
        updateTextView.setText(text);
        return updateTextView;
    }

    /**
     * Update spinner with values in ArrayList
     *
     * @param spinner : Spinner object
     * @param values : Arraylist of values
     * @param defaultValue : Default value
     */
    public void updateSpinner(Spinner spinner, ArrayList<String> values, String defaultValue){
        String[] spinnerArray = new String[values.size()];
        spinnerArray = values.toArray(spinnerArray);
        updateSpinner(spinner, spinnerArray, defaultValue);
    }

    /**
     * Update spinner with values in Array
     *
     * @param spinner : Spinner object
     * @param values : Arraylist of values
     * @param defaultValue : Default value
     */
    public void updateSpinner(Spinner spinner, String[] values, String defaultValue){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Objects.requireNonNull(mContext),
                android.R.layout.simple_spinner_item,
                values
        );

        int spinnerPosition = adapter.getPosition(defaultValue);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerPosition);
    }

    /**
     * This method will generate a JSON String compliant with ITSMNG API to be sent
     *
     * @param inputData : Hash Map key value of the input json to generate
     */
    public String generateJSONStringToInsert(HashMap<String, String> inputData) throws JSONException {

        JSONObject objectInput = new JSONObject();

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            objectInput.put(entry.getKey(), entry.getValue());
        }

        JSONObject inputContainer = new JSONObject();
        inputContainer.put("input", objectInput);

        return inputContainer.toString();
    }

}
