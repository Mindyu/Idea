package com.example.idea;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


/**
 * Created by 杨 陈强 on 2017/7/21.
 */

public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            Toast.makeText(context, "无可用网络", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 方法说明 检查指定ip地址是否有效/是否有连接
     */
    public boolean checkURL(String url){
        boolean result = false;
        try {
            HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
            conn.setConnectTimeout(3000);     ///
            conn.setReadTimeout(3000);
            int code = conn.getResponseCode();
            if(code!=200){
                result=false;
//                Toast.makeText(context, "无网络连接",  Toast.LENGTH_SHORT).show();
            }else{
                result=true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
