package com.anyway.free.pockercustomer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anyway.free.pockercustomer.R;
import com.anyway.free.pockercustomer.Utils.SettingUtils;

/**
 * Created by Administrator on 2016/12/30.
 */
public class EditPasswordActivity extends Activity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newAgainPasswordEditText;
    private Button confirmButton;
    private Button exitButton;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_edit_password);
        context = this;
        initView();
    }

    private void initView(){
        oldPasswordEditText = (EditText) findViewById(R.id.EditText_old_password);
        newPasswordEditText = (EditText) findViewById(R.id.EditText_new_password);
        newAgainPasswordEditText = (EditText) findViewById(R.id.EditText_new_password_again);
        confirmButton = (Button) findViewById(R.id.button_password_confirm);
        exitButton = (Button) findViewById(R.id.button_password_exit);
        setOnClickListener();
    }

    private void setOnClickListener(){
        confirmButton.setOnClickListener(buttonClickListener);
        exitButton.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == confirmButton){
                confirmPassword();
            }

            if (v == exitButton){
                System.exit(0);
            }
        }
    };

    private void confirmPassword(){
        String oldEditPassword = oldPasswordEditText.getText().toString();
        String newEditPassword = newPasswordEditText.getText().toString();
        String newEditAgainPassword = newAgainPasswordEditText.getText().toString();

        String currentPassword = SettingUtils.getPassword(context);

        if (currentPassword.equals(oldEditPassword)){
            if (newEditPassword.equals("")||newEditAgainPassword.equals("")){
                Toast.makeText(context,"密码不能为空！",Toast.LENGTH_SHORT).show();
            }
            else if (newEditPassword.equals(newEditAgainPassword)){
                Toast.makeText(context,"密码修改成功！",Toast.LENGTH_SHORT).show();
                SettingUtils.savePassword(newEditPassword,context);
                finish();
            }
            else{
                Toast.makeText(context,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(context,"请输入正确的旧密码！",Toast.LENGTH_SHORT).show();
        }
    }
}
