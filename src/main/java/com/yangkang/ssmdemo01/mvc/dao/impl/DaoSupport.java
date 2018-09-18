package com.yangkang.ssmdemo01.mvc.dao.impl;

import com.google.common.base.CaseFormat;
import com.yangkang.ssmdemo01.mvc.dao.Dao;
import com.yangkang.ssmdemo01.tools.annotation.Column;
import com.yangkang.ssmdemo01.tools.annotation.Table;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
     * 保存JavaBean对象
     * 利用反射与自定义注解,根据JavaBean对象动态生成数据库中的表及字段值
     *
     * @param javaBean
     * @return
     * @throws Exception
     */
    @Override
    public Object saveBean(Object javaBean) throws Exception {
        StringBuilder sqlStr = new StringBuilder();
        StringBuilder valueStr = new StringBuilder();
        Class<?> jbClass = javaBean.getClass();
        boolean classAnnotation = jbClass.isAnnotationPresent(Table.class);
        if (!classAnnotation){
            throw new Exception("请用DbToBeanUtil工具类生成该JavaBean后再试");
        }

        Table jbTable = jbClass.getAnnotation(Table.class);
        String tableName = jbTable.value();
        sqlStr.append("INSERT INTO " + tableName + "(");
        valueStr.append(") VALUE(");

        Field[] declaredFields = jbClass.getDeclaredFields();
        for (Field field : declaredFields){
            boolean fieldAnnotation = field.isAnnotationPresent(Column.class);
            if (!fieldAnnotation)
                continue;
            String fieldName = field.getName();
            String getMethodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
            Method getMethod = jbClass.getMethod(getMethodName);
            Object fieldValue = getMethod.invoke(javaBean);
            if (fieldValue == null)
                continue;
            Column jbColumn = field.getAnnotation(Column.class);
            String columnName = jbColumn.value();
            sqlStr.append(columnName + ",");
            Class<?> fieldType = field.getType();
            if (fieldType == String.class)
                valueStr.append("'" + fieldValue + "'");
            else if (fieldType == Integer.class || fieldType == BigDecimal.class)
                valueStr.append(fieldValue);
            else if (fieldType == Date.class)
                valueStr.append("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)fieldValue) + "'");
            else
                valueStr.append("'" + fieldValue + "'");        //sqltype是blob的以后再优化
            valueStr.append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length()-1);
        valueStr.deleteCharAt(valueStr.length()-1);
        valueStr.append(")");
        sqlStr.append(valueStr);
        HashMap<String, String> params = new HashMap<>();
        params.put("sqlStr",sqlStr.toString());
        return sqlSessionTemplate.insert("CommonSQL.assembledSql",params);
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
     * 批量保存JavaBean对象
     *
     * @param javaBeans
     * @return
     * @throws Exception
     */
    @Override
    public Object saveBeans(List<? extends Object> javaBeans) throws Exception {
        StringBuilder sqlStr = new StringBuilder();
        StringBuilder valueStr = new StringBuilder();
        Class<?> jbClass = javaBeans.get(0).getClass();
        boolean classAnnotation = jbClass.isAnnotationPresent(Table.class);
        if (!classAnnotation){
            throw new Exception("请用DbToBeanUtil工具类生成该JavaBean后再试");
        }
        String firstElemClass = jbClass.getSimpleName();
        Table jbTable = jbClass.getAnnotation(Table.class);
        String tableName = jbTable.value();
        sqlStr.append("INSERT INTO " + tableName + "(");
        valueStr.append(") VALUES(");
        Field[] declaredFields = jbClass.getDeclaredFields();
        LinkedList<Method> getMethods = new LinkedList<>();
        for (Field field : declaredFields){
            boolean fieldAnnotation = field.isAnnotationPresent(Column.class);
            if (!fieldAnnotation)
                continue;
            String fieldName = field.getName();
            String getMethodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
            Method getMethod = jbClass.getMethod(getMethodName);
            getMethods.add(getMethod);
            Object fieldValue = getMethod.invoke(javaBeans.get(0));
//            if (fieldValue == null)
//                continue;
            Column jbColumn = field.getAnnotation(Column.class);
            String columnName = jbColumn.value();
            sqlStr.append(columnName + ",");
            Class<?> fieldType = field.getType();
            if (fieldValue == null)
                valueStr.append("null");
            else if (fieldType == String.class)
                valueStr.append("'" + fieldValue + "'");
            else if (fieldType == Integer.class || fieldType == BigDecimal.class)
                valueStr.append(fieldValue);
            else if (fieldType == Date.class)
                valueStr.append("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)fieldValue) + "'");
            else
                valueStr.append("'" + fieldValue + "'");        //sqltype是blob的以后再优化
            valueStr.append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length()-1);
        valueStr.deleteCharAt(valueStr.length()-1);
        valueStr.append("),");

        for (int i = 1; i <javaBeans.size(); i++){
            if (jbClass != javaBeans.get(i).getClass())
                throw new Exception("请保持集合里的元素属于同一个类,这样效率会高一些;另外建议使用ArrayList作为此参数集合类!");
            valueStr.append("(");
            for (Method getMethod : getMethods){
                Object fieldValue = getMethod.invoke(javaBeans.get(i));
//                if (fieldValue == null)
//                    continue;
                if (fieldValue == null)
                    valueStr.append("null");
                else if (fieldValue instanceof String)
                    valueStr.append("'" + fieldValue + "'");
                else if (fieldValue instanceof Integer || fieldValue instanceof BigDecimal)
                    valueStr.append(fieldValue);
                else if (fieldValue instanceof Date)
                    valueStr.append("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)fieldValue) + "'");
                else
                    valueStr.append("'" + fieldValue + "'");        //sqltype是blob的以后再优化
                valueStr.append(",");
            }
            valueStr.deleteCharAt(valueStr.length()-1);
            valueStr.append("),");
        }
        valueStr.deleteCharAt(valueStr.length()-1);
        sqlStr.append(valueStr);
        HashMap<String, String> params = new HashMap<>();
        params.put("sqlStr",sqlStr.toString());
        return sqlSessionTemplate.insert("CommonSQL.assembledSql",params);
    }

    /**
     * 批量保存JavaBean对象(集合中可以为不同对象,并且针对大对象作分批次提交)
     *
     * @param javaBeans
     * @return
     * @throws Exception
     */
    @Override
    public Object saveBeans2(List<?> javaBeans) throws Exception {
        StringBuilder sqlStr = new StringBuilder();
        StringBuilder valueStr = new StringBuilder();
        int flag = 0;
        SqlSession batchSqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            for (int i = 0; i < javaBeans.size(); i++){
                Class<?> jbClass = javaBeans.get(i).getClass();
                boolean classAnnotation = jbClass.isAnnotationPresent(Table.class);
                if (!classAnnotation){
                    throw new Exception("请用DbToBeanUtil工具类生成该JavaBean后再试");
                }
                sqlStr.setLength(0);
                valueStr.setLength(0);
                Table jbTable = jbClass.getAnnotation(Table.class);
                String tableName = jbTable.value();
                sqlStr.append("INSERT INTO " + tableName + "(");
                valueStr.append(") VALUE(");

                Field[] declaredFields = jbClass.getDeclaredFields();
                for (Field field : declaredFields){
                    boolean fieldAnnotation = field.isAnnotationPresent(Column.class);
                    if (!fieldAnnotation)
                        continue;
                    String fieldName = field.getName();
                    String getMethodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
                    Method getMethod = jbClass.getMethod(getMethodName);
                    Object fieldValue = getMethod.invoke(javaBeans.get(i));
                    if (fieldValue == null)
                        continue;
                    Column jbColumn = field.getAnnotation(Column.class);
                    String columnName = jbColumn.value();
                    sqlStr.append(columnName + ",");
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class)
                        valueStr.append("'" + fieldValue + "'");
                    else if (fieldType == Integer.class || fieldType == BigDecimal.class)
                        valueStr.append(fieldValue);
                    else if (fieldType == Date.class)
                        valueStr.append("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)fieldValue) + "'");
                    else
                        valueStr.append("'" + fieldValue + "'");        //sqltype是blob的以后再优化
                    valueStr.append(",");
                }
                sqlStr.deleteCharAt(sqlStr.length()-1);
                valueStr.deleteCharAt(valueStr.length()-1);
                valueStr.append(")");
                sqlStr.append(valueStr);
                HashMap<String, String> params = new HashMap<>();
                params.put("sqlStr",sqlStr.toString());
                flag += batchSqlSession.insert("CommonSQL.assembledSql",params);
                if (i!=0 && i%500==0) {
                    //每500条提交一次,对象大的时候可以减小
//                    batchSqlSession.flushStatements();
                    batchSqlSession.commit();
//                    batchSqlSession.clearCache();
                }
            }
//            batchSqlSession.flushStatements();
            batchSqlSession.commit();
//            batchSqlSession.clearCache();
        }finally {
            batchSqlSession.close();
        }
        return flag;
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
     * @param str
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
