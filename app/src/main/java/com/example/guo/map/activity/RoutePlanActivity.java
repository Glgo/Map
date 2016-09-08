package com.example.guo.map.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.guo.map.R;
import com.example.guo.map.overlayutil.BikingRouteOverlay;
import com.example.guo.map.overlayutil.DrivingRouteOverlay;
import com.example.guo.map.overlayutil.TransitRouteOverlay;
import com.example.guo.map.overlayutil.WalkingRouteOverlay;
import com.example.guo.map.search.RouteLineAdapter;

import java.util.List;

public class RoutePlanActivity extends Activity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    private android.widget.Button drive;
    private android.widget.Button transit;
    private android.widget.Button walk;
    private android.widget.Button bike;
    private com.baidu.mapapi.map.MapView mapView;
    private android.widget.Button pre;
    private android.widget.Button next;
    public BaiduMap mBaiduMap;

    private TextView popupText = null; // 泡泡view

    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;//路线数据结构的基类,表示一条路线

    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    TransitRouteResult nowResult = null;
    DrivingRouteResult nowResultd  = null;

    //由上个activity传过来的当前位置
    public double mLatitude;
    public double mLongitude;
    public PlanNode mStNode;
    public PlanNode mEnNode;
    private String mMyCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        this.next = (Button) findViewById(R.id.next);
        this.pre = (Button) findViewById(R.id.pre);
        this.mapView = (MapView) findViewById(R.id.map);
        this.bike = (Button) findViewById(R.id.bike);
        this.walk = (Button) findViewById(R.id.walk);
        this.transit = (Button) findViewById(R.id.transit);
        this.drive = (Button) findViewById(R.id.drive);

        //取出intent传过来的数据
        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra("myLatitude",38.052364);
        mLongitude = intent.getDoubleExtra("myLongitude",114.525676);
        mMyCity = intent.getStringExtra("myCity");
        //隐藏按钮
        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);

        //初始化地图
        mBaiduMap = mapView.getMap();

        // 地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        // 重置浏览节点的路线数据
        route = null;
        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();//清空地图所有的 Overlay 覆盖物以及 InfoWindow

        // 设置起终点信息
        mStNode = PlanNode.withLocation(new LatLng(mLatitude,mLongitude));
        mEnNode = PlanNode.withLocation(MapActivity.currentPt);
        //默认驾车路线
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(mStNode).to(mEnNode));

    }

    /**
     * 发起路线规划搜索示例
     * 路线规划点击事件
     * @param v
     */
    public void searchButtonProcess(View v){

        mBaiduMap.clear();//清空地图所有的 Overlay 覆盖物以及 InfoWindow

        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(mStNode).to(mEnNode));
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(mStNode).city(mMyCity).to(mEnNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(mStNode).to(mEnNode));
        } else if (v.getId() == R.id.bike) {
            mSearch.bikingSearch((new BikingRoutePlanOption())
                    .from(mStNode).to(mEnNode));
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RoutePlanActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

    //步行路线
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //公交车路线
    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);


            if (result.getRouteLines().size() > 1 ) {
                nowResult = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanActivity.this,
                        result.getRouteLines(),
                        RouteLineAdapter.Type.TRANSIT_ROUTE);
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    public void onItemClick(int position) {
                        route = nowResult.getRouteLines().get(position);
                        TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(nowResult.getRouteLines().get(position));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                });
                myTransitDlg.show();



            } else if ( result.getRouteLines().size() == 1 ) {
                // 直接显示
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("transitresult", "结果数<0" );
                return;
            }


        }
    }

    //驾车路线
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;


            if (result.getRouteLines().size() > 1 ) {
                nowResultd = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanActivity.this,
                        result.getRouteLines(),
                        RouteLineAdapter.Type.DRIVING_ROUTE);
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    public void onItemClick(int position) {
                        route = nowResultd.getRouteLines().get(position);
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(nowResultd.getRouteLines().get(position));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                });
                myTransitDlg.show();

            } else if ( result.getRouteLines().size() == 1 ) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                pre.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }

        }
    }

    //骑行路线
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            route = bikingRouteResult.getRouteLines().get(0);
            BikingRouteOverlay overlay = new BikingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(bikingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    /**
     * 单击地图隐藏气泡
     */
    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 单击地图中的POI点
     */
    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mapView.onDestroy();
        super.onDestroy();
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick( int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private  RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List< ? extends RouteLine> transitRouteLines,  RouteLineAdapter.Type
                type) {
            this( context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new  RouteLineAdapter( context, mtransitRouteLines , type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener( new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick( position);
                    pre.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    dismiss();

                }
            });
        }

        public void setOnItemInDlgClickLinster( OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }
}
