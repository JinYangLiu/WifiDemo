package com.ljy.wifidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiUtil.getInstance().init(this);
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(resultsReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resultsReceiver);
    }

    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_is_open:
                //检查WIFI是否开启
                WifiUtil.getInstance().checkState();
                break;
            case R.id.btn_wifi_open:
                //打开WIFI
                WifiUtil.getInstance().openWifi();
                break;
            case R.id.btn_wifi_close:
                //关闭WIFI
                WifiUtil.getInstance().closeWifi();
                break;
            case R.id.btn_wifi_info:
                //获取当前手机所连接的wifi信息
                WifiInfo wifiInfo = WifiUtil.getInstance().getCurrentWifiInfo();
                Toast.makeText(MainActivity.this, "wifiInfo= " + wifiInfo.toString(), Toast.LENGTH_SHORT).show();
                Log.d("LJY_LOG", "wifiInfo= " + wifiInfo.toString());
                break;
            case R.id.btn_wifi_is_connect:
                //判断是否连接了Wifi
                boolean isConnect = WifiUtil.getInstance().isConnectWifi();
                Toast.makeText(MainActivity.this, "isConnect= " + isConnect, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_wifi_scan:
                //搜索附近的热点信息
                boolean started = WifiUtil.getInstance().startScan();

                Toast.makeText(MainActivity.this, "started= " + started, Toast.LENGTH_SHORT).show();
                Log.d("LJY_LOG", "started= " + started);
                break;
            case R.id.btn_wifi_connect:
                //连接WIFI
               boolean isSuccess= WifiUtil.getInstance().connectWifiTest("Hachi_Office_5G","hachismart.com");
                Toast.makeText(MainActivity.this, "isSuccess= " + isSuccess, Toast.LENGTH_SHORT).show();
                Log.d("LJY_LOG", "isSuccess= " + isSuccess);
                break;
            case R.id.btn_wifi_ap:
                //开启wifi热点
                startActivity(new Intent(MainActivity.this,WifiApActivity.class));
                break;
            default:
                break;
        }
    }



    private InOutWifiScanResultsReceiver resultsReceiver = new InOutWifiScanResultsReceiver();

    private class InOutWifiScanResultsReceiver extends BroadcastReceiver {

        private List<ScanResult> getWifiResults(Context context) {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            try {
                return wm != null ? wm.getScanResults() : null;
            } catch (SecurityException e) {
                return new ArrayList<>();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                List<ScanResult> results = getWifiResults(context);
                StringBuilder stringBuilder = new StringBuilder();
                if (results != null) {
                    for (ScanResult item : results) {
                        stringBuilder.append(item.SSID);
                        stringBuilder.append("，");
                    }
                }
                Log.d("LJY_LOG", "scanResult= " + stringBuilder.toString());
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d("LJY_LOG", "WiFi已启用" + SimpleDateFormat.getDateInstance().format(new Date()));
                        WifiUtil.getInstance().startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.d("LJY_LOG", "Wifi已关闭" + SimpleDateFormat.getDateInstance().format(new Date()));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
