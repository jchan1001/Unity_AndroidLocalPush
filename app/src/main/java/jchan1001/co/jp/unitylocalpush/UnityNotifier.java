package jchan1001.co.jp.unitylocalpush;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.ALARM_SERVICE;

public class UnityNotifier extends BroadcastReceiver {

    final int NOTIFICATION_DELAY = 5;
    final int NOTIFICATION_REQUEST_CODE = 1;
    final int NAVIGATION_PENDING_INTENT_CODE = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences data = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
        String title = data.getString("title", null);
        String msg = data.getString("msg", null);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // アプリが一回でも起動した場合
            if (data.getInt("isAppLunched",LocalPush.APP_NOT_LAUNCHED) == LocalPush.APP_LAUNCHED) {
                Intent i = new Intent(context.getApplicationContext(), UnityNotifier.class);
                PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), NOTIFICATION_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, NOTIFICATION_DELAY, sender);
                }
            }
        } else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            PendingIntent sender = PendingIntent.getActivity(context, NAVIGATION_PENDING_INTENT_CODE, launchIntent, 0);
            pushLocalNotification(context, "channelName", title, msg, 1, sender);
        }
    }

    // 通知クリック時にアプリ起動
    public static void pushLocalNotification (Context context, String channelName, String title, String text, int notificationId, PendingIntent intent) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Android 8.0 Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelName,
                    "プッシュ通知",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.enableLights(true);
            channel.setLightColor(Color.WHITE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelName)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(intent)
                .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        Notification noti = builder.build();
        if (manager != null && noti != null) {
            manager.notify(notificationId, noti);
        }
    }
}