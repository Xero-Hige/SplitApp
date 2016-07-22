package ar.uba.fi.splitapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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
        final String[] result = {""};
        Bundle params = new Bundle();
        params.putString("fields", "cover");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId,
                params,
                HttpMethod.GET,
                response -> {
                    if (response == null) {
                        return;
                    }

                    try {
                        result[0] = response.getJSONObject().getJSONObject("cover").getString("source");

                    } catch (JSONException e) {
                    }
                }
        ).executeAndWait();
        return result[0];
    }

    public static List<String> getFriends(String userId) {
        final ArrayList[] friends = new ArrayList[]{null};

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

                        for (int i = 0; i < list.length(); i++) {
                            SplitAppLogger.writeLog(SplitAppLogger.DEBG, "Friend: " + list.getString(i));
                            friends[0].add(list.getString(i));
                        }

                    } catch (JSONException e) {
                        SplitAppLogger.writeLog(SplitAppLogger.ERRO, "Can't decrypt friends JSON");
                    }
                }
        ).executeAsync();
        return friends[0];
    }

    public static void fillWithUserCover(String userId, ImageView view, Context context) {
        ImageFillerTask task = new ImageFillerTask(FacebookManager::getCoverUrl, userId, view, context);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private interface UrlGetter {
        String execute(String id);
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
            if (!success) {
                return;
            }

            Glide.with(mContext).load(mUrl).centerCrop().into(mView);
        }
    }
}

