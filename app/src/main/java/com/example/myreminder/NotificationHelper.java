package com.example.myreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int NOTIFICATION_ID = 1;
    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Напоминания",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для напоминаний");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(Reminder reminder) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("My Reminder")
                .setContentText(reminder.getTitle())
                .setSubText("Время: " + reminder.getTime())
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
