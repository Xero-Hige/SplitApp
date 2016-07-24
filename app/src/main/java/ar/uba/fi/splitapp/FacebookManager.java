package ar.uba.fi.splitapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * ${FILE}
 * <p>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses
 */
public final class FacebookManager {
    private FacebookManager() {
    }

    private static String getCoverUrl(String userId) {
        final String[] result = {null};
        Bundle params = new Bundle();
        params.putString("fields", "cover");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId,
                params,
                HttpMethod.GET,
                response -> {
                    if (response == null) {
                        SplitAppLogger.writeLog(SplitAppLogger.WARN, "Cover null response");
                        return;
                    }

                    SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Cover response: " + response.toString());

                    try {
                        result[0] = response.getJSONObject().getJSONObject("cover").getString("source");
                        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Cover response: " + result[0]);

                    } catch (JSONException e) {
                    }
                }
        ).executeAndWait();
        return result[0];
    }

    public static void checkInit(Activity activity) {
        try {
            Profile.getCurrentProfile();

        } catch (RuntimeException e) {
            FacebookSdk.sdkInitialize(activity.getApplicationContext());
            Intent login = new Intent(activity, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(login);
            activity.finish();
        }
        if (Profile.getCurrentProfile() == null || AccessToken.getCurrentAccessToken() == null) {
            FacebookSdk.sdkInitialize(activity.getApplicationContext());
            Intent login = new Intent(activity, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(login);
            activity.finish();
        }
    }

    public static void executeWithFriendlist(String userId, FriendsCallback callback) {
        ExecuteWithFriendsTask task = new ExecuteWithFriendsTask(userId, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static ArrayList[] getFriends(String userId) {
        final ArrayList[] friends = new ArrayList[]{null, null};

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId + "/friends",
                null,
                HttpMethod.GET,
                response -> {
                    SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Done: " + response.getJSONObject().toString());
                    try {
                        JSONArray list = response.getJSONObject().getJSONArray("data");
                        friends[0] = new ArrayList<>();
                        friends[1] = new ArrayList<>();

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject friend = list.getJSONObject(i);
                            friends[0].add(friend.getString("name"));
                            friends[1].add(friend.getString("id"));
                        }

                    } catch (JSONException e) {
                        SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Can't decrypt friends JSON");
                    }
                }
        ).executeAndWait();
        return friends;
    }

    public static void fillWithUserCover(String userId, ImageView view, Context context) {
        ImageFillerTask task = new ImageFillerTask(FacebookManager::getCoverUrl, userId, view, context);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void fillWithUserPic(String userId, ImageView view, Context context) {
        ImageFillerTask task = new ImageFillerTask(FacebookManager::getUserPicUrl, userId, view, context);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Bitmap getUserImage(Context context, String userId) {
        try {
            URL url = new URL(FacebookManager.getUserPicUrl(userId));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }
    }

    private static String getUserPicUrl(String userId) {
        final String[] result = {null};
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "large");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId + "/picture",
                params,
                HttpMethod.GET,
                response -> {
                    if (response == null) {
                        SplitAppLogger.writeLog(SplitAppLogger.WARN, "Picture null response");
                        return;
                    }

                    SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Picture response: " + response.toString());

                    try {
                        result[0] = response.getJSONObject().getJSONObject("data").getString("url");
                        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Picture response: " + result[0]);

                    } catch (JSONException e) {
                    }
                }
        ).executeAndWait();
        return result[0];
    }

    public interface FriendsCallback {
        void execute(ArrayList<String> names, ArrayList<String> ids);
    }

    private interface UrlGetter {
        String execute(String id);
    }

    private static class ExecuteWithFriendsTask extends AsyncTask<Void, Void, Boolean> {

        String userId;
        FriendsCallback callback;
        ArrayList<String> friendsIds;
        ArrayList<String> friendsNames;


        ExecuteWithFriendsTask(String userId, FriendsCallback callback) {
            this.userId = userId;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList[] friendsData = getFriends(userId);
            if (friendsData == null) {
                return false;
            }
            friendsNames = friendsData[0];
            friendsIds = friendsData[1];
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                return;
            }
            callback.execute(friendsNames, friendsIds);
        }
    }

    private static class ImageFillerTask extends AsyncTask<Void, Void, Boolean> {

        private final UrlGetter mGetter;
        private final String mResId;
        private final ImageView mView;
        private final Context mContext;
        private String mUrl;

        ImageFillerTask(UrlGetter getter, String resId, ImageView view, Context context) {
            mGetter = getter;
            mResId = resId;
            mView = view;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mUrl = mGetter.execute(mResId);
            return mUrl != null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success || mView == null) {
                return;
            }
            try {
                Glide.with(mContext).load(mUrl).centerCrop().into(mView);
            } catch (NullPointerException e) {
                SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Null pointer: " + e.getMessage());
                Glide.with(mContext).load(R.drawable.logo).centerCrop().into(mView);
            }
        }
    }
}

