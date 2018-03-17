package kr.seon.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"");
        wakeLock.acquire();

        Log.d("alarm", "go");

        PendingIntent pendingIntent;

        Toast toast = Toast.makeText(context, "알람 울림", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();

        wakeLock.release();


        try {
            intent = new Intent(context, removeActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d("ServicePending++ :",""+pendingIntent.toString());
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        notification();
    }

    void notification() {
        Intent intent = new Intent();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(android.R.drawable.ic_menu_gallery).setLargeIcon(bitmap).setContentTitle("알람").setContentText("알람 ㄸㄹㄸㄹ~").setAutoCancel(true).setSound(soundUri).setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
