package ar.uba.fi.splitapp;

import android.os.AsyncTask;

import com.facebook.Profile;

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
public final class MockServer {

    private static ChatSessionActivity mSession;
    private static int mMessageNumber;


    private MockServer() {
    }

    public static void setSession(ChatSessionActivity session) {
        mSession = session;
        mMessageNumber = 1;
    }

    public static void sendFriendMessage(String message, String friendId) {
        if (mSession == null) {
            return;
        }
        mSession.addResponse(message, Profile.getCurrentProfile().getId());

        AddResponseTask task = new AddResponseTask(friendId);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void addResponse1(String friendId) throws InterruptedException {
        Thread.sleep(500);
        mSession.runOnUiThread(() -> mSession.addResponse("Hola, como va?", friendId));
        Thread.sleep(2000);
        mSession.runOnUiThread(() -> mSession.addResponse("Gracias por agregarme al evento", friendId));
    }

    private static void addResponse2(String friendId) throws InterruptedException {
        Thread.sleep(2000);
        mSession.runOnUiThread(() -> mSession.addResponse("Si dale, yo me encargo de llevar la carne", friendId));
    }

    private static void addResponse3(String friendId) throws InterruptedException {
        Thread.sleep(2000);
        mSession.runOnUiThread(() -> mSession.addResponse("Ok", friendId));
        Thread.sleep(3000);
        mSession.runOnUiThread(() -> mSession.addResponse("Igual si podes encargaselo al colo porque no tengo tanto cash ahora", friendId));
    }

    private static void addResponse4(String friendId) throws InterruptedException {
        Thread.sleep(2000);
        mSession.runOnUiThread(() -> mSession.addResponse("Che, me voy a seguir laburando. Cualquier cosa dejame dicho", friendId));
    }

    private static class AddResponseTask extends AsyncTask<Void, Void, Boolean> {

        String mFriendId;

        AddResponseTask(String friendId) {
            mFriendId = friendId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mMessageNumber) {
                case 1:
                    try {
                        addResponse1(mFriendId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        addResponse2(mFriendId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        addResponse3(mFriendId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        addResponse4(mFriendId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mMessageNumber++;
        }
    }
}
