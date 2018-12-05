package com.yangkang.ssmdemo01.mvc.controller;

import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MysqlFileController
 * 测试mybatis调用sql语句,导出csv文件和导入csv文件
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

}
