package com.yangkang.ssmdemo01;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * YkTest
 *
 * @author yangkang
 * @date 2018/9/25
 */
public class YkTest {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //test1
//        UserLoginVerify.loadProperties();
        //test2
//        List<String> stringList = Arrays.asList("hhhhh");
//        System.out.println(Arrays.toString(stringList.toArray()));
        //test3
//        String test = "abcAA";
//        String regStr = "^((?![A-Za-z0-9]).)*$;^((?![A-Za-z\\u0021-\\u002F\\u003A-\\u0040\\u005B-\\u0060\\u007B-\\u007E]).)*$;^((?![0-9\\u0021-\\u002F\\u003A-\\u0040\\u005B-\\u0060\\u007B-\\u007E]).)*$";
//        for (String reg : regStr.split(";"))
//            System.out.println(test.matches(reg));
        //test4
//        System.out.println("abcde".matches("abcd"));
        //test5
//        System.out.println(JSON.toJSONString(new ComErrJson("20000", Messages.getString(System.getenv("SYS_LANG"), "LoginVerify.decrypt_failure"))));
        //test6
//        System.out.println(new JSONObject().getIntValue("no"));
//        System.out.println(new JSONObject().getBoolean("no"));
//        System.out.println(StringUtils.trimToEmpty(new JSONObject().getString("no")));
//        System.out.println("123".substring(0, 1));
        //test7
//        String testSplit = "你好呀!&nbsp;测试一下&nbsp;能不能 按照指定的符号分割";
//        for (String splitOne : testSplit.split("&nbsp;"))
//            System.out.println(splitOne);
        //test8
//        System.out.println(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        //test9 arrays.aslist返回的不能用remove方法
//        List<String> strings = Arrays.asList(new String[]{"1", "2", "3", "4", "5"});
//        Iterator<String> iterator = strings.iterator();
//        while (iterator.hasNext()){
//            String next = iterator.next();
//            if (next.equals("5"))
//                iterator.remove();
//        }
//        System.out.println(strings);
        //test10
//        List<String> strings = new LinkedList<>(Arrays.asList(new String[]{"1", "2", "3", "4", "5"}));
//        Iterator<String> iterator = strings.iterator();
//        while (iterator.hasNext()){
//            String next = iterator.next();
//            if (next.equals("2"))
//                iterator.remove();
//        }
//        System.out.println(strings);
        //test11
//        LinkedList<String[]> strings1 = new LinkedList<>();
//        strings1.add(new String[]{"1", "2", "3"});
//        strings1.add(new String[]{"2", "3", "4"});
//        strings1.add(new String[]{"3", "4", "5"});
//        Iterator<String[]> iterator = strings1.iterator();
//        while (iterator.hasNext()){
//            String[] next = iterator.next();
//            if (next[0].equals("2"))
//                iterator.remove();
//        }
//        System.out.println(strings1);
        //test12
//        String randomNum = String.format("%04d", (int)(Math.random() * 10000));
//        System.out.println(randomNum);
        //test13
//        System.out.println(UUID.randomUUID().toString());
//        System.out.println("'" + Date.valueOf("2015-12-25") + "'");
//        String[] test = {"1", "2", "3"};
//        System.out.println(Arrays.asList(test));
//        System.out.println(Arrays.toString(test));
        //test14
//        String[] roomates = {"yangkang", "wangsiyuan", "wanghanjie", null, "zhangwanyi"};
//        List<String> list = Arrays.asList(roomates);
//        FileOutputStream fos = new FileOutputStream("D:\\WorkFile\\TMP\\testcsv.txt");
//        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//        CSVFormat csvFormat = CSVFormat.DEFAULT
//                                .withEscape('"')
//                                .withQuoteMode(QuoteMode.ALL)
//                                .withNullString("")
//                                .withHeader(new String[]{"name"});
//        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
//        csvPrinter.printRecords(list);
//        csvPrinter.flush();
//        csvPrinter.close();
//        osw.close();
//        fos.close();
        //test15 有符号转无符号
        long test = Integer.toUnsignedLong(-1408229028);
        System.out.println(test);
        int addr4 = (int) (test%256);
        test = test/256;
        int addr3 = (int) (test%256);
        test = test/256;
        int addr2 = (int) (test%256);
        test = test/256;
        int addr1 = (int) test;
        System.out.println(addr1 + "." + addr2 + "." + addr3 + "." + addr4);
    }
}
