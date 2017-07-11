package com.baidu.map.poisearch;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

/**
 * 类描述：
 * 创建人：alan
 * 创建时间：2017-07-07 15:52
 * 修改备注：
 */

public class StartPoiSearch implements OnGetPoiSearchResultListener {

    private PoiSearch poiSearch;
    private OnPoiResultListener onPoiResultListener;

    public void setOnPoiResultListener(OnPoiResultListener onPoiResultListener) {
        this.onPoiResultListener = onPoiResultListener;
    }

    public StartPoiSearch() {
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
    }

    public void startSearch(String keyword, LatLng center) {
        Log.e("XLog", "====latitude=======" + center.latitude);
        Log.e("XLog", "====longitude=======" + center.longitude);
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption().
                keyword(keyword).
                location(center).
                pageNum(30).
                radius(1000000);

        poiSearch.searchNearby(poiNearbySearchOption);


    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        Log.e("XLog", "====poiInfos=======" + poiInfos);
        if (poiInfos != null) {
            for (PoiInfo poiInfo : poiInfos) {
                Log.e("XLog", "====address=======" + poiInfo.address);
                Log.e("XLog", "====address=======" + poiInfo.name);
            }
        }

        if (onPoiResultListener != null) {
            onPoiResultListener.onPoiResult(poiResult);
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {


    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    public interface OnPoiResultListener {
        void onPoiResult(PoiResult poiResult);
    }

}
