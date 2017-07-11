package com.baidu.map.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.baidu.map.R;
import com.baidu.map.adapter.PoiResultAdapter;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;


/**
 * 类描述：
 * 创建人：alan
 * 创建时间：2017-07-07 15:23
 * 修改备注：
 */

public class PoiSearchPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context context;
    private ListView searchResultList;
    private PoiResultAdapter poiResultAdapter;
    private OnPoiSearchResultListener onPoiSearchResultListener;

    public void setOnPoiSearchResultListener(OnPoiSearchResultListener onPoiSearchResultListener) {
        this.onPoiSearchResultListener = onPoiSearchResultListener;
    }

    public void addDataToList(List<PoiInfo> poiItems) {
        poiResultAdapter.addDataToList(poiItems);
    }


    public PoiSearchPopWindow(Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchResultList = (ListView)inflater.inflate(R.layout.poi_search_popup_window, null);
        setContentView(searchResultList);


        poiResultAdapter = new PoiResultAdapter(context);
        searchResultList.setAdapter(poiResultAdapter);
        searchResultList.setOnItemClickListener(this);


        // 设置弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setTouchable(true);
        this.setFocusable(true);
        // 设置点击是否消失
        this.setOutsideTouchable(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable background = new ColorDrawable(0x4f000000);
        //设置弹出窗体的背景
        this.setBackgroundDrawable(background);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onPoiSearchResultListener != null) {
            onPoiSearchResultListener.onPoiItemResult(poiResultAdapter.getItem(position));
        }
        dismiss();
    }


    public interface OnPoiSearchResultListener {

        void onPoiItemResult(PoiInfo poiItem);

    }


}