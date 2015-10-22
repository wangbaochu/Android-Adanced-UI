package com.open.ui.horizontalpagescroll;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private PageView mPageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPageView = (PageView) findViewById(R.id.pageview);
        //增加2个页面
        mPageView.addPage(createPage("Page 1", Color.BLUE, Color.WHITE));
        mPageView.addPage(createPage("Page 2", Color.YELLOW, Color.RED));
    }
    
    private View createPage(String pageTitle, int pageColor, int textColor) {
        TextView textView = new TextView(this);
        textView.setText(pageTitle);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        
        LinearLayout pageLayout = new LinearLayout(this);
        pageLayout.setBackgroundColor(pageColor);
        pageLayout.addView(textView);
        return pageLayout;
    }
}
