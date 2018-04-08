package com.deep.app;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wangfei on 2018/3/21.
 */

public class WifiSettingAdapter  extends RecyclerView.Adapter<WifiSettingAdapter.MyViewHolder>{
    private Context context;
    private List<ScanResult> list ;
    //private IcardViewChanger icardViewChanger;
    public WifiSettingAdapter(Context context, List<ScanResult> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
            context).inflate(R.layout.item_cardview, parent,
            false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ssid.setText("名称:"+list.get(position).SSID);
        String c = list.get(position).capabilities;
        if (c.contains("WPA")){
            holder.img.setImageResource(R.drawable.ic_signal_wifi_4_bar_lock_black_48dp);
        }else {
            holder.img.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_48dp);
        }
        holder.level.setText("强度:"+list.get(position).level);
        holder.b.setText("BSSID:"+list.get(position).BSSID);
        holder.cap.setText(c);

        holder.frequency.setText("frequency:"+list.get(position).frequency);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView ssid;
        TextView cap;
        TextView b;
        TextView level;
        TextView frequency;
        ImageView img;
        CardView cardView;
        public MyViewHolder(View view)
        {
            super(view);
            ssid = (TextView) view.findViewById(R.id.ssid);
            level = (TextView) view.findViewById(R.id.cap);
            cap = (TextView) view.findViewById(R.id.capabilities);
            b = (TextView) view.findViewById(R.id.bssid);
            frequency = (TextView) view.findViewById(R.id.frequency);
            img = view.findViewById(R.id.logo);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }


}
