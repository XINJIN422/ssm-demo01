package com.yangkang.ssmdemo01.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyElasticsearchConfig
 *
 * @author yangkang
 * @date 2019/1/23
 */
@Configuration
public class MyElasticsearchConfig {

    //shit! 调试了半天还是不能连接, 不知道什么问题, 换其他方式连接吧
//    @Bean
//    public TransportClient transportClient() throws UnknownHostException {
//        Settings settings = Settings.builder()
////                .put("cluster.name", "my-application")
//                .put("client.transport.sniff", false)
//                .build();
////        Settings.EMPTY
//        return new PreBuiltTransportClient(settings)
//                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
//                    ;
//    }

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return myESClientDecorator().getRestHighLevelClient();
    }

    @Bean
    public MyESClientDecorator myESClientDecorator(){
        //如果是集群, 可以将HttpPost参数定义为数组
        return new MyESClientDecorator(new HttpHost("localhost", 9200, "http"));
    }
}
