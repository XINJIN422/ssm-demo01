package com.yangkang.ssmdemo01.tools.dbtobean;

import com.google.common.base.CaseFormat;
import com.yangkang.ssmdemo01.tools.annotation.Column;
import com.yangkang.ssmdemo01.tools.annotation.Table;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CreateBeans
 *
 * @author yangkang
 * @date 2018/9/6
 */
public class CreateBeans {
    private static StringBuffer sb = null;
    private static boolean isDateType = false;
    private static boolean isBigDecimal = false;
    private static List<ColumnBeans> colList = null;
    private static CreateBeanConfig appConfig = null;
    private static Map<String,Object> fmTempl = null;

    public CreateBeans(){
    }

    public static void createJavaBean(CreateBeanConfig appConfig) throws SQLException, ClassNotFoundException, IOException, TemplateException {
        CreateBeans.appConfig = appConfig;
        Connection conn = null;
        try{
            conn = ConnectionTools.getConnection(appConfig.getDriverClassName(), appConfig.getDburl(), appConfig.getDbusername(), appConfig.getDbpassword());
            for (String tableName : appConfig.getTableNames()){
                run(conn, tableName);
            }
        }finally {
            ConnectionTools.closeConnection(null,null,conn);
        }
    }

    private static void run(Connection conn, String tableName) throws SQLException, IOException, TemplateException {
        sb = new StringBuffer();
        fmTempl = new HashMap<>();
        isDateType = false;
        isBigDecimal = false;
        colList = new ArrayList<>();
        createClassMessage(conn,tableName);
        sb.append("@Table(\"" + tableName.toUpperCase() + "\")\r");
        sb.append("public class " + createClassName(tableName) + " implements Serializable" + " {\r\r");
        sb.append("\tprivate static final long serialVersionUID = 1L;\r\r");
        queryFieldNames(conn,tableName);
        createFieldNames();
        createMethod();
        sb.append("}");
        String beanStr = sb.toString();
        if (isDateType)
            beanStr = beanStr.replaceFirst("#date#","import java.util.Date;\r");
        else
            beanStr = beanStr.replaceFirst("#date#","");
        if (isBigDecimal)
            beanStr = beanStr.replaceFirst("#bigDecimal#","import java.math.BigDecimal;\r");
        else
            beanStr = beanStr.replaceFirst("#bigDecimal#","");
//        createBeanFiles(beanStr, tableName);
        //这个方法用了非阻塞式IO流,以及freemarker模板引擎文件生成工具
        createBeanFilesNIO();
    }

    private static void createClassMessage(Connection conn, String tableName) throws SQLException {
        String tableDesc = queryTableComments(conn, tableName);
        if (tableDesc == null){
            tableDesc = "";
        }
        if (!StringUtils.isEmpty(appConfig.getPackageName())){
            sb.append("package "+appConfig.getPackageName() + ";\r\r");
        }
        sb.append("import "+ Table.class.getName()+";\r");
        sb.append("import "+ Column.class.getName()+";\r");
        sb.append("import "+ Serializable.class.getName()+";\r");
        sb.append("#date#");
        sb.append("#bigDecimal#");
        sb.append("\r");
        sb.append("/**\r");
        sb.append(" * @Type "+ createClassName(tableName)+ "\r");
        sb.append(" * @Desc "+ tableDesc + "\r");
        sb.append(" * @author "+ System.getProperty("user.name")+ "\r");
        sb.append(" * @date "+ createClassTime() + "\r");
        sb.append(" * 1.本类由工具类DbToBeanUtil自动生成\r");
        sb.append(" * 2.默认读取resources下的jdbc.properties配置文件,也可以在main函数里设置覆盖相关属性\r");
        sb.append(" * 3.不建议直接修改本类,必要时建议创建子类扩展\r");
        sb.append(" */\r");

        fmTempl.put("tableDesc",tableDesc);
        fmTempl.put("userName",System.getProperty("user.name"));
        fmTempl.put("createDate",createClassTime());
        fmTempl.put("tableName",tableName.toUpperCase());
        fmTempl.put("className",createClassName(tableName));
    }

    private static String queryTableComments(Connection conn, String tableName) throws SQLException {
        ResultSet rs = null;
        rs = conn.getMetaData().getTables(null, getSchema(conn), tableName.toUpperCase(), new String[]{"TABLE"});
        try {
            while (rs.next()){
                if (rs.getString("REMARKS") != null){
                    return rs.getString("REMARKS");
                }
            }
        } finally {
            ConnectionTools.closeConnection(rs,null,null);
        }
        return null;
    }

    private static String getSchema(Connection conn) throws SQLException {
//        String connUrl = conn.getMetaData().getURL().toLowerCase();
        String dbUrl = appConfig.getDburl().toLowerCase();
        if (dbUrl.startsWith("jdbc:oracle")){
//            String schema = conn.getMetaData().getUserName();
            return appConfig.getDbusername().toUpperCase();
        } else {
            return "%";
        }
    }

