package com.yangkang.ssmdemo01.mvc.dao.impl;

import com.yangkang.ssmdemo01.mvc.dao.Dao;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Priority;
import javax.annotation.Resource;
import java.util.List;

@Repository
public class DaoSupport implements Dao {

    @Resource(name = "sqlSessionTemplate5")
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 保存对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public Object save(String str, Object obj) throws Exception {
        return sqlSessionTemplate.insert(str,obj);
    }

    /**
     * 批量保存对象
     *
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    @Override
    public Object saveBatch(String str, List objs) throws Exception {
        return sqlSessionTemplate.insert(str,objs);
    }

    /**
     * 修改对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public Object update(String str, Object obj) throws Exception {
        return sqlSessionTemplate.update(str,obj);
    }

    /**
     * 批量修改对象
     *
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    @Override
    public Object updateBatch(String str, List objs) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        //批量执行器
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        try {
            if(objs!=null){
                for(int i=0,size=objs.size();i<size;i++){
                    sqlSession.update(str,objs.get(i));
                }
                sqlSession.flushStatements();
                sqlSession.commit();
                sqlSession.clearCache();
            }
        } finally {
            sqlSession.close();
        }
        return null;
    }

    /**
     * 删除对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public Object delete(String str, Object obj) throws Exception {
        return sqlSessionTemplate.delete(str,obj);
    }

    /**
     * 批量删除对象
     *
     * @param str
     * @param objs
     * @return
     * @throws Exception
     */
    @Override
    public Object deleteBatch(String str, List objs) throws Exception {
        return sqlSessionTemplate.delete(str,objs);
    }

    /**
     * 查找对象
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public Object findForObject(String str, Object obj) throws Exception {
        return sqlSessionTemplate.selectOne(str,obj);
    }

    /**
     * 查找对象集合
     *
     * @param str
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public Object findForList(String str, Object obj) throws Exception {
        return sqlSessionTemplate.selectList(str,obj);
    }

    /**
     * 查找对象封装成map
     *
     * @param sql
     * @param obj
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public Object findForMap(String str, Object obj, String key, String value) throws Exception {
        return sqlSessionTemplate.selectMap(str,obj,key);
    }
}
