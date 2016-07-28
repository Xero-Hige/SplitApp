package ar.uba.fi.splitapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;


/**
 * @author Xero-Hige
 * Copyright 2016 Gaston Martinez Gaston.martinez.90@gmail.com
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

/**
 *
 */
public final class ServerHandler {

    public static final int USER_TOKEN = 0;
    public static final int EVENT_LIST = 1;
    public static final int EVENT_DETAIL = 2;
    public static final int EVENT_TASKS = 3;
    public static final int EVENT_INVITEE = 4;
    public static final int EVENT_TRANSACTION = 5;
    public static final int EVENT_TEMPLATE = 6;

    public static final String FAILED_TOKEN = "-";
    public static final String ERROR_TOKEN = "";
    public static final String SIGNUP_USEREXIST = "E";
    public static final String SIGNUP_FAILED = "F";
    public static final String SIGNUP_SUCCESS = "S";

    private static final String SERVER_URL = "http://splitapp.medicmanager.com.ar/public/index.php/";
    private static final String TOKEN_BASE_URL = "tokens";
    private static final String TOKEN_MOD_URL = "";

    private static final String EVENT_LIST_BASE_URL = "events";
    private static final String EVENT_LIST_MOD_URL = "";
    private static final String EVENT_DETAIL_BASE_URL = "events";
    private static final String EVENT_DETAIL_MOD_URL = "";
    private static final String EVENT_TASKS_BASE_URL = "events";
    private static final String EVENT_TASKS_MOD_URL = "/eventTask";
    private static final String EVENT_INVITEE_BASE_URL = "events";
    private static final String EVENT_INVITEE_MOD_URL = "/eventInvitee";
    private static final String EVENT_TRANSACTION_BASE_URL = "events";
    private static final String EVENT_TRANSACTION_MOD_URL = "/settlementTransactions";
    private static final String EVENT_TEMPLATE_BASE_URL = "eventsTemplates";
    private static final String EVENT_TEMPLATE_MOD_URL = "";


    /**
     * Private
     */
    private static final int MAX_TRIES = 30;

    private static String mToken = ERROR_TOKEN;

    private ServerHandler() {
    }

    private static String getUrl(int urlType, String resId) {
        String resUrl = (resId.equals("") ? "" : String.format(Locale.ENGLISH, "/%s", resId));
        return SERVER_URL + getBaseUrl(urlType) + resUrl + getModUrl(urlType);
    }

    private static String getModUrl(int urlType) {
        switch (urlType) {
            case USER_TOKEN:
                return TOKEN_MOD_URL;
            case EVENT_LIST:
                return EVENT_LIST_MOD_URL;
            case EVENT_DETAIL:
                return EVENT_DETAIL_MOD_URL;
            case EVENT_TASKS:
                return EVENT_TASKS_MOD_URL;
            case EVENT_INVITEE:
                return EVENT_INVITEE_MOD_URL;
            case EVENT_TRANSACTION:
                return EVENT_TRANSACTION_MOD_URL;
            case EVENT_TEMPLATE:
                return EVENT_TEMPLATE_MOD_URL;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static String getBaseUrl(int urlType) {
        switch (urlType) {
            case USER_TOKEN:
                return TOKEN_BASE_URL;
            case EVENT_LIST:
                return EVENT_LIST_BASE_URL;
            case EVENT_DETAIL:
                return EVENT_DETAIL_BASE_URL;
            case EVENT_TASKS:
                return EVENT_TASKS_BASE_URL;
            case EVENT_INVITEE:
                return EVENT_INVITEE_BASE_URL;
            case EVENT_TRANSACTION:
                return EVENT_TRANSACTION_BASE_URL;
            case EVENT_TEMPLATE:
                return EVENT_TEMPLATE_BASE_URL;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static JSONObject getFromServer(String queryUrl, String fbId, String fbToken) {
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "Begin GET " + queryUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<String> result;

        try {
            HttpEntity<String> entity = new HttpEntity<>(getAuthHeader(fbId, fbToken));
            result = restTemplate.exchange(queryUrl,
                    HttpMethod.GET,
                    entity,
                    String.class);
        } catch (HttpServerErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
            return null;
        } catch (HttpClientErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
            return null;
        } catch (ResourceAccessException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Failed to connect: " + e.getMessage());
            return null;
        }
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "End GET " + queryUrl);

        if (result == null) {
            SplitAppLogger.writeLog(SplitAppLogger.WARN, "Empty GET response from :" + queryUrl);
            return null;
        }

        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "GET result: \n" + result);

        try {
            JSONObject response = new JSONObject(result.getBody());
            return response;
        } catch (JSONException e) {
            return null;
        }
    }

    private static HttpHeaders getAuthHeader(String fbId, String fbToken) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token", mToken);
        SplitAppLogger.writeLog(1, "getAuthHeader - fbId: " + fbId);
        requestHeaders.add("x-Auth-Facebook-ID", fbId);
        requestHeaders.add("x-Auth-Facebook-Token", fbToken);
        return requestHeaders;
    }

    private static boolean deleteFromServer(String queryUrl, String fbId, String fbToken) {
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "Begin DELETE " + queryUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        try {
            HttpEntity<String> entity = new HttpEntity<>(getAuthHeader(fbId, fbToken));
            restTemplate.exchange(queryUrl,
                    HttpMethod.DELETE,
                    entity,
                    String.class);
        } catch (HttpServerErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
            return false;
        } catch (HttpClientErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
            return false;
        } catch (ResourceAccessException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Failed to connect: " + e.getMessage());
            return false;
        }
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "End DELETE " + queryUrl);

        return true;
    }

