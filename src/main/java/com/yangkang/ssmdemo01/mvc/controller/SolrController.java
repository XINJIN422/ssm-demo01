package com.yangkang.ssmdemo01.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangkang.ssmdemo01.mvc.entity.User;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * SolrController
 *
 * @author yangkang
 * @date 2019/1/17
 */
@Controller
@RequestMapping("/solr")
public class SolrController {

    @Resource
    private SolrClient solrClient;
//                                new HttpSolrClient
//                                .Builder("http://127.0.0.1:8983/solr")
//                                .withConnectionTimeout(10000)
//                                .withSocketTimeout(60000)
//                                .build();

    @Resource
    private SolrTemplate solrTemplate;

    //spring-boot里会扫描生成bean, 普通的不行
//    @Resource
//    private UserSolrRepository userSolrRepository;

    @RequestMapping("/testSolrQuery")
    public void testSolrQuery() throws IOException, SolrServerException {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", "username:今天的天气不错");
        MapSolrParams mapSolrParams = new MapSolrParams(params);
        QueryResponse response = solrClient.query("mycore2", mapSolrParams);
        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results){
            System.out.println(result);
        }
    }

    @RequestMapping("/testSolrAdd")
    public void testSolrAdd() throws IOException, SolrServerException {
        //测试手动插入数据库没有的数据到solr里, 在配置了自动增量同步后, 会不会自动删除
        //结果是: 不会!
        SolrInputDocument solrInputFields = new SolrInputDocument();
        solrInputFields.addField("id", "5");
        solrInputFields.addField("username", "天天5");
        //如果id已经存在, 则更新; 否则, 插入;
        UpdateResponse add = solrClient.add("mycore2", solrInputFields);
        solrClient.commit("mycore2");
        testSolrQuery();
        System.out.println(add);
    }

    @RequestMapping("/testSolrDel")
    public void testSolrDel() throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrClient.deleteById("mycore2", "5");
        solrClient.commit("mycore2");
        System.out.println(updateResponse);
        //还可以根据查询条件删除
//        solrClient.deleteByQuery("mycore2", "查询条件");
    }

    @RequestMapping("/testSolrQuery2")
    public void testSolrQuery2() throws JsonProcessingException {
        //各个jar包及变量格式要匹配, 否则会报错
        //根据主键查询
        User user = null;
        user = solrTemplate.getById("mycore2", "1", User.class).get();
        System.out.println(new ObjectMapper().writeValueAsString(user));
    }

    @RequestMapping("/testSolrQuery3")
    public void testSolrQuery3() throws JsonProcessingException {
        //分页查询
        SimpleQuery query = new SimpleQuery("*:*");
        query.setOffset(new Long(0));   //开始索引(默认为0)
        query.setRows(3);   //每页记录数(默认为10)
        ScoredPage<User> userScoredPage = solrTemplate.queryForPage("mycore2", query, User.class);
        System.out.println("总记录数: " + userScoredPage.getTotalElements());
        System.out.println("总页数: " + userScoredPage.getTotalPages());
        //下面只是打了第一页, 并不是全部
        List<User> userList = userScoredPage.getContent();
        System.out.println(new ObjectMapper().writeValueAsString(userList));
    }

    @RequestMapping("/testSolrQuery4")
    public void testSolrQuery4() throws JsonProcessingException {
        //条件查询 + 过滤 + 排序
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("email").contains("456");
        query.addCriteria(criteria);
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        filterQuery.addCriteria(new Criteria("status").is(1));
        query.addFilterQuery(filterQuery);
        Sort sort = new Sort(Sort.Direction.DESC, "regTime");
        query.addSort(sort);
        ScoredPage<User> userScoredPage = solrTemplate.queryForPage("mycore2", query, User.class);
        System.out.println(new ObjectMapper().writeValueAsString(userScoredPage.getContent()));
    }
    //添加删除和solrClient差不多,记得要commit

//    @RequestMapping("/testSolrQuery5")
//    public void testSolrQuery5() throws JsonProcessingException {
//        List<User> userList = userSolrRepository.findByUsername("杨康");
//        System.out.println(new ObjectMapper().writeValueAsString(userList));
//    }

    @RequestMapping("/testSolrQuery6")
    public void testSolrQuery6() throws JsonProcessingException {
        Criteria criteria = new Criteria("email").contains("456");
        //高亮显示 highlighting
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);
        HighlightOptions highlightOptions = new HighlightOptions().addField("email");    //设置高亮域
        highlightOptions.setSimplePrefix("<font color='red'>");     //设置高亮前缀
        highlightOptions.setSimplePostfix("</font>");     //设置高亮后缀
        highlightQuery.setHighlightOptions(highlightOptions);
        HighlightPage<User> userHighlightPage = solrTemplate.queryForHighlightPage("mycore2", highlightQuery, User.class);
        for (HighlightEntry<User> hUser : userHighlightPage.getHighlighted()){
            User user = hUser.getEntity();
            if (hUser.getHighlights().size()>0 && hUser.getHighlights().get(0).getSnipplets().size()>0){
                user.setEmail(hUser.getHighlights().get(0).getSnipplets().get(0));   //设置高亮的结果
            }
            System.out.println(new ObjectMapper().writeValueAsString(user));
        }
    }
}
