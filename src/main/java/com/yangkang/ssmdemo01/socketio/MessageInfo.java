package com.yangkang.ssmdemo01.socketio;

import org.springframework.stereotype.Component;

/**
 * MessageInfo
 *
 * @author yangkang
 * @date 2019/3/19
 */
@Component
public class MessageInfo {
    private String sourceClientId;
    private String targetClientId;
    private String msg;

    public String getSourceClientId() {
        return sourceClientId;
    }

    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }

    public String getTargetClientId() {
        return targetClientId;
    }

    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
