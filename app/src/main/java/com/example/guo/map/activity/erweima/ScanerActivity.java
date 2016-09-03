package com.example.guo.map.activity.erweima;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.covics.zxingscanner.OnDecodeCompletionListener;
import com.covics.zxingscanner.ScannerView;
import com.example.guo.map.R;

/**
 * 二维码扫描页面
 * Created by Guo on 2016/9/3.
 */
public class ScanerActivity extends Activity implements
        OnDecodeCompletionListener {

    private ScannerView cScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scaner);
        init();
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
