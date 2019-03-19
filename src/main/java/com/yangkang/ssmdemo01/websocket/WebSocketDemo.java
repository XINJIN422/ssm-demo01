package com.yangkang.ssmdemo01.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * WebSocketDemo
 *
 * @author yangkang
 * @date 2019/3/19
 */
@ServerEndpoint(value = "/websocketDemo/{userId}"
//        ,configurator = SpringConfigurator.class  //当该类需要依赖注入spring bean时, 必须加这个配置
)
public class WebSocketDemo {

    private Logger logger = LoggerFactory.getLogger(WebSocketDemo.class);

    private static String userId;

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session){
        this.userId = userId;
        logger.debug("新连接: {}", userId);
    }

    @OnClose
    public void onClose(){
        logger.debug("连接: {} 关闭", this.userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.debug("收到用户: {} 的消息: {}", this.userId, message);
        session.getBasicRemote().sendText("收到 " + this.userId + " 的消息");
    }

    @OnError
    public void onError(Session session, Throwable error){
        logger.debug("用户id为: {} 的连接发生错误", this.userId);
        error.printStackTrace();
    }
}
