package com.suttori.demobottty3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "channel")
public class Channel {

    @Id
    private Long channelId;
    private String channelName;
    private String channelUsername;
    private Long userId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUsername() {
        return channelUsername;
    }

    public void setChannelUsername(String channelUsername) {
        this.channelUsername = channelUsername;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", channelName='" + channelName + '\'' +
                ", channelUsername='" + channelUsername + '\'' +
                ", userId=" + userId +
                '}';
    }
}
