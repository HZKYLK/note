package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class language extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Contants.activityList.add(this);

        final RadioGroup radio = (RadioGroup) findViewById(R.id.radio);
        final RadioButton btn = (RadioButton) findViewById(R.id.radio_en);
        final RadioButton btn2 = (RadioButton) findViewById(R.id.radio_cn);

        if (Contants.setting.equals("cn")) {
            btn2.setChecked(true);
        } else {
            btn.setChecked(true);
        }

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_cn) {
                    Contants.setting = "cn";
                    btn.setChecked(false);
                } else {
                    btn2.setChecked(false);
                    Contants.setting = "en";

                }
                ;
            }
        });
    }
    public void onClick(View view){
        Locale locale =new Locale(Contants.setting);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Resources resources = getResources();
        resources.updateConfiguration(config,resources.getDisplayMetrics());

        for (Activity activity: Contants.activityList){
            activity.finish();
        }
        startActivity(new Intent(this, MainActivity.class));


    }
}

