package com.baidu.map.mark;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * 类描述：
 * 创建人：alan
 * 创建时间：2017-07-07 11:15
 * 修改备注：
 */

public class LocationMark implements onCreateMarkListener {


    public OverlayOptions createMarkOptions(LatLng point, int markIcon) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(markIcon);
        OverlayOptions options = new MarkerOptions()
                .position(point)  //设置marker的位置
                .icon(bitmap)  //设置marker图标
                .zIndex(18)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽
        return options;
    }

}
