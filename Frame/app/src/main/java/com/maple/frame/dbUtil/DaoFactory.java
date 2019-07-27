package com.maple.frame.dbUtil;

import java.util.HashMap;

/**
 * @author maple on 2019/6/14 17:11.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class DaoFactory {
    private static HashMap<Class, BaseDao> sMap=new HashMap<>();
    //todo:不支持并发
    public static <T> BaseDao<T> getDao(Class<T> table) {
        BaseDao<T> dao = sMap.get(table);
        if (dao == null) {
            dao = new BaseDao<>(table);
            sMap.put(table, dao);
        }
        return dao;
    }
}
