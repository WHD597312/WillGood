package com.peihou.willgood.devicelist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.AlermDialog;
import com.peihou.willgood.custom.AlermDialog2;
import com.peihou.willgood.custom.AlermDialog3;
import com.peihou.willgood.custom.DialogLoad;
import com.peihou.willgood.database.dao.impl.DeviceAlermDaoImpl;
import com.peihou.willgood.pojo.Alerm;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.TenTwoUtil;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.Utils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报警设置
 */
public class AlermActivity extends BaseActivity {


    @BindView(R.id.list_alerm)
    RecyclerView list_alerm;//报警视图列表
    private List<Alerm> list = new ArrayList<>();

    private MyAdapter adapter;
    private long deviceId;
    private String deviceMac;
    private DeviceAlermDaoImpl deviceAlermDao;
    int[] data = new int[17];
    int[] x = new int[8];//报警类型
    String topicName;
    int mcuVersion;
    public static boolean running = false;
    boolean online;

    @Override
    public void initParms(Bundle parms) {
        deviceId = parms.getLong("deviceId");
        deviceMac = parms.getString("deviceMac");
        mcuVersion = parms.getInt("mcuVersion");
        online = parms.getBoolean("online");

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_alerm;
    }

    Map<String, Object> params = new HashMap<>();

    @Override
    public void initView(View view) {

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);
        deviceAlermDao = new DeviceAlermDaoImpl(getApplicationContext());
        list_alerm.setLayoutManager(new LinearLayoutManager(this));

        list = deviceAlermDao.findDeviceAlerms(deviceMac);
        List<Alerm> list2 = deviceAlermDao.findDeviceAlerms(deviceMac);

        if (list2.size() != 8) {
            deviceAlermDao.deleteDeviceAlerms(deviceMac);
            list2.add(new Alerm("来电报警", 0, "设备已来电!", false,  deviceMac, 0));
            list2.add(new Alerm("断电报警", 1, "设备已断电,请及时处理", false,  deviceMac, 0));
            list2.add(new Alerm("温度报警", 2, "温度报警,请注意", false, deviceMac, 0));
            list2.add(new Alerm("湿度报警", 3, "湿度报警,请注意", false, deviceMac, 0));
            list2.add(new Alerm("电压报警", 4, "电压报警,请注意", false, deviceMac, 0));
            list2.add(new Alerm("电流报警", 5, "电流报警,请注意", false, deviceMac, 0));
            list2.add(new Alerm("功率报警", 6, "功率报警,请注意", false, deviceMac, 0));
            list2.add(new Alerm("开关量报警", 7, "开关量报警,请注意", false, deviceMac, 0));
            deviceAlermDao.insertDeviceAlerms(list2);
            list.addAll(list2);
        }



        topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        adapter = new MyAdapter(this, list);
        list_alerm.setAdapter(adapter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

        receiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter("AlermActivity");
        intentFilter.addAction("offline");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
        if (mqService!=null){
//            mqService.connectMqtt(deviceMac);

            List<Alerm> alerms=deviceAlermDao.findDeviceAlerms(deviceMac);
            list.clear();
            list.addAll(alerms);
            adapter.notifyDataSetChanged();
            mqService.getData(topicName, 0x66);
            countTimer.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
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
            setLoadDialog();
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
        }
    }
    DialogLoad dialogLoad;
    private void setLoadDialog() {
        if (dialogLoad != null && dialogLoad.isShowing()) {
            return;
        }

        dialogLoad = new DialogLoad(this);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.setLoad("正在加载,请稍后");
        dialogLoad.show();
    }
//    private PopupWindow popupWindow2;

//    public void popupmenuWindow3() {
//        if (popupWindow2 != null && popupWindow2.isShowing()) {
//            return;
//        }
//        View view = View.inflate(this, R.layout.progress, null);
//        TextView tv_load = view.findViewById(R.id.tv_load);
//        tv_load.setTextColor(getResources().getColor(R.color.white));
//
//            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        //添加弹出、弹入的动画
//        popupWindow2.setAnimationStyle(R.style.Popupwindow);
//        popupWindow2.setFocusable(false);
//        popupWindow2.setOutsideTouchable(false);
//        backgroundAlpha(0.6f);
//        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1.0f);
//            }
//        });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//        popupWindow2.showAtLocation(list_alerm, Gravity.CENTER, 0, 0);
//        //添加按键事件监听
//    }

//    class UpdateAlermAeync extends AsyncTask<Map<String, Object>, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(Map<String, Object>... maps) {
//            int code = 0;
//            try {
//                Map<String, Object> params = maps[0];
//
//                String url = HttpUtils.ipAddress + "device/changeAlarmName";
//                String result = HttpUtils.requestPost(url, params);
//                if (TextUtils.isEmpty(result)) {
//                    result = HttpUtils.requestPost(url, params);
//                }
//                if (!TextUtils.isEmpty(result)) {
//                    JSONObject jsonObject = new JSONObject(result);
//                    code = jsonObject.getInt("returnCode");
//                    if (code == 100) {
//                        if (params.containsKey("alarmName")) {
//                            String alarmName = (String) params.get("alarmName");
//                            Alerm alerm = list.get(updatePosition - 4);
//                            alerm.setContent(alarmName);
//                            list.set(updatePosition - 4, alerm);
//                        }
//                        if (params.containsKey("deviceAlarmBroadcast")) {
//                            once = (int) params.get("deviceAlarmBroadcast");
//                            for (Alerm alerm : list) {
//                                alerm.setDeviceAlarmBroadcast(once);
//                                deviceAlermDao.update(alerm);
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return code;
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            if (integer == 100) {
//                if (alermDialog != null && alermDialog.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改成功");
//                    alermDialog.dismiss();
//                } else if (alermDialog2 != null && alermDialog2.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改成功");
//                    alermDialog2.dismiss();
//                } else if (alermDialog3 != null && alermDialog3.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改成功");
//                    alermDialog3.dismiss();
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                if (alermDialog != null && alermDialog.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改失败");
//                    alermDialog.dismiss();
//                } else if (alermDialog2 != null && alermDialog2.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改失败");
//                    alermDialog2.dismiss();
//                } else if (alermDialog3 != null && alermDialog3.isShowing()) {
//                    ToastUtil.showShort(AlermActivity.this, "修改失败");
//                    alermDialog3.dismiss();
//                }
//            }
//        }
//    }

    int userId;
    SharedPreferences preferences;
    int notify = 0;//0为不提醒语音播报,1为提醒语音播报

    @OnClick({R.id.img_back, R.id.img_log})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                mqService.initAlarmData();
                finish();
                break;
            case R.id.img_log:
                Intent intent = new Intent(this, AlermLogActivity.class);
                intent.putExtra("deviceMac", deviceMac);
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
        }
    }

