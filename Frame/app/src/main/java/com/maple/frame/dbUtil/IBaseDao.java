package com.maple.frame.dbUtil;

import java.util.List;

/**
 * @author maple on 2019/6/14 16:49.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public interface IBaseDao<T> {
    boolean insert(T t);

    boolean update(T t);

    boolean delete(T t);

    T find(T t);

    List<T> findAll();
}
