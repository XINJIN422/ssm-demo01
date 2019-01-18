package com.yangkang.ssmdemo01.mvc.controller;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;

/**
 * SolrController
 *
 * @author yangkang
 * @date 2019/1/17
 */
@Controller
@RequestMapping("/solr")
public class SolrController {

    private static SolrClient solrClient = new HttpSolrClient
                                .Builder("http://127.0.0.1:8983/solr")
                                .withConnectionTimeout(10000)
                                .withSocketTimeout(60000)
                                .build();

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
}
