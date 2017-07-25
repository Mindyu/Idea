package com.example.idea;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import static com.example.idea.CameraActivity.URL_CON;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        设置手机通知栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.btn_login);
        Button register = (Button) findViewById(R.id.btn_register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionDetector cd = new ConnectionDetector(MainActivity.this);
                if (cd.isConnectingToInternet()) {
                    if ( !cd.checkURL(URL_CON) ){
                        MainActivity.this.finish();
                    }else {
//                        Log.d("123456","123456");
                    }
                }else {
                    MainActivity.this.finish();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Intent intentLogin = new Intent(MainActivity.this, Login.class);
                startActivity(intentLogin);
                break;
            case R.id.btn_register:
                Intent intentRegister = new Intent(MainActivity.this, Register.class);
                startActivity(intentRegister);
                break;
        }
    }
}
