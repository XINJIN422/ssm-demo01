package com.yangkang.ssmdemo01.socketio;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClientInfo
 *
 * @author yangkang
 * @date 2019/3/19
 */
@Component
public class ClientInfo {
    private String clientInfo;
    private boolean isOnline;
    private long mostSignificantBits;
    private long leastSignificantBits;
    private Date lastConnectedTime;

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getMostSignificantBits() {
        return mostSignificantBits;
    }

    public void setMostSignificantBits(long mostSignificantBits) {
        this.mostSignificantBits = mostSignificantBits;
    }

    public long getLeastSignificantBits() {
        return leastSignificantBits;
    }

    public void setLeastSignificantBits(long leastSignificantBits) {
        this.leastSignificantBits = leastSignificantBits;
    }

    public Date getLastConnectedTime() {
        return lastConnectedTime;
    }

    public void setLastConnectedTime(Date lastConnectedTime) {
        this.lastConnectedTime = lastConnectedTime;
    }
}
