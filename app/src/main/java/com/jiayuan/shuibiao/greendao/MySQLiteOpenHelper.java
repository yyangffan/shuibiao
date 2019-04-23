package com.jiayuan.shuibiao.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jiayuan.shuibiao.gen.DaoMaster;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
 
    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        //----------------------------使用sql实现升级逻辑
        if (oldVersion == newVersion) {
            Log.e("onUpgrade", "数据库是最新版本,无需升级");
            return;
        }
        Log.e("onUpgrade", "数据库从版本" + oldVersion + "升级到版本" + newVersion);
        switch (oldVersion) {
            case 1:
                String sql = "";
                db.execSQL(sql);
            case 2:
            default:
                break;
        }
    }
}