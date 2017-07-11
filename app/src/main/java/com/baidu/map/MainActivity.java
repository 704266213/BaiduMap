package com.baidu.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.map.helper.BottomNavigationViewHelper;
import com.baidu.map.mark.LocationMark;
import com.baidu.map.poisearch.StartPoiSearch;
import com.baidu.map.popwindow.PoiSearchPopWindow;
import com.baidu.map.task.LocationTask;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiResult;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BDLocationListener,
        BaiduMap.OnMapStatusChangeListener, OnGetGeoCoderResultListener,
        StartPoiSearch.OnPoiResultListener, PoiSearchPopWindow.OnPoiSearchResultListener {


    public LocationTask locationTask;

    private LatLng locationPoint;
    private boolean isLocationMarkAdded = false;
    private TextView tips;
    private InfoWindow infoWindow;
    private Marker marker;
    private StartPoiSearch startPoiSearch;

    //更加坐标检索位置
    private GeoCoder searchByLatLng;
    private EditText keyWord;
    private BottomNavigationView navigation;
    private MapView mapView;
    private BaiduMap baiduMap;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
    }

    public void initView() {
        tips = (TextView) LayoutInflater.from(this).inflate(R.layout.tips, null);

        keyWord = (EditText) findViewById(R.id.keyWord);
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();

        baiduMap.setOnMapStatusChangeListener(this);
        searchByLatLng = GeoCoder.newInstance();
        searchByLatLng.setOnGetGeoCodeResultListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initLocation();

        startPoiSearch = new StartPoiSearch();
        startPoiSearch.setOnPoiResultListener(this);

    }

    private void initLocation() {
        locationTask = ((BaiduMapApplication) getApplication()).locationTask;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        //注册监听
        locationTask.registerListener(this);
        startLocation(true);
    }

    public void onKeySearchClickListener(View view) {
        if (locationPoint != null) {
            String key = keyWord.getText().toString();
            if (!TextUtils.isEmpty(key)) {
                startPoiSearch.startSearch(key, locationPoint);
            }
        }
    }

    public void onStartLocationClick(View view) {
        Log.e("XLog", "=========onStartLocationClick=================");
        isLocationMarkAdded = false;
//        startLocation(false);
    }

    private void startLocation(boolean isDefaultOption) {
        if (locationTask == null)
            locationTask = ((BaiduMapApplication) getApplication()).locationTask;

        LocationClientOption locationClientOption = isDefaultOption ? locationTask.getDefaultLocationClientOption() : locationTask.getOption();
        locationTask.setLocationOption(locationClientOption);
        // 定位SD
        locationTask.start();

    }

    private Marker addLocationMark(LatLng point, int markIcon) {
        LocationMark locationMark = new LocationMark();
        OverlayOptions options = locationMark.createMarkOptions(point, markIcon);
        Marker marker = (Marker) baiduMap.addOverlay(options);
        return marker;
    }

    private void showTips(String location, LatLng point) {
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        infoWindow = new InfoWindow(tips, point, -120);
        tips.setText(location);
        baiduMap.showInfoWindow(infoWindow);
    }


    private void updateMacCenter(LatLng point) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(20)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
    }


    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        LatLng point = new LatLng(mapStatus.target.latitude, mapStatus.target.longitude);
        //定义Maker坐标点
        if (marker == null) {
            marker = addLocationMark(point, R.drawable.map_mark);
        }
        marker.setPosition(point);
        baiduMap.hideInfoWindow();
//        showTips(tips.getText().toString(),point);
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        LatLng point = new LatLng(mapStatus.target.latitude, mapStatus.target.longitude);
        showTips("", point);
        baiduMap.showInfoWindow(infoWindow);
        searchByLatLng.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));

    }

    /*
     * 根据坐标检索位置
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult != null || geoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
            //获取地理编码结果
        }


    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult != null || reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
            //获取地理编码结果
            Log.e("XLog", "=======Address============" + reverseGeoCodeResult.getAddress());
            showTips(reverseGeoCodeResult.getAddress(), reverseGeoCodeResult.getLocation());
        }

    }


    /*
     * 定位成功的回调
     */
    @Override
    public void onReceiveLocation(final BDLocation location) {
        // TODO Auto-generated method stub
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(location.getTime());
            sb.append("\nlocType : ");// 定位类型
            sb.append(location.getLocType());
            sb.append("\nlocType description : ");// *****对应的定位类型说明*****
            sb.append(location.getLocTypeDescription());
            sb.append("\nlatitude : ");// 纬度
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");// 经度
            sb.append(location.getLongitude());
            sb.append("\nradius : ");// 半径
            sb.append(location.getRadius());
            sb.append("\nCountryCode : ");// 国家码
            sb.append(location.getCountryCode());
            sb.append("\nCountry : ");// 国家名称
            sb.append(location.getCountry());
            sb.append("\ncitycode : ");// 城市编码
            sb.append(location.getCityCode());
            sb.append("\ncity : ");// 城市
            sb.append(location.getCity());
            sb.append("\nDistrict : ");// 区
            sb.append(location.getDistrict());
            sb.append("\nStreet : ");// 街道
            sb.append(location.getStreet());
            sb.append("\naddr : ");// 地址信息
            sb.append(location.getAddrStr());
            sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
            sb.append(location.getUserIndoorState());
            sb.append("\nDirection(not all devices have value): ");
            sb.append(location.getDirection());// 方向
            sb.append("\nlocationdescribe: ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            sb.append("\nPoi: ");// POI信息
            if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                for (int i = 0; i < location.getPoiList().size(); i++) {
                    Poi poi = (Poi) location.getPoiList().get(i);
                    sb.append(poi.getName() + ";");
                }
            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 速度 单位：km/h
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());// 卫星数目
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 海拔高度 单位：米
                sb.append("\ngps status : ");
                sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                // 运营商信息
                if (location.hasAltitude()) {// *****如果有海拔高度*****
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                }
                sb.append("\noperationers : ");// 运营商信息
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            Log.e("XLog", sb.toString());


            locationPoint = new LatLng(location.getLatitude(), location.getLongitude());
            if (!isLocationMarkAdded) {
                isLocationMarkAdded = true;
                updateMacCenter(locationPoint);
                //定义Maker坐标点
                addLocationMark(locationPoint, R.drawable.location_mark);
            }

        }
    }


    public void onConnectHotSpotMessage(String s, int i) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationTask.unregisterListener(this); //注销掉监听
        locationTask.stop(); //停止定位服务
        searchByLatLng.destroy();
    }


    @Override
    public void onPoiResult(PoiResult poiResult) {
        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        Log.e("XLog", "====poiInfos=======" + poiInfos);
        if (poiInfos != null) {
            for (PoiInfo poiInfo : poiInfos) {
                addLocationMark(poiInfo.location, R.drawable.poi_marker_1);
            }

            PoiSearchPopWindow poiSearchPopWindow = new PoiSearchPopWindow(MainActivity.this);
            poiSearchPopWindow.addDataToList(poiInfos);
            poiSearchPopWindow.setOnPoiSearchResultListener(this);
            poiSearchPopWindow.showAsDropDown(keyWord);
        }
    }

    @Override
    public void onPoiItemResult(PoiInfo poiItem) {

    }
}
