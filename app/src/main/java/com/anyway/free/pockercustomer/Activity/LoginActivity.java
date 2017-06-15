package com.anyway.free.pockercustomer.Activity;

/**
 * Created by Administrator on 2016/12/29.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.anyway.free.pockercustomer.MyApplication;
import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.SettingUtils;

public class LoginActivity extends Activity {
    private final String TAG = "LoginActivity";
    private TextView passwordErrorTextView;
    private EditText passwordEditText;
    private CheckBox passwordSaveCheckBox;
    private Button loginButton;
    private Button editButton;
    private Button exitButton;
    public static boolean isOnBackGroud = true;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        LoginActivity.isOnBackGroud = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        LoginActivity.isOnBackGroud = true;
        super.onPause();
    }

    private void initView(){
//        Log.d(TAG,"initView getIsSavePassword "+ SettingUtils.getIsSavePassword(context)+" getPassword "+ SettingUtils.getPassword(context));
        passwordErrorTextView = (TextView) findViewById(R.id.textView_password_error);
        passwordEditText = (EditText) findViewById(R.id.EditText_password);
        passwordSaveCheckBox = (CheckBox) findViewById(R.id.checkbox_password_save);
        passwordSaveCheckBox.setChecked(SettingUtils.getIsSavePassword(context));
        loginButton = (Button) findViewById(R.id.button_login);
        editButton = (Button) findViewById(R.id.button_edit);
        exitButton = (Button) findViewById(R.id.button_login_exit);
        if(SettingUtils.getIsSavePassword(context)){
            passwordEditText.setText( SettingUtils.getPassword(context));
        }
        setOnClickListener();
    }

    private void setOnClickListener(){
        passwordSaveCheckBox.setOnCheckedChangeListener(checkChangelistener);
        loginButton.setOnClickListener(buttonClickListener);
        editButton.setOnClickListener(buttonClickListener);
        exitButton.setOnClickListener(buttonClickListener);
    }

    private CompoundButton.OnCheckedChangeListener checkChangelistener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            SettingUtils.savePasswordIsSave(isChecked,context);
        }
    };

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == loginButton){
                String editPassword = passwordEditText.getText().toString();
                String curPassword = SettingUtils.getPassword(context);
                if (editPassword.equals(curPassword)){
                    startAuthActivity();
                }
                else{
                    passwordErrorTextView.setVisibility(View.VISIBLE);
                }
            }

            if (v == editButton){
                startEditPasswordActivity();
            }

            if (v == exitButton){
                LoginActivity.isOnBackGroud = true;
                System.exit(0);
            }
        }
    };

    private void startAuthActivity(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void startEditPasswordActivity(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, EditPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                System.exit(0);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
