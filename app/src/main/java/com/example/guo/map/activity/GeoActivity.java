package com.example.guo.map.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.guo.map.R;

import java.math.BigDecimal;

/**
 * 展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class GeoActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;
    public EditText mLat;
    public EditText mLon;
    public EditText mEditCity;
    public EditText mEditGeoCodeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);
        CharSequence titleLable = "地理编码功能";
        setTitle(titleLable);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 设置地图中心点为石家庄
        LatLng hmPos = new LatLng(38.041357,114.514513);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(hmPos);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        // 设置地图缩放为13
        mapStatusUpdate = MapStatusUpdateFactory.zoomTo(13);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    /**
     * 发起搜索
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        if (v.getId() == R.id.reversegeocode) {
            mLat = (EditText) findViewById(R.id.lat);
            mLon = (EditText) findViewById(R.id.lon);
            LatLng ptCenter = new LatLng((Float.valueOf(mLat.getText()
                    .toString())), (Float.valueOf(mLon.getText().toString())));
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        } else if (v.getId() == R.id.geocode) {
            mEditCity = (EditText) findViewById(R.id.city);
            mEditGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
            // Geo搜索
            mSearch.geocode(new GeoCodeOption().city(
                    mEditCity.getText().toString()).address(mEditGeoCodeKey.getText().toString()));
        }
    }

    /**
     * 地理编码查询结果回调函数
     *
     * @param result
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GeoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        //获取经纬度的字符串形式，小数点后保留6位
        double latitude = result.getLocation().latitude;
        double longitude = result.getLocation().longitude;
        BigDecimal bigDecimal1 = new BigDecimal(latitude);
        String lat = bigDecimal1.setScale(6, BigDecimal.ROUND_HALF_UP).toString();
        BigDecimal bigDecimal2 = new BigDecimal(longitude);
        String lon = bigDecimal2.setScale(6, BigDecimal.ROUND_HALF_UP).toString();

        String strInfo = String.format("纬度：%f 经度：%f", latitude, longitude);
//        mLat.setText(lat);
//        mLon.setText(lon);
        Toast.makeText(GeoActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 反地理编码查询结果回调函数
     *
     * @param result
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GeoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        Toast.makeText(GeoActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        super.onDestroy();
    }
}