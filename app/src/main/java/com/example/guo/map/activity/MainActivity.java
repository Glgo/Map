package com.example.guo.map.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.example.guo.map.R;
import com.example.guo.map.activity.erweima.QRActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerSDKCheckReceiver();
    }

    private void registerSDKCheckReceiver() {
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR.equals(action)) {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                } else if (SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR.equals(action)) {
                    Toast.makeText(getApplicationContext(), "key验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(receiver, filter);
    }

    //按钮点击事件
    public void startAct(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.bt_map:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_geo:
                intent = new Intent(this, GeoActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_busLine:
                intent = new Intent(this, BusLineActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_QR:
                intent = new Intent(this, QRActivity.class);
                startActivity(intent);
                break;

        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private Boolean isExit = false;//是否准备退出
    //双击返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);//如果2秒内没有按下返回键，则启动定时器取消刚才执行的任务
        } else {
            finish();//关闭当前界面
            System.exit(0);//关闭整个程序
        }
        return false;//拦截点击一次返回键直接退出应用
    }
}

