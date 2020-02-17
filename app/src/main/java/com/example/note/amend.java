package com.example.note;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.text.SimpleDateFormat;
import java.util.Date;


public class amend extends base implements View.OnClickListener{

    private final static String TAG = "AmendActivity";

    DatabaseHelper myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView amendTime;
    private TextView amendTitle;
    private EditText amendBody;
    private EditText amendClassify;
    private Record record;
    private AlertDialog.Builder dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend);
        init();

    }


    @Override
    public void onClick(View v) {
        String body;
        body = amendBody.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (updateFunction(body)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                showDialog(body);
                clearDialog();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当返回按键被按下
            if (!isShowIng()){
                showDialog(amendBody.getText().toString());
                clearDialog();
            }
        }
        return false;
    }

    /*
     * 初始化函数
     */
    @SuppressLint("SetTextI18n")
    void init(){
        myDB = new DatabaseHelper(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);
        amendTime = findViewById(R.id.amend_title_time);
        amendClassify = findViewById(R.id.amend_classify);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (intent!=null){

            record = new Record();

            record.setId(Integer.valueOf(intent.getStringExtra(DatabaseHelper.RECORD_ID)));
            record.setTitleName(intent.getStringExtra(DatabaseHelper.RECORD_TITLE));
            record.setTextBody(intent.getStringExtra(DatabaseHelper.RECORD_BODY));
            record.setCreateTime(intent.getStringExtra(DatabaseHelper.RECORD_TIME));
            record.setNoticeTime(intent.getStringExtra(DatabaseHelper.NOTICE_TIME));
            record.setClassify(intent.getStringExtra(DatabaseHelper.RECORD_CLASSIFY));

            amendTitle.setText(record.getTitleName());
            String str="";
            if (record.getNoticeTime()!=null){
                str = "    提醒时间："+record.getNoticeTime();
            }
            amendTime.setText(record.getCreateTime()+str);
            amendBody.setText(record.getTextBody());
            amendClassify.setText(record.getClassify());
        }
    }

    /*
     * 返回主界面
     */
    void intentStart(){
        Intent intent = new Intent(amend.this, note.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 保存函数
     */
    boolean updateFunction(String body){

        SQLiteDatabase db;
        ContentValues values;

        boolean flag = true;
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            // update
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(DatabaseHelper.RECORD_BODY,body);
            values.put(DatabaseHelper.RECORD_CLASSIFY,);
            values.put(DatabaseHelper.RECORD_TIME,getNowTime());
            db.update(DatabaseHelper.TABLE_NAME_RECORD,values,DatabaseHelper.RECORD_ID +"=?",
                    new String[]{record.getId().toString()});
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
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
    void showDialog(final String body){
        dialog = new AlertDialog.Builder(amend.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateFunction(body);
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

    /*
     * 得到当前时间
     * @return
     */
    String getNowTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
