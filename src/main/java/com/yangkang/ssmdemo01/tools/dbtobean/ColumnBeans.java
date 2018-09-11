package com.yangkang.ssmdemo01.tools.dbtobean;

/**
 * ColumnBeans
 *
 * @author yangkang
 * @date 2018/9/6
 */
public class ColumnBeans {
    //所属javabean类名
    private String className;
    //列的成员变量名
    private String name;
    //列的Java类型
    private String type;
    //列的数据库名(大写)
    private String cName;
    //列的数据库类型
    private int cType;
    //列在数据库中所能容纳的最大字符数
    private int length;
    //列是否自增长
    private boolean autoIncrement;
    //列的备注
    private String comment;

    public ColumnBeans() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
