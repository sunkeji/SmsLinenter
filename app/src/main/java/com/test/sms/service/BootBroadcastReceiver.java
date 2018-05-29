package com.test.sms.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.test.sms.util.SMSBean;
import com.test.sms.util.SMSInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMS:";
    public SMSInterface smsInterface;

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收短信广播，处理特殊短信指令
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object p : pdus) {
            byte[] pdu = (byte[]) p;
            SmsMessage message = SmsMessage.createFromPdu(pdu);
            final String content = message.getMessageBody();
            Date date = new Date(message.getTimestampMillis());
            SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String receiveTime = Dateformat.format(date);
            final String senderNumber = message.getOriginatingAddress();
            String smscontent = "content=" + content + "& receivetime=" + receiveTime + "& sendernumber=" + senderNumber;
            Log.i(TAG, smscontent);
            SMSBean smsBean = new SMSBean();
            smsBean.setContent(content);
            smsBean.setReceiveTime(receiveTime);
            smsBean.setSenderNumber(senderNumber.replace("+86", ""));
            smsBean.setSmsType(0);
            smsInterface.CallBack(smsBean);
        }
    }

    public void setSmsInterface(SMSInterface smsInterface) {
        this.smsInterface = smsInterface;
    }
}
