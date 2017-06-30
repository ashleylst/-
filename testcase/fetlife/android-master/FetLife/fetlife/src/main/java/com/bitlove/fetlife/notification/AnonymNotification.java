package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnonymNotification {

    private static List<AnonymNotification> notifications = new ArrayList<AnonymNotification>();

    private final OneSignalNotification oneSignalNotification;

    public AnonymNotification(OneSignalNotification oneSignalNotification) {
        this.oneSignalNotification = oneSignalNotification;
        oneSignalNotification.setNotificationType(getClass().getSimpleName());
    }

    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    public void display(FetLifeApplication fetLifeApplication) {
        synchronized (notifications) {
            notifications.add(this);

            NotificationCompat.Builder notificationBuilder = oneSignalNotification.getDefaultNotificationBuilder(fetLifeApplication);

            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.getResources(),R.mipmap.app_icon_vanilla))
                    .setSmallIcon(R.drawable.ic_anonym_notif_small)
                    .setContentTitle(fetLifeApplication.getString(R.string.noification_title_new_one_or_more_notification));

            if (notifications.size() > 1) {
                notificationBuilder.setContentText(fetLifeApplication.getString(R.string.noification_text_new_anonym_notifications, notifications.size()));
            } else {
                notificationBuilder.setContentText(fetLifeApplication.getString(R.string.noification_text_new_anonym_notification));
            }

            // Sets an ID for the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_ANONYM, notificationBuilder.build());
        }
    }
}
