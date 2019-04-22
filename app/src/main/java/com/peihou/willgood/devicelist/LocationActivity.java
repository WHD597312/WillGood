package com.peihou.willgood.devicelist;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.Marker;
import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.WeakRefHandler;
import com.peihou.willgood.pojo.Position;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.BdMapUtils;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LocationActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    double discance = 0;//轨迹的距离
    private List<Position> positions = new ArrayList<>();//存储定位的集合
    int position = 0;//定位的几几次位置
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.img_back)
    ImageView img_back;
    String deviceMac;
    String topicName;

    @Override
    public void initParms(Bundle parms) {
        deviceMac = parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_location;
    }

    BaiduMap mMap;

    boolean bind;
    @Override
    public void initView(View view) {
        permissionGrantedSuccess();
        double latitude = 30.179158;
        double longtitude = 121.266949;
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("LocationActivity");
        registerReceiver(receiver, filter);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        //反地理编码，获取经纬度详细地址
//        BdMapUtils.reverseGeoParse(longtitude, latitude, new OnGetGeoCoderResultListener() {
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//            }
//
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                    // 没有检测到结果
//
//                } else {////得到结果后处理方法
//                    String address = result.getAddress();
//                    Log.i("address", "-->" + address);
//                }
//            }
//        });
    }


    double latitude = 30.179158;
    //获取经度信息
    double longitude = 121.266949;

    @OnClick({R.id.img_back, R.id.rl_position, R.id.img_position, R.id.img_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    break;
                }
                finish();
                break;
            case R.id.rl_position:
                popupWindow();
                break;
            case R.id.img_position:
                if (popupWindow2!=null && popupWindow2.isShowing()){
                    ToastUtil.showShort(this,"请稍后");
                    break;
                }
                if (mqService!=null){
                    mqService.getData(topicName,0x77);
                    countTimer.start();
                }
                if (lastLatitude!=0 && lastLongitude!=0) {
                    LatLng latLng=new LatLng(lastLatitude, lastLongitude);
                    BitmapDescriptor startBitmap = BitmapDescriptorFactory
                            .fromResource(R.mipmap.image_location);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions startOption = new MarkerOptions()
                            .position(latLng)
                            .icon(startBitmap);
                    //在地图上添加Marker，并显示
                    mMap.addOverlay(startOption);

                    MapStatusUpdate mapStatusUpdate2 = MapStatusUpdateFactory.newLatLng(latLng);
                    mMap.setMapStatus(mapStatusUpdate2);
                }

                break;
            case R.id.img_set:
                Intent intent=new Intent(this,LocationSetActivity.class);
                intent.putExtra("deviceMac",deviceMac);
                if (mqService!=null){
                    int location=mqService.getDeviceLocationFre(deviceMac);
                    intent.putExtra("location",location);
                }

                startActivity(intent);

        }
    }
    private PopupWindow popupWindow2;
    public void popupmenuWindow3() {
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.progress, null);
        TextView tv_load = view.findViewById(R.id.tv_load);
        tv_load.setTextColor(getResources().getColor(R.color.white));

            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //添加弹出、弹入的动画
        popupWindow2.setAnimationStyle(R.style.Popupwindow);
        popupWindow2.setFocusable(false);
        popupWindow2.setOutsideTouchable(false);
        backgroundAlpha(0.6f);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow2.showAsDropDown(et_wifi, 0, -20);
        popupWindow2.showAtLocation(img_back, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    CountTimer countTimer = new CountTimer(2000, 1000);
    class CountTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            popupmenuWindow3();
        }

        @Override
        public void onFinish() {
            if (popupWindow2 != null && popupWindow2.isShowing()) {
                popupWindow2.dismiss();
            }
        }
    }

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1001) {
                String s = (String) msg.obj;
                String[] ss = s.split("&");
                String ss1 = ss[0];
                String ss2 = ss[1];
                double latitude = Double.parseDouble(ss1);
                double longitude = Double.parseDouble(ss2);
                //构建Marker图标
                LatLng latLng = new LatLng(latitude, longitude);
                BitmapDescriptor startBitmap = BitmapDescriptorFactory
                        .fromResource(R.mipmap.image_location);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions startOption = new MarkerOptions()
                        .position(latLng)
                        .icon(startBitmap);
                //在地图上添加Marker，并显示
                mMap.addOverlay(startOption);
                MapStatusUpdate mapStatusUpdate2 = MapStatusUpdateFactory.newLatLng(latLng);
                mMap.setMapStatus(mapStatusUpdate2);
            }
            return true;
        }
    };
    Handler handler = new WeakRefHandler(callback);
    MessageReceiver receiver;
    double lastLatitude;
    double lastLongitude;
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x77);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String macAddress = intent.getStringExtra("macAddress");
            try {
                if (macAddress.equals(deviceMac)) {
                    double latitude = intent.getDoubleExtra("latitude", 0);
                    double longitude = intent.getDoubleExtra("longitude", 0);
                    lastLatitude=latitude;
                    lastLongitude=longitude;
                    String s = latitude + "&" + longitude;
                    Message msg = handler.obtainMessage();
                    msg.what = 1001;
                    msg.obj = s;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean running = false;

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        mMap = mapView.getMap();
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mapView.showScaleControl(false);
        // 隐藏缩放控件
        mapView.showZoomControls(false);


//        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                .fromResource(R.mipmap.img_position);
//        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(new com.baidu.mapapi.model.LatLng(30.17915,121.266949))
//                .icon(bitmap);
//        //在地图上添加Marker，并显示
//        mMap.addOverlay(option);
//        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(5);
//        mMap.animateMapStatus(mapStatusUpdate);
//        MapStatusUpdate mapStatusUpdate2=MapStatusUpdateFactory.newLatLng(new com.baidu.mapapi.model.LatLng(30.17915,121.266949));
//        mMap.setMapStatus(mapStatusUpdate2);
    }


    @Override
    public void onBackPressed() {

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        super.onBackPressed();

    }

    private PopupWindow popupWindow;
    TextView tv_start;
    TextView tv_end;
    TextView tv_distance;
    TextView tv_timer;

    private void popupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.popup_trace, null);
        tv_start = view.findViewById(R.id.tv_start);
        tv_end = view.findViewById(R.id.tv_end);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_timer = view.findViewById(R.id.tv_timer);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        popupWindow.showAtLocation(img_back, Gravity.BOTTOM, 0, 50);
        //添加按键事件监听
        backgroundAlpha(0.7f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        if (!positions.isEmpty()) {
            Position start = positions.get(0);
            Position end = positions.get(positions.size() - 1);
            String startAddress = start.getAddress();
            String endAddress = end.getAddress();
            tv_start.setText(startAddress);
            tv_end.setText(endAddress);
            tv_distance.setText(discance + "");
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timer = format.format(date);
            tv_timer.setText(timer);
        }
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }

        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        if (bind){
            unbindService(connection);
        }
        super.onDestroy();

    }

    @Override
    public void doBusiness(Context mContext) {

    }


    private static final int RC_CAMERA_AND_LOCATION = 0;
    private boolean isNeedCheck = true;


    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void permissionGrantedSuccess() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经申请过权限，做想做的事