    private static JSONObject postToServer(String queryUrl, JSONObject body, String fbId, String fbToken) {
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "Begin POST " + queryUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpEntity<String> result;

        try {
            HttpHeaders headers = getAuthHeader(fbId, fbToken);
            headers.add("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            result = restTemplate.postForEntity(queryUrl, entity, String.class);
        } catch (HttpServerErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
            return null;
        } catch (HttpClientErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
            return null;
        } catch (ResourceAccessException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Failed to connect: " + e.getMessage());
            return null;
        }
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "End POST " + queryUrl);

        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "POST result: \n" + result);

        try {
            JSONObject response = new JSONObject(result.getBody());
            return response;
        } catch (JSONException e) {
            return null;
        }
    }

    private static JSONObject putToServer(String queryUrl, JSONObject body, String fbId, String fbToken) {
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "Begin PUT " + queryUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpEntity<String> result;

        try {
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), getAuthHeader(fbId, fbToken));
            result = restTemplate.exchange(queryUrl, HttpMethod.PUT, entity, String.class);
        } catch (HttpServerErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
            return null;
        } catch (HttpClientErrorException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
            return null;
        } catch (ResourceAccessException e) {
            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Failed to connect: " + e.getMessage());
            return null;
        }
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "End PUT " + queryUrl);

        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "PUT result: \n" + result);

        try {
            JSONObject response = new JSONObject(result.getBody());
            return response;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Logouts from the current session
     */
    public static void logout() {
        mToken = "";
    }

    /**
     * Get the current session token
     *
     * @return The current session token. If not possible, one of the listed Error tokens
     */
    public static String getToken() {
        return mToken;
    }

    public static void signIn(String facebookId,
                              String facebookToken,
                              JsonCallbackOperation onSucces,
                              JsonCallbackOperation onError) {
        ServerHandler.executeGet(USER_TOKEN, facebookId, facebookToken, result -> {
            if (result == null) {
                onError.execute(null);
            } else {
                try {
                    mToken = result.getJSONObject("data").getString("token");
                } catch (JSONException e) {
                    onError.execute(null);
                    return;
                }
                onSucces.execute(result);
            }
        });
    }

    public static void executeGet(int requestType, String facebookId, String facebookToken, JsonCallbackOperation operation) {
        executeGet("", requestType, facebookId, facebookToken, operation);
    }

    public static void executeGet(String resId, int requestType, String facebookId, String facebookToken, JsonCallbackOperation operation) {
        GetDataTask task = new GetDataTask(requestType, resId, facebookId, facebookToken, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void executeGet(int requestType, JsonCallbackOperation operation) {
        executeGet("", requestType, operation);
    }

    public static void executeGet(String resId, int requestType, JsonCallbackOperation operation) {
        executeGet(resId, requestType, "", "", operation);
    }

    public static void executeDelete(int requestType, BoolCallbackOperation operation) {
        executeDelete("", requestType, operation);
    }

    public static void executeDelete(String resId, int requestType, BoolCallbackOperation operation) {
        executeDelete(resId, requestType, "", "", operation);
    }

    public static void executeDelete(String resId, int requestType, String facebookId, String facebookToken, BoolCallbackOperation operation) {
        DeleteDataTask task = new DeleteDataTask(requestType, resId, facebookId, facebookToken, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void executePost(int requestType, JSONObject body, JsonCallbackOperation operation) {
        executePost("", requestType, body, operation);
    }

    public static void executePost(String resId, int requestType, JSONObject body, JsonCallbackOperation operation) {
        executePost(resId, requestType, "", "", body, operation);
    }

    public static void executePost(String resId, int requestType, String facebookId, String facebookToken, JSONObject body, JsonCallbackOperation operation) {
        PostDataTask task = new PostDataTask(requestType, resId, facebookId, facebookToken, body, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void executePut(int requestType, JSONObject body, JsonCallbackOperation operation) {
        executePut("", requestType, body, operation);
    }

    public static void executePut(String resId, int requestType, JSONObject body, JsonCallbackOperation operation) {
        executePut(resId, requestType, "", "", body, operation);
    }

    public static void executePut(String resId, int requestType, String facebookId, String facebookToken, JSONObject body, JsonCallbackOperation operation) {
        PutDataTask task = new PutDataTask(requestType, resId, facebookId, facebookToken, body, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Callbacks interface
     */
    public interface JsonCallbackOperation {
        void execute(JSONObject data);
    }

    public interface BoolCallbackOperation {
        void execute(boolean data);
    }

    private static class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        private final JsonCallbackOperation mCallbackOp;
        private String mResourceUrl;
        private JSONObject mData;
        private String mFbId;
        private String mFbToken;

        GetDataTask(int resourceType, String resourceId, String facebookId, String facebookToken, JsonCallbackOperation callbackOperation) {
            mResourceUrl = getUrl(resourceType, resourceId);
            this.mCallbackOp = callbackOperation;
            this.mData = null;
            mFbId = facebookId;
            mFbToken = facebookToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mData = getFromServer(mResourceUrl, mFbId, mFbToken);
            return mData != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success) {
                SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Failed to GET data from server");
            }
            mCallbackOp.execute(mData);
        }

    }

    private static class PostDataTask extends AsyncTask<Void, Void, Boolean> {

        private final JsonCallbackOperation mCallbackOp;
        private String mResourceUrl;
        private JSONObject mData;
        private String mFbId;
        private String mFbToken;
        private JSONObject mBody;

        PostDataTask(int resourceType, String resourceId, String facebookId, String facebookToken, JSONObject object, JsonCallbackOperation callbackOperation) {
            mResourceUrl = getUrl(resourceType, resourceId);
            this.mCallbackOp = callbackOperation;
            this.mData = null;
            mFbId = facebookId;
            mFbToken = facebookToken;
            mBody = object;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mData = postToServer(mResourceUrl, mBody, mFbId, mFbToken);
            return mData != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success) {
                SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Failed to POST data to server");
            }
            mCallbackOp.execute(mData);
        }

    }

    private static class PutDataTask extends AsyncTask<Void, Void, Boolean> {

        private final JsonCallbackOperation mCallbackOp;
        private String mResourceUrl;
        private JSONObject mData;
        private String mFbId;
        private String mFbToken;
        private JSONObject mBody;

        PutDataTask(int resourceType, String resourceId, String facebookId, String facebookToken, JSONObject object, JsonCallbackOperation callbackOperation) {
            mResourceUrl = getUrl(resourceType, resourceId);
            this.mCallbackOp = callbackOperation;
            this.mData = null;
            mFbId = facebookId;
            mFbToken = facebookToken;
            mBody = object;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mData = putToServer(mResourceUrl, mBody, mFbId, mFbToken);
            return mData != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success) {
                SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Failed to PUT data to server");
            }
            mCallbackOp.execute(mData);
        }

    }

    private static class DeleteDataTask extends AsyncTask<Void, Void, Boolean> {

        private final BoolCallbackOperation mCallbackOp;
        private String mResourceUrl;
        private boolean mData;
        private String mFbId;
        private String mFbToken;

        DeleteDataTask(int resourceType, String resourceId, String facebookId, String facebookToken, BoolCallbackOperation callbackOperation) {
            mResourceUrl = getUrl(resourceType, resourceId);
            this.mCallbackOp = callbackOperation;
            this.mData = false;
            mFbId = facebookId;
            mFbToken = facebookToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mData = deleteFromServer(mResourceUrl, mFbId, mFbToken);
            return mData;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCallbackOp.execute(mData);
        }

    }

}