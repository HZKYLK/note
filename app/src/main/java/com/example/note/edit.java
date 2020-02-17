package com.example.note;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import java.util.Date;

import static com.example.note.format.getTimeStr;
import static com.example.note.format.myDateFormat;


/**
 * create_by Android Studio
 *
 * @author zouguo0212@
 * @package_name fun.zzti
 * @description
 * @date 2018/10/26 17:30
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class edit extends base implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    private final static String TAG = "EditActivity";

    DatabaseHelper myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView editTime;
    private EditText editTitle;
    private EditText editBody;
    private EditText editClassify;
    private AlertDialog.Builder dialog;



    private DatePickerDialog dialogDate;
    private TimePickerDialog dialogTime;

    private String createDate;//完整的创建时间，插入数据库
    private String dispCreateDate;//创建时间-显示变量可能会去除年份

    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer hour;
    private Integer minute;
    private boolean timeSetTag;

    time myTimeGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        if (editTime.getText().length()==0)
            editTime.setText(dispCreateDate);
    }

    /*
     * 初始化函数
     */
    void init(){
        myDB = new DatabaseHelper(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        editTime = findViewById(R.id.edit_title_time);
        editClassify = findViewById(R.id.classify_title);


        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);


        Date date = new Date(System.currentTimeMillis());
        createDate = myDateFormat(date, com.example.note.date.NORMAL_TIME);
        dispCreateDate = getTimeStr(date);

        dialogDate = null;
        dialogTime = null;
        hour = 0;
        minute = 0;
        year = 0;
        month = 0;
        dayOfMonth = 0;
        timeSetTag = false;
    }

    /*
     *  返回键监听，消除误操作BUG
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title;
            String body;
            String createDate;
            String classify;
            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            createDate = editTime.getText().toString();
            classify = editClassify.getText().toString();
            //当返回按键被按下
            if (!isShowIng()){
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate,classify);
                    clearDialog();
                } else {
                    intentStart();
                }
            }
        }
        return false;
    }

    /*
     *  按钮点击事件监听
     *
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        String title;
        String body;
        String classify;
        title = editTitle.getText().toString();
        body = editBody.getText().toString();
        classify = editClassify.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (saveFunction(title,classify,body,createDate)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate,classify);
                    clearDialog();
                } else {
                    intentStart();
                }
                break;
            default:
                break;
        }
    }

    /*
     * 返回主界面
     */
    void intentStart(){
        Intent intent = new Intent(edit.this,note.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 备忘录保存函数
     */
    boolean saveFunction(String title,String classify,String body,String createDate){

        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"标题过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if ("".equals(createDate)){
            Toast.makeText(this,"时间格式错误",Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if(flag){
            SQLiteDatabase db;
            ContentValues values;
            //  存储备忘录信息
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(DatabaseHelper.RECORD_TITLE,title);
            values.put(DatabaseHelper.RECORD_BODY,body);
            values.put(DatabaseHelper.RECORD_TIME,createDate);
            values.put(DatabaseHelper.RECORD_CLASSIFY,classify);
            if (timeSetTag){
                //  为当前备忘录添加提醒
                DatePicker datePicker = dialogDate.getDatePicker();
                String str = datePicker.getYear()+"-"+
                        (datePicker.getMonth()+1)+"-"+
                        datePicker.getDayOfMonth()+" "+
                        format.timeFormat(hour,minute);
                values.put(DatabaseHelper.NOTICE_TIME,str);
            }
            db.insert(DatabaseHelper.TABLE_NAME_RECORD,null,values);
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    /*
     * 弹窗函数
     * @param title
     * @param body
     * @param createDate
     */
    void showDialog(final String title, final String body, final String createDate,final String classify){
        dialog = new AlertDialog.Builder(edit.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveFunction(title,classify,body, createDate);
                        intentStart();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentStart();
                    }
                });
        dialog.show();
    }

    /*
     *  清空弹窗
     */
    void clearDialog(){
        dialog = null;
    }

    /*
     *  判断是否弹窗是否显示
     */
    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }

    /*
     *  询问是否修改提醒时间的弹窗函数
     */
    void showAskDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(edit.this);
        dialog.setTitle("提示");
        DatePicker datePicker = dialogDate.getDatePicker();
        String str = datePicker.getYear()+"年"+
                (datePicker.getMonth()+1)+"月"+
                datePicker.getDayOfMonth()+"日"+
                " "+ format.timeFormat(hour,minute);
        dialog.setMessage("是否修改提醒时间？\n当前提醒时间为:"+str);
        dialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setNoticeDate();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }

    /*
     *  设置提醒时间函数
     */
    void setNoticeDate(){
        Calendar calendar=Calendar.getInstance();
        dialogDate = new DatePickerDialog(this,
                android.app.AlertDialog.THEME_HOLO_LIGHT,this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.getDatePicker().setCalendarViewShown(false);
        dialogDate.getDatePicker().setMinDate(calendar.getTime().getTime());
        dialogDate.setTitle("请选择日期");
        dialogDate.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG,"您选的日期为："+year+"年"+(month+1)+"月"+dayOfMonth+"日");
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

        myTimeGetter = new time(new Date(System.currentTimeMillis()));
        //  取出年月日时分
        int t_year = myTimeGetter.getYear();
        int t_month = myTimeGetter.getMonth();
        int t_dayOfMonth = myTimeGetter.getDay();
        int paramHour = 8;
        int paramMinute = 0;
        if (t_month==(this.month+1) && t_dayOfMonth==this.dayOfMonth){
            paramHour = myTimeGetter.getHour();
            //  如果是设置当天提醒，则最小时间显示默认不小于五分钟以内
            paramMinute = myTimeGetter.getMinute()+5;
        }
        dialogTime = new TimePickerDialog(this,
                android.app.AlertDialog.THEME_HOLO_LIGHT,this,
                paramHour,
                paramMinute,
                true);
        dialogTime.setTitle("请选择时间");
        if (t_year==this.year&&t_month==this.month&&t_dayOfMonth==this.dayOfMonth){
            //  设置的当天提醒，需要设置可选择的最小时间不小于当前五分钟之内
            //  暂未实现
        }
        dialogTime.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        timeSetTag = true;
        Toast.makeText(this,"提醒时间设置成功！",Toast.LENGTH_SHORT).show();
        String noticeStr = "  提醒时间："+getTimeStr(
                myDateFormat(year,(month+1),dayOfMonth,hour,minute,date.NORMAL_TIME));
        editTime.setText(dispCreateDate + noticeStr);
    }

}
