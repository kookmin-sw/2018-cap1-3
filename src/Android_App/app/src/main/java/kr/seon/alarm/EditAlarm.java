package kr.seon.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


import butterknife.OnClick;

import static java.security.AccessController.getContext;

public class EditAlarm extends Activity {

    private static String TAG = "Server_Test";

    private EditText mTitle;
    private String voice_model;


    private TextView mVoiceType; //?쒕쾭寃곌낵李?
    private TextView mAlarmText;


    private Button mDateButton;
    private Button mTimeButton;
    private ToggleButton Day_0, Day_1, Day_2, Day_3, Day_4, Day_5, Day_6;

    private Alarm mAlarm;
    private DateTime mDateTime;

    private GregorianCalendar mCalendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    Spinner Voice_spinner;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    static final int DAYS_DIALOG_ID = 2;
    //?쒕쾭濡?蹂대궡湲곗쐞??String 蹂??
    public String inna;

    //  TCP?곌껐 愿??
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 35357;
    private final String ip = "13.125.237.51";
    private MyHandler myHandler;
    private MyThread myThread;



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_alarm);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mTitle = (EditText) findViewById(R.id.title); //紐⑹냼由щ줈 諛붽? text?댁슜

        mDateButton = (Button) findViewById(R.id.date_button);
        mTimeButton = (Button) findViewById(R.id.time_button);
        mAlarmText = (TextView) findViewById(R.id.alarmtext);
        mVoiceType = (TextView) findViewById(R.id.voicetype);

//        Typeface typeFace = Typeface.createFromAsset(getAssets(), "SDMiSaeng.ttf");  //asset > fonts ?대뜑 ???고듃?뚯씪 ?곸슜
//        mDateButton.setTypeface(typeFace);
//        mTimeButton.setTypeface(typeFace);
//        mAlarmText.setTypeface(typeFace);
//        mVoiceType.setTypeface(typeFace);


        mAlarm = new Alarm(this);
        mAlarm.fromIntent(getIntent());

        mDateTime = new DateTime(this);

        mTitle.setText(mAlarm.getTitle());
        mTitle.addTextChangedListener(mTitleChangedListener);





        mCalendar = new GregorianCalendar();
        mCalendar.setTimeInMillis(mAlarm.getDate());
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);



        Voice_spinner = (Spinner)findViewById(R.id.spinner);



        //?ㅽ뵾???대뙌???ㅼ젙
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.voice,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Voice_spinner.setAdapter(adapter);

        //?ㅽ뵾???대깽??諛쒖깮
        Voice_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //?꾩씠???좏깮??
                mAlarm.setVoice_model(Voice_spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //?꾩씠???좏깮?덊븷??
            }
        });

        updateButtons();
    }


    class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // InputStream??媛믪쓣 ?쎌뼱???data?????
                    String data = socketIn.readLine();
                    // Message 媛앹껜瑜??앹꽦, ?몃뱾?ъ뿉 ?뺣낫瑜?蹂대궪 ????硫붿꽭吏 媛앹껜瑜??댁슜
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (DATE_DIALOG_ID == id)
            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        else if (TIME_DIALOG_ID == id)
            return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, mDateTime.is24hClock());
        else if (DAYS_DIALOG_ID == id)
            return DaysPickerDialog();
        else
            return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (DATE_DIALOG_ID == id)
            ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
        else if (TIME_DIALOG_ID == id)
            ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
    }

    public void onDateClick(View view) {
        if (Alarm.ONCE == mAlarm.getOccurence())
            showDialog(DATE_DIALOG_ID);
        else if (Alarm.WEEKLY == mAlarm.getOccurence())
            showDialog(DAYS_DIALOG_ID);
    }

    public void onTimeClick(View view) {
        showDialog(TIME_DIALOG_ID);
    }

    public void onDoneClick(View view) {
        Intent intent = new Intent();

        mAlarm.toIntent(intent);
        setResult(RESULT_OK, intent);

        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //?좏깮??紐⑹냼由ъ? ?띿뒪?몃? ?쒕쾭濡?蹂대궡湲?肄붾뱶


        String ret = mAlarm.getTitle().toString();
        Log.d("test",ret);
        socketOut.println(ret);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            mCalendar = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute);
            mAlarm.setDate(mCalendar.getTimeInMillis());

            updateButtons();
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;

            mCalendar = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute);
            mAlarm.setDate(mCalendar.getTimeInMillis());

            updateButtons();
        }
    };

    private TextWatcher mTitleChangedListener = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            mAlarm.setTitle(Voice_spinner.getSelectedItem().toString() +"_"+mTitle.getText().toString());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private AdapterView.OnItemSelectedListener mOccurenceSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            mAlarm.setOccurence(position);
            updateButtons();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private CompoundButton.OnCheckedChangeListener mAlarmEnabledChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mAlarm.setEnabled(isChecked);
        }
    };
    //?좎씤??紐⑹냼由??좏깮
    private CompoundButton.OnCheckedChangeListener mVoiceChoice_innaChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            inna = "inna";
        }
    };

    private void updateButtons() {
        if (Alarm.ONCE == mAlarm.getOccurence()) {
            String temp = mDateTime.formatDate(mAlarm);
            String[] tmp = temp.split("\\s");
            mDateButton.setText(tmp[3] + " "+ tmp[1] + " "+ tmp[2] + " "+ tmp[0]);
        }
        else if (Alarm.WEEKLY == mAlarm.getOccurence()) {
            String temp = mDateTime.formatDays(mAlarm);
            String[] tmp = temp.split("\\s");
            mDateButton.setText(tmp[3] + " "+ tmp[1] + " "+ tmp[2] + " "+ tmp[0]);

        }
        String temp = mDateTime.formatTime(mAlarm);
        String[] tmp = temp.split("\\s");
        mTimeButton.setText(tmp[1] + " "+ tmp[0]);
    }

    private Dialog DaysPickerDialog() {
        AlertDialog.Builder builder;
        final boolean[] days = mDateTime.getDays(mAlarm);
        final String[] names = mDateTime.getFullDayNames();

        builder = new AlertDialog.Builder(this);

        builder.setTitle("Week days");

        builder.setMultiChoiceItems(names, days, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mDateTime.setDays(mAlarm, days);
                updateButtons();
            }
        });

        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }



}