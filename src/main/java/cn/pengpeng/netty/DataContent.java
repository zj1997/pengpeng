package cn.pengpeng.netty;

import java.io.Serializable;

/**
 * @author zhaojie
 * @date 2018\12\16 0016 - 16:13
 */
public class DataContent implements Serializable{

    private Integer action;

    private ChatMessage ChatMessage;

    private String extand;

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public cn.pengpeng.netty.ChatMessage getChatMessage() {
        return ChatMessage;
    }

    public void setChatMessage(cn.pengpeng.netty.ChatMessage chatMessage) {
        ChatMessage = chatMessage;
    }

    public String getExtand() {
        return extand;
    }

    public void setExtand(String extand) {
        this.extand = extand;
    }
}
