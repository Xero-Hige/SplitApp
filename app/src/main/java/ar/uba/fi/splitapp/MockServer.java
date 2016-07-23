package ar.uba.fi.splitapp;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.facebook.Profile;

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
public final class MockServer {

    private static ChatSessionActivity mSession;
    private static int mMessageNumber;
    private static int mGMessageNumber;
    private static ChatRoomActivity mRoom;

    private static String chattingFriend;
    private static String chattingGroup;

    private static Context appContext;

    private static ArrayList<String[]> mMessages = new ArrayList<>();


    private MockServer() {
    }

    public static void setChatRoom(ChatRoomActivity room) {
        mRoom = room;
        mGMessageNumber = 1;
        if (appContext == null && room != null) {
            appContext = room.getApplicationContext();
        }
    }

    public static void sendGroupMessage(String message, List<String> friendsId) {
        if (mSession == null) {
            return;
        }
        mSession.addResponse(message, Profile.getCurrentProfile().getId());

        AddGroupResponseTask task = new AddGroupResponseTask(friendsId);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void addGResponse1(List<String> friendsId) throws InterruptedException {
        Thread.sleep(1000);
        publishResponse(friendsId.get(0), "", "Sisi, no te hagas drama");
        Thread.sleep(2000);
        publishResponse(friendsId.get(1), "", "Yo ya compre, pero colgue en avisar");
        Thread.sleep(1000);
        publishResponse(friendsId.get(1), "", "Ahi lo marco");
    }

    private static void publishResponse(String friendId, String sender, String message) {
        if (mSession != null) {
            mSession.runOnUiThread(() -> mSession.addResponse(message, friendId));
        } else {
            Bitmap bitmap = FacebookManager.getUserImage(appContext, friendId);
            showNotification("Nuevo mensaje de " + sender + ": ", message, bitmap);
        }
        mMessages.add(new String[]{friendId, message});
    }

    private static void showNotification(String header, String message, Bitmap icon) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(appContext)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(MockServer.getCircleBitmap(icon))
                .setContentTitle(header)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(appContext.getResources().getColor(R.color.colorPrimaryLight));

        NotificationManager notificationManager
                = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public static void setChatSession(ChatSessionActivity session) {
        mSession = session;
        mMessageNumber = 1;
        if (appContext == null && session != null) {
            appContext = session.getApplicationContext();
        }
    }

    public static void sendFriendMessage(String message, String friendId, String friendName) {
        if (mSession == null) {
            return;
        }
        mSession.addResponse(message, Profile.getCurrentProfile().getId());

        AddResponseTask task = new AddResponseTask(friendId, friendName);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void addResponse1(String friendId, String mFriendName) throws InterruptedException {
        Thread.sleep(1000);
        String message = "Hola, como va?";
        publishResponse(friendId, mFriendName, message);
        Thread.sleep(2000);
        publishResponse(friendId, mFriendName, "Gracias por agregarme al evento");
    }

    private static void addResponse2(String friendId, String mFriendName) throws InterruptedException {
        Thread.sleep(2000);
        publishResponse(friendId, mFriendName, "Si dale, yo me encargo de llevar la carne");
    }

    private static void addResponse3(String friendId, String mFriendName) throws InterruptedException {
        Thread.sleep(2000);
        publishResponse(friendId, mFriendName, "Ok");
        Thread.sleep(3000);
        publishResponse(friendId, mFriendName, "Igual si podes encargaselo al colo porque no tengo tanto cash ahora");
    }

    private static void addResponse4(String friendId, String mFriendName) throws InterruptedException {
        Thread.sleep(2000);
        publishResponse(friendId, mFriendName, "Che, me voy a seguir laburando. Cualquier cosa dejame dicho");
    }

    private static class AddGroupResponseTask extends AsyncTask<Void, Void, Boolean> {

        List<String> mFriendsId;

        AddGroupResponseTask(List<String> friendsId) {
            mFriendsId = friendsId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mMessageNumber) {
                case 1:
                    try {
                        addGResponse1(mFriendsId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        addGResponse1(mFriendsId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        addGResponse1(mFriendsId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        addGResponse1(mFriendsId);
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
            mGMessageNumber++;
        }
    }

    private static class AddResponseTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFriendName;
        String mFriendId;

        AddResponseTask(String friendId, String friendName) {
            mFriendId = friendId;
            mFriendName = friendName;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mMessageNumber) {
                case 1:
                    try {
                        addResponse1(mFriendId, mFriendName);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        addResponse2(mFriendId, mFriendName);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        addResponse3(mFriendId, mFriendName);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        addResponse4(mFriendId, mFriendName);
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
