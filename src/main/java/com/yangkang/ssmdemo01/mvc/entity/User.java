package com.yangkang.ssmdemo01.mvc.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;

import java.io.Serializable;
import java.util.Date;

//@SolrDocument(solrCoreName = "mycore2")     //solr注解(SolrCrudRepository使用)
public class User implements Serializable{
//    @Id     //solr注解(SolrCrudRepository使用)
    @Parsed
    @CsvBindByName
    private long id;
    @Parsed
    @CsvBindByName
    private String email;
    @Parsed
    @CsvBindByName
    private String password;
    @Parsed
    @CsvBindByName
    private String username;
    @Parsed
    @CsvBindByName
    private String role;
    @Parsed
    @CsvBindByName
    private int status;
    @Parsed(field = "regtime")
    @Format(formats = "yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "regtime")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    private Date regTime;
    @Parsed(field = "regip")
    @CsvBindByName(column = "regip")
    private String regIp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }
}
