package com.maple.frame.dbUtil;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * @author maple on 2019/6/14 17:11.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class DaoFactory {
    private static HashMap<Class, BaseDao<?>> sMap = new HashMap<>();
    private String sqliteDatabasePath;
    private SQLiteDatabase sqLiteDatabase;

    private DaoFactory() {
        sqliteDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/user.db";
        // 如果目录结构不存在要手动创建,
      /*  File dir = new File(sqliteDatabasePath);
     if (!dir.exists()) dir.mkdirs();*/
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }
    private static DaoFactory INSTANCE = new DaoFactory();
    public static DaoFactory get(){
        return INSTANCE;
    }

    public synchronized <T extends BaseDao<M>, M> T getDao(Class<T> t, Class<M> m) {
        T dao;
        if (!sMap.containsKey(t)) {
            try {
                dao = t.newInstance();
                dao.init(m,sqLiteDatabase);
                sMap.put(t, dao);
            } catch (Exception e) {
                throw new RuntimeException("Dao初始化异常"+e.getMessage());
            }

        } else {
            dao = (T) sMap.get(t);
        }
        return dao;
    }
}