    int once = 2;//2时，为循环，1时为3次，0时是0次


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoad!=null && dialogLoad.isShowing()){
            dialogLoad.dismiss();
        }

        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mqService.initAlarmData();

    }

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                String macAddress = intent.getStringExtra("macAddress");
                if ("offline".equals(action)) {
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        online = false;
                    }
                } else {
                    List<Alerm> alerms = (List<Alerm>) intent.getSerializableExtra("alerms");
                    if (macAddress.equals(deviceMac) && alerms.size() == 8) {
                        boolean online2 = intent.getBooleanExtra("online", false);
                        online = online2;
                        int len=alerms.size();
                        for (int i = 0; i < len; i++) {
                            list.set(i, alerms.get(i));
                        }
                        if (click == 1) {
                            mqService.starSpeech(macAddress,3);
                            click=0;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean bind;
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            mqService.getData(topicName, 0x66);
            countTimer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    int click = 0;

    @Override
    public void doBusiness(Context mContext) {

    }

    int updatePosition = 0;//修改的位置

    class MyAdapter extends RecyclerView.Adapter {

        private Context context;
        private List<Alerm> list;

        public MyAdapter(Context context, List<Alerm> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = View.inflate(context, R.layout.item_alerm3, null);
                return new ViewHolder3(view);
            } else if (viewType == 1) {
                View view = View.inflate(context, R.layout.item_alerm, null);
                return new ViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder2, final int position) {
//            if (position == 1) {
//                ViewHolder1 holder = (ViewHolder1) holder2;
////                int once=2;//1时，为三次，2时为循环，0时是0次
//                if (once == 0) {
//                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
//                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_close.setTextColor(Color.parseColor("#ffffff"));
//                    holder.btn_loop.setTextColor(Color.parseColor("#939393"));
//                    holder.btn_thr.setTextColor(Color.parseColor("#939393"));
//                } else if (once == 2) {
//                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
//                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_close.setTextColor(Color.parseColor("#939393"));
//                    holder.btn_loop.setTextColor(Color.parseColor("#ffffff"));
//                    holder.btn_thr.setTextColor(Color.parseColor("#939393"));
//                } else if (once == 1) {
//                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
//                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
//                    holder.btn_close.setTextColor(Color.parseColor("#939393"));
//                    holder.btn_loop.setTextColor(Color.parseColor("#939393"));
//                    holder.btn_thr.setTextColor(Color.parseColor("#ffffff"));
//                }
//                holder.btn_close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (once == 0) {
//                            return;
//                        }
//                        once = 0;
//
//                        for (int i = 0; i <list.size() ; i++) {
//                            Alerm alerm=list.get(i);
//                            alerm.setDeviceAlarmBroadcast(once);
//                            deviceAlermDao.update(alerm);
//                            mqService.update(alerm);
//                        }
//
//                        adapter.notifyItemChanged(1);
//                    }
//
//                });
//                holder.btn_loop.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (once == 2) {
//                            return;
//                        }
//                        once = 2;
//                        for (int i = 0; i <list.size() ; i++) {
//                            Alerm alerm=list.get(i);
//                            alerm.setDeviceAlarmBroadcast(once);
//                            deviceAlermDao.update(alerm);
//                            mqService.update(alerm);
//                        }
//                        adapter.notifyItemChanged(1);
//                    }
//                });
//                holder.btn_thr.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (once == 1) {
//                            return;
//                        }
//                        once = 1;
//                        for (int i = 0; i <list.size() ; i++) {
//                            Alerm alerm=list.get(i);
//                            alerm.setDeviceAlarmBroadcast(once);
//                            deviceAlermDao.update(alerm);
//                            mqService.update(alerm);
//                        }
//                        adapter.notifyItemChanged(1);
//                    }
//                });
//
//            }
//            else if (position == 2) {
//                ViewHolder2 holder = (ViewHolder2) holder2;
//                holder.img_switch.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (notify == 0) {
//                            notify = 1;
//                        } else if (notify == 1) {
//                            notify = 0;
//                        }
//                        for (int i = 0; i <list.size() ; i++) {
//                            Alerm alerm=list.get(i);
//                            alerm.setDeviceAlarmFlag(notify);
//                            deviceAlermDao.update(alerm);
//                            mqService.update(alerm);
//                        }
//                        adapter.notifyItemChanged(2);
//                    }
//                });
//                if (notify == 1) {
//                    holder.img_switch.setImageResource(R.mipmap.img_open);
//                } else {
//                    holder.img_switch.setImageResource(R.mipmap.img_close);
//                }
//
//            }
            if (position > 0) {
                final Alerm alerm = list.get(position-1);
                String name = alerm.getName();
                String content = alerm.getContent();
                int state = alerm.getState();
                ViewHolder holder = (ViewHolder) holder2;
                holder.tv_name.setText(name);
                if (!TextUtils.isEmpty(content)) {
                    holder.tv_alerm.setText(content);
                } else {
                    holder.tv_alerm.setText("");
                }
                if (state == 1) {
                    holder.img_open.setImageResource(R.mipmap.img_open);
                } else {
                    holder.img_open.setImageResource(R.mipmap.img_close);
                }
                holder.img_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!online) {
                            mqService.getData(topicName, 0x11);
                            ToastUtil.showShort(AlermActivity.this, "设备已离线");
                            return;
                        }
                        int[] data=mqService.getAlermData();
                        int alermState=data[0];
                        int states[]=TenTwoUtil.changeToTwo(alermState);
                        if (alerm.getState() == 1) {
                            states[position-1]=0;
                        } else {
                            states[position - 1] = 1;
                        }
                        data[0] = TenTwoUtil.changeToTen2(states);
                        if (mqService != null) {
                            boolean success = mqService.sendAlerm(topicName, mcuVersion, data);
                            countTimer.start();
                            click=1;
//                            if (success) {
//                                list.set(position - 4, alerm);
//                                notifyDataSetChanged();
//                            }
                        }
                    }
                });

                holder.rl_alerm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePosition = position;
                        if (position < 3) {
                            setAlermDialog(position - 1);
                        } else if (position >= 3 && position < 8) {
                            setAlermDialog2(position - 3);
                        } else if (position == 8) {
                            setAlermDialog3();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 1) {
                return position;
            } else {
                return 1;
            }
        }
    }





    private AlermDialog alermDialog;

    /**
     * @param type 0为来电报警 1为断电报警
     */
    private void setAlermDialog(final int type) {
        if (alermDialog != null && alermDialog.isShowing()) {
            return;
        }
        alermDialog = new AlermDialog(this);
        alermDialog.setMode(type);
        alermDialog.setCanceledOnTouchOutside(false);
        alermDialog.setOnNegativeClickListener(new AlermDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                alermDialog.dismiss();
            }
        });
        alermDialog.setOnPositiveClickListener(new AlermDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                String content = alermDialog.getContent();
                if (TextUtils.isEmpty(content))
                    ToastUtil.showShort(AlermActivity.this, "内容不能为空");
                else {
                    try {
                        alermDialog.dismiss();
//                        params.clear();
//                        params.put("deviceId", deviceId);
//                        params.put("deviceAlarmNum", updatePosition - 2);
//                        params.put("alarmName", content);
//                        new UpdateAlermAsync(AlermActivity.this).execute(params).get(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        alermDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.6f);
        alermDialog.show();
    }

    private AlermDialog2 alermDialog2;

    /**
     * @param type 为0时，温度报警 1为湿度报警，2为电压报警，3，电流报警，4，功率报警
     */
    private void setAlermDialog2(final int type) {
        if (alermDialog2 != null && alermDialog2.isShowing()) {
            return;
        }
        Alerm alerm = list.get(type + 2);
        alermDialog2 = new AlermDialog2(this);
        alermDialog2.setMode(type);
        double value = alerm.getValue();
        BigDecimal b = new BigDecimal(value);
        value = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        String ss = "" + value;
        alermDialog2.setValue(ss);
        int state2 = alerm.getState2();
        if (0x11 == state2) {
            alermDialog2.setOpen(1);
        } else if (0x22 == state2) {
            alermDialog2.setOpen(0);
        }
        alermDialog2.setCanceledOnTouchOutside(false);
        alermDialog2.setCanceledOnTouchOutside(false);

        alermDialog2.setOnNegativeClickListener(new AlermDialog2.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                alermDialog2.dismiss();
            }
        });
        alermDialog2.setOnPositiveClickListener(new AlermDialog2.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                String content = alermDialog2.getContent();
                String value = alermDialog2.getValue() + "";
                int open = alermDialog2.getOpen();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(AlermActivity.this, "报警内容不能为空");
                    return;
                }
                if (TextUtils.isEmpty(value)) {
                    ToastUtil.showShort(AlermActivity.this, "报警数值不能为空");
                    return;
                } else {
                    if (!Utils.isNumeric(value)) {
                        ToastUtil.showShort(AlermActivity.this, "不正确的报警数值");
                        return;
                    }
                }

                try {
                    double s = Double.parseDouble(value);
                    String ss=Math.abs(s)+"";
                    if (ss.contains(".")){
                        ss=ss.substring(ss.indexOf(".")+1);
                    }else {
                        ss="";
                    }
                    if (ss.length()>1){
                        ToastUtil.showShort(AlermActivity.this, "报警数值精确到小数后1位");
                        return;
                    }
                    if (type == 0) {
                        if (s >= -128.0 && s <= 512.9) {
                            s += 128;
                            BigDecimal b = new BigDecimal(s);
                            s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            int alermValue = (int)( s * 10);
                            int data []=mqService.getAlermData();
                            data[1] = alermValue / 256;
                            data[2] = alermValue % 256;
                            if (open == 1) {
                                data[3] = 0x11;
                            } else {
                                data[3] = 0x22;
                            }
                            mqService.sendAlerm(topicName, mcuVersion, data);
                            countTimer.start();
                           click = 1;

                        } else {
                            ToastUtil.showShort(AlermActivity.this, "不在该报警范围之内!");
                            return;
                        }
                    } else if (type == 1) {
                        if (s >= 0 && s <= 99.9) {
                            BigDecimal b = new BigDecimal(s);
                            s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            int alermValue = (int) (s * 10);
                            int data []=mqService.getAlermData();
                            data[4] = alermValue / 256;
                            data[5] = alermValue % 256;
                            if (open == 1) {
                                data[6] = 0x11;
                            } else {
                                data[6] = 0x22;
                            }
                            mqService.sendAlerm(topicName, mcuVersion, data);
                            click=1;
                            countTimer.start();

                        } else {
                            ToastUtil.showShort(AlermActivity.this, "不在该报警范围之内!");
                            return;
                        }
                    } else if (type == 2) {
                        if (s >= 0 && s <= 1024.9) {
                            BigDecimal b = new BigDecimal(s);
                            s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            int alermValue = (int) (s*10);
                            int data []=mqService.getAlermData();
                            data[7] = alermValue / 256;
                            data[8] = alermValue % 256;
                            if (open == 1) {
                                data[9] = 0x11;
                            } else {
                                data[9] = 0x22;
                            }
                            mqService.sendAlerm(topicName, mcuVersion, data);
                            click=1;
                            countTimer.start();

                        } else {
                            ToastUtil.showShort(AlermActivity.this, "不在该报警范围之内!");
                            return;
                        }
                    } else if (type == 3) {
                        if (s >= 0 && s <= 1024.9) {
                            BigDecimal b = new BigDecimal(s);
                            s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            int alermValue = (int) (s * 10);
                            int data []=mqService.getAlermData();
                            data[10] = alermValue / 256;
                            data[11] = alermValue % 256;
                            if (open == 1) {
                                data[12] = 0x11;
                            } else {
                                data[12] = 0x22;
                            }
                            mqService.sendAlerm(topicName, mcuVersion, data);
                            click=1;
                            countTimer.start();

                        } else {
                            ToastUtil.showShort(AlermActivity.this, "不在该报警范围之内!");
                            return;
                        }
                    } else if (type == 4) {
                        if (s >= 0 && s<=131071.9) {
                            BigDecimal b = new BigDecimal(s);
                            s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            int rangeValue=(int)( s*10);
                            String range=Integer.toHexString(rangeValue);
                            if (range.length()==1){
                                range="00000"+range;
                            }else if (range.length()==2){
                                range="0000"+range;
                            }else if (range.length()==3){
                                range="000"+range;
                            }else if (range.length()==4){
                                range="00"+range;
                            }else if (range.length()==5) {
                                range="0"+range;
                            }
                            int middlePower=0;
                            int lowPower=0;
                            int highPower=0;
                            highPower=Integer.parseInt(range.substring(0,2),16);
                            middlePower=Integer.parseInt(range.substring(2,4),16);
                            lowPower=Integer.parseInt(range.substring(4),16);

                            int data []=mqService.getAlermData();
                            data[13] =middlePower;
                            data[14] =lowPower;
                            if (open == 1) {
                                data[15] = 0x11;
                            } else {
                                data[15] = 0x22;
                            }
                            data[17]=highPower;
                            mqService.sendAlerm(topicName, mcuVersion, data);
                            click=1;
                            countTimer.start();

                        } else {
                            ToastUtil.showShort(AlermActivity.this, "不在该报警范围之内!");
                            return;
                        }
                    }
                    alermDialog2.dismiss();
//                    params.clear();
//                    params.put("deviceId", deviceId);
//                    params.put("deviceAlarmNum", updatePosition - 2);
//                    params.put("alarmName", content);
//                    new UpdateAlermAsync(AlermActivity.this).execute(params).get(3, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alermDialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.6f);
        alermDialog2.show();

    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private AlermDialog3 alermDialog3;

    /**
     * 开关量报警
     */
    private void setAlermDialog3() {
        if (alermDialog3 != null && alermDialog3.isShowing()) {
            return;
        }
        alermDialog3 = new AlermDialog3(this);
        Alerm alerm = list.get(7);
        int state2 = alerm.getState2();
        alermDialog3.setOpen(state2);
        alermDialog3.setCanceledOnTouchOutside(false);

        alermDialog3.setOnNegativeClickListener(new AlermDialog3.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                alermDialog3.dismiss();
            }
        });
        alermDialog3.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        alermDialog3.setOnPositiveClickListener(new AlermDialog3.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                String content = alermDialog3.getContent();
                int open = alermDialog3.getOpen();
                if (TextUtils.isEmpty(content))
                    ToastUtil.showShort(AlermActivity.this, "内容不能为空");
                else {
                    try {
                        int[] data=mqService.getAlermData();
                        data[16] = open;
                        mqService.sendAlerm(topicName, mcuVersion, data);
                        click=1;
//                        params.clear();
//                        params.put("deviceId", deviceId);
//                        params.put("deviceAlarmNum", updatePosition - 2);
//                        params.put("alarmName", content);
//                        new UpdateAlermAsync(AlermActivity.this).execute(params).get(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                alermDialog3.dismiss();
            }
        });
        backgroundAlpha(0.6f);
        alermDialog3.show();

    }


    /**
     * 开关量报警
     */

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_alerm)
        RelativeLayout rl_alerm;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_alerm)
        TextView tv_alerm;
        @BindView(R.id.img_open)
        ImageView img_open;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {

        public ViewHolder0(View itemView) {
            super(itemView);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_thr)
        TextView btn_thr;
        @BindView(R.id.btn_loop)
        TextView btn_loop;
        @BindView(R.id.btn_close)
        TextView btn_close;

        public ViewHolder1(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        @BindView(R.id.img_switch)
        ImageView img_switch;

        public ViewHolder2(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolder3 extends RecyclerView.ViewHolder {

        public ViewHolder3(View itemView) {
            super(itemView);
        }
    }

}
