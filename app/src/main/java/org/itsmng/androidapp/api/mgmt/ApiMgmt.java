package org.itsmng.androidapp.api.mgmt;

import org.itsmng.androidapp.common.ParametersMgmt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class ApiMgmt {

    // Async client
    private OkHttpClient client = new OkHttpClient();

    // Variables retrieved from app configuration
    private String apiBaseUrl;
    private String apiAuthToken;
    public int apiUserID;
    private boolean apiSslCheck = true; // SSL Check by default
    private String apiSessionToken;

    // API Excepted return type
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Empty constructor
     */
    protected ApiMgmt(){
        // Not used
    }

    /**
     * Construct API Manager, will get app params and auth
     */
    public ApiMgmt(ParametersMgmt paramMgmt){
        this.apiBaseUrl = paramMgmt.getStringParam("itsmng_url");
        this.apiAuthToken = paramMgmt.getStringParam("itsmng_api_token");
        this.apiSslCheck = paramMgmt.getBoolParam("itsmng_com_ssl");
        this.apiUserID = paramMgmt.getIntParam("glpiID");

        this.apiSessionToken = paramMgmt.getStringParam(ApiAuthentication.SESSION_TOKEN_KEY);
    }

    /**
     * Send a GET request to URL
     *
     * @param relativeUrl : URL to contact
     *
     * @return JSONObject : Reponse body to JSON
     */
    public Object get(String relativeUrl){

        Request.Builder builder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", String.format("%s %s", "Basic", this.apiAuthToken))
                .url(getAbsoluteUrl(relativeUrl))
                .get();

        if(this.apiSessionToken != null){
            builder.addHeader("Session-Token", this.apiSessionToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return new JSONTokener(Objects.requireNonNull(response.body()).string()).nextValue();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Send post request to URL
     *
     * @param relativeUrl : URL to contact
     * @param params : JSON as a string
     */
    public Object post(String relativeUrl, String params) {

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                params
        );

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Session-Token", this.apiSessionToken)
                .addHeader("Authorization", String.format("%s %s", "Basic", this.apiAuthToken))
                .url(getAbsoluteUrl(relativeUrl))
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new JSONTokener(Objects.requireNonNull(response.body()).string()).nextValue();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Send patch request to URL
     *
     * @param relativeUrl : URL to contact
     * @param params : JSON as a string
     */
    public Object patch(String relativeUrl, String params){
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                params
        );

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Session-Token", this.apiSessionToken)
                .addHeader("Authorization", String.format("%s %s", "Basic", this.apiAuthToken))
                .url(getAbsoluteUrl(relativeUrl))
                .patch(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new JSONTokener(Objects.requireNonNull(response.body()).string()).nextValue();
        }catch (Exception e){
            return null;
        }
    }

    protected void setApiSessionToken(String sessionToken){
        this.apiSessionToken = sessionToken;
    }

    /**
     * Get absolute URL (ITSMG Base API URL + endpoint)
     *
     * @param relativeUrl : API endpoint
     *
     * @return String : Relative URL
     */
    private String getAbsoluteUrl(String relativeUrl) {
        return String.format("%s%s", this.apiBaseUrl, relativeUrl);
    }

}
