package com.yangkang.ssmdemo01.mvc.entity;

import com.yangkang.ssmdemo01.tools.annotation.Table;
import com.yangkang.ssmdemo01.tools.annotation.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * @Type User2
 * @Desc 
 * @author YK
 * @date 2018-09-11 14:09:49
 * 1.本类由工具类DbToBeanUtil自动生成
 * 2.默认读取resources下的jdbc.properties配置文件,也可以在main函数里设置覆盖相关属性
 * 3.不建议直接修改本类,必要时建议创建子类扩展
 */
@Table("USER2")
public class User2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column("ID")
    private Integer id;
    @Column("EMAIL")
    private String email;
    @Column("PASSWORD")
    private String password;
    @Column("USERNAME")
    private String username;
    @Column("ROLE")
    private String role;
    @Column("STATUS")
    private Integer status;
    @Column("REGTIME")
    private Date regtime;
    @Column("REGIP")
    private String regip;

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }

    public void setRegtime(Date regtime){
        this.regtime = regtime;
    }

    public Date getRegtime(){
        return regtime;
    }

    public void setRegip(String regip){
        this.regip = regip;
    }

    public String getRegip(){
        return regip;
    }

}