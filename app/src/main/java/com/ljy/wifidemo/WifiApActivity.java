package com.ljy.wifidemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class WifiApActivity extends AppCompatActivity {

    //接收message，做处理
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WifiAPUtil.MESSAGE_AP_STATE_ENABLED:
                    String ssid = WifiAPUtil.getInstance().getValidApSsid();
                    String pw = WifiAPUtil.getInstance().getValidPassword();
                    int security = WifiAPUtil.getInstance().getValidSecurity();
                    String openInfo = "wifi热点开启成功" + "\n"
                            + "SSID = " + ssid + "\n"
                            + "Password = " + pw + "\n"
                            + "Security = " + security;
                    Toast.makeText(WifiApActivity.this, "openInfo:" + openInfo, Toast.LENGTH_SHORT).show();
                    Log.d("LJY_LOG", "openInfo:" + openInfo);
                    break;
                case WifiAPUtil.MESSAGE_AP_STATE_FAILED:
                    Toast.makeText(WifiApActivity.this, "wifi热点关闭", Toast.LENGTH_SHORT).show();
                    Log.d("LJY_LOG", "wifi热点关闭");
                    break;
                default:
                    break;
            }
        }
    };
    private EditText mEditName;
    private EditText mEditPwd;
    private EditText mEditType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ap);
        init();
        initView();
    }

    private void initView() {
        mEditName = findViewById(R.id.edit_name);
        mEditPwd = findViewById(R.id.edit_pwd);
        mEditType = findViewById(R.id.edit_type);
    }

    private void init() {
        //初始化WifiAPUtil类
        WifiAPUtil.getInstance().init(this);
        //注册handler
        WifiAPUtil.getInstance().regitsterHandler(mHandler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiAPUtil.getInstance().unregitsterHandler();
        WifiAPUtil.getInstance().unregisterReceiver();
    }

    public void onApBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_ap_open:
                //开启wifi热点
                String type = mEditType.getText().toString();
                WifiAPUtil.WifiSecurityType typeWifi;
                if ("1".equals(type)) {
                    typeWifi = WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA;
                } else if ("2".equals(type)) {
                    typeWifi = WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA2;
                } else {
                    typeWifi = WifiAPUtil.WifiSecurityType.WIFICIPHER_NOPASS;
                }
                WifiAPUtil.getInstance().turnOnWifiAp(mEditName.getText().toString(), mEditPwd.getText().toString(), typeWifi);
                break;
            case R.id.btn_wifi_ap_close:
                break;
            default:
                break;
        }
    }
}
