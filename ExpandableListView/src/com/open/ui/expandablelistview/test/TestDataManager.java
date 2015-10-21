package com.open.ui.expandablelistview.test;

import com.open.ui.expandablelistview.ui.ExpandDataManager;

import android.content.Context;

public class TestDataManager extends ExpandDataManager {

    /**
     * Initial the data associate with the list view adapter
     * @param context
     */
    public void initData(Context context) {
        
        for (int i = 0; i < 3; i++) {
            //The first level data
            TestExpandedData data_0 = new TestExpandedData();
            data_0.setExanded(true);
            addChild(data_0);

            if (i == 0) {
                //The second level data
                TestExpandedData data_1 = new TestExpandedData();
                data_1.setExanded(true);
                data_0.addChild(data_1);
                
                //The third level data
                TestExpandedData data_2 = new TestExpandedData();
                data_2.setExanded(true);
                data_1.addChild(data_2);
            } else if (i == 1) {
                TestExpandedData data_1 = new TestExpandedData();
                data_1.setExanded(true);
                data_0.addChild(data_1);
            } else if (i == 2) {
            }
        }
    }
}
