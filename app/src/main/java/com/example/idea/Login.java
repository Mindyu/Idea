package com.example.idea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText etPhoneNumber;
    EditText etPassWord;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                BmobQuery<UserInfo> queryPhoneNumber = new BmobQuery<>();
                queryPhoneNumber.addWhereEqualTo("phoneNumber", phoneNumber);
                queryPhoneNumber.findObjects(new FindListener<UserInfo>() {
                    @Override
                    public void done(List<UserInfo> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                Toast.makeText(Login.this, "手机号尚未注册", Toast.LENGTH_SHORT).show();
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
