package com.maple.frame.dbUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author maple on 2019/6/14 16:51.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class BaseDao<T> implements IBaseDao<T> {
    private static final String TAG = "BaseDao";
    private SQLiteDatabase mDatabase;
    private String mTableName;
    private HashMap<String, Field> mMap;//缓存反射得到的字段名称及字段
    private boolean mInit;
    private Class<?> mClz;

    @SuppressWarnings("all")
    protected boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (mInit) return true;
        mDatabase = sqLiteDatabase;
        mClz = entity;
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
     * 创建数据库语句拼接
     * 反射遍历,
     * 子类可重写优化创建sql.
     *
     * @param fields 字段
     * @return sql
     */
    @SuppressWarnings("all")
    String buildCreateSql(Field[] fields) {
        StringBuilder createSql = new StringBuilder();
        createSql.append("create table if not exists ").append(mTableName).append(" ( ");
        // usertable(_id integer primary key autoincrement, sname text, snumber text) "
        for (Field f : fields) {
            DbField dbAnno = f.getAnnotation(DbField.class);
            if (dbAnno == null) continue;
            String dbFieldName = dbAnno.value();
            createSql.append(dbFieldName);
            if (f.getType() == int.class || f.getType() == Integer.class
                    || f.getType() == Long.class || f.getType() == long.class) {
                createSql.append(" integer");
            } else if (f.getType() == String.class) {
                createSql.append(" text");
            } else if (f.getType() == double.class
                    || f.getType() == float.class
                    || f.getType() == Double.class
                    || f.getType() == Float.class) {
                createSql.append(" REAL");
            } else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                createSql.append(" NUMERIC");
            } else {
                Log.e(TAG, "BaseDao: " + dbFieldName);
            }
            if (dbAnno.key()) {
                createSql.append(" PRIMARY KEY AUTOINCREMENT, ");
            } else {
                createSql.append(" , ");
            }
        }
        // 删除末尾多余的", "
        createSql.setLength(createSql.length() - 2);
        createSql.append(" ) ");
        return createSql.toString();
    }

    @Override
    public long insert(T o) {
        return mDatabase.insert(mTableName, null, getContentValues(o));
    }

    @Override
    public long update(T o) {
        Condition condition = new Condition(o, false);
        return mDatabase.update(mTableName, getContentValues(o), condition.whereClause.toString(), condition.whereArgs);
    }


    @Override
    public long delete(T o) {
        Condition condition = new Condition(o);
        return mDatabase.delete(mTableName, condition.whereClause.toString(), condition.whereArgs);
    }
    @Override
    public T find(T o) {
        // 手动拼接方式
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(mTableName).append(" where 1=1 ");
        for (Map.Entry<String, Field> e :
                mMap.entrySet()) {
            try {
                e.getValue().setAccessible(true);
                Object tmp = e.getValue().get(o);
                if (tmp != null) {
                    sql.append("AND ").append(e.getKey()).append(" ='").append(tmp).append("'");

                }
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        Log.d(TAG, "find: " + sql.toString());
        Cursor cursor = mDatabase.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                return getFromCursor(cursor);
            } catch (Exception e) {
                throw new RuntimeException("find 执行异常: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        Cursor cursor = mDatabase.query(mTableName, null, null, null, null, null, null);
        List<T> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {

                    list.add(getFromCursor(cursor));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("不支持的解析类型" + e.getMessage());
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    @SuppressWarnings("unchecked")
    private T getFromCursor(Cursor cursor) throws InstantiationException, IllegalAccessException {
        // todo: 构造函数添加中间层进行对象缓存.参考greendao缓存机制
        T t = (T) mClz.newInstance();
        for (Map.Entry<String, Field> e :
                mMap.entrySet()) {
            Object value;
            if (e.getValue().getType() == String.class) {
                value = cursor.getString(cursor.getColumnIndex(e.getKey()));
            } else if (e.getValue().getType() == Long.class) {
                value = cursor.getLong(cursor.getColumnIndex(e.getKey()));
            } else {//todo 扩展可解析的类型
                value = cursor.getBlob(cursor.getColumnIndex(e.getKey()));
            }
            e.getValue().setAccessible(true);
            e.getValue().set(t, value);
        }
        return t;
    }

    private Map<String, String> getValues(T entity) {
        HashMap<String, String> result = new HashMap<>();
        Iterator<Field> filedsIterator = mMap.values().iterator();
        //循环遍历 映射map的  Field
        while (filedsIterator.hasNext()) {
            Field columnField = filedsIterator.next();
            String cacheKey = null;
            String cacheValue = null;
            if (columnField.getAnnotation(DbField.class) != null) {
                cacheKey = columnField.getAnnotation(DbField.class).value();
            } else {
                cacheKey = columnField.getName();
            }
            try {
                if (null == columnField.get(entity)) {
                    continue;
                }
                cacheValue = columnField.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey, cacheValue);
        }

        return result;
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

    /**
     * @param o 传入数据
     * @return ContentValues
     */
    private ContentValues getContentValues(T o) {
        ContentValues contentValues = new ContentValues();
        Map<String, String> map = o2Map(o);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            contentValues.put(entry.getKey(), entry.getValue());
        }
        return contentValues;
    }

    /**
     * 初始化 字段缓存
     *
     * @param fields 字段
     */
    private void initMap(Field[] fields) {
        mMap = new HashMap<>();
        for (Field f : fields) {
            if (f.getAnnotation(DbField.class) == null) continue;
            String dbFieldName = f.getAnnotation(DbField.class).value();
            mMap.put(dbFieldName, f);
        }
    }

    /**
     * 封装where语句
     */
    private class Condition {
        // 查询条件 name=?
        private StringBuilder whereClause = new StringBuilder();
        private String[] whereArgs;

        private Condition(T o) {
            this(o, false);
        }

        private Condition(T o, boolean onlyKey) {
            ArrayList<String> args = new ArrayList<>();
            whereClause.append(" 1=1 ");
            for (Map.Entry<String, Field> e :
                    mMap.entrySet()) {
                DbField anno = e.getValue().getAnnotation(DbField.class);
                if (!onlyKey || (anno != null && anno.key())) {//只用主键当查询条件

                    try {
                        e.getValue().setAccessible(true);
                        Object v = e.getValue().get(o);
                        if (v == null) continue;
                        whereClause.append(" AND ").append(e.getKey()).append("=?");
                        args.add(v.toString());
                    } catch (Exception e1) {
                        throw new RuntimeException("Condition" + e1.getMessage());
                    }
                }
            }
            // 数组长度在toArray中会进行重置.
            whereArgs = args.toArray(new String[0]);
        }
    }
}
