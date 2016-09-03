package com.example.guo.map.activity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.example.guo.map.activity.erweima.QRActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {

	private BroadcastReceiver receiver;


	
	private ClassAndName[] datas = {
			new ClassAndName(MapActivity.class,"定位"),
			new ClassAndName(GeoActivity.class, "地理编码"),
			new ClassAndName(BusLineActivity.class, "公交路线"),
			new ClassAndName(QRActivity.class, "二维码")
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerSDKCheckReceiver();
		ArrayAdapter<ClassAndName> adapter = 
				new ArrayAdapter<ClassAndName>(this, android.R.layout.simple_list_item_1,datas);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// 取出单击位置的ClassAndName
		ClassAndName classAndName = (ClassAndName) l.getItemAtPosition(position);
		startActivity(new Intent(this, classAndName.clazz));
	}
	
	
	class ClassAndName {
		public Class<?> clazz;
		public String name;
		public ClassAndName(Class<?> cls, String name) {
			this.clazz = cls;
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
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
