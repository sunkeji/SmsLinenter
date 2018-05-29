package com.test.sms.util;

/**
 * Created by 孙科技 on 2018/5/7.
 */
public class SMSBean {

    private String content;
    private String receiveTime;
    private String senderNumber;

    private int smsType;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public int getSmsType() {
        return smsType;
    }

    public void setSmsType(int smsType) {
        this.smsType = smsType;
    }

}
