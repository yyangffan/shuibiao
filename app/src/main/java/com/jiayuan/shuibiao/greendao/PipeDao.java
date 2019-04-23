package com.jiayuan.shuibiao.greendao;

import android.util.Log;

import com.jiayuan.shuibiao.entity.PipeData;
import com.jiayuan.shuibiao.gen.PipeDataDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class PipeDao {
 
    private final GreenDaoManager daoManager;
    private static PipeDao mPipeDao;

    public PipeDao() {
        daoManager = GreenDaoManager.getInstance();
    }

    public static PipeDao getInstance() {
        if (mPipeDao == null) {
            mPipeDao = new PipeDao();
        }
        return mPipeDao;
    }

    /**
     * 插入数据 若未建表则先建表
     *
     * @param pipeData
     * @return
     */
    public boolean insertUserData(PipeData pipeData) {
        boolean flag = false;
        flag = getUserInfoDao().insert(pipeData) == -1 ? false : true;
        return flag;
    }
 
    /**
     * 插入或替换数据
     *
     * @param pipeData
     * @return
     */
    public boolean insertOrReplaceData(PipeData pipeData) {
        boolean flag = false;
        try {
            flag = getUserInfoDao().insertOrReplace(pipeData) == -1 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 插入多条数据  子线程完成
     *
     * @param list
     * @return
     */
    public boolean insertMultiData(final List<PipeData> list) {
        boolean flag = false;
        try {
            getUserInfoDao().getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    Log.e("INSERT TAG","==================插入开始"+System.currentTimeMillis());
                    for (PipeData PipeData : list) {
                        daoManager.getDaoSession().insert(PipeData);
                    }
                    Log.e("INSERT TAG","=================插入结束"+System.currentTimeMillis());
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
 
    /**
     * 插入多条数据  子线程完成
     *
     * @param list
     * @return
     */
    public boolean insertOrReplaceMultiData(final List<PipeData> list) {
        boolean flag = false;
        try {
            getUserInfoDao().getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    Log.e("INSERT TAG","==================插入开始"+System.currentTimeMillis());
                    for (PipeData PipeData : list) {
                        daoManager.getDaoSession().insertOrReplace(PipeData);
                    }
                    Log.e("INSERT TAG","=================插入结束"+System.currentTimeMillis());
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
 
    /**
     * 更新数据
     *
     * @param PipeData
     * @return
     */
    public boolean updatePipeData(PipeData PipeData) {
        boolean flag = false;
        try {
            getUserInfoDao().update(PipeData);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
 
    /**
     * 根据id删除数据
     *
     * @param pipeData
     * @return
     */
    public boolean deletePipeData(PipeData pipeData) {
        boolean flag = false;
        try {
            getUserInfoDao().delete(pipeData);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
 
    /**
     * 删除所有数据
     *
     * @return
     */
    public boolean deleteAllData() {
        boolean flag = false;
        try {
            getUserInfoDao().deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
 
    /**
     * 根据主键查询
     *
     * @param key
     * @return
     */
    public PipeData queryPipeDataById(long key) {
        return getUserInfoDao().load(key);
    }
 
    /**
     * 查询所有数据
     *
     * @return
     */
    public List<PipeData> queryAllData() {
        return getUserInfoDao().loadAll();
    }
 
    /**
     * 根据名称查询 以年龄降序排列
     *
     * @param name
     * @return
     */
    public List<PipeData> queryUserByName(String name) {
        Query<PipeData> build = null;
        try {
            build = getUserInfoDao().queryBuilder()
                    .where(PipeDataDao.Properties.ObjectId.eq(name))
                    .orderDesc(PipeDataDao.Properties.LocalityRoad)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return build.list();
    }
 
    /**
     * 根据参数查询
     *
     * @param where
     * @param param
     * @return
     */
    public List<PipeData> queryPipeDataByParams(String where, String... param) {
        return getUserInfoDao().queryRaw(where, param);
    }
 
    public PipeDataDao getUserInfoDao() {
        return daoManager.getDaoSession().getPipeDataDao();
    }
}