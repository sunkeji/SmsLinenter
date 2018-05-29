package com.test.sms;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skj.wheel.definedview.LayoutView;
import com.skj.wheel.swiperecyclerview.MyRecyclerView;
import com.test.sms.app.PermissionsActivity;
import com.test.sms.service.BootBroadcastReceiver;
import com.test.sms.util.PermissionsChecker;
import com.test.sms.util.SMSBean;
import com.test.sms.util.SMSInterface;
import com.test.sms.util.SPUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.recycler_view)
    MyRecyclerView recyclerView;
    @BindView(R.id.layout_view)
    LayoutView layoutView;
    @BindView(R.id.text_phoneA)
    TextView textPhoneA;
    @BindView(R.id.text_phoneD)
    TextView textPhoneD;
    private static final String TAG = "SMS:";

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };
    private List<SMSBean> list = new ArrayList<>();
    private SMSAdapter smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPermissionsChecker = new PermissionsChecker(this);
        initView();
        setReceiver();
        updateUI();
    }

    private void initView() {
        textPhoneA.setText("A手机指令：4A或4a----手机号：" + SPUtil.getPhoneA());
        textPhoneD.setText("D手机:" + SPUtil.getPhoneD());
        smsAdapter = new SMSAdapter(list);
        recyclerView.setAdapter(smsAdapter);
    }


    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        String phone = editPhone.getText().toString();
        SPUtil.saveToApp("phoneD", phone);
        textPhoneD.setText("D手机:" + SPUtil.getPhoneD());
    }

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 0; // 请求码

    @Override
    protected void onResume() {
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    /**
     * UI线程刷新界面
     */
    private Handler mHandler;

    private void updateUI() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1001:
                        SMSBean smsBean = (SMSBean) msg.obj;
                        list.add(smsBean);
                        smsAdapter.updateList(list);

                        if (smsBean.getContent().equalsIgnoreCase("4A")) {
                            SPUtil.saveToApp("phoneA", smsBean.getSenderNumber());
                            textPhoneA.setText("A手机指令：4A或4a----手机号：" + SPUtil.getPhoneA());
                            prepareSMS(smsBean.getContent());
                        } else {
                            if (smsBean.getSenderNumber().equals(SPUtil.getPhoneD())) {
                                sendSMS(SPUtil.getPhoneA(), smsBean.getContent());
                            }
                        }
                        break;
                }
            }
        };
    }

    /**
     * 转发判断
     *
     * @param message
     */
    private void prepareSMS(String message) {
        if (!TextUtils.isEmpty(SPUtil.getPhoneD())) {
            sendSMS(SPUtil.getPhoneD(), message);
        } else {
            Toast.makeText(this, "请输入转发的手机号", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 注册广播
     */
    private BootBroadcastReceiver receiver;

    private void setReceiver() {
        receiver = new BootBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(receiver, intentFilter);
        receiver.setSmsInterface(new SMSInterface() {
            @Override
            public void CallBack(SMSBean smsBean) {
                Message message = new Message();
                message.what = 1001;
                message.obj = smsBean;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
    }

    /**
     * 注销广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        SMSBean smsBean = new SMSBean();
        smsBean.setSmsType(1);
        smsBean.setSenderNumber(phoneNumber);
        smsBean.setContent(message);
        list.add(smsBean);
        smsAdapter.updateList(list);

        //处理返回的发送状态
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        //处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        PendingIntent paIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT_SMS_ACTION), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED_SMS_ACTION), 0);
        // register the Broadcast Receivers
//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context _context, Intent _intent) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(MainActivity.this,
//                                "短信发送成功", Toast.LENGTH_SHORT)
//                                .show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT_SMS_ACTION));
//
//
//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context _context, Intent _intent) {
//                Toast.makeText(MainActivity.this,
//                        "收信人已经成功接收", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }, new IntentFilter(DELIVERED_SMS_ACTION));
        // 获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            Log.i(TAG, "C手机发送给" + phoneNumber + "--" + text);
            smsManager.sendTextMessage(phoneNumber, null, text, paIntent,
                    deliveryIntent);
        }

    }
}
