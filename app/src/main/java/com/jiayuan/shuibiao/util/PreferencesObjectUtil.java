package com.jiayuan.shuibiao.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class PreferencesObjectUtil {
	private Context context;

	public PreferencesObjectUtil(Context context) {
		this.context = context;
	}

	public Object readObject(String key) {
		Object obj = null;
		SharedPreferences preferences = context.getSharedPreferences("base64",
				Context.MODE_PRIVATE);
		String productBase64 = preferences.getString(key, "");

		// 读取字节

		byte[] base64 = Base64.decode(productBase64.getBytes(), 0);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				obj = (Object) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("ok", "读取失败");
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ok", "读取失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ok", "读取失败");
		}
		return obj;
	}

	public void saveObject(Object obj, String key) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
                Context.MODE_PRIVATE);
		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			// 将对象写入字节流
			oos.writeObject(obj);
			// 将字节流编码成base64的字符串
			String obj_base64 = new String(Base64.encode(baos.toByteArray(), 0));
			Editor editor = preferences.edit();
			editor.putString(key, obj_base64);

			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated
			Log.i("ok", "存储失败");
		}

	}

	public void removeObject(String key){
        SharedPreferences preferences = context.getSharedPreferences("base64",
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
