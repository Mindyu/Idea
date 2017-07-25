package com.example.idea;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.idea.db.DetectionResult;
import com.example.idea.util.ExcelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CreatExcelActivity extends Activity {

    private ArrayList<ArrayList<String>> recordList;
    private List<DetectionResult> detectionResults;
    private static String[] title1 = { "检测结果", "结果分析"};
    private static String[] title2 = {"组别", "标准", "试品", "标准平均值", "试品平均值", "相对误差(%)", "标准偏差", "分压比"};
    private File file;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_excel);
        // 模拟数据集合
        detectionResults = new ArrayList<>();
        for (int i=0; i<100; i++) {
            detectionResults.add(new DetectionResult(""+(i/10+1), "-1234", "-1233", "-1234", "-1233", "0.025", "0.029", "0.997"));
        }

        exportExcel();
        doOpenExcel();
    }

    /**
     * 导出excel
     */
    public void exportExcel() {
        file = new File(getSDPath() + "/Record");
        makeDir(file);
        ExcelUtils.initExcel(file.toString() + "/检测记录.xls", title1, title2);
        fileName = getSDPath() + "/Record/检测记录.xls";
        ExcelUtils.writeObjListToExcel(getRecordData(), 10,  fileName, this);
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        recordList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i <detectionResults.size(); i++) {
            DetectionResult detectionResult = detectionResults.get(i);
            ArrayList<String> bean = new ArrayList<>();
            bean.add(detectionResult.group);
            bean.add(detectionResult.standard);
            bean.add(detectionResult.test);
            bean.add(detectionResult.standardAvg);
            bean.add(detectionResult.testAvg);
            bean.add(detectionResult.relativeError);
            bean.add(detectionResult.standardAvg);
            bean.add(detectionResult.stressProportion);
            recordList.add(bean);
        }
        return recordList;
    }

    private  String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public  void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    private void doOpenExcel(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        String fileMimeType = "application/msexcel";
        intent.setDataAndType(Uri.fromFile(new File(fileName)), fileMimeType);
        try{
            CreatExcelActivity.this.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Toast.makeText(CreatExcelActivity.this, "未找到软件", Toast.LENGTH_LONG).show();
        }
    }

}
