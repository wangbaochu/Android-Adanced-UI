package com.open.ui.refinedpageloading.net;

import android.content.Intent;

public interface ServiceCallback {
    //call when user cancel the servcie call
    abstract void onServiceCancelled();
    
    //call when network service returned.
    abstract void onServiceComplete(Intent intent); 
}
