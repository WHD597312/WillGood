package com.peihou.willgood.devicelist;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.peihou.willgood.R;
import com.peihou.willgood.activity.MainActivity;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.base.MyApplication;
import com.peihou.willgood.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood.pojo.Device;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.camera.CameraManager;
import com.peihou.willgood.util.decoding.CaptureActivityHandler;
import com.peihou.willgood.util.decoding.InactivityTimer;
import com.peihou.willgood.util.http.BaseWeakAsyncTask;
import com.peihou.willgood.util.http.HttpUtils;
import com.peihou.willgood.util.view.ViewfinderView;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 扫描二维码
 */
public class QRScannerActivity extends BaseActivity implements SurfaceHolder.Callback,EasyPermissions.PermissionCallbacks {

    @BindView(R.id.viewfinder_view) ViewfinderView viewfinderView;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    @BindView(R.id.rl_body4) RelativeLayout rl_body4;
    @BindView(R.id.rl_body3) RelativeLayout rl_body3;
    @BindView(R.id.tv_wifi)
    TextView tv_wifi;//wifi添加设备
    @BindView(R.id.tv_gprs) TextView tv_gprs;//gprs添加设备
    @BindView(R.id.et_name)
    EditText et_name;//gprs/wifi名称
    @BindView(R.id.et_pswd) EditText et_pswd;//WiFi密码
    @BindView(R.id.et_orignal_code) EditText et_orignal_code;//wifi状态下的初始码
    DeviceDaoImpl deviceDao;


    String shareDeviceId;
    String shareContent;
    String shareMacAddress;
    private String userId;

    ImageView back;

    private boolean isBound=false;


    int addType=0;
    int type=-1;
    @Override
    public void initParms(Bundle parms) {
        type=parms.getInt("type");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_qrscanner;
    }

    @Override
    public void initView(View view) {
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        init();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void init() {
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionGrantedSuccess();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;


    }

    int deviceModel=0;
    @OnClick({R.id.back,R.id.img_book,R.id.rl_body3,R.id.rl_body4,R.id.bt_add_finish,R.id.tv_wifi,R.id.tv_gprs})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img_book:
                if (addType==0){
                    addType=1;
                    rl_body3.setVisibility(View.VISIBLE);
                    rl_body4.setVisibility(View.GONE);
                }else if (addType==1){
                    addType=0;
                    rl_body3.setVisibility(View.GONE);
                    rl_body4.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.tv_wifi:
                if (deviceModel==0)
                    break;
                tv_wifi.setTextColor(Color.parseColor("#09c585"));
                tv_gprs.setTextColor(Color.parseColor("#646464"));
                et_name.setHint("WiFi扫描中");
                et_pswd.setHint("请输入WiFi密码");
                et_orignal_code.setHint("请输入初始码");
                et_orignal_code.setHint("请输入初始码");
                et_orignal_code.setVisibility(View.VISIBLE);
                et_orignal_code.setBackgroundColor(Color.parseColor("#f7f7fa"));
                deviceModel=0;
                break;
            case R.id.tv_gprs:
                if (deviceModel==1)
                    break;
                tv_wifi.setTextColor(Color.parseColor("#646464"));
                tv_gprs.setTextColor(Color.parseColor("#09c585"));
                et_name.setHint("请输入IMEI号");
                et_pswd.setHint("请输入初始码");
                et_orignal_code.setHint("");
                et_orignal_code.setVisibility(View.GONE);
                deviceModel=1;
                break;
            case R.id.bt_add_finish:
                String name=et_name.getText().toString();
                if (TextUtils.isEmpty(name)){
                    ToastUtil.show(this,"设备IMEI不能为空",Toast.LENGTH_SHORT);
                    break;
                }

                deviceDao.deleteAll();
                Device device=new Device();
                device.setDeviceOnlyMac(name.trim());
                device.setSystem(type);
                if (deviceDao.insert(device)){
                    ToastUtil.show(this,"添加成功",Toast.LENGTH_SHORT);
                    Intent intent=new Intent(this,MainActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtil.show(this,"添加失败",Toast.LENGTH_SHORT);
                }
                break;
        }
    }


    class AddDeivceAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,QRScannerActivity> {

        public AddDeivceAsync(QRScannerActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(QRScannerActivity activity,Map<String, Object>... maps) {
            String url=HttpUtils.ipAddress+ "device/addDeviceByAPP";
            Map<String,Object> params=maps[0];
            int code=0;
            try {
                String result=HttpUtils.requestPost(url,params);
                Log.i("result","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        String s=returnData.toString();
//                        Gson gson=new Gson();
//                        device=gson.fromJson(s, com.peihou.willgood2.pojo.Device.class);
//                        String deviceMac=device.getDeviceOnlyMac();
//                        device.setDeviceName(deviceName);
//                        List<Device> deleteDevices=deviceDao.findDevicesByMac(deviceMac);
//                        deviceDao.deleteDevices(deleteDevices);
//                        deviceDao.insert(device);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(QRScannerActivity activity,Integer code) {
            switch (code){
                case 100:
                    ToastUtil.showShort(QRScannerActivity.this,"添加成功");
                    Intent intent=new Intent();
//                    intent.putExtra("device",device);
                    setResult(100,intent);
                    finish();
                    break;
                case 10007:
                    ToastUtil.showShort(QRScannerActivity.this,"对不起您的设备初始码错误，请重置后重新添加");
                    break;
                default:
                    ToastUtil.showShort(QRScannerActivity.this,"添加失败");
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     */
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        try {
            if (TextUtils.isEmpty(resultString)) {
                Toast.makeText(QRScannerActivity.this, "扫描失败!", Toast.LENGTH_SHORT).show();
            } else {
                String content = resultString;
                if (!TextUtils.isEmpty(content)) {
                    Device device=new Device();
                    device.setDeviceOnlyMac(content.trim());
                    device.setSystem(type);
                    if (deviceDao.insert(device)){
                        ToastUtil.show(this,"添加成功",Toast.LENGTH_SHORT);
                        Intent intent=new Intent(this,MainActivity.class);
                        startActivity(intent);
                    }else {
                        ToastUtil.show(this,"添加失败",Toast.LENGTH_SHORT);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
//            handler = new CaptureActivityHandler(QRScannerActivity.this, decodeFormats, characterSet);
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private static final int RC_CAMERA_AND_LOCATION=0;
    private boolean isNeedCheck=true;
    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void permissionGrantedSuccess(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
//             没有申请过权限，现在去申请
            if (isNeedCheck){
                EasyPermissions.requestPermissions(this, getString(R.string.camer),
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
        if (isNeedCheck){
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
                    .setRationale("请点击\"设置\"打开相机权限。")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .build()
                    .show();
            isNeedCheck=false;
        }
    }
}