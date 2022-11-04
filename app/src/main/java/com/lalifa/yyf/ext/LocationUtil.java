package com.lalifa.yyf.ext;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.lalifa.yyf.R;

public class LocationUtil implements AMapLocationListener {
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption clientOption;
    private ILocationCallBack callBack;

    public void startLocate(Context context) throws Exception {
        aMapLocationClient = new AMapLocationClient(context);

        //设置监听回调
        aMapLocationClient.setLocationListener(this);

        //初始化定位参数
        clientOption = new AMapLocationClientOption();
        clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        clientOption.setNeedAddress(true);
        //单次监听
        clientOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        clientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        clientOption.setMockEnable(false);
        //多次监听，设置定位间隔
        clientOption.setInterval(2000);
        aMapLocationClient.setLocationOption(clientOption);

        aMapLocationClient.startLocation();
    }

    //完成定位回调
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            if(aMapLocation.getErrorCode() == 0){
                //定位成功完成回调
                String adCode = aMapLocation.getAdCode();
                String country = aMapLocation.getCountry();
                String province = aMapLocation.getProvince();
                String city = aMapLocation.getCity();
                String district = aMapLocation.getDistrict();
                String street = aMapLocation.getStreet();
                double lat = aMapLocation.getLatitude();
                double lgt = aMapLocation.getLongitude();

                callBack.callBack(adCode, district ,lat,lgt,aMapLocation);
            }else{
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    /**
     * 自定义图标
     * @return
     */
    public MarkerOptions getMarkerOption(String str, double lat, double lgt){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(com.lalifa.base.R.drawable.icon_bg_hui));
        markerOptions.position(new LatLng(lat,lgt));
        markerOptions.title(str);
        markerOptions.snippet("纬度:" + lat + "   经度:" + lgt);
        markerOptions.period(100);

        return markerOptions;
    }

    /**
     * 自定义图标
     * @return
     */
    public MarkerOptions getstart(String str,double lat,double lgt){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(com.lalifa.base.R.drawable.icon_bg_hui));
        markerOptions.position(new LatLng(lat,lgt));
        markerOptions.title(str);
        markerOptions.snippet("纬度:" + lat + "   经度:" + lgt);
        markerOptions.period(100);

        return markerOptions;
    }

    /**
     * 自定义图标
     * @return
     */
    public MarkerOptions getend(String str,double lat,double lgt){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(com.lalifa.base.R.drawable.icon_bg_hui));
        markerOptions.position(new LatLng(lat,lgt));
        markerOptions.title(str);
        markerOptions.snippet("纬度:" + lat + "   经度:" + lgt);
        markerOptions.period(100);

        return markerOptions;
    }

    public interface ILocationCallBack{
        void callBack(String code, String str, double lat, double lgt, AMapLocation aMapLocation);
    }

    public void setLocationCallBack(ILocationCallBack callBack){
        this.callBack = callBack;
    }

//    /**
//     * 搜索Poi回调
//     *
//     * @param poiResult  搜索结果
//     * @param resultCode 错误码
//     */
//    @Override
//    public void onPoiSearched(PoiResult poiResult, int resultCode) {
//
//        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
//            if (poiResult != null && poiResult.getPois().size() > 0) {
//                List<PoiItem> poiItems = poiResult.getPois();
//                datas.addAll(poiItems);
//                adapter.notifyDataSetChanged();
//            } else {
//                ToastUtil.toast("无搜索结果");
//            }
//        } else {
//            ToastUtil.toast("搜索失败" );
//        }
//    }
}
