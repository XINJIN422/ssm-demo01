package com.yangkang.ssmdemo01.mvc.controller;

import com.alibaba.fastjson.JSON;
import com.yangkang.ssmdemo01.mvc.entity.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * ESController
 *
 * @author yangkang
 * @date 2019/1/23
 */
@RequestMapping("/es")
@Controller
public class ESController {

    @Resource
    private RestHighLevelClient restHighLevelClient;

//    @Resource
//    private TransportClient transportClient;

//    @Resource
//    private ElasticsearchTemplate elasticsearchTemplate;

    @RequestMapping("/testESInsert")
    public void testESInsert(){

    }

    @RequestMapping("/testQuery")
    public void testQuery() throws IOException {
        //用最新版es, transportclient连接各种调不通
//        SearchResponse response = transportClient.prepareSearch("my-first-es-index")
//                .setTypes("user")
//                //设置查询类型
//                //SearchType.DFS_QUERY_THEN_FETCH 精确查询
//                //SearchType.SCAN 扫描查询,无序
//                //SearchType.COUNT 默认
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.matchQuery("username", "forodo"))
//                .highlighter(new HighlightBuilder().field("username").preTags("<em>").postTags("</em>"))
//                //设置查询数据起始位置, 结果集大小
//                .setFrom(0)
//                .setSize(3)
//                //设置是否按查询匹配度_score排序
//                .setExplain(false)
//                .execute()
//                .actionGet();
//        SearchHits searchHits = response.getHits();
//        System.out.println("-------" + searchHits.getTotalHits() + "-------");
//        SearchHit[] hits = searchHits.getHits();
//        for (SearchHit searchHit : hits){
//            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
//            HighlightField highlightField = highlightFields.get("username");
//            System.out.println("高亮字段:" + highlightField.getName() + "\n高亮内容:" + highlightField.getFragments()[0].string());
//            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
//            Set<String> keySet = sourceAsMap.keySet();
//            for (String key : keySet)
//                System.out.println(key + ":" + sourceAsMap.get(key));
//        }

        SearchRequest request = new SearchRequest();
        //索引库
        request.indices("my-first-es-index");
        //类名
        request.types("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("username", "yang"))
                .query(QueryBuilders.matchQuery("status", 1));
        //排序(需要开启该字段的mapping属性fielddata为true)
//        sourceBuilder.sort("regTime", SortOrder.DESC);
        //分页
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request);
        ArrayList<User> list = new ArrayList<>();
        if (response.getHits() != null){
            System.out.println("=========" + response.getHits().toString());
            response.getHits().forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), User.class)));
        }
        System.out.println(list.size());
    }

    @RequestMapping("/testQuery2")
    public void testQuery2() throws IOException {
        //多条件查询
        SearchRequest request = new SearchRequest();
        request.indices("my-first-es-index");
        request.types("user");
        MatchPhraseQueryBuilder mpq1 = QueryBuilders.matchPhraseQuery("username", "yang");
        MatchPhraseQueryBuilder mpq2 = QueryBuilders.matchPhraseQuery("status", 1);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(mpq1).must(mpq2);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(3);
        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request);
        ArrayList<User> list = new ArrayList<>();
        if (response.getHits() != null){
            System.out.println("=========" + response.getHits().toString());
            response.getHits().forEach(item -> list.add(JSON.parseObject(item.getSourceAsString(), User.class)));
        }
        System.out.println(list.size());
    }

    @RequestMapping("/testInsertOne")
    public void testInsertOne() throws IOException {
        IndexRequest request = new IndexRequest("my-first-es-index", "user");
        for (int i = 0; i < 10; i++){
            User user = new User();
            user.setUsername("杨" + i);
            user.setStatus(i%2);
            user.setEmail("" + i + i + i + "123@qq.com");
            user.setRegTime(new Date());
//            request.id("" + i);
            request.source(JSON.toJSONString(user), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(request);
            System.out.println(response.toString());
        }
    }
}
