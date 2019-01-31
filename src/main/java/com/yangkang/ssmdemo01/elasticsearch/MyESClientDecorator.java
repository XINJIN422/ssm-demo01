package com.yangkang.ssmdemo01.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * MyESClientDecorator
 *
 * @author yangkang
 * @date 2019/1/23
 */
public class MyESClientDecorator
        implements InitializingBean, DisposableBean
{

    private RestHighLevelClient restHighLevelClient;

    private HttpHost httpHost;

    public MyESClientDecorator(HttpHost httpHost){
        this.httpHost = httpHost;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    @Override
    public void destroy() throws Exception {
        restHighLevelClient.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost));
    }
}
