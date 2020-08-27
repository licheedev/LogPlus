package com.licheedev.logplusdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.licheedev.myutils.LogPlus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogPlus.e(this.getClass().getName());
        
    }
}