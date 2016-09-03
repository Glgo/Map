package com.example.guo.map.activity.erweima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guo.map.R;

/**
 * Created by Guo on 2016/9/3.
 */
public class QRActivity extends AppCompatActivity {

    // 扫描按钮
    private Button cButtonScan;
    // 扫描结果
    private TextView cTextView;
    // 生成二维码
    private Button cButtonCreate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erweima);
        init();
    }

    private void init() {

        cButtonScan = (Button) findViewById(R.id.btn_scan);
        cButtonCreate = (Button) findViewById(R.id.btn_create);
        cTextView = (TextView) findViewById(R.id.tv_info);
        cButtonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRActivity.this,
                        ScanerActivity.class);
                startActivityForResult(intent, 0x01);
            }
        });
        cButtonCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRActivity.this,
                        CreateActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 扫描结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && resultCode == 0x02 && data != null) {
            if (data.getExtras().containsKey("result")) {
                cTextView.setText(data.getExtras().getString("result"));
            }
        }
    }
}
