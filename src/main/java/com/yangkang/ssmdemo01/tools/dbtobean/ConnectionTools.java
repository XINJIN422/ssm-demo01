package com.yangkang.ssmdemo01.tools.dbtobean;

import java.sql.*;
import java.util.Properties;

/**
 * ConnectionTools
 *
 * @author yangkang
 * @date 2018/9/6
 */
public class ConnectionTools {
    public ConnectionTools(){
    }

    public static Connection getConnection(String driverClassName, String url, String user, String password) throws ClassNotFoundException, SQLException {
        Properties props = new Properties();
        props.put("user",user);
        props.put("password",password);

        Class.forName(driverClassName);
        if(driverClassName.indexOf("oracle") != -1){
            props.setProperty("remarks","true");
        }

        return DriverManager.getConnection(url, props);
    }

    public static void closeConnection(ResultSet rs, PreparedStatement pre, Connection conn) throws SQLException {
        if(rs != null){
            rs.close();
        }

        if(pre != null){
            pre.close();
        }

        if(conn != null){
            conn.close();
        }
    }
}
