package com.yangkang.ssmdemo01.mvc.controller;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.yangkang.ssmdemo01.mvc.entity.User;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

/**
 * MysqlFileController
 * 测试mybatis调用sql语句,导出csv文件和导入csv文件
 * 测试OpenCSV读取并解析csv数据文件的接口
 * 测试UnivocityParsers读取并解析csv数据文件的接口
 *
 * @author yangkang
 * @date 2018/12/5
 */
@Controller
@RequestMapping("/MysqlFile")
public class MysqlFileController {
    private Logger logger = LoggerFactory.getLogger(MysqlFileController.class);

    @Resource
    private IUserService userService;

    @RequestMapping("/testOutFile")
    public void testOutFile(HttpServletRequest request, HttpServletResponse response){
        //成功返回行数,失败返回-1
        int result = userService.testOutFile();
        logger.info(String.valueOf(result));
    }

    @RequestMapping("/testInFile")
    public void testInFile(HttpServletRequest request, HttpServletResponse response){
        //成功返回插入记录数,失败返回-1
        int result = userService.testInFile();
        logger.info(String.valueOf(result));
    }

    @RequestMapping("/testOpenCsvReader")
    public void testOpenCsvReader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //D:/WorkFile/TMP/auto.csv后缀名也可以是txt等
        InputStreamReader in = new InputStreamReader(new FileInputStream("D:/WorkFile/TMP/auto.csv"), "UTF-8");
        //方式一:
        //CSVReader构造函数参数:separator分隔符默认逗号;quotechar引号字符默认双引号;escapechar转移字符默认双引号;lineend行结束符默认\n或\r\n
        long timeStamp1 = new Date().getTime();
        CSVReader csvReader = new CSVReader(in);
        List<String[]> allRecords = csvReader.readAll();
        System.out.println("OpenCSV解析方式1用时:----------" + (new Date().getTime() - timeStamp1) + "ms");
//        for (String[] records : allRecords){
//            for (String field : records)
//                System.out.print(field + " ");
//            System.out.println();
//        }
        csvReader.close();
        in.close();

        //方式二:
        //直接转成对象
        InputStreamReader in2 = new InputStreamReader(new FileInputStream("D:/WorkFile/TMP/auto.txt"), "UTF-8");
        long timeStamp2 = new Date().getTime();
        HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
        strategy.setType(User.class);
        //链式编程:后跟.withSeparator(","),.withSkipLines(0)等来设置分隔符等参数
        CsvToBean<User> csvtoBean = new CsvToBeanBuilder<User>(in2).withMappingStrategy(strategy).build();
        List<User> userList = csvtoBean.parse();
        System.out.println("OpenCSV解析方式2用时:----------" + (new Date().getTime() - timeStamp2) + "ms");
//        for (User user : userList)
//            System.out.println(new ObjectMapper().writeValueAsString(user));
        in2.close();

        //1600行数据
//        OpenCSV解析方式1用时:----------68ms/61ms/5ms
//        OpenCSV解析方式2用时:----------456ms/268ms/81ms
    }

    @RequestMapping("/testUnivocityParseReader")
    public void testUnivocityParseReader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream("D:/WorkFile/TMP/auto.csv"), "UTF-8");
        long timeStamp1 = new Date().getTime();
        //方式一:读取所有行为字符串数组列表
        CsvParserSettings settings = new CsvParserSettings();
//        settings.getFormat().setDelimiter(',');
//        settings.getFormat().setQuote('"');
//        settings.getFormat().setQuoteEscape('"');
//        settings.getFormat().setCharToEscapeQuoteEscaping('"');
//        settings.getFormat().setLineSeparator("\r\n");
        CsvParser csvParser = new CsvParser(settings);
        List<String[]> allRows = csvParser.parseAll(in);
        System.out.println("UnivocityParser解析方式1用时:----------" + (new Date().getTime() - timeStamp1) + "ms");
//        for (String[] rows : allRows){
//            for (String field : rows)
//                System.out.print(field + " ");
//            System.out.println();
//        }
        in.close();

        InputStreamReader in2 = new InputStreamReader(new FileInputStream("D:/WorkFile/TMP/auto.txt"), "UTF-8");
        long timeStamp2 = new Date().getTime();
        //方式二:转换成对应对象
        BeanListProcessor<User> beanListProcessor = new BeanListProcessor<User>(User.class);
        //除了在注解上进行格式转换,还可以在这转换
//        beanListProcessor.convertFields(Conversions.toDate("yyyy-MM-dd HH:mm:ss")).set("regtime");
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setProcessor(beanListProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(in2);

        List<User> beans = beanListProcessor.getBeans();
        System.out.println("UnivocityParser解析方式2用时:----------" + (new Date().getTime() - timeStamp2) + "ms");

//        for (User user : beans)
//            System.out.println(new ObjectMapper().writeValueAsString(user));
        in2.close();

        //1600行数据
//        UnivocityParsers解析方式1用时:----------117ms/75ms/16ms
//        UnivocityParsers解析方式2用时:----------191ms/82ms/35ms
    }

}
