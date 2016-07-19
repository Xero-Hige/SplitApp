package ar.uba.fi.splitapp;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

import org.json.JSONException;

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

    public static String getCoverUrl(String userId) {
        final String[] result = {""};
        SplitAppLogger.writeLog(SplitAppLogger.DEBG, "/" + userId + "?fields=cover");
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

                    SplitAppLogger.writeLog(SplitAppLogger.DEBG, response.toString());


                    try {
                        result[0] = response.getJSONObject().getJSONObject("cover").getString("source");

                    } catch (JSONException e) {
                    }
                }
        ).executeAndWait();
        return result[0];
    }


}

