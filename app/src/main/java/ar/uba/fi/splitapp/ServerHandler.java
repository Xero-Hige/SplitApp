package ar.uba.fi.splitapp;

import android.os.AsyncTask;

import org.json.JSONArray;
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

import java.util.LinkedList;
import java.util.List;
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

    private static String face_id;

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

    private static List<JSONObject> getFromServer(String queryUrl, String fbId, String fbToken) {
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
            return new LinkedList<>();
        }

        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "GET result: \n" + result);

        JSONArray data;

        try {
            JSONObject response = new JSONObject(result.getBody());
            data = response.getJSONArray("data");
        } catch (JSONException e) {
            return new LinkedList<>();
        }

        LinkedList<JSONObject> objectsList = new LinkedList<>();

        for (int i = 0; i < data.length(); i++) {
            try {
                objectsList.push(data.getJSONObject(i));
            } catch (JSONException e) {
                return new LinkedList<>();
            }
        }

        return objectsList;
    }

    private static HttpHeaders getAuthHeader(String fbId, String fbToken) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token", mToken);
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
            if (result == null || result.size() == 0) {
                onError.execute(result);
            } else {
                try {
                    mToken = result.get(0).getString("token");
                    face_id = facebookId;
                } catch (JSONException e) {
                    onError.execute(result);
                    return;
                }
                onSucces.execute(result);
            }
        });
    }

    public static void getEvents(CallbackOperation onSucces, CallbackOperation onError) {
        ServerHandler.executeGet(EVENT_LIST, face_id, "", result -> {
            if (result == null || result.size() == 0) {
                onError.execute(result);
            } else {
                try {
                    mToken = result.get(0).getString("events");
                } catch (JSONException e) {
                    onError.execute(result);
                    return;
                }
                onSucces.execute(result);
            }
        });
    }

