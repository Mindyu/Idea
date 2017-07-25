package com.example.idea;

/**
 * Created by 杨 陈强 on 2017/7/22.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idea.util.FTP;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;


public class CameraActivity extends Activity {

    final static String URL_CON = "http://118.89.140.80/ruishijie.txt";
    int result=0;
    /**
     * Called when the activity is first created.
     */
    Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            TextView tv = (TextView) findViewById(R.id.text);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
            switch (msg.what) {
                case 0:
                    tv.setText("上传成功。");
                    pb.setVisibility(ProgressBar.GONE);
                    break;
                case 1:
                    tv.setText("无可用网络。");
                    pb.setVisibility(ProgressBar.GONE);
                    break;
                case 2:
                    tv.setText("找不到服务器地址");
                    pb.setVisibility(ProgressBar.GONE);
                    break;
                default:
                    break;
            }

            Intent intent = new Intent(CameraActivity.this, MainPage.class);
            startActivity(intent);
        }

    };


    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
            if (msg.what == 0x111) {
                pb.setProgress(result);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("test", "onActivityResult() requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);
        if (null != data) {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            } else {
                Cursor c = getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
                if (c != null && c.moveToFirst()) {
                    String filPath = c.getString(0);
                    new Upload(filPath).start();
                }
            }
        }else {
            this.finish();
        }

    }

    public class Upload extends Thread {
        String filpath;

        public Upload(String filpath) {
            super();
            this.filpath = filpath;
        }


        @Override
        public void run() {
            super.run();
            ConnectionDetector cd = new ConnectionDetector(CameraActivity.this);
            if (cd.isConnectingToInternet()) {
                if (cd.checkURL(URL_CON)) {//cd.checkURL(URL_CON)
                    if (uploadFile(filpath)) ;
                    {
                        handle.sendEmptyMessage(0);
                    }
                } else {     //找不到服务器地址
                    handle.sendEmptyMessage(2);
                }
            } else {    //无法连接网络
                handle.sendEmptyMessage(1);
            }
        }

    }

    public boolean uploadFile(String imageFilePath) {
        // 上传
        File file = new File(imageFilePath);    //"/mnt/sdcard/doc/video.mp4"
        try {

            //单文件上传
            new FTP().uploadSingleFile(file, "/video", new FTP.UploadProgressListener() {

                @Override
                public void onUploadProgress(String currentStep, long uploadSize, File file) {
                    // TODO Auto-generated method stub
                    Log.d("upload", currentStep);
                    if (currentStep.equals("ftp文件上传成功")) {
                        Log.d("upload", "-----shanchuan--successful");
                    } else if (currentStep.equals("ftp文件正在上传")) {
                        long fize = file.length();
                        float num = (float) uploadSize / (float) fize;
                        result = (int) (num * 100);
                        Message m = new Message();
                        m.what = 0x111;
                        // 发送消息
                        mHandler.sendMessage(m);
                        Log.d("upload", "-----shangchuan---" + result + "%");
                    }
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        /*try {
            URL url = new URL(URL_CON);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            con.setRequestMethod("POST");

            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            File file = new File(imageFilePath);

            FileInputStream fStream = new FileInputStream(file);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;

            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }

            fStream.close();
            ds.flush();

            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
//            Log.d("image",b.toString());
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

}
