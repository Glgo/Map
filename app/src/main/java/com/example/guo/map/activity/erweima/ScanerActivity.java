package com.example.guo.map.activity.erweima;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.covics.zxingscanner.OnDecodeCompletionListener;
import com.covics.zxingscanner.ScannerView;
import com.example.guo.map.R;

/**
 * 二维码扫描页面
 * Created by Guo on 2016/9/3.
 */
public class ScanerActivity extends Activity implements
        OnDecodeCompletionListener {

    public static final int CAMERAPM = 0;
    private ScannerView cScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scaner);
        //检查用户权限
        checkPermission();
        init();
    }

    private void checkPermission() {
        // 检查是否已经授权该权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // 判断是否需要解释获取权限原因
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // 需要向用户解释
                // 此处可以弹窗或用其他方式向用户解释需要该权限的原因
                Snackbar.make(cScannerView, "需要使用摄像头",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat
                                        .requestPermissions(ScanerActivity.this, new
                                                String[]{Manifest.permission.CAMERA}, CAMERAPM);
                            }
                        });
            } else {
                // 无需解释，直接请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERAPM
                );
                // CAMERAPM 是自定义的常量，在回调方法中可以获取到
            }

        }
    }

    private void init() {
        cScannerView = (ScannerView) findViewById(R.id.scanner_view);
        cScannerView.setOnDecodeListener(this);
    }

    //扫描结果
    @Override
    public void onDecodeCompletion(String barcodeFormat, String barcode, Bitmap bitmap) {

        if (barcode != null) {
            Intent intent = new Intent();
            intent.putExtra("result", barcode);
            setResult(0x02, intent);
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cScannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cScannerView.onPause();
    }
}
