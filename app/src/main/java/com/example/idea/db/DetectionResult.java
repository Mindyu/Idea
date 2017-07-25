package com.example.idea.db;

/**
 * Created by liuhdme on 2017/7/22.
 */

public class DetectionResult {

    public String group;               // 组别
    public String standard;            // 标准
    public String test;                // 试品
    public String standardAvg;         // 标准平均值
    public String testAvg;             // 试品平均值
    public String relativeError;       // 相对误差(%)
    public String standardError;       // 标准偏差(%)
    public String stressProportion;    // 分压比

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getStandardAvg() {
        return standardAvg;
    }

    public void setStandardAvg(String standardAvg) {
        this.standardAvg = standardAvg;
    }

    public String getTestAvg() {
        return testAvg;
    }

    public void setTestAvg(String testAvg) {
        this.testAvg = testAvg;
    }

    public String getRelativeError() {
        return relativeError;
    }

    public void setRelativeError(String relativeError) {
        this.relativeError = relativeError;
    }

    public String getStandardError() {
        return standardError;
    }

    public void setStandardError(String standardError) {
        this.standardError = standardError;
    }

    public String getStressProportion() {
        return stressProportion;
    }

    public void setStressProportion(String stressProportion) {
        this.stressProportion = stressProportion;
    }

    // 构造方法
    public DetectionResult(String group, String standard, String test, String standardAvg, String testAvg, String relativeError, String standardError, String stressProportion) {
        this.group = group;
        this.standard = standard;
        this.test = test;
        this.standardAvg = standardAvg;
        this.testAvg = testAvg;
        this.relativeError = relativeError;
        this.standardError = standardError;
        this.stressProportion = stressProportion;
    }
}
