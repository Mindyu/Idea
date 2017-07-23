package com.example.idea;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Login extends Activity implements View.OnClickListener{

    EditText etPhoneNumber;
    EditText etPassWord;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        Bmob.initialize(this, "b5c45e4964f247ffe8eea309f0990414");

        etPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        etPassWord = (EditText) findViewById(R.id.edit_passWord);
        login = (Button) findViewById(R.id.btn_login);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String passWord = etPassWord.getText().toString();
                if (phoneNumber.length() !=11 ) {
                    Toast.makeText(Login.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passWord.length()<6||passWord.length()>15) {
                    Toast.makeText(Login.this, "请输入正确的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobQuery<UserInfo> queryPhoneNumber = new BmobQuery<>();
                queryPhoneNumber.addWhereEqualTo("phoneNumber", phoneNumber);
                queryPhoneNumber.findObjects(new FindListener<UserInfo>() {
                    @Override
                    public void done(List<UserInfo> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                Toast.makeText(Login.this, "您的手机号未注册", Toast.LENGTH_SHORT).show();
                            } else {
                                BmobQuery<UserInfo> eq1 = new BmobQuery<UserInfo>();
                                eq1.addWhereEqualTo("phoneNumber", phoneNumber);
                                BmobQuery<UserInfo> eq2 = new BmobQuery<UserInfo>();
                                eq2.addWhereEqualTo("passWord", passWord);
                                List<BmobQuery<UserInfo>> andQuery = new ArrayList<BmobQuery<UserInfo>>();
                                andQuery.add(eq1);
                                andQuery.add(eq2);
                                BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
                                query.and(andQuery);
                                query.findObjects(new FindListener<UserInfo>() {
                                    @Override
                                    public void done(List<UserInfo> list, BmobException e) {
                                        if (e == null) {
                                            if (list.size() == 0) {
                                                Toast.makeText(Login.this, "密码错误", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Intent intent = new Intent(Login.this, MainPage.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                break;
        }
    }
}
