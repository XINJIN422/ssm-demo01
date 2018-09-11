package com.yangkang.ssmdemo01.tools.dbtobean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * CreateBeanConfig
 *
 * @author yangkang
 * @date 2018/9/6
 */
public class CreateBeanConfig {
    private String driverClassName;
    private String dburl;
    private String dbusername;
    private String dbpassword;
    private String packageName;
    private String packagePath;
    private String[] tableNames;

    public CreateBeanConfig() throws IOException {
        Properties prop = new Properties();
        //第一种读取properties配置文件方式
//        File file = new File("src/main/resources/jdbc.properties");
//        InputStream is  = new FileInputStream(file);
        //第二种
        InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        prop.load(is2);
        driverClassName = prop.getProperty("jdbc.driver","");
        dburl = prop.getProperty("jdbc.url","");
        dbusername = prop.getProperty("jdbc.username","");
        dbpassword = prop.getProperty("jdbc.password","");
        packageName = prop.getProperty("javabean.packagename","");
        packagePath = prop.getProperty("javabean.packagepath","");
        tableNames = prop.getProperty("javabean.tablenames","").split(",");
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDburl() {
        return dburl;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    public String getDbusername() {
        return dbusername;
    }

    public void setDbusername(String dbusername) {
        this.dbusername = dbusername;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String[] getTableNames() {
        return tableNames;
    }

    public void setTableNames(String[] tableNames) {
        this.tableNames = tableNames;
    }
}
