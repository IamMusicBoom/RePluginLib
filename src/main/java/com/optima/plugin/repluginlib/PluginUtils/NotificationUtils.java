package com.optima.plugin.repluginlib.PluginUtils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

/**
 * create by wma
 * on 2020/8/19 0019
 */
public class NotificationUtils {

    public static final String CHANNEL_ID = "888";
    public static final String NAME = "NotificationUtils";
    public static final int NOTIFICATION_ID = 666;

    private String mChannelId;
    private String mName;
    private int mImportance;
    private Notification.Builder mBuilder;
    private NotificationManager mManager;

    public NotificationUtils(String mChannelId, String mName, int importance) {
        this.mChannelId = mChannelId;
        this.mName = mName;
        this.mImportance = importance;
        mManager = (NotificationManager) P_Context.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = createNotificationBuilder(mChannelId, mName, mImportance);

    }

    private Notification.Builder createNotificationBuilder(String channelId, String name, int importance) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            mManager.createNotificationChannel(channel);
            builder = new Notification.Builder(P_Context.getContext(), channelId);
        } else {
            builder = new Notification.Builder(P_Context.getContext());
        }
        builder.setWhen(0);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        return builder;
    }


    public void show(String title, String content, Bitmap largeIcon, int smallIcon) {
        if (!TextUtils.isEmpty(title)) {
            mBuilder.setContentTitle(title);
        }

        if (!TextUtils.isEmpty(content)) {
            mBuilder.setContentText(content);
        }
        if (largeIcon != null) {
            mBuilder.setLargeIcon(largeIcon);
        }
        if (smallIcon > 0) {
            mBuilder.setSmallIcon(smallIcon);
        }

        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
