package com.open.ui.refinedpageloading.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetUtils {
    
    public static final String SUBMIT_STATUS = "SUBMIT_STATUS";
    
    public static HttpParams setTimeOut() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 1000 * 30);
        HttpConnectionParams.setSoTimeout(params, 1000 * 30);
        return params;
    }

    public static JSONObject doGet(String path) throws JSONException{
        return doGet(new HttpGet(path));
    }

    /**
     * Get data from server
     * @param httpRequest
     * @return
     */
    public static JSONObject doGet(HttpGet httpRequest) throws JSONException {
        try {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>等待服务器..");
            System.out.println("连接地址:" + httpRequest.getURI().toString());
            HttpGet request = httpRequest;
            HttpClient client = new DefaultHttpClient(setTimeOut());
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>服务器已响应");
                String result = EntityUtils.toString(response.getEntity());
                if (result == null) {
                    return null;
                } else {
                    System.out.println("返回数据" + result);
                    return new JSONObject(result);
                }                
            } else {
                System.out.println(response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (Exception e) {
            throw new JSONException(e.getMessage());
        }
    }    

    public static JSONObject doSend(String url, List<NameValuePair> nameValuePairs) {
        return doSend(new HttpPost(url), nameValuePairs);
    }
    
    /**
     * Currently, only used to re-upload the saved form data.
     * @param url
     * @param nameValuePairs
     * @return
     */
    public static JSONObject doSend(HttpPost hpost, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient(setTimeOut());
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = hpost;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        System.out.println("<<<<<<<<<<<<<<<<开始上传");
        System.out.println("上传链接地址:" + httpPost.getURI().toString());
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (nameValuePairs != null) {
                for (int index = 0; index < nameValuePairs.size(); index++) {
                    if (nameValuePairs.get(index).getName().equalsIgnoreCase("files")) {
                        entity.addPart(nameValuePairs.get(index).getName(),
                                new FileBody(new File(nameValuePairs.get(index).getValue())));
                    } else {
                        // Normal string data
                        // Note: Currently, the intent only contains "url" and "files" two fields.
                        entity.addPart(nameValuePairs.get(index).getName(),
                                new StringBody(nameValuePairs.get(index).getValue()));
                    }
                }
            }
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity e = response.getEntity();
            in = e.getContent();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            System.out.println("statusLine : " + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new Exception("CodeLine:"
                        + response.getStatusLine().getStatusCode());
            }
            JSONObject json = new JSONObject(out.toString());
            System.out.println("返回数据: " + json.toString());
            System.out.println("flag: " + json.getBoolean("flag"));
            if (json.getBoolean("flag")) {
                //delete the files after uploading successfully
                Utils.deleteFiles((ArrayList<NameValuePair>)nameValuePairs);
            } else {
                throw new Exception("上传失败,");
            }
            System.out.println("MSG: " + json.getString("msg"));
            System.out.println(">>>>>>>>>>>>>>>>>>>>上传完毕");
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * check whether the current network is available.
     * @param context
     * @return
     */
    public static boolean checkNetworkCondition(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isAvailable() && activeNetInfo.isConnected()) {
            return true;
        }
        return false;
    }


}