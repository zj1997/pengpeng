package cn.pengpeng.netty;

/**
 * @author zhaojie
 * @date 2018\12\16 0016 - 16:16
 */
public class ChatMessage {

    private String senderId;  //发送者id

    private String reciverId;  //接受者id

    private String msg;       //发送信息内容

    private String msgId;     //用于消息签收


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
