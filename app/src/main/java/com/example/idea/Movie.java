package com.example.idea;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 杨 陈强 on 2017/7/22.
 */

public class Movie extends BmobObject {
    private String name;//电影名称
    private BmobFile file;//电影文件

    public Movie(){
    }

    public Movie(String name,BmobFile file){
        this.name =name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}
