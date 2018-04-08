package com.deep.app;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.deep.deepwifi.DeepWifi;
import com.deep.deepwifi.L;
import com.deep.deepwifi.WifiSecurityType;
import com.deep.deepwifi.bean.ARPBean;
import com.deep.deepwifi.interfaces.ARPCallback;
import com.deep.deepwifi.interfaces.WiFiListener;

public class MainActivity extends AppCompatActivity {
    TextView tv ;
    DrawerLayout drawer ;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initRecyclerView();
        floatingActionButton = findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation operatingAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                operatingAnim.setDuration(500);
                floatingActionButton.startAnimation(operatingAnim);
                initRecyclerView();
            }
        });

        findViewById(R.id.arp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            MyQueue.runInBack(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeepWifi.getInstance(MainActivity.this).getLAN_IP(new ARPCallback() {
                            @Override
                            public void callback(final ArrayList<ARPBean> list) {
                                MyQueue.runInMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        StringBuilder s = new StringBuilder();
                                        for (ARPBean b:list){
                                            s.append("mac:").append(b.mac).append("  ip:").append(b.ip).append("\n").append("-----------\n");
                                        }

                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("本地设备");
                                        builder.setMessage(s.toString());
                                        builder.setPositiveButton("确定", null);
                                        builder.show();
                                    }
                                });



                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            }
        });
        findViewById(R.id.hotpoint).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               boolean result =  DeepWifi.getInstance(MainActivity.this).turnOnWifiAp("deep","12345678", WifiSecurityType.WIFICIPHER_WPA2);
                if (result){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("热点建立");
                    builder.setMessage("建立成功");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("热点建立");
                    builder.setMessage("建立失败");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.list);
        initText();
        DeepWifi.getInstance(this).registerWifiBroadCast(new WiFiListener() {
            @Override
            public void onWifiSwitch(boolean isTurnOn) {
                L.e("onWifiSwitch");
            }

            @Override
            public void onWifiScanResult() {
                L.e("onWifiScanResult");
                List<ScanResult> results =  DeepWifi.getInstance(MainActivity.this).getScanRealResults();
                L.e("count="+results.size());
                for (ScanResult s:results){
                    L.e("name："+s.SSID);
                }
                WifiSettingAdapter adapter = new WifiSettingAdapter(MainActivity.this,results);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onWifiConnect() {
               L.e("onWifiConnect");
            }

            @Override
            public void onNetStateChange() {
                L.e("onNetStateChange");
            }
        });
    }
    private void initText(){
        tv = findViewById(R.id.ip);
        StringBuilder sb = new StringBuilder();
        sb.append("IP地址：").append( DeepWifi.getInstance(this).getIP()).append("\n");
        sb.append("BSSID：").append( DeepWifi.getInstance(this).getConnectedBSSID()).append("\n");
        sb.append("SSID：").append( DeepWifi.getInstance(this).getConnectedSSID()).append("\n");
        sb.append("链接速度：").append( DeepWifi.getInstance(this).getSpeed()).append("Mbps").append("\n");
        sb.append("信号强度：").append( DeepWifi.getInstance(this).getRssi()).append("\n");
        sb.append("mac:").append(DeepWifi.getInstance(this).getConnectedMac());
        tv.setText(sb);
    }
    private void initRecyclerView(){
        DeepWifi.getInstance(this).WifiStartScan();

    }
    private void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(toolbar);
        drawer =  (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeepWifi.getInstance(this).unRegisterWifiBroadCast();
    }
}
