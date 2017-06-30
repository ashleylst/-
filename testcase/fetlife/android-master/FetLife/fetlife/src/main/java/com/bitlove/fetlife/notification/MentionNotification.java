package com.bitlove.fetlife.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.NotificationHistoryActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MentionNotification extends OneSignalNotification {

    private static List<MentionNotification> notifications = new ArrayList<MentionNotification>();

    public MentionNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
        notificationType = NotificationParser.JSON_VALUE_TYPE_CONVERSATION_RESPONSE;
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    @Override
    public void display(FetLifeApplication fetLifeApplication) {
        synchronized (notifications) {
            notifications.add(this);

            NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(fetLifeApplication);

            List<String> mentions = getGroupedLovedTexts(fetLifeApplication, notifications);
            String title = fetLifeApplication.getString(R.string.noification_title_new_mentions);
            String firstMention = mentions.get(0);

            notificationBuilder.setContentTitle(title).setContentText(firstMention);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(title);
            //TODO: localization
            inboxStyle.setSummaryText("…");
            for (String message : mentions) {
                inboxStyle.addLine(message);
            }
            notificationBuilder.setStyle(inboxStyle);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_MENTION, notificationBuilder.build());

            onNotificationDisplayed(fetLifeApplication,NOTIFICATION_ID_DO_NOT_COLLAPSE);
        }
    }

    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId, null);
        notificationHistoryItem.save();
    }

    @Override
    PendingIntent getPendingIntent(Context context) {
        Intent contentIntent = NotificationHistoryActivity.createIntent(context,true);
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getNotificationType());

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return contentPendingIntent;
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {

    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_mentions_enabled);
    }

    private List<String> getGroupedLovedTexts(FetLifeApplication fetLifeApplication, List<MentionNotification> notifications) {
        LinkedHashMap<String,Integer> userMentionGroups = new LinkedHashMap<>();
        for (MentionNotification notification : notifications) {
            Integer userMentionCount = userMentionGroups.get(notification.title);
            if (userMentionCount == null) {
                userMentionCount = 1;
            } else {
                userMentionCount++;
            }
            userMentionGroups.put(notification.title,userMentionCount);
        }
        List<String> mentions = new ArrayList<>();
        for (Map.Entry<String,Integer> userMentionGroup : userMentionGroups.entrySet()) {
            //ignore count for now
            mentions.add(fetLifeApplication.getString(R.string.noification_text_new_mention,userMentionGroup.getKey()));
        }
        Collections.reverse(mentions);
        return mentions;
    }
}
