package com.yangkang.ssmdemo01.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MySolrConfig
 *
 * @author yangkang
 * @date 2019/1/18
 */
@Configuration
public class MySolrConfig {
//    @Bean
//    public SolrClient solrClient() throws Exception {
//        HttpSolrClientFactoryBean httpSolrClientFactoryBean = new HttpSolrClientFactoryBean();
//        httpSolrClientFactoryBean.setUrl("http://127.0.0.1:8983/solr");
//        return httpSolrClientFactoryBean.getObject();
//    }

    @Bean
    public SolrClient solrClient(){
        return  new HttpSolrClient.Builder("http://127.0.0.1:8983/solr")
                                .withConnectionTimeout(10000)
                                .withSocketTimeout(60000)
                                .build();
    }
}
