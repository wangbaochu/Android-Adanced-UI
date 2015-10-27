package com.open.ui.horizontalScrollViewForGallery3D;

import com.example.mytesting.R;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MyHorizontalScrollView myScrollView = (MyHorizontalScrollView) findViewById(R.id.my_scroll_view);
        myScrollView.initDatas();
    }
}