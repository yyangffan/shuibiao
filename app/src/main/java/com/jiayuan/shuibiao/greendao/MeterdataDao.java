package com.jiayuan.shuibiao.greendao;

import com.jiayuan.shuibiao.entity.MeterdataTempStorage;
import com.jiayuan.shuibiao.gen.MeterdataTempStorageDao;

import java.util.List;

public class MeterdataDao {


    private final GreenDaoManager daoManager;
    private static MeterdataDao mMeterdataDao;

    public MeterdataDao() {
        daoManager = GreenDaoManager.getInstance();
    }

    public static MeterdataDao getInstance() {
        if (mMeterdataDao == null) {
            mMeterdataDao = new MeterdataDao();
        }
        return mMeterdataDao;
    }

    /**
     * 插入或替换数据
     *
     * @param meterdataTempStorage
     * @return
     */
    public boolean insertOrReplaceData(MeterdataTempStorage meterdataTempStorage) {
        boolean flag = false;
        try {
            flag = getMeterdataTempStorageDao().insertOrReplace(meterdataTempStorage) == -1 ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据参数查询
     *
     * @param where
     * @param param
     * @return
     */
    public List<MeterdataTempStorage> queryMeterdataTempStorageByParams(String where, String... param) {
        return getMeterdataTempStorageDao().queryRaw(where, param);
    }


    public MeterdataTempStorageDao getMeterdataTempStorageDao() {
        return daoManager.getDaoSession().getMeterdataTempStorageDao();
    }

    public List<MeterdataTempStorage> loadAll(){
        return getMeterdataTempStorageDao().loadAll();
    }

    public void deleteAll(){
        getMeterdataTempStorageDao().deleteAll();
    }



    /**
     * 根据id删除数据
     *
     * @param meterdataTempStorage
     * @return
     */
    public boolean deleteMeterdata(MeterdataTempStorage meterdataTempStorage) {
        boolean flag = false;
        try {
            getMeterdataTempStorageDao().delete(meterdataTempStorage);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
