package com.example.idea;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class CreateInfo extends Activity implements View.OnClickListener {

    EditText etNickName;
    EditText etPassWord;
    Button btnConfirm;

    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
//        去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_info);

        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectId");

        Toast.makeText(this, objectId, Toast.LENGTH_SHORT).show();

        etNickName = (EditText) findViewById(R.id.edit_nickName);
        etPassWord = (EditText) findViewById(R.id.edit_passWord);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        Bmob.initialize(this, "b5c45e4964f247ffe8eea309f0990414");

        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                UserInfo userInfo = new UserInfo();
                userInfo.setUserName(etNickName.getText().toString());
                userInfo.setPassWord(etPassWord.getText().toString());
                userInfo.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent(CreateInfo.this, MainPage.class);
                            startActivity(intent);
                            this.onFinish();
                        } else {
                            Toast.makeText(CreateInfo.this, "请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
