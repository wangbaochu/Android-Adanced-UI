package com.open.ui.advancelistview.test;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper of application info.
 */
public class AppInfo {

    public String mPkgName;
    
    public int mLableId;
    public String mLable;
    
    public int mIconId;
    public Drawable mIcon;
    
    public ApplicationInfo mApplicationInfo;
    
    public AppInfo(ApplicationInfo appInfo) {
        mApplicationInfo = appInfo;
    }

    /**
     * Create a AppInfo object to wrap all the app info.
     * @param context
     * @param packageInfo
     * @param loadLable If true, load the application label
     * @param loadDescription If true, load the application description
     * @param loadIcon If true, load the application icon
     * @return
     */
    public static AppInfo createAppInfo(Context context, ApplicationInfo applicationInfo, boolean loadLable, boolean loadDescription, boolean loadIcon) {
        if (applicationInfo == null) {
            return null;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(applicationInfo.packageName, 0);

            AppInfo appInfo = new AppInfo(applicationInfo);
            appInfo.mPkgName = pkgInfo.packageName;

            if (loadLable) {
                appInfo.mLable = loadLabel(pm, appInfo.mApplicationInfo);
            } else {
                appInfo.mLableId = applicationInfo.labelRes;
            }
            if (loadIcon) {
                appInfo.mIcon = loadIcon(pm, appInfo.mApplicationInfo);
            } else {
                appInfo.mIconId = applicationInfo.icon;
            }

            return appInfo;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Load App's label */
    public static String loadLabel(PackageManager pm, ApplicationInfo applicationInfo) {      
        CharSequence label = applicationInfo.loadLabel(pm);
        if(!TextUtils.isEmpty(label)) {
            return label.toString();
        } else {
            return applicationInfo.packageName;
        }
    }

    /** 
     * Load App's Icon 
     */
    public static Drawable loadIcon(PackageManager pm, ApplicationInfo applicationInfo) {
        Drawable icon = null;
        if (pm != null) {
            icon = applicationInfo.loadIcon(pm);
        }
        return icon;
    }

    /** 
     * Query all the app info that installed on this device
     */
    public static List<AppInfo> loadAllInstalledAppInfo(Context context, boolean loadLabl, boolean loadDecription, boolean loadIcon) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> listAppcations = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        //Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(packageManager));
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        if (listAppcations != null && listAppcations.size() > 0) {
            for (ApplicationInfo appInfo : listAppcations) {
                appInfoList.add(AppInfo.createAppInfo(context, appInfo, loadLabl, loadDecription, loadIcon));
            }
        }

        return appInfoList;
    }
}
