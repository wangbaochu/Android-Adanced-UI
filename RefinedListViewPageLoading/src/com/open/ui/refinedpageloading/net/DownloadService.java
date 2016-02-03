package com.open.ui.refinedpageloading.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.open.ui.refinedpageloading.test.MainApplication;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

public class DownloadService {
    public static final String RECEIVER_SERVICE_DOWNLOAD = "service_app_download";
	public static final String CONNECTION_URL = "url";
	public static final String DONWLOAD_STATUS_KEY = "status";
	public static final int DONWLOAD_OK = 1;
	public static final int DONWLOAD_FAIL = 0;
	public static boolean downloading = false;
	
	private boolean isCancelled = false;
	private String fileDir = null;
	private ServiceCallback mServiceCallback;
	private HttpURLConnection httpConn;
	private InputStream inputStream;
	private OutputStream outputStream;

	public DownloadService(Context context, ServiceCallback serviceCallback) {
	    mServiceCallback = serviceCallback;
	}

	public void start(final Intent intent) {
		if (intent == null) {
		    return;
		} else if (TextUtils.isEmpty(intent.getStringExtra("url")) || TextUtils.isEmpty(intent.getStringExtra("companytype")) ) {
		    return;
		}
		if (downloading) {
		    return;
		}
		downloading = true;
		isCancelled = false;
		new Thread() {

			public void run() {
				try {
				    //检查并获取sdcard根目录
                    File sdDir = null;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { 
                        sdDir = Environment.getExternalStorageDirectory();//获取sdcard根目录 
                    } 
                    
                    if (sdDir != null) {
                        fileDir = sdDir.toString(); 
                    } else {
                        //未安装SD卡，无法下载
                        notifyUI(DONWLOAD_FAIL, ErrorCode.APP_DOWNLOAD_SD_ERROR, null, null);
                        downloading = false;
                        return;
                    }
					String url = intent.getStringExtra("url")+"?companytype="+intent.getStringExtra("companytype");
					System.out.println("下载地址："+url);
					URL mURL = new URL(url);
					httpConn = (HttpURLConnection) mURL.openConnection();
					inputStream = httpConn.getInputStream();
					if (inputStream == null){
						throw new RuntimeException("stream is null");
					}else{
						// 把文件存到path
						String dir = fileDir + "/mss/temp/mss.";
						File file = new File(dir);
						if (!file.exists()) {
							file.mkdirs();
						}
						String path = dir + intent.getStringExtra("companytype")+".apk";
						System.out.println(path);
						file = new File(path);
						if (file.exists()) {
							file.delete();
						}
						file.createNewFile();
						
						outputStream = new FileOutputStream(file);
						byte buf[] = new byte[1024];
						int length = 0;
						while ((length = inputStream.read(buf)) != -1) {
							outputStream.write(buf, 0, length);
						}
						inputStream.close();
						outputStream.close();
						httpConn.disconnect();
						
						notifyUI(DONWLOAD_OK, 200, path, intent.getStringExtra("companytype"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					notifyUI(DONWLOAD_FAIL, ErrorCode.APP_DOWNLOAD_FAIL, null, null);
				} finally {
					downloading = false;
				}
			};
		}.start();
	}

    //Sends the results to the main UI thread.
    private void notifyUI(int code, int msgID, String filepath, String companyType) {
        final Intent intent = new Intent(RECEIVER_SERVICE_DOWNLOAD); 
        intent.putExtra(DONWLOAD_STATUS_KEY, code);
        intent.putExtra("what", msgID);
        if (code == DONWLOAD_OK) {
            intent.putExtra("path", filepath);
            intent.putExtra("companytype", companyType);
        } 
        
        MainApplication.getInstance().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isCancelled) {
                    return;
                } else {
                    mServiceCallback.onServiceComplete(intent);
                }
            }
        });
    }
    
    //call from UI thread to cancel the service call.
    public void cancel() {
        isCancelled = true;
        closeConnection();
        
        if(mServiceCallback != null) {
            mServiceCallback.onServiceCancelled();
        }
    }
    
    private void closeConnection() {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (httpConn != null) {
            try {
                httpConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
