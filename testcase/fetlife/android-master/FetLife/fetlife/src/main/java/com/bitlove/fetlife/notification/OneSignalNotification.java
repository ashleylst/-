package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;

import org.json.JSONObject;

public abstract class OneSignalNotification {

    protected static final int NOTIFICATION_ID_DO_NOT_COLLAPSE = -1;

    public static final int NOTIFICATION_ID_ANONYM = 10;
    public static final int NOTIFICATION_ID_FRIEND_REQUEST = 20;
    public static final int NOTIFICATION_ID_MESSAGE = 30;
    public static final int NOTIFICATION_ID_LOVE = 40;
    public static final int NOTIFICATION_ID_COMMENT = 50;
    public static final int NOTIFICATION_ID_MENTION = 60;
    public static int NOTIFICATION_ID_INFO_INTERVAL = 1000;

    protected final String id;
    protected final String title;
    protected final String message;
    protected final String launchUrl;
    protected final String group;
    protected final JSONObject additionalData;

    protected String notificationType;

    public OneSignalNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        this.title = title;
        this.message = message;
        this.launchUrl = launchUrl;
        this.additionalData = additionalData;
        this.group = group;
        this.id = id;
        notificationType = null;
    }

    public void display(FetLifeApplication fetLifeApplication) {}

    PendingIntent getPendingIntent(Context context) {
        return null;
    }

    public abstract boolean handle(FetLifeApplication fetLifeApplication);

    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {}

    public abstract void onClick(FetLifeApplication fetLifeApplication);

    public abstract String getAssociatedPreferenceKey(Context context);

    public boolean isEnabled(FetLifeApplication fetLifeApplication) {
        SharedPreferences sharedPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(getAssociatedPreferenceKey(fetLifeApplication), true);
    }

    protected NotificationCompat.Builder getDefaultNotificationBuilder(FetLifeApplication fetLifeApplication) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(fetLifeApplication)
                .setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.getResources(), R.mipmap.app_icon_kinky))
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setContentIntent(getPendingIntent(fetLifeApplication))
                .setLights(fetLifeApplication.getUserSessionManager().getNotificationColor(),1000,1000)
                .setSound(fetLifeApplication.getUserSessionManager().getNotificationRingtone());

        long[] vibrationSetting = fetLifeApplication.getUserSessionManager().getNotificationVibration();
        if (vibrationSetting == null) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else {
            notificationBuilder.setVibrate(vibrationSetting);
        }

        return notificationBuilder;
    }

    protected String getNotificationType() {
        return notificationType;
    }

    protected void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    protected NotificationHistoryItem createNotificationItem(int notificationId, String collapseId) {
        NotificationHistoryItem notificationItem = new NotificationHistoryItem();
        notificationItem.setDisplayId(notificationId);
        notificationItem.setDisplayHeader(title);
        notificationItem.setDisplayMessage(message);
        notificationItem.setLaunchUrl(launchUrl);
        notificationItem.setCollapseId(collapseId);
        return notificationItem;
    }

}
