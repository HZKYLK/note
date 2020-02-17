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
    private AlertDialog.Builder dialog;



    private DatePickerDialog dialogDate;
    private TimePickerDialog dialogTime;

    private String createDate;
    private String dispCreateDate;
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


    void init(){
        myDB = new DatabaseHelper(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        editTime = findViewById(R.id.edit_title_time);



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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title;
            String body;
            String createDate;
            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            createDate = editTime.getText().toString();

            if (!isShowIng()){
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();
                } else {
                    intentStart();
                }
            }
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        String title;
        String body;

        title = editTitle.getText().toString();
        body = editBody.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (saveFunction(title,body,createDate)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();
                } else {
                    intentStart();
                }
                break;
            default:
                break;
        }
    }

    void intentStart(){
        Intent intent = new Intent(edit.this,note.class);
        startActivity(intent);
        this.finish();
    }

    boolean saveFunction(String title,String body,String createDate){

        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"\n" +
                    "The title can not be blank",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"\n" +
                    "Title is too long",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"\n" +
                    "Content is too long",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if ("".equals(createDate)){
            Toast.makeText(this,"Time format error",Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if(flag){
            SQLiteDatabase db;
            ContentValues values;

            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(DatabaseHelper.RECORD_TITLE,title);
            values.put(DatabaseHelper.RECORD_BODY,body);
            values.put(DatabaseHelper.RECORD_TIME,createDate);
            if (timeSetTag){
                DatePicker datePicker = dialogDate.getDatePicker();
                String str = datePicker.getYear()+"-"+
                        (datePicker.getMonth()+1)+"-"+
                        datePicker.getDayOfMonth()+" "+
                        format.timeFormat(hour,minute);
                values.put(DatabaseHelper.NOTICE_TIME,str);
            }
            db.insert(DatabaseHelper.TABLE_NAME_RECORD,null,values);
            Toast.makeText(this,"\n" +
                    "Saved successfully",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }


    void showDialog(final String title, final String body, final String createDate){
        dialog = new AlertDialog.Builder(edit.this);
        dialog.setTitle("\n" +
                "prompt");
        dialog.setMessage("Whether to save the current edits");
        dialog.setPositiveButton("\n" +
                        "save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveFunction(title,body, createDate);
                        intentStart();
                    }
                });

        dialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentStart();
                    }
                });
        dialog.show();
    }


    void clearDialog(){
        dialog = null;
    }


    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }


    void showAskDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(edit.this);
        dialog.setTitle("remind");
        DatePicker datePicker = dialogDate.getDatePicker();
        String str = datePicker.getYear()+"year"+
                (datePicker.getMonth()+1)+"month"+
                datePicker.getDayOfMonth()+"day"+
                " "+ format.timeFormat(hour,minute);
        dialog.setMessage("Whether to modify reminder time？\n\n" +
                "Current reminder time is:"+str);
        dialog.setPositiveButton("sure",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setNoticeDate();
                    }
                });

        dialog.setNegativeButton("cancel",
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
        dialogDate.setTitle("\n" +
                "Please select a date");
        dialogDate.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i(TAG,"Your selected date is："+year+"year"+(month+1)+"month"+dayOfMonth+"day");
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

        myTimeGetter = new time(new Date(System.currentTimeMillis()));

        int t_year = myTimeGetter.getYear();
        int t_month = myTimeGetter.getMonth();
        int t_dayOfMonth = myTimeGetter.getDay();
        int paramHour = 8;
        int paramMinute = 0;
        if (t_month==(this.month+1) && t_dayOfMonth==this.dayOfMonth){
            paramHour = myTimeGetter.getHour();
            paramMinute = myTimeGetter.getMinute()+5;
        }
        dialogTime = new TimePickerDialog(this,
                android.app.AlertDialog.THEME_HOLO_LIGHT,this,
                paramHour,
                paramMinute,
                true);
        dialogTime.setTitle("\n" +
                "Please select a time");
        if (t_year==this.year&&t_month==this.month&&t_dayOfMonth==this.dayOfMonth){
        }
        dialogTime.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        timeSetTag = true;
        Toast.makeText(this,"Reminder time set successfully！",Toast.LENGTH_SHORT).show();
        String noticeStr = "  Reminder time："+getTimeStr(
                myDateFormat(year,(month+1),dayOfMonth,hour,minute,date.NORMAL_TIME));
        editTime.setText(dispCreateDate + noticeStr);
    }

}
