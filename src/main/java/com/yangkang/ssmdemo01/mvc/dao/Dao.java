package com.yangkang.ssmdemo01.mvc.dao;

import java.util.List;

/**
 * 通用dao接口
 */
public interface Dao {

    /**
     * 保存对象
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object save(String str,Object obj) throws Exception;

    /**
     * 批量保存对象
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    public Object saveBatch(String str,List objs) throws Exception;

    /**
     * 修改对象
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object update(String str,Object obj) throws Exception;

    /**
     * 批量修改对象
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    public Object updateBatch(String str,List objs) throws Exception;

    /**
     * 删除对象
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object delete(String str,Object obj) throws Exception;

    /**
     * 批量删除对象
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    public Object deleteBatch(String str,List objs) throws Exception;

    /**
     * 查找对象
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForObject(String str,Object obj) throws Exception;

    /**
     * 查找对象集合
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    public Object findForList(String str,Object obj) throws Exception;

    /**
     * 查找对象封装成map
     * @param sql
     * @param obj
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public Object findForMap(String str,Object obj,String key,String value) throws Exception;

}
