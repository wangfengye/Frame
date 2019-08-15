package com.maple.frame.dbUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maple on 2019/6/14 16:51.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class BaseDao<T> implements IBaseDao<T> {
    public static final String TAG = "BaseDao";
    private SQLiteDatabase mDatabase;
    private String mTableName;
    private HashMap<String, Field> mMap;//缓存反射得到的字段名称及字段
    private boolean mInit;
    private Class<?> mClz;

    protected boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (mInit) return true;
        mDatabase = sqLiteDatabase;
        mClz= entity;
        // 获取表名
        mTableName = entity.getAnnotation(Entity.class).value();
        Field[] fields = entity.getDeclaredFields();
        initMap(fields);
        //创建表
        String sql = buildCreateSql(fields);
        mDatabase.execSQL(sql);
        mInit = true;
        return true;
    }

    protected BaseDao() {
    }

    /**
     * 初始化 字段缓存
     *
     * @param fields 字段
     */
    private void initMap(Field[] fields) {
        mMap = new HashMap<>();
        for (Field f : fields) {
            String dbFieldName = f.getAnnotation(DbField.class).value();
            mMap.put(dbFieldName, f);
        }
    }

    /**
     * 创建数据库语句拼接
     * 反射遍历,
     * 子类可重写优化创建sql.
     *
     * @param fields 字段
     * @return sql
     */
    String buildCreateSql(Field[] fields) {
        StringBuilder createSql = new StringBuilder();
        createSql.append("create table if not exists ").append(mTableName).append(" ( ");
        // usertable(_id integer primary key autoincrement, sname text, snumber text) "
        for (Field f : fields) {
            if (f.getAnnotation(DbField.class) == null) continue;
            String dbFieldName = f.getAnnotation(DbField.class).value();
            createSql.append(dbFieldName);
            if (f.getType() == int.class || f.getType() == Integer.class) {
                createSql.append(" integer, ");
            } else if (f.getType() == String.class) {
                createSql.append(" text, ");
            } else if (f.getType() == double.class
                    || f.getType() == float.class
                    || f.getType() == Double.class
                    || f.getType() == Float.class) {
                createSql.append(" REAL, ");
            } else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                createSql.append(" NUMERIC, ");
            } else {
                Log.e(TAG, "BaseDao: " + dbFieldName);
            }
        }
        // 删除末尾多余的", "
        createSql.setLength(createSql.length() - 2);
        createSql.append(" ) ");
        return createSql.toString();
    }

    /**
     * 对象转键值对;
     *
     * @param o 对象
     * @return 键值对
     */
    private Map<String, String> o2Map(T o) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Field> entry : mMap.entrySet()) {
            entry.getValue().setAccessible(true);
            try {
                Object va = entry.getValue().get(o);
                if (va != null) map.put(entry.getKey(), va.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public boolean insert(T o) {
        ContentValues contentValues = new ContentValues();
        Map<String, String> map = o2Map(o);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            contentValues.put(entry.getKey(), entry.getValue());
        }
        return mDatabase.insert(mTableName, null, contentValues) > 0;
    }

    @Override
    public boolean update(T o) {
        return false;
    }

    @Override
    public boolean delete(T o) {
        return false;
    }

    @Override
    public T find(T o) {
        return null;
    }

    @Override
    public List<T> findAll() {
        Cursor cursor = mDatabase.query(mTableName, null, null, null, null, null, null);
        List<T> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    T t = (T) mClz.newInstance();
                    for (Map.Entry<String, Field> e :
                            mMap.entrySet()) {
                        String value = cursor.getString(cursor.getColumnIndex(e.getKey()));
                        e.getValue().set(t,value);
                    }
                    list.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
