package com.anyway.free.pockercustomer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.anyway.free.pockercustomer.Model.SettingModel.ItemType;
import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.Constants;
import com.anyway.free.pockercustomer.Utils.SettingUtils;

public class PockerSettingActivity extends Activity {

    private Spinner spinner_gameType;
    private Spinner spinner_peopleNum;
    private Spinner spinner_broadcastType;
    private Spinner spinner_playType;
    private Spinner spinner_ev;

    private Button button_startPreview;
    private Button button_exit;

    private int gameTypeSelectedPosition;
    private int peopleNumSelectedPosition;
    private int broadcastTypeSelectedPosition;
    private int playTypeSelectedPosition;
    private int evSelectedPosition;

    private String gameTypeContentSelected;
    private String peopleNumContentSelected;
    private String broadcastTypeContentSelected;
    private String playTypeContentSelected;
    private String evContentSelected;

//    private WifiManager wifiManager;
//    private Button testCloseWifiBtn;
//    private Button testOpenWifiBtn;

    private boolean isTry = false;

    private final int TRY_INTERVAL = 5*60*1000; //试用5分钟
    private static final int START_AUTH = 0x13;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case START_AUTH:
                    startAuthActivity();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        parseIntent();
        setContentView(R.layout.activity_pockersetting);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(START_AUTH);
    }

    private void parseIntent(){
        Intent intent = getIntent();
        isTry = intent.getBooleanExtra(Constants.INTENT_PARAM_TRY, false);
        if (isTry){
            startTryTimer();
        }
    }

    private void startTryTimer(){
        mHandler.sendEmptyMessageDelayed(START_AUTH, TRY_INTERVAL);
    }

    private void startAuthActivity(){
        Intent intent = new Intent();
        intent.setClass(PockerSettingActivity.this, AuthActivity.class);
        intent.putExtra(Constants.INTENT_PARAM_TRY, isTry);
        startActivity(intent);
        finish();
    }

    private void initView(){
        readPreference();
        spinner_gameType = (Spinner) findViewById(R.id.spinner_gameType);
        spinner_peopleNum = (Spinner) findViewById(R.id.spinner_peopleNum);
        spinner_broadcastType = (Spinner) findViewById(R.id.spinner_broadcastType);
        spinner_playType = (Spinner) findViewById(R.id.spinner_playType);
        spinner_ev = (Spinner) findViewById(R.id.spinner_ev);

        button_startPreview = (Button) findViewById(R.id.button_start);
        button_exit = (Button) findViewById(R.id.button_exit);

//        testOpenWifiBtn = (Button) findViewById(R.id.server_btn1);
//        testCloseWifiBtn = (Button) findViewById(R.id.server_btn2);
//
//        testOpenWifiBtn.setOnClickListener(buttonClickListener);
//        testCloseWifiBtn.setOnClickListener(buttonClickListener);

        setDefaultSelection();
        setSpinnerSelectListener();
        setOnClickListener();
    }

    private void setOnClickListener(){
        button_startPreview.setOnClickListener(buttonClickListener);
        button_exit.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == button_startPreview){
                startViewActivity();
            }

            if (v == button_exit){
                System.exit(0);
            }

