package com.yangkang.ssmdemo01.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * JustTestController
 *
 * @author yangkang
 * @date 2019/1/15
 */
@Controller
@RequestMapping("/test")
public class JustTestController {

    @RequestMapping(value = "/testGetParamNotExist", method = RequestMethod.GET)
    public void testGetParamNotExist(@RequestParam("param1") String param1,
                                     @RequestParam(value = "param2", required = false) String param2){
        System.out.println(param1 + "=====" + param2);
    }
}