//// 开始定位
            initLocation();
            startLocation();

        } else {
//             没有申请过权限，现在去申请
            if (isNeedCheck) {
                EasyPermissions.requestPermissions(this, getString(R.string.location),
                        RC_CAMERA_AND_LOCATION, perms);
            }

        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 把执行结果的操作给EasyPermissions
        System.out.println(requestCode);
        if (isNeedCheck) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle("提示")
                    .setRationale("请点击\"设置\"打开定位权限。")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .build()
                    .show();
            isNeedCheck = false;
        }
    }


    LocationClient bLocationClient;

    /**
     * 初始化定位参数配置  setScanSpan可以设置定位的周期这里我设置的是10秒
     */

    MyLocationListener myLocationListener;

    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动

//声明LocationClient类实例并配置定位参数
        bLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption locationOption = new LocationClientOption();
        myLocationListener = new MyLocationListener();
//注册监听函数
        bLocationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        bLocationClient.setLocOption(locationOption);

        startBaiduLocation();
    }

    /**
     * 开始定位
     */
    private void startBaiduLocation() {
        bLocationClient.start();
    }

    /**
     * 结束定位
     */
    private void stopBaiduLocation() {
        bLocationClient.stop();
    }

    String address;

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明


            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();

            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            Log.i("MyLocationListenerType", "-->" + errorCode);
            if (errorCode == 161) {
                //获取纬度信息
                latitude = location.getLatitude();
                //获取经度信息
                longitude = location.getLongitude();

                BdMapUtils.reverseGeoParse(longitude, latitude, new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            // 没有检测到结果

                        } else {////得到结果后处理方法
                            address = result.getAddress();
                            Log.i("addressaaaaaaaa", "-->" + address);
                            LatLng latLng = new LatLng(latitude, longitude);
                            Position position2 = new Position(position, address, latLng);
                            positions.add(position2);


                            Collections.sort(positions, new Comparator<Position>() {
                                @Override
                                public int compare(Position o1, Position o2) {
                                    if (o1.getPosition() > o2.getPosition()) {
                                        return 1;
                                    } else if (o1.getPosition() < o2.getPosition()) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            discance = 0;
                            for (int i = 0; i < positions.size(); i++) {
                                Position position = positions.get(i);
                                LatLng p1 = position.getLatLng();
                                LatLng p2 = position.getLatLng();
                                discance += DistanceUtil.getDistance(p1, p2);
                                posints.add(positions.get(i).getLatLng());
                            }
                            if (posints.size() > 3) {
                                //设置折线的属性
                                OverlayOptions mOverlayOptions = new PolylineOptions()
                                        .width(10)
                                        .color(0xAAFF0000)
                                        .points(posints);
                                Overlay mPolyline = mMap.addOverlay(mOverlayOptions);
                            }


                            MapStatusUpdate mapStatusUpdate2 = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                            mMap.setMapStatus(mapStatusUpdate2);
                            if (popupWindow != null && popupWindow.isShowing()) {
                                if (!positions.isEmpty()) {
                                    Position start = positions.get(0);
                                    Position end = positions.get(positions.size() - 1);
                                    String startAddress = start.getAddress();
                                    String endAddress = end.getAddress();
                                    tv_start.setText(startAddress);
                                    tv_end.setText(endAddress);
                                    tv_distance.setText(discance + "");
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    String timer = format.format(date);
                                    tv_timer.setText(timer);
                                }
                            }
                            position++;
                        }
                    }
                });
            }
        }
    }

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000 * 30);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */

    List<com.baidu.mapapi.model.LatLng> posints = new ArrayList<>();
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    Log.i("latLng", "(" + location.getLatitude() + "," + location.getLongitude() + ")");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");

                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
                //解析定位结果，
                String result = sb.toString();
                Log.i("reSult", "-->" + result);
                if ("定位失败".equals(result)) {
                    Log.i("mmmm", "-->" + "定位失败");
                } else {
                    String address = location.getAddress();
                    double latitude = location.getLatitude();
                    double longtitude = location.getLongitude();
                    com.baidu.mapapi.model.LatLng latLng = new com.baidu.mapapi.model.LatLng(latitude, longtitude);
                    Position position2 = new Position(position, address, latLng);
                    positions.add(position2);
                    Collections.sort(positions, new Comparator<Position>() {
                        @Override
                        public int compare(Position o1, Position o2) {
                            if (o1.getPosition() > o2.getPosition()) {
                                return 1;
                            } else if (o1.getPosition() < o2.getPosition()) {
                                return -1;
                            }
                            return 0;
                        }
                    });




                    MapStatusUpdate mapStatusUpdate2 = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longtitude));
                    mMap.setMapStatus(mapStatusUpdate2);
                    stopLocation();
                    if (mqService!=null){
                        mqService.getData(topicName,0x77);
                    }
                }
            }
        }
    };

    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
