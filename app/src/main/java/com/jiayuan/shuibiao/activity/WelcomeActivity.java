package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 欢迎页面
 * 初始化信息
 */
public class WelcomeActivity extends BaseActivity {

    private static final String DB_NAME = "greendao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //创建表
//        initTable();

        //将数据库文件复制到手机中（提前预制的管线数据）
        initDB();
    }


//    private void initTable() {
//        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
//                App.getInstance().getApplicationContext(), DB_NAME, null);
//        Database db = helper.getWritableDb();
//        MeterdataTempStorageDao.createTable(db, true);
//
//    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return 0;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent =  new Intent(this,LoginActivity.class);
        startActivity(intent);

    }

    private static final String DATABASE_PATH = "/data/data/com.jiayuan.shuibiao/databases/";
    private static final String DATABASE_NAME = "greendao";

    public void initDB() {
        File fileDB = new File(DATABASE_PATH + DATABASE_NAME);

        if(!fileDB.exists() || (fileDB.length()/1024<200)) {
            File file = new File(DATABASE_PATH);
            if(!file.exists())
                file.mkdirs();

            try {
                InputStream is = getAssets().open(DATABASE_NAME);
                OutputStream os = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);

                byte[] buffer = new byte[1024];
                int len;
                while((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }

                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
