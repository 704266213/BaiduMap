package com.baidu.map.mark;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * 类描述：
 * 创建人：alan
 * 创建时间：2017-07-07 13:45
 * 修改备注：
 */

public interface onCreateMarkListener {

    OverlayOptions createMarkOptions(LatLng point, int markIcon);
}
