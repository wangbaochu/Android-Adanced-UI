package com.open.ui.refinedhorizontallistview.test;

import com.open.ui.refinedhorizontallistview.R;
import com.open.ui.refinedhorizontallistview.view.HorizontalListView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private HorizontalListView mHorizontalListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mHorizontalListView = (HorizontalListView) findViewById(R.id.list_view);
        TestAdapter adapter = new TestAdapter(this);
        mHorizontalListView.setAdapter(adapter);
        mHorizontalListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
                Toast.makeText(MainActivity.this, (String)view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
        mHorizontalListView.setHighlightBackgroundResource(R.drawable.orange_outline_sharp_corner);
    }

}
