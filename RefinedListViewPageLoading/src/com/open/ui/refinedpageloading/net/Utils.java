package com.open.ui.refinedpageloading.net;

import android.annotation.SuppressLint;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;

public class Utils {

    /**
     * 格式化时间 yyyy.MM.dd hh:mm
     * 
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateSecond(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    @SuppressLint("SimpleDateFormat")
    public static String formatMounth(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		return sdf.format(date);
	}
    
    @SuppressLint("SimpleDateFormat")
    public static String formatDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static boolean isEmpty(String str) {
        if (str != null && str.length() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public static boolean isEmpty(List<?> list) {
        if (list != null && list.size() > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public static boolean isEmptyOrZero(String digitString) {
        if (digitString != null && digitString.length() > 0) {
            try {
                long value = Long.parseLong(digitString); 
                return value == 0;
            } catch (NumberFormatException e) {
                //do nothing
            }
            
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * delete the file if it exists
     * @param files, file's path
     */
    public static void deleteFiles(List<String> files) {
        if (files != null && files.size() > 0) {
            for (String path : files) {
                File mFile = new File(path);
                if (mFile.exists()) {
                    mFile.delete();
                    mFile = null;
                }
            }
        }  
    }

    public static void deleteFiles(ArrayList<NameValuePair> nameValuePairs) {
        if (nameValuePairs != null && nameValuePairs.size() > 0) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                if (nameValuePair.getName().equalsIgnoreCase("files")) {
                    File mFile = new File(nameValuePair.getValue());
                    if (mFile.exists()) {
                        mFile.delete();
                        mFile = null;
                    }
                }
            }
        }
    }

}
