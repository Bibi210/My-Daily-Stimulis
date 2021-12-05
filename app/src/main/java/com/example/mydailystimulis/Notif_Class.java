package com.example.mydailystimulis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notif_Class {

    private final Context ctx;
    String Notified_Channel_Id;

    Notif_Class(Context context) {
        ctx = context;
    }

    void create_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notified_Channel_Id = "Daily_Stimulis_Notif";
            CharSequence name = "My Daily Stimulis";
            String description = "The Daily Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    Notified_Channel_Id,
                    name,
                    importance
            );
            channel.setDescription(description);

            NotificationManager notif = ctx.getSystemService(
                    NotificationManager.class
            );
            notif.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    // Display the notif to user
    void Run_Notif() {
        create_channel();
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                ctx,
                Notified_Channel_Id
        )
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Daily Game")
                .setContentText("Venez pour votre jeu quotidien")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                ctx
        );
        notificationManager.notify(1, builder.build());
    }
}
