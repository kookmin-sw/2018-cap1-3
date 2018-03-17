package kr.seon.alarm;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Calendar Time;

    private Intent intent;
    private PendingIntent ServicePending;
    private AlarmManager alarmManager;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 mm분 ss초");

    TextView textView;

    DatePickerDialog.OnDateSetListener eDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int mothOfYear, int dayOfMonth) {
            Time.set(Calendar.YEAR, year);
            Time.set(Calendar.MONTH, mothOfYear);
            Time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }
    };
        private TimePickerDialog.OnTimeSetListener sTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                Log.d("check","1 = " + hourOfDay);
                Time.set(Calendar.MINUTE, minute);
                Log.d("check", "2 = " + minute);
                Time.set(Calendar.SECOND, 0);

                updateLabel();

            }
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Time = Calendar.getInstance();

        Button.OnClickListener bClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.setAlarm:

                        setAlarm();

                        break;
                    case R.id.removeAlarm:

                        removeAlarm();

                        break;
                    case R.id.button:
                        new TimePickerDialog(MainActivity.this, sTimeSetListener, Time.get(Calendar.HOUR_OF_DAY), Time.get(Calendar.MINUTE),false).show();

                        new DatePickerDialog(MainActivity.this, eDateSetListener, Time.get(Calendar.YEAR),Time.get(Calendar.MONTH), Time.get(Calendar.DAY_OF_MONTH)).show();

                        break;
                    case R.id.repeatAlarm:

                        setRepeatAlarm();

                        break;
                }
            }
        };

        findViewById(R.id.setAlarm).setOnClickListener(bClickListener);
        findViewById(R.id.removeAlarm).setOnClickListener(bClickListener);
        findViewById(R.id.button).setOnClickListener(bClickListener);
        findViewById(R.id.repeatAlarm).setOnClickListener(bClickListener);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        textView = (TextView)findViewById(R.id.textView);

        updateLabel();
    }
    private void updateLabel() {
        textView.setText(simpleDateFormat.format(Time.getTime()));
    }


    void setAlarm() {
        //intent = new Intent(this, AlarmReceiver.class);
        intent = new Intent("AlarmReceiver");
        //PendingIntent.getBroadcast(Context context, int requestCod, Intent intent, int flag);

        ServicePending = PendingIntent.getBroadcast(MainActivity.this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), ServicePending);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), 1000, ServicePending);

        Toast.makeText(getBaseContext(), "알람 설정" + Time.getTime(), Toast.LENGTH_SHORT).show();


    }
    void removeAlarm() {

        intent = new Intent("AlarmReceiver");
        //PendingIntent.getBroadcast(Context context, int requestCod, Intent intent, int flag);

        ServicePending = PendingIntent.getBroadcast(MainActivity.this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Toast.makeText(getBaseContext(), "알람 해제" + Time.getTime(), Toast.LENGTH_SHORT).show();

        alarmManager.cancel(ServicePending);
    }
    void setRepeatAlarm() {
        //intent = new Intent(this, AlarmReceiver.class);
        intent = new Intent("AlarmReceiver");
        //PendingIntent.getBroadcast(Context context, int requestCod, Intent intent, int flag);

        ServicePending = PendingIntent.getBroadcast(MainActivity.this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("ServicePending :",""+ ServicePending.toString());

        //alarmManager.set(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), ServicePending);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), 20000, ServicePending);

        Toast.makeText(getBaseContext(), "알람 설정" + Time.getTime(), Toast.LENGTH_SHORT).show();

    }
}
