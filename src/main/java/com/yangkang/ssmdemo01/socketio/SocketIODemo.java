package com.yangkang.ssmdemo01.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SocketIODemo
 *
 * @author yangkang
 * @date 2019/3/19
 */
public class SocketIODemo implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SocketIODemo.class);

    private static final ConcurrentSkipListMap<String, ClientInfo> webSocketMap = new ConcurrentSkipListMap<>();

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    private SocketIOServer server;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("开始启动socket服务=============");
        //避免重复启动
        if( server== null) {
            Configuration cfg = new Configuration();
            cfg.setHostname("localhost");
            //测试证明, socketio不像websocket那样可以共用该项目的8080端口, 并且访问连接不用带上项目名称
            cfg.setPort(8081);
            //该处进行身份验证
            cfg.setAuthorizationListener(handshakeData -> {
                //http://localhost:8081?username=test&password=test
                //例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息
                //String username = data.getSingleUrlParam("username");
                //String password = data.getSingleUrlParam("password");
                return true;
            });
            cfg.setExceptionListener(new ExceptionListenerAdapter() {
                @Override
                public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
                    logger.debug("错误:\n" + e.getMessage());
                    ctx.close();
                    return true;
                }
            });
            server = new SocketIOServer(cfg);
            server.addConnectListener(new ConnectListener() {
                @Override
                public void onConnect(SocketIOClient client) {
                    String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
                    logger.debug("web socket连接:" + clientId);
                    UUID sessionId = client.getSessionId();
                    ClientInfo ci = webSocketMap.get(clientId);
                    if (ci == null) {
                        ci = new ClientInfo();
                        ci.setOnline(true);
                        logger.debug("socket 建立新连接、sessionId:" + sessionId + "、clientId:" + clientId + "、当前连接数：" + onlineCount.incrementAndGet());
                    }
                    ci.setLeastSignificantBits(sessionId.getLeastSignificantBits());
                    ci.setMostSignificantBits(sessionId.getMostSignificantBits());
                    ci.setLastConnectedTime(new Date());
                    webSocketMap.put(clientId, ci);
                }
            });
            server.addDisconnectListener(new DisconnectListener() {
                @Override
                public void onDisconnect(SocketIOClient client) {
                    String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
                    webSocketMap.remove(clientId);
                    logger.debug("socket 断开连接、sessionId:" + client.getSessionId() + "、clientId:" + clientId + "、当前连接数：" + onlineCount.decrementAndGet());
                }
            });
            server.addEventListener("message_event", MessageInfo.class, new DataListener<MessageInfo>() {
                @Override
                public void onData(SocketIOClient client, MessageInfo data, AckRequest ackRequest) throws Exception {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String time = sdf.format(new Date());
                    ChatInfo chatInfo = new ChatInfo();
                    chatInfo.setUserSendId(data.getSourceClientId());
                    chatInfo.setUserReceiveId(data.getTargetClientId());
                    chatInfo.setContent(data.getMsg());
                    chatInfo.setCreatetime(time);
                    String targetClientId = data.getTargetClientId();
                    ClientInfo clientInfo = webSocketMap.get(targetClientId);
                    if (clientInfo != null && clientInfo.isOnline()) {
                        UUID target = new UUID(clientInfo.getMostSignificantBits(), clientInfo.getLeastSignificantBits());
                        logger.debug("会话目标UUID:" + target);
                        MessageInfo sendData = new MessageInfo();
                        sendData.setSourceClientId(data.getSourceClientId());
                        sendData.setTargetClientId(data.getTargetClientId());
                        sendData.setMsg(data.getMsg());
                        client.sendEvent("message_event", sendData);
                        //这边根据sessionId来获取目标对象,如果是一个浏览器的话就有问题
                        server.getClient(target).sendEvent("message_event", sendData);
                    }
                }
            });
            server.start();
            logger.debug("服务开始");
        }
    }
}