//            if (v == testOpenWifiBtn){
//                Log.d("NetworkReceiver", "--testOpenWifiBtn");
//                wifiManager.setWifiEnabled(true);
//            }
//
//            if (v == testCloseWifiBtn){
//                Log.d("NetworkReceiver", "--testCloseWifiBtn");
//                wifiManager.setWifiEnabled(false);
//            }
        }
    };
    private void readPreference(){
        gameTypeSelectedPosition = SettingUtils.getGameType(this);
        peopleNumSelectedPosition = SettingUtils.getPeopleNum(this);
        broadcastTypeSelectedPosition = SettingUtils.getBroadcatType(this);
        playTypeSelectedPosition = SettingUtils.getPlayType(this);
        evSelectedPosition = SettingUtils.getEV(this);

        gameTypeContentSelected = getStringItemSelected(ItemType.GAMETYPE, gameTypeSelectedPosition);
        peopleNumContentSelected = getStringItemSelected(ItemType.PEOPLENUM, peopleNumSelectedPosition);
        broadcastTypeContentSelected = getStringItemSelected(ItemType.BROADCASTTYPE, broadcastTypeSelectedPosition);
        playTypeContentSelected = getStringItemSelected(ItemType.PLAYTYPE, playTypeSelectedPosition);
        evContentSelected = getStringItemSelected(ItemType.EV,evSelectedPosition);
    }

    private void setDefaultSelection(){
        spinner_gameType.setSelection(gameTypeSelectedPosition, true);
        spinner_peopleNum.setSelection(peopleNumSelectedPosition, true);
        spinner_broadcastType.setSelection(broadcastTypeSelectedPosition, true);
        spinner_playType.setSelection(playTypeSelectedPosition, true);
        spinner_ev.setSelection(evSelectedPosition,true);
    }

    private void setSpinnerSelectListener(){
        spinner_gameType.setOnItemSelectedListener(gameTypeSpinnerSelectedListener);
        spinner_peopleNum.setOnItemSelectedListener(peopleNumSpinnerSelectedListener);
        spinner_broadcastType.setOnItemSelectedListener(broadcastTypeSpinnerSelectedListener);
        spinner_playType.setOnItemSelectedListener(playTypeSpinnerSelectedListener);
        spinner_ev.setOnItemSelectedListener(evSpinnerSelectedListener);
    }

    private String getStringItemSelected(ItemType itemType,int pos){
        String[] settings;
        switch (itemType){
            case GAMETYPE:
                settings = getResources().getStringArray(R.array.gameTypes);
                return settings[pos];
            case PEOPLENUM:
                settings = getResources().getStringArray(R.array.peopleNums);
                return settings[pos];
            case BROADCASTTYPE:
                settings = getResources().getStringArray(R.array.broadcastTypes);
                return settings[pos];
            case PLAYTYPE:
                settings = getResources().getStringArray(R.array.playTypes);
                return settings[pos];
            case EV:
                settings = getResources().getStringArray(R.array.evs);
                return settings[pos];
        }
        return "";
    }

    private void onItemSelect(ItemType itemType,int pos){
        String[] settings;
        switch (itemType){
            case GAMETYPE:
                gameTypeContentSelected = getStringItemSelected(ItemType.GAMETYPE,pos);
                gameTypeSelectedPosition = pos;
                SettingUtils.saveGameType(pos,this);
                Toast.makeText(PockerSettingActivity.this, "你选择的是:" + gameTypeContentSelected, Toast.LENGTH_LONG).show();
                break;
            case PEOPLENUM:
                peopleNumContentSelected = getStringItemSelected(ItemType.PEOPLENUM,pos);
                peopleNumSelectedPosition = pos;
                SettingUtils.savePeopleNum(pos, this);
                Toast.makeText(PockerSettingActivity.this, "你选择的是:" + peopleNumContentSelected, Toast.LENGTH_LONG).show();
                break;
            case BROADCASTTYPE:
                broadcastTypeContentSelected = getStringItemSelected(ItemType.BROADCASTTYPE,pos);
                broadcastTypeSelectedPosition = pos;
                SettingUtils.saveBroadcatType(pos, this);
                Toast.makeText(PockerSettingActivity.this, "你选择的是:" + broadcastTypeContentSelected, Toast.LENGTH_LONG).show();
                break;
            case PLAYTYPE:
                playTypeContentSelected = getStringItemSelected(ItemType.PLAYTYPE,pos);
                playTypeSelectedPosition = pos;
                SettingUtils.savePlayType(pos, this);
                Toast.makeText(PockerSettingActivity.this, "你选择的是:" +playTypeContentSelected, Toast.LENGTH_LONG).show();
                break;
            case EV:
                evContentSelected = getStringItemSelected(ItemType.EV,pos);
                evSelectedPosition = pos;
                SettingUtils.saveEV(pos, this);
                Toast.makeText(PockerSettingActivity.this, "你选择的是:" +evContentSelected, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private AdapterView.OnItemSelectedListener gameTypeSpinnerSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            onItemSelect(ItemType.GAMETYPE, pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    };

    private AdapterView.OnItemSelectedListener peopleNumSpinnerSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            onItemSelect(ItemType.PEOPLENUM, pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    };

    private AdapterView.OnItemSelectedListener broadcastTypeSpinnerSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            onItemSelect(ItemType.BROADCASTTYPE, pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    };

    private AdapterView.OnItemSelectedListener playTypeSpinnerSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            onItemSelect(ItemType.PLAYTYPE, pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    };

    private AdapterView.OnItemSelectedListener evSpinnerSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {

            onItemSelect(ItemType.EV, pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    };

    private void startViewActivity(){
        Intent intent = new Intent();
//        intent.setClass(PockerSettingActivity.this, ServerActivity.class);
        intent.setClass(PockerSettingActivity.this, RtspViewerActivity.class);
        intent.putExtra(Constants.INTENT_PARAM_TRY, isTry);
        startActivity(intent);
        //            finish();
    }
}
