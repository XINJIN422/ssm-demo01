package com.yangkang.ssmdemo01.mvc.controller;

import com.yangkang.ssmdemo01.tools.encryption.RSAEncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * EncryptController2
 *
 * @author yangkang
 * @date 2018/10/31
 */
@Controller
@RequestMapping("/encrypt2")
@SessionAttributes(value = {"cliPubKey"})
public class EncryptController2 {
    private Logger logger = LoggerFactory.getLogger(EncryptController2.class);

    @RequestMapping("/testRsaEncTrans")
    public void testRsaEncTrans(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws IOException {
        String cliPubKey = (String)modelMap.get("cliPubKey");
        System.out.println("跨controller类获取springMvc的session属性:" + cliPubKey);
        response.setCharacterEncoding("UTF8");
        String result = "解密失败!";
        try {
            System.out.println(RSAEncryptUtil.decrypt(request.getParameter("password")));
            result = "解密成功!";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                result = RSAEncryptUtil.encrypt(result, RSAEncryptUtil.getClientPublicKeyFromStr(cliPubKey));
            } catch (Exception e) {
                e.printStackTrace();
                result = "返回结果加密失败!";
            } finally {
                response.getWriter().write(result);
                response.getWriter().close();
            }
        }
    }

    @RequestMapping("/testSignature")
    public void testSignature(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws IOException {
        String password = request.getParameter("password");
        String sign = request.getParameter("sign");
        String cliPubKey = (String)modelMap.get("cliPubKey");
        boolean verifySignFlag = false;
        try {
            verifySignFlag = RSAEncryptUtil.checkSign(password, sign, cliPubKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.getWriter().write(String.valueOf(verifySignFlag));
            response.getWriter().close();
        }
    }
}
