package com.peihou.willgood.devicelist;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood.R;
import com.peihou.willgood.activity.MainActivity;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.ChangeDialog;
import com.peihou.willgood.database.dao.DeviceDao;
import com.peihou.willgood.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood.pojo.Device;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.http.HttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设备列表
 */
public class DeviceListActivity extends BaseActivity {

    @BindView(R.id.tv_ps) TextView tv_ps;//配电系统
    @BindView(R.id.image_right_ps) ImageView image_right_ps;//配电系统右键头
    @BindView(R.id.tv_ks) TextView tv_ks;//控电系统
    @BindView(R.id.image_right_ks) ImageView image_right_ks;//控电系统右键头
    @BindView(R.id.tv_ps2) TextView tv_ps2;
    @BindView(R.id.image_right_ps2) ImageView image_right_ps2;
    @BindView(R.id.tv_kz) TextView tv_kz;
    @BindView(R.id.image_right_kz) ImageView image_right_kz;
    DeviceDaoImpl deviceDao;
    int userId;
    Device device;
    Device device2;
    @Override
    public void initParms(Bundle parms) {
        userId=parms.getInt("userId");
        device= (Device) parms.getSerializable("device");
        device2= (Device) parms.getSerializable("device2");
    }
    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_list;
    }

    private boolean unbind=false;
    @Override
    public void initView(View view) {
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        Intent service=new Intent(this,MQService.class);
        unbind =bindService(service,connection,Context.BIND_AUTO_CREATE);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder localBinder= (MQService.LocalBinder) service;
            mqService=localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbind){
            unbindService(connection);
        }
    }

    int ps=0;//为0时，表示没有操作任何系统，为1是表示操作配电系统，为2时表示操作控电系统
    int delete=0;
    @OnClick({R.id.img_back,R.id.rl_ps,R.id.rl_ks,R.id.rl_ps2,R.id.rl_kz2})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_ps:
                setPsMode(1);
                if (device!=null){
                    ToastUtil.showShort(this,"请先删除原有配电系统设备");
                    break;
                }
                Intent intent=new Intent(this,QRScannerActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("userId",userId);
                startActivity(intent);
                break;
            case R.id.rl_ks:
                setPsMode(2);
                if (device2!=null){
                    ToastUtil.showShort(this,"请先删除原有控制系统设备");
                    break;
                }


                Intent intent2=new Intent(this,QRScannerActivity.class);
                intent2.putExtra("type",2);
                intent2.putExtra("userId",userId);
                startActivity(intent2);
                break;
            case R.id.rl_ps2:
                setPsMode(3);
                if (device==null){
                    ToastUtil.showShort(this,"请先添加配电系统设备");
                    break;
                }else {
                    long deviceId = device.getDeviceId();
                    changeDialog(1,deviceId);
                }
                break;
            case R.id.rl_kz2:
                setPsMode(4);
                if (device2==null){
                    ToastUtil.showShort(this,"请先添加控制系统设备");
                    break;
                }else {
                    long deviceId = device2.getDeviceId();
                    changeDialog(2,deviceId);
                }
                break;
        }
    }
    ChangeDialog dialog;
    private void changeDialog(final int type, final long deviceId) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        String tips="";
        if (type==1){
            tips="确定要删除配电系统吗?";
        }else if (type==2){
            tips="确定要删除控制系统吗?";
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
            dialog.setMode(1);
            dialog.setTips(tips);
            dialog.setTitle("删除");

        backgroundAlpha(0.4f);
        dialog.setOnNegativeClickListener(new ChangeDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new ChangeDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                try {
                    String content = dialog.getContent();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.show(DeviceListActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                    } else {
                        dialog.dismiss();
                        params.put("deviceId", deviceId);
                        params.put("type",type);
                        new DeleteDeviceAsync().execute(params).get(3, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        dialog.show();
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    Map<String, Object> params = new HashMap<>();
    class DeleteDeviceAsync extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            int type= (int) params.get("type");
            params.remove(type);
            int code = 0;
            try {
                String url = HttpUtils.ipAddress + "device/deleteDeviceByApp";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                       if (mqService!=null){
                           String deviceMac="";
                            if (type==1){
                               deviceMac=device.getDeviceOnlyMac();
                               deviceDao.delete(device);
                               device=null;
                            }else if (type==2){
                                deviceMac=device2.getDeviceOnlyMac();
                                deviceDao.delete(device2);
                                device2=null;
                            }
                            String server="qjjc/gateway/"+deviceMac+"/client_to_server";
                           String lwt="qjjc/gateway/"+deviceMac+"/client_to_server";

                           mqService.unsubscribe(server);
                           mqService.unsubscribe(lwt);
                       }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            switch (code) {
                case 100:
                    ToastUtil.showShort(DeviceListActivity.this, "删除成功");
                    break;
                default:
                    ToastUtil.showShort(DeviceListActivity.this, "删除失败");
                    break;
            }
        }
    }

    /**
     * 设置配电系统逻辑
     */

    private void setPsMode(int mode){
        if (mode==1){
            tv_ps.setTextColor(Color.parseColor("#09C585"));
            image_right_ps.setImageResource(R.mipmap.right_arrow_green);
            tv_ks.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ks.setImageResource(R.mipmap.right_arrow_black);
            tv_ps2.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps2.setImageResource(R.mipmap.right_arrow_black);
            tv_kz.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_kz.setImageResource(R.mipmap.right_arrow_black);
        }else if (mode==2){
            tv_ps.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps.setImageResource(R.mipmap.right_arrow_black);
            tv_ks.setTextColor(Color.parseColor("#09C585"));
            image_right_ks.setImageResource(R.mipmap.right_arrow_green);
            tv_ps2.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps2.setImageResource(R.mipmap.right_arrow_black);
            tv_kz.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_kz.setImageResource(R.mipmap.right_arrow_black);
        }else if (mode==3){
            tv_ps.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps.setImageResource(R.mipmap.right_arrow_black);
            tv_ks.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ks.setImageResource(R.mipmap.right_arrow_black);
            tv_ps2.setTextColor(Color.parseColor("#09C585"));
            image_right_ps2.setImageResource(R.mipmap.right_arrow_green);
            tv_kz.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_kz.setImageResource(R.mipmap.right_arrow_black);
        }else if (mode==4){
            tv_ps.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps.setImageResource(R.mipmap.right_arrow_black);
            tv_ks.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ks.setImageResource(R.mipmap.right_arrow_black);
            tv_ps2.setTextColor(Color.parseColor("#4b4b4b"));
            image_right_ps2.setImageResource(R.mipmap.right_arrow_black);
            tv_kz.setTextColor(Color.parseColor("#09C585"));
            image_right_kz.setImageResource(R.mipmap.right_arrow_green);
        }
    }

}
