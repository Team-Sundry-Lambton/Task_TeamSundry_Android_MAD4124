package mad4124.team_sundry.task.utils;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import mad4124.team_sundry.task.R;

public class NotificationHelper {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    /*
    The way to use
        Calendar calendar = Calendar.getInstance();
        //10 is for how many seconds from now you want to schedule also you can create a custom instance of Callender to set on exact time
        calendar.add(Calendar.SECOND, 20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context context = getActivity();
            NotificationHelper.scheduleNotification(NotificationHelper.getNotification("5 second delay", context), calendar, context);
        }
    **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    static public void scheduleNotification(Notification notification, Calendar calendar, Context context) {
        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(context, "Scheduled ", Toast.LENGTH_LONG).show();
    }

    static public Notification getNotification(String content, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentTitle("Task Reminder")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }
}