//    /**
//     * Fetches token from server and returns it
//     *
//     * @param email    User email
//     * @param password User password
//     * @param location User location
//     * @return Token string. If error, one of the listed error tokens
//     */
//    static String getLoginToken(String email, String password, String location) {
//
//        mToken = null;
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        String user = getUsernameFrom(email);
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        addAuthHeader(password, user, requestHeaders);
//
//        String body = String.format(Locale.ENGLISH, "localization=%s", location);
//
//        HttpEntity<?> requestEntity = new HttpEntity<>(body, requestHeaders);
//
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//
//        ResponseEntity<String> response;
//
//        try {
//            response = restTemplate.exchange(getLoginUrl(), HttpMethod.POST,
//                    requestEntity, String.class);
//        } catch (HttpServerErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
//            mToken = ERROR_TOKEN;
//            return mToken;
//        } catch (HttpClientErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
//            if (e.getStatusCode().value() == 401) {
//                mToken = FAILED_TOKEN;
//                return FAILED_TOKEN;
//            }
//            mToken = ERROR_TOKEN;
//            return mToken;
//        } catch (ResourceAccessException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_WARN, "Failed to connect: " + e.getMessage());
//            mToken = ERROR_TOKEN;
//            return mToken;
//        }
//
//        int statusCode = response.getStatusCode().value();
//
//        if (statusCode != 200) {
//            String errorMessage = "Failed login post: "
//                    + response.getStatusCode().value()
//                    + " " + response.getStatusCode().getReasonPhrase();
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, errorMessage);
//        }
//
//        mToken = response.getBody();
//        return response.getBody();
//    }


    public static void executeGet(int requestType, String facebookId, String facebookToken, JsonCallbackOperation operation) {
        executeGet("", requestType, facebookId, facebookToken, operation);
    }

    public static void executeGet(String resId, int requestType, String facebookId, String facebookToken, JsonCallbackOperation operation) {
        GetDataTask task = new GetDataTask(requestType, resId, facebookId, facebookToken, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void executeDelete(String resId, int requestType, String facebookId, String facebookToken, BoolCallbackOperation operation) {
        DeleteDataTask task = new DeleteDataTask(requestType, resId, facebookId, facebookToken, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

//    /**
//     * Deletes user profile of the current logged in user
//     *
//     * @param token Session token
//     * @return true if success
//     */
//    public static boolean deleteProfile(String token) {

//
//    /**
//     * Creates a new user with the info
//     *
//     * @param email    User email
//     * @param password User password
//     * @param userdata Map containing userdata as field:value
//     * @return Sign up result (one of the listed results const)
//     */
//    public static String signUp(String email, String password, Map<String, String> userdata) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        String user = getUsernameFrom(email);
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        addAuthHeader(password, user, requestHeaders);
//
//        String name = userdata.get("name");
//        String age = userdata.get("age");
//        String sex = userdata.get("sex");
//        String interest = userdata.get("interest");
//
//        StringWriter sWriter = new StringWriter();
//        CSVWriter writer = new CSVWriter(sWriter, ',');
//        String[] line = {name, age, user, email, sex, " ", interest};
//        writer.writeNext(line);
//        String body = "User=" + sWriter.toString();
//
//        HttpEntity<?> requestEntity = new HttpEntity<>(body, requestHeaders);
//
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//
//        ResponseEntity<String> response;
//
//        try {
//            response = restTemplate.exchange(getSignupUrl(), HttpMethod.POST,
//                    requestEntity, String.class);
//        } catch (HttpServerErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
//            return SIGNUP_FAILED;
//        } catch (HttpClientErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
//            if (e.getStatusCode().value() == 401) {
//                return SIGNUP_USEREXIST;
//            }
//            return SIGNUP_FAILED;
//        } catch (ResourceAccessException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_WARN, "Failed to connect: " + e.getMessage());
//            return SIGNUP_FAILED;
//        }
//
//        int statusCode = response.getStatusCode().value();
//
//        if (statusCode != 201) {
//            String errorMessage = "Failed login post: "
//                    + response.getStatusCode().value()
//                    + " " + response.getStatusCode().getReasonPhrase();
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, errorMessage);
//            return SIGNUP_FAILED;
//        }
//
//        mToken = response.getBody();
//        return SIGNUP_SUCCESS;
//    }
//    /**
//     * Updates user info
//     *
//     * @param token    Session token
//     * @param userdata Map containing userdata as field:value
//     * @return true if success
//     */
//    public static boolean updateInfo(String token, Map<String, String> userdata) {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        String name = userdata.get("name");
//        String age = userdata.get("age");
//        String sex = userdata.get("sex");
//        String interest = userdata.get("interest");
//
//        StringWriter sWriter = new StringWriter();
//        CSVWriter writer = new CSVWriter(sWriter, ',');
//        String[] line = {name, age, sex, interest};
//        writer.writeNext(line);
//        String body = "User=" + sWriter.toString();
//
//        Uri.Builder uriBuilder = Uri.parse(getUpdateUrl()).buildUpon();
//        uriBuilder.appendQueryParameter("token", token);
//        String updateUrl = uriBuilder.build().toString();
//
//        try {
//            restTemplate.put(updateUrl, body);
//        } catch (Exception e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Exception on update: " + e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Sends a message from the active user
//     *
//     * @param token      session token
//     * @param receiverId receiver id
//     * @param message    message to send
//     * @return true if success
//     */
//    public static boolean sendMessage(String token, String receiverId, String message) {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        String bodyTemplate = "user_id=%s&msg=%s";
//
//        String body = String.format(Locale.ENGLISH, bodyTemplate, receiverId, message);
//
//        Uri.Builder uriBuilder = Uri.parse(getMessagesUrl()).buildUpon();
//        uriBuilder.appendQueryParameter("token", token);
//        String matchesUrl = uriBuilder.build().toString();
//
//        boolean sent = false;
//        int tries = 0;
//        while (!sent && tries < MAX_TRIES) {
//            try {
//                restTemplate.postForEntity(matchesUrl, body, String.class);
//                sent = true;
//            } catch (Exception e) {
//                SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Exception on msg send: " + e.getMessage());
//                tries += 1;
//            }
//        }
//
//        return sent;
//    }

    /**
     * Callbacks interface
     */
    public interface JsonCallbackOperation {
        void execute(List<JSONObject> data);
    }

    public interface BoolCallbackOperation {
        void execute(boolean data);
    }

    private static class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        private final JsonCallbackOperation mCallbackOp;
        private String mResourceUrl;
        private List<JSONObject> mData;
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
                SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Failed to get data from server");
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