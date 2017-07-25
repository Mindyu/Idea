package com.example.idea;

/**
 * Created by 杨 陈强 on 2017/7/23.
 */
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.idea.db.DetectionResult;
import com.example.idea.util.ExcelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchData extends Activity {

    private EditText etStandardId;
    private EditText etTestId;
    private EditText etDate;
    private Button btnConfirm;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private Dialog myDialog;

    private ArrayList<ArrayList<String>> recordList;
    private List<DetectionResult> detectionResultBeen;
    private static String[] title1 = { "检测结果", "结果分析"};
    private static String[] title2 = {"组别", "标准", "试品", "标准平均值", "试品平均值", "相对误差(%)", "标准偏差", "分压比"};
    private File file;
    private String fileName;

    private String standardId;
    private String testId;
    private String date;
    private String responseData;
    String values="";
    String values1="";

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myDialog.dismiss();
            if (responseData.length()>25){
                //数据解析
                praseToResult();
                exportExcel(); // 生成 Excel
                doOpenExcel(); // 打开 Excel
            }else {
                Toast.makeText(SearchData.this,"未查询到指定数据",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_data);

        initView();

        //获取日历对象
        calendar=Calendar.getInstance();
        //获取年月日时分秒的信息
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;  //Calendar.MONTH获取的月份从0开始
        day=calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);

        detectionResultBeen = new ArrayList<>();

//        // 模拟数据集合
//        for (int i=0; i<100; i++) {
//            detectionResultBeen.add(new DetectionResult(""+(i/10+1), "-1234", "-1233", "-1234", "-1233", "0.025", "0.029", "0.997"));
//        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standardId = etStandardId.getText().toString();
                testId = etTestId.getText().toString();
                date = etDate.getText().toString();
                Log.d("Length",""+standardId.length()+testId.length()+date.length());
                if(standardId.length()!=0&&testId.length()!=0&&date.length()!=0){
                    // 从服务器获取数据
                    getData(standardId, testId, date);
                    myDialog= ProgressDialog.show(SearchData.this,"请稍后","正在处理中...",true);
                }else {
                    Toast.makeText(SearchData.this,"请输入完整的数据",Toast.LENGTH_SHORT).show();
                }
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(SearchData.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                        if (etDate.getText().length()<12){
//                            etDate.setText(String.valueOf(etDate.getText())+hour+":"+minute);
//                            Log.d("etDate",String.valueOf(etDate.getText())+hour+":"+minute);
//                        }
                        values1=hour+":"+minute;
                        etDate.setText(values+values1);
//                        Log.d("etDate",values+values1);
                    }
                },hour,minute,true).show();
                new DatePickerDialog(SearchData.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                        etDate.setText("");
//                        etDate.setText(i+"-"+(i1+1)+"-"+i2+" ");
                        values=""+i+"-"+(i1+1)+"-"+i2+" ";
                    }
                },year,month-1,day).show();
            }
        });
    }

    private void initView() {
        etStandardId = (EditText) findViewById(R.id.et_standard_id);
        etTestId = (EditText) findViewById(R.id.et_test_id);
        etDate = (EditText) findViewById(R.id.et_date);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    /**
     * 根据标准型号、试品型号、日期从服务器获取数据，
     * @param standardId 标准型号
     * @param testId 试品型号
     * @param date 日期
     */
    private void getData(final String standardId, final String testId, final String date) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("standardXH", standardId)
                            .add("testXH", testId)
                            .add("testTime", date)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://192.168.1.116/get_typeinfo.php")
                                            .post(requestBody)
                                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    handler.sendMessage(new Message());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    public void praseToResult(){
        //responseData;
        String[] result=responseData.split("_");
        detectionResultBeen.clear();
        for (int i=0;i<result.length/25;i++){
            // 模拟数据集合
            for (int j=0; j<10; j++) {
                detectionResultBeen.add(new DetectionResult(""+(i+1), result[25*i+j], result[25*i+j+10], result[25*i+20], result[25*i+21], result[25*i+22], result[25*i+23], result[25*i+24]));
            }
        }
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        recordList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < detectionResultBeen.size(); i++) {
            DetectionResult DetectionResult = detectionResultBeen.get(i);
            ArrayList<String> bean = new ArrayList<>();
            bean.add(DetectionResult.getGroup());
            bean.add(DetectionResult.getStandard());
            bean.add(DetectionResult.getTest());
            bean.add(DetectionResult.getStandardAvg());
            bean.add(DetectionResult.getTestAvg());
            bean.add(DetectionResult.getRelativeError());
            bean.add(DetectionResult.getStandardAvg());
            bean.add(DetectionResult.getStressProportion());
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
            SearchData.this.startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Toast.makeText(SearchData.this, "未找到软件", Toast.LENGTH_LONG).show();
        }
    }
}