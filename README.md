# deepWiFi
## 功能描述
现有功能：

* 网络监听
* 建立热点
* Arp扫描

未来陆续要添加的功能

* wifi直连
* 网络抓包
* 连接指定wifi网络

## 依赖方式
* 可以直接依赖deep_wifi那个module
* 也可以使用gradle依赖
 ```
 compile 'com.deep:deep_wifi:1.0'
 ```

## 接口描述

### wifi扫描

```
  DeepWifi.getInstance(this).WifiStartScan();
```
接收的结果在广播中。
可以调用如下接口进行广播的接听：

```
 DeepWifi.getInstance(this).registerWifiBroadCast(new WiFiListener() {
            @Override
            public void onWifiSwitch(boolean isTurnOn) {
                L.e("onWifiSwitch");
            }

            @Override
            public void onWifiScanResult() {
                L.e("onWifiScanResult");
                List<ScanResult> results =  DeepWifi.getInstance(MainActivity.this).getScanRealResults();
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
```

### 建立热点

```
               boolean result =  DeepWifi.getInstance(MainActivity.this).turnOnWifiAp("deep","12345678", HotPointType.WIFICIPHER_WPA2);

```
第一个参数为AP名称，第二个参数为AP密码

>需要注意一下，最好设置targetSdkVersion为22,否则需要动态申请权限。

### ARP扫描

```
    DeepWifi.getInstance(MainActivity.this).getLAN_IP(new ARPCallback() {
                            @Override
                            public void callback(final ArrayList<ARPBean> list) {
                              

                            }
                        });
```
该操作尽量在子线程中进行。回调结果即为扫描到的IP地址和mac地址。

### 其它获取信息：
可以直接调用` DeepWifi.getInstance(MainActivity.this).getXXX`进行获取，可以获取到mac地址，ip，信号强度等信息。

|接口|作用|
|:---|:---|
|getNetId|获取netid|
|getConnectedBSSID|获取连接wifi的BSSID|
|getConnectedSSID|获取连接wifi的SSID|
|getConnectedFrequency|获取连接wifi的Frequency|
|getIP|获取连接wifi的IP|
|getSpeed|获取连接wifi的速度|
|getRssi|获取连接wifi的Rssi|
|getConnectedInfo|获取连接wifi的WifiInfo|
