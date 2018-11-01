package com.yangkang.ssmdemo01.mvc.controller;

import com.yangkang.ssmdemo01.tools.encryption.AESEncryptUtil;
import com.yangkang.ssmdemo01.tools.encryption.RSAEncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * EncryptController
 *
 * @author yangkang
 * @date 2018/10/30
 */
@Controller
@RequestMapping("/encrypt")
@SessionAttributes(value = {"cliPubKey"})
public class EncryptController {
    private Logger logger = LoggerFactory.getLogger(EncryptController.class);

    @RequestMapping("/testAesEncTrans")
    public void testAesEncTrans(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String encryptedPd = request.getParameter("password");
        String decryptedPd = null;
        response.setCharacterEncoding("UTF8");
        try {
            decryptedPd = AESEncryptUtil.decrypt(encryptedPd);
            response.getWriter().write("解密成功!");
        } catch (IllegalBlockSizeException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            response.getWriter().write("解密失败!");
            e.printStackTrace();
        } finally {
            response.getWriter().close();
        }
        System.out.println(decryptedPd);
    }

    @RequestMapping("/exchangePublicKey")
    public void exchangePublicKey(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws IOException {
        modelMap.addAttribute("cliPubKey", request.getParameter("cliPubKey"));
        response.getWriter().write(RSAEncryptUtil.getServerPublicKeyStr());
    }
}