    private static String createClassName(String tableName) {
        if (StringUtils.isEmpty(tableName)){
            throw new RuntimeException("表名不能为空!");
        }else{
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,tableName);
        }
    }

    private static String createClassTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void queryFieldNames(Connection conn, String tableName) throws SQLException {
        colList = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = conn.getMetaData().getColumns(null, getSchema(conn), tableName, "%");
            while (rs.next()){
                ColumnBeans columnBeans = new ColumnBeans();
                columnBeans.setClassName(createClassName(tableName));
                columnBeans.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,rs.getString("COLUMN_NAME")));
                columnBeans.setType(TypeMapper.getMapperClass(rs.getInt("DATA_TYPE")).getSimpleName());
                if (rs.getInt("DATA_TYPE") == Types.DECIMAL)
                    isBigDecimal = true;
                else if (rs.getInt("DATA_TYPE") == Types.DATE || rs.getInt("DATA_TYPE") == Types.TIME || rs.getInt("DATA_TYPE") == Types.TIMESTAMP)
                    isDateType = true;
                columnBeans.setcName(rs.getString("COLUMN_NAME").toUpperCase());
                columnBeans.setcType(rs.getInt("DATA_TYPE"));
                columnBeans.setLength(rs.getInt("COLUMN_SIZE"));
//                columnBeans.setLength(rs.getInt("BUFFER_LENGTH"));
//                columnBeans.setLength(rs.getInt("DECIMAL_DIGITS"));
                columnBeans.setAutoIncrement(rs.getBoolean("IS_AUTOINCREMENT"));
                columnBeans.setComment(rs.getString("REMARKS"));
                colList.add(columnBeans);
            }
        }finally {
            ConnectionTools.closeConnection(rs, null, null);
        }
    }

    private static void createFieldNames(){
        Iterator<ColumnBeans> it = colList.iterator();
        List<Map<String, Object>> fields = new LinkedList<>();
        while (it.hasNext()){
            ColumnBeans columnBeans = it.next();
            sb.append("\t/**\r");
            sb.append("\t * ");
            sb.append(columnBeans.getComment()+"\r");
            sb.append("\t */\r");
            sb.append("\t@Column(\""+columnBeans.getcName()+"\")\r");
            sb.append("\tprivate " + columnBeans.getType() + " " + columnBeans.getName() + ";\r\r");

            Map<String, Object> item = new HashMap<>();
            item.put("colName",columnBeans.getcName());
            item.put("javName",columnBeans.getName());
            item.put("javNameCap",CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,columnBeans.getName()));
            item.put("javType",columnBeans.getType());
            fields.add(item);
        }
        fmTempl.put("fields", fields);
    }

    private static void createMethod(){
        Iterator<ColumnBeans> it = colList.iterator();
        while (it.hasNext()){
            ColumnBeans columnBeans = it.next();
            sb.append("\tpublic " + columnBeans.getType() + " get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,columnBeans.getName()) + "() {\r");
            sb.append("\t\treturn " + columnBeans.getName() + ";\r");
            sb.append("\t}\r\r");
            sb.append("\tpublic void set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,columnBeans.getName()) + "(" + columnBeans.getType() + " " + columnBeans.getName() + ") {\r");
            sb.append("\t\tthis." + columnBeans.getName() + " = " + columnBeans.getName() + ";\r");
            sb.append("\t}\r\r");
        }
    }

    private static void createBeanFiles(String beanStr, String tableName) throws IOException {
        File packPath = new File(appConfig.getPackagePath());
        if(!packPath.exists() && !packPath.isDirectory()){
            packPath.mkdir();
        }

        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(packPath + "\\" + createClassName(tableName) + ".java");
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(beanStr);
            bw.flush();
        }finally {
            bw.close();
            fos.close();
        }
    }

    private static void createBeanFilesNIO() throws IOException, TemplateException {
        fmTempl.put("packageName",appConfig.getPackageName());
        if (isDateType)
            fmTempl.put("isDateType","import java.util.Date;\r");
        else
            fmTempl.put("isDateType","");
        if (isBigDecimal)
            fmTempl.put("isBigDecimal","import java.math.BigDecimal;\r");
        else
            fmTempl.put("isBigDecimal","");

        Path packPath = Paths.get(appConfig.getPackagePath());
        Files.createDirectories(packPath);
        Path javabeanPath = Paths.get(appConfig.getPackagePath(), fmTempl.get("className").toString() + ".java");
        BufferedWriter bw = Files.newBufferedWriter(javabeanPath);

        Configuration cfg = new Configuration();
//        cfg.setTemplateLoader(new ClassTemplateLoader(CreateBeans.class, ""));
        cfg.setTemplateLoader(new FileTemplateLoader(new File("./src/main/resources")));
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("/template/JavaBeanTemplate.ftl");
        template.process(fmTempl, bw);

        bw.flush();
        bw.close();
    }
}
