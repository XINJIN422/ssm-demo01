package com.yangkang.ssmdemo01.tools;

import com.yangkang.ssmdemo01.tools.dbtobean.CreateBeanConfig;
import com.yangkang.ssmdemo01.tools.dbtobean.CreateBeans;

/**
 * DbToBeanUtil
 *
 * @author yangkang
 * @date 2018/9/6
 */
public class DbToBeanUtil {
    public static void main(String[] args) throws Exception{
        String packageName = "com.yangkang.ssmdemo01.mvc.entity";
        String packagePath = "D:\\WorkFile\\Idea\\workspace_svn\\ssm-demo01\\src\\main\\java\\com\\yangkang\\ssmdemo01\\mvc\\entity";
        String[] tableNames = new String[]{"user2"};
        CreateBeanConfig appConfig = new CreateBeanConfig();
//        appConfig.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        appConfig.setDburl("jdbc:oracle:thin:@IP:PORT:DBNAME");
//        appConfig.setDbusername("username");
//        appConfig.setDbpassword("password");
        appConfig.setPackageName(packageName);
        appConfig.setPackagePath(packagePath);
        appConfig.setTableNames(tableNames);
        CreateBeans.createJavaBean(appConfig);
    }
}
