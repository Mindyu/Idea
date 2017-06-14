package com.example.idea;

import cn.bmob.v3.BmobObject;

/**
 * Created by liuhdme on 2017/6/11.
 */

public class UserInfo extends BmobObject {

    private String phoneNumber;
    private String userName;
    private String passWord;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
