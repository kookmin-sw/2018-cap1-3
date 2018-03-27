package kr.seon.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
//import android.view.ContextMenu;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnTouch;


public class MainActivity extends Activity {

    private final String TAG = "Alarm";

    private ListView mAlarmList;
    private AlarmListAdapter mAlarmListAdapter;
    private Alarm mCurrentAlarm;

    private final int NEW_ALARM_ACTIVITY = 0;
    private final int EDIT_ALARM_ACTIVITY = 1;
    private final int PREFERENCES_ACTIVITY = 2;

    @Bind(R.id.on_off_switch) SwitchCompat mSwitch;

//    private final int CONTEXT_MENU_EDIT = 0;
//    private final int CONTEXT_MENU_DELETE = 1;
//    private final int CONTEXT_MENU_DUPLICATE = 2;

    public int del_pos;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlarmList = (ListView) findViewById(R.id.alarm_list);

        mAlarmListAdapter = new AlarmListAdapter(this);
        mAlarmList.setAdapter(mAlarmListAdapter);
        mAlarmList.setOnItemLongClickListener(mListOnItemLongClickListener);
        mAlarmList.setOnItemClickListener(mListOnItemClickListener);
        registerForContextMenu(mAlarmList);

        mCurrentAlarm = null;


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Alarm.onDestroy()");
        mAlarmListAdapter.save();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Alarm.onResume()");
        mAlarmListAdapter.updateAlarms();
    }

    public void onAddAlarmClick(View view) {
        Intent intent = new Intent(getBaseContext(), EditAlarm.class);

        mCurrentAlarm = new Alarm(this);
        mCurrentAlarm.toIntent(intent);

        MainActivity.this.startActivityForResult(intent, NEW_ALARM_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ALARM_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                mCurrentAlarm.fromIntent(data);
                mAlarmListAdapter.add(mCurrentAlarm);
            }
            mCurrentAlarm = null;
        } else if (requestCode == EDIT_ALARM_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                mCurrentAlarm.fromIntent(data);
                mAlarmListAdapter.update(mCurrentAlarm);
            }
            mCurrentAlarm = null;
        } else if (requestCode == PREFERENCES_ACTIVITY) {
            mAlarmListAdapter.onSettingsUpdated();
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (R.id.menu_settings == item.getItemId()) {
//            Intent intent = new Intent(getBaseContext(), Preferences.class);
//            startActivityForResult(intent, PREFERENCES_ACTIVITY);
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        if (v.getId() == R.id.alarm_list) {
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//
//            menu.setHeaderTitle(mAlarmListAdapter.getItem(info.position).getTitle());
//            menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
//            menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
//            menu.add(Menu.NONE, CONTEXT_MENU_DUPLICATE, Menu.NONE, "Duplicate");
//        }
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int index = item.getItemId();
//
//        if (index == CONTEXT_MENU_EDIT) {
//            Intent intent = new Intent(getBaseContext(), EditAlarm.class);
//
//            mCurrentAlarm = mAlarmListAdapter.getItem(info.position);
//            mCurrentAlarm.toIntent(intent);
//            startActivityForResult(intent, EDIT_ALARM_ACTIVITY);
//        } else if (index == CONTEXT_MENU_DELETE) {
//            mAlarmListAdapter.delete(info.position);
//        } else if (index == CONTEXT_MENU_DUPLICATE) {
//            Alarm alarm = mAlarmListAdapter.getItem(info.position);
//            Alarm newAlarm = new Alarm(this);
//            Intent intent = new Intent();
//
//            alarm.toIntent(intent);
//            newAlarm.fromIntent(intent);
//            newAlarm.setTitle(alarm.getTitle() + " (copy)");
//            mAlarmListAdapter.add(newAlarm);
//        }
//
//        return true;
//    }

    //toggle버튼 구현
    @OnTouch(R.id.on_off_switch)
    boolean slide(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            mSwitch.setPressed(true); // needed so the OnCheckedChange event calls through
        }
        return false; // proceed as usual
    }

    @OnCheckedChanged(R.id.on_off_switch)
    void toggle(boolean checked) {

        if (mSwitch.isPressed()) {
            if(mSwitch.isChecked()==true){
                mCurrentAlarm.setEnabled(true);
            }
            else if (mSwitch.isChecked()==false) {
                mCurrentAlarm.setEnabled(false);
            }
        }
    }

    private AdapterView.OnItemLongClickListener mListOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            new AlertDialog.Builder(MainActivity.this).setTitle("삭제").setMessage("삭제하시겠습니까")
                    .setPositiveButton("예", mDelClick).setNegativeButton("아니오", mDelClick).show();
            return false;
        }
    };

    DialogInterface.OnClickListener mDelClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                mAlarmListAdapter.delete(del_pos);
            } else {
                return;
            }
        }
    };

    private AdapterView.OnItemClickListener mListOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), EditAlarm.class);

            mCurrentAlarm = mAlarmListAdapter.getItem(position);
            mCurrentAlarm.toIntent(intent);
            MainActivity.this.startActivityForResult(intent, EDIT_ALARM_ACTIVITY);
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
