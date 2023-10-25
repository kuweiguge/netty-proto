package com.github.kuweiguge.common.message;

/**
 * @author zhengwei
 * @version 1.0
 * @since 2023/10/25 09:49
 */
public class DemoMsg {
    private int magicNumber;

    private String appName;

    private MessageType messageType;

    private String body;

    public DemoMsg() {
    }

    public DemoMsg(MessageType messageType, String appName) {
        this.appName = appName;
        this.messageType = messageType;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}