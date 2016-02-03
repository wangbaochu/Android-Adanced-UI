package com.open.ui.refinedpageloading.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.open.ui.refinedpageloading.test.MainApplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MService {

	public static final String RECEIVER_SERVICE = "service_basic";

	/** 请求数据 */
	public static final String ACTION_DO_GET = "action_doget";
	/** 发送数据 */
	public static final String ACTION_DO_POST = "action_dopost";
	
	public static final String SAVE_IF_POST_FAIL = "save_if_post_fail";

	/** 当前是post还是get */
	public static final String EXTRA_NAME = "do";
	public static final int EXTRA_VALUE_GET = 0x00012;
	public static final int EXTRA_VALUE_POST = 0x00031;
	
	private ServiceCallback mServiceCallback;
	private HttpGet request;
	private HttpPost httpPost;
	private boolean isCancelled = false;
	
	public MService(Context context, ServiceCallback serviceCallback) {
	    mServiceCallback = serviceCallback;
	}
	
	/**
     * Begin the http connection using a worker thread.
     */
    public void start(final Intent intent) {
        isCancelled = false;
        // 获得要链接的地址
        if (intent == null) {
            return;
        }
        final String url = intent.getStringExtra("url");
        // 开启线程为了防止主线程不被卡死
        new Thread() {
            public void run() {
                if (ACTION_DO_GET.equals(intent.getAction())) {
                    doGet(url);
                } else if (ACTION_DO_POST.equals(intent.getAction())) {
                    doPost(url, intent);
                }
            };
        }.start();
    }
  
    //call from UI thread to cancel the service call.
    public void cancel() {
        isCancelled = true;
        
        if (request != null && !request.isAborted()) {
            request.abort();
        }
        
        if (httpPost != null && !httpPost.isAborted()) {
            httpPost.abort();
        }
        
        if(mServiceCallback != null) {
            mServiceCallback.onServiceCancelled();
        }
    }
    
	/**
	 * get方法 用于 请求数据列表用
	 * @param url
	 */
	public void doGet(String url) {
	    request = new HttpGet(url);
        try {
            JSONObject json = InternetUtils.doGet(request);
            if (json != null) {
                if (json.getBoolean("flag")) {
                    //下载成功
                    onExecuteResult(getBundle(json), EXTRA_VALUE_GET);
                } else {
                    onExecuteResult(ErrorCode.SERVER_RETURN_ERROR, getBundle(json));
                    return;
                }
            } else {
                onExecuteResult(ErrorCode.SERVER_CONNECT_ERROR, null);
                return;
            }
        } catch (JSONException e) {
            onExecuteResult(ErrorCode.INTERNET_ERROR, null);
            e.printStackTrace();
        }
	}

	/**
	 * post方法 用于 向服务器提交数据用 在这里如果提交失败了 也就是出现异常了 将会保存到数据库里下次当系统广播了链接网络的时候自动上传
	 * 
	 * @param url
	 * @param intent.getBundleExtra("data"); 里 装入NameValuePair key value 对应
	 */
	public void doPost(String url, Intent intent) {
		System.out.println("doPost");
		List<NameValuePair> nameValuePairs = null;
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			nameValuePairs = new ArrayList<NameValuePair>();
			for (String key : bundle.keySet()) {
				// Note: Currently, the intent only contains "url" and "files" two fields.
				if (key.equals("url")) {
					continue;
				} else if (key.equals("files")){
					String[] filePath = bundle.getStringArray(key);
					for (int i = 0; i < filePath.length; i++) {
						nameValuePairs.add(new BasicNameValuePair("files", filePath[i]));
					}
					continue;
				} else {
				    //Actually, never come here.
				    nameValuePairs.add(new BasicNameValuePair(key, bundle.getString(key)));
				}
			}
			if (nameValuePairs.size() == 0) {
				nameValuePairs = null;
			}
		}

		httpPost = new HttpPost(url);
		JSONObject json = InternetUtils.doSend(httpPost, nameValuePairs);
		try {
			if (json != null) {
				if (json.getBoolean("flag")) {
				    //上传成功
					//onExecuteResult(MsgUtils.UPLOAD_SUCCESS);
					onExecuteResult(getBundle(json), EXTRA_VALUE_POST);
				} else {
					onExecuteResult(ErrorCode.JSON_ERROR, getBundle(json));
					return;
				}
			} else {
				onExecuteResult(ErrorCode.UPLOAD_FAIL, null);
				return;
			}
		} catch (JSONException e) {
			onExecuteResult(ErrorCode.JSON_ERROR, null);
			e.printStackTrace();
		}
	}

	/**
	 * 封装 JSON对象 到 Bundle 中
	 * 
	 * @param json
	 * @return Bundle
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public static Bundle getBundle(JSONObject json) throws JSONException {
		Bundle bundle = new Bundle();
		Iterator<String> it = json.keys();
		while (it.hasNext()) {
			String key = it.next();
			Object value = json.get(key);
			if (value instanceof JSONObject) {
				// JSONObject
				bundle.putBundle(key, getBundle((JSONObject) value));
			} else if (value instanceof JSONArray) {
				// JSONArray 解析是 取
				// ((Bundle)((Bundle)data).get(index)).getString(key)
				bundle.putBundle(key, getBundle((JSONArray) value));
			}else {
				// 字符串 直接插入
				bundle.putString(key, value.toString());
			}
		}
		return bundle;
	}

	/**
	 * JSONArray 对象
	 * 
	 * 把 每个 jsonarray 装成一个bundle key为 i
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static Bundle getBundle(JSONArray array) throws JSONException {
		Bundle bundle = new Bundle();
		for (int i = 0; i < array.length(); i++) {
			bundle.putBundle(i + "", getBundle(array.getJSONObject(i)));
		}
		return bundle;
	}

	/**
	 * Sends the results to the main UI thread.
	 * 发生了异常时
	 */
	public void onExecuteResult(int code, Bundle bundle) {
		System.out.println("发出错误通知" + code + ErrorCode.getMsg(code));
		Intent intent = new Intent(RECEIVER_SERVICE);
		intent.putExtra("what", code);
		if (bundle != null) {
		    intent.putExtras(bundle);
		}
		notifyUI(intent);
	}

	/**
	 * Sends the results to the main UI thread.
	 * 没有发生异常时
	 */
	public void onExecuteResult(Bundle bundle, int DO) {
		System.out.println("发出正确通知" + bundle.toString());
		Intent intent = new Intent(RECEIVER_SERVICE);
		intent.putExtras(bundle);
		intent.putExtra(EXTRA_NAME, DO);
		notifyUI(intent);
	}
	
	//Sends the results to the main UI thread.
	private void notifyUI(final Intent intent) {
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
}
