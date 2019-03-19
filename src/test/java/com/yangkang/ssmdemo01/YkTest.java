package com.yangkang.ssmdemo01;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

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
//        long test = Integer.toUnsignedLong(-1408229028);
//        System.out.println(test);
//        int addr4 = (int) (test%256);
//        test = test/256;
//        int addr3 = (int) (test%256);
//        test = test/256;
//        int addr2 = (int) (test%256);
//        test = test/256;
//        int addr1 = (int) test;
//        System.out.println(addr1 + "." + addr2 + "." + addr3 + "." + addr4);
        //test16
//        String hexStrWithSpace = "02 00 00 02 23 0d 73 f1 eb d2 cf c9 27 7b b5 bf b6 79 0e 0a 92 1a 36 18 88 a3 fc b0 72 fb 26 6c 21 b3 e4 82 61 20 ea c1 37 c2 57 dd c8 56 5e ba 78 a4 97 1e e6 80 fe 15 5a 6e 89 d9 c4 b7 38 3e 84 11 d7 0e dd 89 17 54 fe 39 74 99 f6 a0 4d f0 cb 23 ba 4c 80 8e 0b 05 04 2a fb 14 95 37 7a 88 f3 9a 27 1a ba 4a 1b 56 03 c7 32 84 7a 0c c3 4f 4a 98 ee 58 81 05 2d a0 24 87 16 60 1c d3 2b a5 9f f2 40 0f 46 c9 37 17 c7 ff 5c 94 87 ee 1d 33 8e 1e 9c 87 8d a3 da d6 65 42 d5 9a 3c 16 b1 2f ae 27 59 bd df 77 9a 2a d4 fc 11 08 49 af dc 7e f1 ae c2 be ed f2 44 ef 99 9a 00 82 3a a2 dc c9 c8 7d 6d b6 cc 0d 0d 3d da ae fc 61 a2 6d a1 6d 5f 1f 94 aa 1d 82 32 59 de 91 0c 62 21 9e 7f e1 13 76 7f b2 d8 f5 37 be 97 41 63 83 e5 81 a9 fd f5 ce 16 04 14 fd 7d 0c 3c 4e d1 da 0e 11 0d de 49 fe 7e 2b 04 01 01 00";
//        byte[] bytes = HexConversion.parseHexString_haveSpace(hexStrWithSpace);
//        CreateTunnelThread createTunnelThread = new CreateTunnelThread("test", "test");
//        try {
//            Method method = CreateTunnelThread.class.getDeclaredMethod("analyseResponsePackage", byte[].class, String.class);
//            method.setAccessible(true);
//            byte[] data = (byte[]) method.invoke(createTunnelThread, bytes, "C:/Users/by/Desktop/shebei.cer");
//            String s = Base64.getEncoder().encodeToString(data);
//            System.out.println(s);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        //test17
//        String oriStr = "123456abcde";
//        X509Certificate cert = null;
////        cert = P12FileUtil.loadCertificate(new File("C:/Users/by/Desktop/shebei.cer"));
////        PublicKey publicKey = cert.getPublicKey();
//        PrivateKey privateKey = P12FileUtil.getPrivateKey("D:/WorkFile/TMP/反向UDP-v4.1.5/非AIX操作系统/send/config/stonewall.p12", "12345678".toCharArray());
//        Cipher cipher = null;
//        try {
//            cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//            cipher.init(1, privateKey);
//            byte[] encBytes = cipher.doFinal(oriStr.getBytes("UTF-8"));
//            String encStr = HexConversion.hexify_haveSpace(encBytes);
//            System.out.println(encStr);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
        //test18
        byte[] r1 = new byte[2];
        byte[] r2 = new byte[2];
        r1[0] = 123;
        r1[1] = 36;
        r2[0] = 36;
        r2[1] = 123;
        BigInteger br1 = new BigInteger(r1);
        BigInteger br2 = new BigInteger(r2);
        BigInteger br3 = br1.xor(br2);
        byte[] r3 = br3.toByteArray();
        System.out.println("");
    }
}
