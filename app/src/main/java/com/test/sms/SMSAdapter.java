package com.test.sms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.sms.util.SMSBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 孙科技 on 2018/5/7.
 */
public class SMSAdapter extends RecyclerView.Adapter {
    private List<SMSBean> list;

    public SMSAdapter(List<SMSBean> list) {
        this.list = list;

    }

    public void updateList(List<SMSBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            SMSBean smsBean = list.get(position);
            if (smsBean.getSmsType() == 0) {
                ((ItemViewHolder) holder).textLog.setText("C手机收到：" + smsBean.getSenderNumber() + "--发出的--" + smsBean.getContent());
            } else if (smsBean.getSmsType() == 1) {
                ((ItemViewHolder) holder).textLog.setText("C手机转发：" + smsBean.getSenderNumber() + "--发出的--" + smsBean.getContent());
            }


        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_log)
        TextView textLog;

        public ItemViewHolder(View itemView) {
            super(itemView);
//            R.layout.item_sms
            ButterKnife.bind(this, itemView);
        }
    }
}
