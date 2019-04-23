package com.jiayuan.shuibiao.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * wa.android.common.utils.PreferencesUtil
 * @author guowla
 * create at 2014-8-8 下午4:43:17
 * 读写对象的工具方法
 */
public class PreferencesUtil {

	public static final String NAME_COMMON = "COMMON";
	
	//服务器地址
	public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
	//服务器ip
	public static final String SERVER_IP = "SERVER_IP";
	//服务器port
	public static final String SERVER_PORT = "SERVER_PORT";
	public static final String SESSION_ID_HEADER = "SESSION_ID_HEADER";
	public static final String SESSION_ID_SP = "SESSION_ID_SP";
	public static final String SESSION_ID = "SESSION_ID";

	// for request vo
	//登录的时候有几个serviceCodeRes
	public static final String LOGIN_SERVICECODERES_SIZE = "LOGIN_SERVICECODERES_SIZE";
	//用户ID
	public static final String USER_ID = "USER_ID";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_PASS = "USER_PASS";

	//目前应用将usercode 存成了username，这个字段是真正的username（后台返回的）
	public static final String USERNAME_REAL = "username_real";
	//上次登录版本
	public static final String LAST_VERSION = "LAST_VERSION";
	public static final String DEVICE_INFO = "DEVICE_INFO";
	//集团ID
	public static final String GROUP_ID = "GROUP_ID";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String GROUP_CODE = "GROUP_CODE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String SERVICE_CODE = "SERVICE_ID";
	//集团签到标记和签到范围
	public static final String ORG_SIGN_FLAG = "signinoutofrangeflag";//超出范围是否可以签到
	public static final String ORG_SIGN_FUTURE = "signfuture";//超出范围是否可以签到
	public static final String ORG_SIGN_RANGE = "signinrange";//签到半径，若为空，则不需做范围判断
	public static final String GEO_KEY = "geokey";//高德地图key
    // 是否有名片扫描权限 -- Modify by naray
	public static final String ORG_CARDSCAN_FLAG = "cardscanflag";
    // 当cardscanflag=Y时，返回端对应的SDK的user，否则不返回该字段 -- Modify by naray
    public static final String ORG_CARDSCAN_USER = "camCardUserAndroid";
    // 当cardscanflag=Y时，返回端对应的SDK的key，否则不返回该字段 -- Modify by naray
    public static final String ORG_CARDSCAN_KEY = "camCardKeyAndroid";

	//是否自动登陆
	public static final String IS_AUTOLOGIN = "IS_AUTOLOGIN";
	public static final String LOGIN_SESSION = "LOGIN_SESSION";
	public static final String LOGIN_SESSION_4POLL = "LOGIN_SESSION_4POLL";

	//文件服务器 内外网 标识  intranet extranet
	public static final String FILESERVER = "fileserver";

	/**
	 * SharedPreferences工具方法,用来读取一个值 如果没有读取到，会返回""
	 * 
	 * @param key
	 * @return
	 */
	public static String readPreference(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_COMMON, Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, "");
		return value;
	}
	
	
	/**
	 * SharedPreferences工具方法,用来写入一个值
	 * 
	 * @param key
	 * @param value
	 */
	public static void writePreference(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_COMMON, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * SharedPreferences工具方法,用来删除一个值
	 *
	 * @param key
	 * @return
	 */
	public static void delPreference(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_COMMON, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}
}
