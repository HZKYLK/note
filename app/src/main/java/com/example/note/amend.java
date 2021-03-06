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
            if (!isShowIng()){
                showDialog(amendBody.getText().toString());
                clearDialog();
            }
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    void init(){
        myDB = new DatabaseHelper(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);
        amendTime = findViewById(R.id.amend_title_time);

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

            amendTitle.setText(record.getTitleName());
            String str="";
            if (record.getNoticeTime()!=null){
                str = "    remind time："+record.getNoticeTime();
            }
            amendTime.setText(record.getCreateTime()+str);
            amendBody.setText(record.getTextBody());
        }
    }

    void intentStart(){
        Intent intent = new Intent(amend.this, note.class);
        startActivity(intent);
        this.finish();
    }


    boolean updateFunction(String body){

        SQLiteDatabase db;
        ContentValues values;

        boolean flag = true;
        if (body.length()>200){
            Toast.makeText(this,"Content is too long",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            // update
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(DatabaseHelper.RECORD_BODY,body);
            values.put(DatabaseHelper.RECORD_TIME,getNowTime());
            db.update(DatabaseHelper.TABLE_NAME_RECORD,values,DatabaseHelper.RECORD_ID +"=?",
                    new String[]{record.getId().toString()});
            Toast.makeText(this,"Successfully modified",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }


    void showDialog(final String body){
        dialog = new AlertDialog.Builder(amend.this);
        dialog.setTitle("prompt");
        dialog.setMessage("Whether to save the current edits");
        dialog.setPositiveButton("save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateFunction(body);
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


    String getNowTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
