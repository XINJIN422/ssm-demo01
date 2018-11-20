package com.yangkang.ssmdemo01.shiro.realm;

import com.yangkang.ssmdemo01.mvc.entity.ShiroUser;
import com.yangkang.ssmdemo01.mvc.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * UserRealm
 *
 * @author yangkang
 * @date 2018/11/19
 */
public class UserRealm extends AuthorizingRealm{

    @Resource
    private IUserService userService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String)authenticationToken.getPrincipal();
        ShiroUser shiroUser = userService.findByUsername(username);
        if (shiroUser == null)
            throw new UnknownAccountException();
        if (Boolean.TRUE.equals(shiroUser.getLocked()))
            throw new LockedAccountException();

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                shiroUser.getUsername(),    //用户名
                shiroUser.getPassword(),    //密码
                ByteSource.Util.bytes(shiroUser.getCredentialsSalt()),  //salt=username+salt
                getName()   //realm name
        );
        return authenticationInfo;
    }
}
