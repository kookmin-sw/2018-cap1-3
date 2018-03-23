package kr.seon.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
    private final String TAG = "Alarm";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent newIntent = new Intent(context, AlarmNotification.class);
        Alarm alarm = new Alarm(context);

        alarm.fromIntent(intent);
        alarm.toIntent(newIntent);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Log.i(TAG, "AlarmReceiver.onReceive('" + alarm.getTitle() + "')");

        context.startActivity(newIntent);
    }
}