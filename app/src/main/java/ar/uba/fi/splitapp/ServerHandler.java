package ar.uba.fi.splitapp;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
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

    /**
     * Query type: Candidates for the user
     */
    public static final int USER_CANDIDATES = 0;
    /**
     * Query type: Matches for the user
     */
    public static final int USER_MATCHES = 1;
    /**
     * Query type: Chat for the user
     */
    public static final int USER_CHAT = 2;
    /**
     * Query type: Information of the user
     */
    public static final int USER_INFO = 3;
    /**
     * Query type: New matches for the service
     */
    public static final int SERVICE_MATCHES = 4;
    /**
     * Query type: New matches for the service
     */
    public static final int SERVICE_CHAT = 5;
    /**
     * Result Token: Failed to get
     */
    public static final String FAILED_TOKEN = "-";
    /**
     * Result Token: Error occurred while fetching
     */
    public static final String ERROR_TOKEN = "";
    /**
     * Sign up result: User already exists
     */
    public static final String SIGNUP_USEREXIST = "E";
    /**
     * Sign up result: Sign up failed
     */
    public static final String SIGNUP_FAILED = "F";
    /**
     * Sign up result: Sign up successful
     */
    public static final String SIGNUP_SUCCESS = "S";
    private static final String CANDIDATES_URL = "users";
    private static final String CHATMSG_URL = "chats";
    private static final String USERINFO_URL = "user";
    private static final String NEW_MATCHES_URL = "matches";
    private static final String NEW_CHATS_URL = "chats/new";
    private static final String SERVER_URL = "";
    private static final int MAX_TRIES = 30;
    private static final String LOGIN_URL = "user";
    private static final String DELETE_URL = "users";
    private static final String SIGNUP_URL = "users";
    private static final String UPDATE_URL = "users";
    private static final String AVATAR_URL = "users/photo";
    private static final String MATCHES_URL = "matches";
    private static final String MESSAGES_URL = "chats";
    private static String mToken = ERROR_TOKEN;

    private ServerHandler() {
    }

    private static String getUrlByType(Integer type) {
        String url = SERVER_URL;
        switch (type) {
            case USER_CANDIDATES:
                return url + CANDIDATES_URL;
            case USER_MATCHES:
                return url + MATCHES_URL;
            case USER_CHAT:
                return url + CHATMSG_URL;
            case USER_INFO:
                return url + USERINFO_URL;
            case SERVICE_MATCHES:
                return url + NEW_MATCHES_URL;
            case SERVICE_CHAT:
                return url + NEW_CHATS_URL;
            default:
                return "";
        }
    }

    /**
     * Executes a query in a detached thread. Once it finishes executes the callback
     *
     * @param requestType Request type (One of the listed request types)
     * @param operation   Callback operation
     */
    public static void executeGet(int requestType, CallbackOperation operation) {
        executeGet(requestType, operation);
    }

    /**
     * Executes a query in a detached thread. Once it finishes executes the callback
     *
     * @param resId       Queried resource id
     * @param requestType Request type (One of the listed request types)
     * @param operation   Callback operation
     */
    public static void executeGet(String resId, int requestType, CallbackOperation operation) {
        FetchDataTask task = new FetchDataTask(requestType, resId, mToken, operation);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static List<JSONObject> fetchData(String queryUrl) {
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "Begin fetch " + queryUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        String result;

        try {
            result = restTemplate.getForObject(queryUrl, String.class, "Android");
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
        SplitAppLogger.writeLog(SplitAppLogger.NET_INFO, "End fetch " + queryUrl);

        if (result == null) {
            SplitAppLogger.writeLog(SplitAppLogger.WARN, "Empty response from :" + queryUrl);
            return new LinkedList<>();
        }

        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Fetch result: \n" + result);

        JSONArray data;

        try {
            JSONObject response = new JSONObject(result);
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

    private static void addAuthHeader(String password, String user, HttpHeaders requestHeaders) {
        requestHeaders.add("Authorization", String.format(Locale.ENGLISH,
                "Authorization := username=\"%s\" pass=\"%s\"", user, password));
    }

    /**
     * Logouts from the current session
     */
    public static void logout() {
        mToken = "";
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

    /**
     * Get the current session token
     *
     * @return The current session token. If not possible, one of the listed Error tokens
     */
    public static String getToken() {
        return mToken;
    }

    /**
     * Callbacks interface
     */
    public interface CallbackOperation {
        void execute(List<JSONObject> data);
    }

//    /**
//     * Deletes user profile of the current logged in user
//     *
//     * @param token Session token
//     * @return true if success
//     */
//    public static boolean deleteProfile(String token) {
//        Uri.Builder uriBuilder = Uri.parse(getDeleteUrl()).buildUpon();
//        uriBuilder.appendQueryParameter("token", token);
//        String deleteUrl = uriBuilder.build().toString();
//
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            restTemplate.delete(deleteUrl);
//        } catch (HttpServerErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Server error: " + e.getMessage());
//        } catch (HttpClientErrorException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_ERRO, "Client error: " + e.getMessage());
//        } catch (ResourceAccessException e) {
//            SplitAppLogger.writeLog(SplitAppLogger.NET_WARN, "Failed to connect: " + e.getMessage());
//        }
//        mToken = null;
//        return true;
//    }
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

    private static class FetchDataTask extends AsyncTask<Void, Void, Boolean> {

        private final CallbackOperation mCallbackOp;
        private String mResourceUrl;
        private List<JSONObject> mData;

        FetchDataTask(int resourceType, String resourceId, String token, CallbackOperation callbackOperation) {
            setImageUrl(resourceType, resourceId, token);
            this.mCallbackOp = callbackOperation;
            this.mData = null;
        }

        private void setImageUrl(int resourceType, String resId, String token) {
            String url = getUrlByType(resourceType);
            Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
            uriBuilder.appendQueryParameter("token", token);
            if (!resId.equals("")) {
                uriBuilder.appendQueryParameter("res_id", resId);
            }
            this.mResourceUrl = uriBuilder.build().toString();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mData = fetchData(mResourceUrl);
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
}