package kr.seon.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditAlarm extends Activity {

    private static String TAG = "Server_Test";

    private EditText mTitle;
    private CheckBox mAlarmEnabled;

    private CheckBox mVoiceChoice_inna;//유인나
    private TextView mTextViewResult; //서버결과창

    private Spinner mOccurence;
    private Button mDateButton;
    private Button mTimeButton;

    private Alarm mAlarm;
    private DateTime mDateTime;

    private GregorianCalendar mCalendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    static final int DAYS_DIALOG_ID = 2;
    //서버로 보내기위한 String 변수
    public String inna;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_edit_alarm);

        mTitle = (EditText) findViewById(R.id.title); //목소리로 바꿀 text내용
        mTextViewResult = (TextView)findViewById(R.id.textView_result);//서버 결과 창
        mAlarmEnabled = (CheckBox) findViewById(R.id.alarm_checkbox);
        mVoiceChoice_inna = (CheckBox) findViewById(R.id.voice_checkbox_inna);//유인나
        mOccurence = (Spinner) findViewById(R.id.occurence_spinner);
        mDateButton = (Button) findViewById(R.id.date_button);
        mTimeButton = (Button) findViewById(R.id.time_button);

        mAlarm = new Alarm(this);
        mAlarm.fromIntent(getIntent());

        mDateTime = new DateTime(this);

        mTitle.setText(mAlarm.getTitle());
        mTitle.addTextChangedListener(mTitleChangedListener);

        mOccurence.setSelection(mAlarm.getOccurence());
        mOccurence.setOnItemSelectedListener(mOccurenceSelectedListener);

        mAlarmEnabled.setChecked(mAlarm.getEnabled());
        mAlarmEnabled.setOnCheckedChangeListener(mAlarmEnabledChangeListener);

        //유인나 목소리 선택
        mVoiceChoice_inna.setChecked(mAlarm.getEnabled());
        mVoiceChoice_inna.setOnCheckedChangeListener(mVoiceChoice_innaChangeListener);

        mCalendar = new GregorianCalendar();
        mCalendar.setTimeInMillis(mAlarm.getDate());
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        updateButtons();
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
        //유인나 목소리 선택됨을 서버로 보내기 코드
        inna = "유인나 목소리";
        String Convert_Text = mTitle.getText().toString();

        InsertData task = new InsertData();
        task.execute(inna, Convert_Text);

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
            mAlarm.setTitle(mTitle.getText().toString());
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
    //유인나 목소리 선택
    private CompoundButton.OnCheckedChangeListener mVoiceChoice_innaChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            inna = "inna";
        }
    };

    private void updateButtons() {
        if (Alarm.ONCE == mAlarm.getOccurence())
            mDateButton.setText(mDateTime.formatDate(mAlarm));
        else if (Alarm.WEEKLY == mAlarm.getOccurence())
            mDateButton.setText(mDateTime.formatDays(mAlarm));
        mTimeButton.setText(mDateTime.formatTime(mAlarm));
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
    //서버로 데이터 전송 코드
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(EditAlarm.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String)params[0];
            String address = (String)params[1];

            String serverURL = "http://203.246.112.106/insert.php";
            String postParameters = "voice=" + name + "&message=" + address;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}