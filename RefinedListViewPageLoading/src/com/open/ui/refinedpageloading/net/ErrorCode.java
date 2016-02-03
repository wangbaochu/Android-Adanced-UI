package com.open.ui.refinedpageloading.net;

public class ErrorCode {
    
    public static final int APP_NO_NEW_VERSION = 7;
    /**应用程序下载成功*/
    public static final int APP_DOWNLOAD_SUCCESS = 6;
	/** 上传失败 */
	public static final int UPLOAD_FAIL = 3;
	/** 上传成功 */
    public static final int UPLOAD_SUCCESS = 2;
	/** 设置添加数据在UI上 */
	public static final int SET_DATA = 1;
	/** 登录失败 */
	public static final int LOGIN_ERROR = 0;
	/** 网络连接错误 ; 当json==null,三种可能.1.网络延迟,2.没有连接网络,3.服务器出现问题 */
	public static final int INTERNET_ERROR = -1;
	/** JSON解析错误 ; json格式错误的时候 */
	public static final int JSON_ERROR = -2;
	/** 到户操作错误 */
	public static final int OPR_SIGN_ERR = -3;
	/** 密码修改失败 */
	public static final int UPDATE_PWD_FAIL = -4;
	/** 密码修改成功 */
	public static final int UPDATE_PWD_SUCCESS = -5;
	/**服务器遇到问题，下载失败*/
	public static final int APP_DOWNLOAD_FAIL = -6;
	/**未安装SD卡，无法下载...*/
	public static final int APP_DOWNLOAD_SD_ERROR = -7;

	public static final int SERVER_CONNECT_ERROR = -8;
	   
    public static final int SERVER_RETURN_ERROR = -9;

	public static final int CONN_FAIL = -10;
	
	public static final int SERVER_AUTHEN_FAIL = -11;
	
    //--------------------------------------------------------
	public static String getMsg(int msg) {
		switch (msg) {
		case APP_DOWNLOAD_SUCCESS:
		    return "下载成功";
		case UPLOAD_FAIL:
			return "服务器返回错误，本次上传失败";
		case LOGIN_ERROR:
			return "登陆失败, 用户名或密码错误";
		case INTERNET_ERROR:
			return "网络连接异常，请检查网络设置后再试";
		case JSON_ERROR:
			return "数据格式不正确";
		case OPR_SIGN_ERR:
			return "上次任务未完工，请返回完工";
		case UPDATE_PWD_FAIL:
			return "密码修改失败";
		case UPDATE_PWD_SUCCESS:
			return "修改密码成功";
		case APP_DOWNLOAD_FAIL:
		    return "服务器遇到问题，下载失败";
		case APP_DOWNLOAD_SD_ERROR:
		    return "未安装SD卡，无法下载...";
		case APP_NO_NEW_VERSION:
		    return "当前版本为最新版无需更新";
		case SERVER_CONNECT_ERROR:
		    return "服务器连接失败，请稍后重试!";
		case SERVER_RETURN_ERROR:
		    return "服务器返回错误!";
		case SERVER_AUTHEN_FAIL:
		    return "身份验证失败";
		}
		return "沒有定义";
	}
}
