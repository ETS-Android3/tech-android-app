package org.itsmng.androidapp.async;

import android.content.Context;
import android.os.AsyncTask;

import org.itsmng.androidapp.api.mgmt.ApiEndpoint;
import org.itsmng.androidapp.api.mgmt.ApiMgmt;

public class RestAsyncTask extends AsyncTask<ApiMgmt, Void, Object> {

    // Static public var
    public static final String GET_REQUEST = "GET";
    public static final String POST_REQUEST = "POST";
    public static final String PATCH_REQUEST = "PATCH";

    // Processing vars
    private Context mContext;
    private String mApiEndpoint;
    private String mApiDefaultEndpoint;
    private String mEndpointOverride = null;
    private String mRequestType = RestAsyncTask.GET_REQUEST;
    private String mParams = null;

    // Interface OBJ
    private RestAsyncResponse asyncResponse;

    /**
     * Create a RestASyncTask :
     * - without endpointOverride
     * - GET Request
     * - Without body params
     *
     * @param currentContext : Current activity context
     * @param apiEndpoint : Api endpoint to call
     */
    public RestAsyncTask(RestAsyncResponse delegate, Context currentContext, String apiEndpoint){
        this(delegate, currentContext, apiEndpoint, null);
    }

    /**
     * Create a RestASyncTask :
     * - with endpointOverride
     * - GET Request
     * - Without body params
     *
     * @param currentContext : Current activity context
     * @param apiEndpoint : Api endpoint to call
     * @param endpointOverride : Value you want to override %s in apiEndpoint
     */
    public RestAsyncTask(RestAsyncResponse delegate, Context currentContext, String apiEndpoint, String endpointOverride){
        this(delegate, currentContext, apiEndpoint, endpointOverride, GET_REQUEST, null);
    }

    /**
     * Create a full-featured RestASyncTask
     *
     * @param currentContext : Current activity context
     * @param apiEndpoint : Api endpoint to call
     * @param endpointOverride : Value you want to override %s in apiEndpoint
     * @param requestType : Can be RestAsyncTask::GET_REQUEST or RestAsyncTask::POST_REQUEST
     * @param params : Body params
     */
    public RestAsyncTask(RestAsyncResponse delegate, Context currentContext, String apiEndpoint, String endpointOverride, String requestType, String params){
        this.asyncResponse = delegate;
        this.mContext = currentContext;
        this.mApiEndpoint = apiEndpoint;
        this.mApiDefaultEndpoint = apiEndpoint;
        this.mEndpointOverride = endpointOverride;
        this.mRequestType = requestType;
        this.mParams = params;
    }

    protected Object doInBackground(ApiMgmt... apiMgmt) {
        try
        {
            if(this.mEndpointOverride != null){
                this.mApiEndpoint = String.format(this.mApiEndpoint, this.mEndpointOverride);
            }

            ApiMgmt apiManager = apiMgmt[0];
            if(this.mRequestType.equals(POST_REQUEST) && this.mParams != null){
                return apiManager.post(this.mApiEndpoint, this.mParams);
            }else if(this.mRequestType.equals(PATCH_REQUEST)){
                return apiManager.patch(this.mApiEndpoint, this.mParams);
            }else{
                return apiManager.get(this.mApiEndpoint);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Object result) {
        if(this.mRequestType.equals(POST_REQUEST)){
            asyncResponse.managePostAsyncReponse(result, mApiDefaultEndpoint);
        }else if(this.mRequestType.equals(PATCH_REQUEST)){
            asyncResponse.managePatchAsyncResponse(result, mApiDefaultEndpoint);
        }else{
            asyncResponse.manageGetAsyncResponse(result, mApiDefaultEndpoint);
        }

    }

    /**
     * Implement interface to manage ASyncResponse to view
     */
    public interface RestAsyncResponse {
        /**
         * Method to add in order to manage GET REST Call responde in activity / fragment
         *
         * @param restResponse : Returned JSON Object from API
         */
        void manageGetAsyncResponse(Object restResponse, String apiEndpoint );

        /**
         * Method to add in order to manage PATCH REST Call responde in activity / fragment
         *
         * @param restResponse : Returned JSON Object from API
         */
        void managePatchAsyncResponse(Object restResponse, String apiEndpoint );

        /**
         * Method to add in order to manage POST REST Call responde in activity / fragment
         *
         * @param restResponse : Returned JSON Object from API
         */
        void managePostAsyncReponse(Object restResponse, String apiEndpoint);
    }

}
