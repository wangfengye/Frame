package com.maple.frame.dbUtil;

import java.util.List;

/**
 * @author maple on 2019/6/14 16:49.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public interface IBaseDao<T> {
    long insert(T t);

    /**
     * 根据id xiu改
     * @param t 修改后的对象
     * @return 是否修改成功
     */
    long update(T t);

    long delete(T t);

    /**
     * 根据id查询
     * @param t
     * @return
     */
    T find(T t);

    List<T> findAll();
}
