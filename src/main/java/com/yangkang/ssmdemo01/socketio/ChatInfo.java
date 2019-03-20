package com.yangkang.ssmdemo01.socketio;

import org.springframework.stereotype.Component;

/**
 * ChatInfo
 *
 * @author yangkang
 * @date 2019/3/19
 */
@Component
public class ChatInfo {
    private String userSendId;
    private String userReceiveId;
    private String content;
    private String createtime;

    public String getUserSendId() {
        return userSendId;
    }

    public void setUserSendId(String userSendId) {
        this.userSendId = userSendId;
    }

    public String getUserReceiveId() {
        return userReceiveId;
    }

    public void setUserReceiveId(String userReceiveId) {
        this.userReceiveId = userReceiveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
