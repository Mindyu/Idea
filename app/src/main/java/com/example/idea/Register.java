package com.example.idea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends Activity implements View.OnClickListener {

    private EditText etPhoneNumber;        // 电话号码
    private Button sendVerificationCode;   // 发送验证码
    private EditText etVerificationCode;   // 验证码
    private Button nextStep;               // 下一步

    private String phoneNumber;         // 电话号码
    private String verificationCode;    // 验证码

    private boolean flag = false;   // 验证码是否已发送

    private final Handler handler1 = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sendVerificationCode.setBackground(getDrawable(R.drawable.login));
            }
            sendVerificationCode.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        设置手机通知栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
//        去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        Bmob.initialize(this, "b5c45e4964f247ffe8eea309f0990414");

        init(); // 初始化控件、注册点击事件

        final Context context = Register.this;                       // context
        final String AppKey = "1e6c1a4551e93";                       // AppKey
        final String AppSecret = "cf779e82b20aa66d680596cf93be818e"; // AppSecret

        SMSSDK.initSDK(context, AppKey, AppSecret);           // 初始化 SDK 单例，可以多次调用
        EventHandler eventHandler = new EventHandler(){       // 操作回调
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);     // 注册回调接口
    }

    private void init() {
        etPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        sendVerificationCode = (Button) findViewById(R.id.btn_send_verification_code);
        etVerificationCode = (EditText) findViewById(R.id.edit_verification_code);
        nextStep = (Button) findViewById(R.id.btn_next_step);
        sendVerificationCode.setOnClickListener(this);
        nextStep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_verification_code:
                if (!TextUtils.isEmpty(etPhoneNumber.getText())) {
                    if (etPhoneNumber.getText().length() == 11) {
                        phoneNumber = etPhoneNumber.getText().toString();
                        BmobQuery<UserInfo> query = new BmobQuery<>();
                        query.addWhereEqualTo("phoneNumber", phoneNumber);
                        query.findObjects(new FindListener<UserInfo>() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void done(List<UserInfo> list, BmobException e) {
                                if (e == null) {
                                    if (list.size() == 0) {
                                        sendVerificationCode.setBackground(getDrawable(R.drawable.after));
                                        sendVerificationCode.setEnabled(false);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(60000);
                                                    handler1.sendMessage(new Message());
                                                } catch (InterruptedException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }).start();
//                                        Toast.makeText(Register.this, "可以注册", Toast.LENGTH_SHORT).show();
                                        SMSSDK.getVerificationCode("86", phoneNumber);
                                        etVerificationCode.requestFocus();
                                        flag = true;
                                    } else {
                                        Toast.makeText(Register.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Register.this, "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(this, "请输入完整的电话号码", Toast.LENGTH_SHORT).show();
                        etPhoneNumber.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                }
                break;

            case R.id.btn_next_step:
                if (!TextUtils.isEmpty(etVerificationCode.getText())) {
                    if (etVerificationCode.getText().length() == 4) {
                        verificationCode = etVerificationCode.getText().toString();
                        SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
                    } else {
                        Toast.makeText(this, "请输入完整的验证码", Toast.LENGTH_SHORT).show();
                        etVerificationCode.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    etVerificationCode.requestFocus();
                }
                break;

            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                // 如果操作成功
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    // 校验验证码，返回校验的手机和国家代码
                    UserInfo userInfo = new UserInfo();
                    userInfo.setPhoneNumber(etPhoneNumber.getText().toString());
                    userInfo.setUserName("");
                    userInfo.setPassWord("");
                    userInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                Toast.makeText(Register.this, "注册成功" + objectId, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, CreateInfo.class);
                                intent.putExtra("objectId", objectId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Register.this, "注册失败" + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    // 获取验证码成功，true为智能验证，false为普通下发短信
                    Toast.makeText(Register.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    // 返回支持发送验证码的国家列表
                }
            } else {
                // 如果操作失败
                if (!flag) {
                    Toast.makeText(Register.this, "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                    etPhoneNumber.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    Toast.makeText(Register.this, "验证码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();  // 注销回调接口
    }
}
