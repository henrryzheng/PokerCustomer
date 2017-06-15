package com.anyway.free.pockercustomer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.Constants;
import com.anyway.free.pockercustomer.Utils.SettingUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/12/30.
 */
public class AuthActivity extends Activity {
    private TextView imeiTextView;
    private TextView descTextView;
    private EditText authEditText;
    private Button tryButton;
    private Button authButton;
    private Context context;
    private final int TRY_COUNT_TOTAL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        todoAuth();
    }

    private void todoAuth(){
        if (isAuthSuccess()){
            startLoginActivity(false);
        }
        else{
            setContentView(R.layout.activity_auth);
            initView();
        }
    }

    private boolean isAuthSuccess(){
        return SettingUtils.getAuthSuccess(context);
    }

    private int getTryCount(){
        return SettingUtils.getTryCount(context);
    }

    private void initView(){
        imeiTextView = (TextView) findViewById(R.id.textView_imei);
        authEditText = (EditText) findViewById(R.id.EditText_auth);
        tryButton = (Button) findViewById(R.id.button_try);
        authButton = (Button) findViewById(R.id.button_auth);
        descTextView = (TextView) findViewById(R.id.textView_desc);
        setOnClickListener();
        if (getTryCount() < 0){
            tryButton.setText("继续试用(5分钟)");
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) authButton.getLayoutParams();
            layoutParams.leftMargin= 170;
            authButton.setLayoutParams(layoutParams);
//            tryButton.setEnabled(false);
            descTextView.setVisibility(View.VISIBLE);
        }
        showImeiCode();
    }

    private void showImeiCode(){
        String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        if (imei != null){
            imeiTextView.setText("机器编码:" + convertMD510(imei));
        }
        else{
            imeiTextView.setText("机器编码:");
        }
    }

    private void setOnClickListener(){
        tryButton.setOnClickListener(buttonClickListener);
        authButton.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tryButton){
//                if (getTryCount() > -TRY_COUNT_TOTAL){
                    SettingUtils.saveTryCount(getTryCount() - 1,context);
                    startLoginActivity(true);
//                }
            }

            if (v == authButton){
                if(checkKey()){
                    Toast.makeText(context,"验证成功!",Toast.LENGTH_SHORT).show();
                    SettingUtils.saveAuthSuccess(true,context);
                    startLoginActivity(false);
                }else {
                    SettingUtils.saveAuthSuccess(false,context);
                    Toast.makeText(context,"请输入有效的验证码!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void startLoginActivity(boolean isTry){
        Intent intent = new Intent();
        intent.setClass(AuthActivity.this, PockerSettingActivity.class);
        intent.putExtra(Constants.INTENT_PARAM_TRY,isTry);
        startActivity(intent);
        finish();
    }


    private boolean checkKey(){
        String imeiText = imeiTextView.getText().toString();
        String[] keys = imeiText.split(":");
        if (keys.length <= 1){
            return "sss".equals(authEditText.getText().toString());
        }
        return (keys[1] + "sss").equals(authEditText.getText().toString());
    }



    public static String convertMD5(String plainText) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static String convertMD510(String plainText) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (md != null){
                md.update(plainText.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                result= buf.toString().substring(8, 17);
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
