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
import com.peihou.willgood.custom.AlermDialog3;
import com.peihou.willgood.database.dao.impl.DeviceAlermDaoImpl;
import com.peihou.willgood.pojo.Alerm;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.TenTwoUtil;
import com.peihou.willgood.util.ToastUtil;

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
        if (list.size()!=8){
            deviceAlermDao.deleteDeviceAlerms(deviceMac);
            list.add(new Alerm("来电报警",1,false,deviceMac));
            list.add(new Alerm("断电报警",2,false,deviceMac));
            list.add(new Alerm("温度报警",3,false,deviceMac));
            list.add(new Alerm("湿度报警",4,false,deviceMac));
            list.add(new Alerm("电压报警",5,false,deviceMac));
            list.add(new Alerm("电流报警",6,false,deviceMac));
            list.add(new Alerm("功率报警",7,false,deviceMac));
            list.add(new Alerm("开关量报警",8,false,deviceMac));
            deviceAlermDao.insertDeviceAlerms(list);
        }
        int j = 1;

        for (int i = 2; i < list.size() - 1; i++) {
            Alerm alerm = list.get(i);
            int state = alerm.getState();
            x[i] = state;
            once = alerm.getDeviceAlarmBroadcast();
            notify = alerm.getDeviceAlarmFlag();
            double value = alerm.getValue();
            BigDecimal b = new BigDecimal(value);
            value = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            int k = (int) (value * 10);
            data[j] = k / 256;// 1 3 5,
            data[++j] = k % 256;//2 4 6
            int x = alerm.getState2();
            if (x != 0x11 || x != 0x22)
                data[++j] = 0x11;
            else {
                data[++j] = x;
            }
            j++;
        }
        if (list.size() == 8) {
            Alerm alerm = list.get(7);
            int state2 = alerm.getState2();
            data[16] = state2;
            Alerm alerm1 = list.get(0);
            Alerm alerm2 = list.get(1);
            int state = alerm1.getState();
            int state3 = alerm2.getState();
            x[0] = state;
            x[1] = state3;
        }
        data[0] = TenTwoUtil.changeToTen(x);

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
            popupmenuWindow3();
        }

        @Override
        public void onFinish() {
            if (popupWindow2 != null && popupWindow2.isShowing()) {
                popupWindow2.dismiss();
            }
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
        popupWindow2.showAtLocation(list_alerm, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

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
        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
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
                        for (int i = 0; i < alerms.size(); i++) {
                            list.set(i, alerms.get(i));
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
                View view = View.inflate(context, R.layout.item_alerm0, null);
                return new ViewHolder0(view);
            } else if (viewType == 1) {
                View view = View.inflate(context, R.layout.item_alerm1, null);
                return new ViewHolder1(view);
            } else if (viewType == 2) {
                View view = View.inflate(context, R.layout.item_alerm2, null);
                return new ViewHolder2(view);
            } else if (viewType == 3) {
                View view = View.inflate(context, R.layout.item_alerm3, null);
                return new ViewHolder3(view);
            } else if (viewType == 4) {
                View view = View.inflate(context, R.layout.item_alerm, null);
                return new ViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder2, final int position) {
            if (position == 1) {
                ViewHolder1 holder = (ViewHolder1) holder2;
//                int once=2;//1时，为三次，2时为循环，0时是0次
                if (once == 0) {
                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_close.setTextColor(Color.parseColor("#ffffff"));
                    holder.btn_loop.setTextColor(Color.parseColor("#939393"));
                    holder.btn_thr.setTextColor(Color.parseColor("#939393"));
                } else if (once == 2) {
                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_close.setTextColor(Color.parseColor("#939393"));
                    holder.btn_loop.setTextColor(Color.parseColor("#ffffff"));
                    holder.btn_thr.setTextColor(Color.parseColor("#939393"));
                } else if (once == 1) {
                    holder.btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
                    holder.btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
                    holder.btn_close.setTextColor(Color.parseColor("#939393"));
                    holder.btn_loop.setTextColor(Color.parseColor("#939393"));
                    holder.btn_thr.setTextColor(Color.parseColor("#ffffff"));
                }
                holder.btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (once == 0) {
                            return;
                        }
                        once = 0;

                        for (int i = 0; i <list.size() ; i++) {
                            Alerm alerm=list.get(i);
                            alerm.setDeviceAlarmBroadcast(once);
                            deviceAlermDao.update(alerm);
                            mqService.update(alerm);
                        }

                        adapter.notifyItemChanged(1);
                    }

                });
                holder.btn_loop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (once == 2) {
                            return;
                        }
                        once = 2;
                        for (int i = 0; i <list.size() ; i++) {
                            Alerm alerm=list.get(i);
                            alerm.setDeviceAlarmBroadcast(once);
                            deviceAlermDao.update(alerm);
                            mqService.update(alerm);
                        }
                        adapter.notifyItemChanged(1);
                    }
                });
                holder.btn_thr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (once == 1) {
                            return;
                        }
                        once = 1;
                        for (int i = 0; i <list.size() ; i++) {
                            Alerm alerm=list.get(i);
                            alerm.setDeviceAlarmBroadcast(once);
                            deviceAlermDao.update(alerm);
                            mqService.update(alerm);
                        }
                        adapter.notifyItemChanged(1);
                    }
                });

            } else if (position == 2) {
                ViewHolder2 holder = (ViewHolder2) holder2;
                holder.img_switch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notify == 0) {
                            notify = 1;
                        } else if (notify == 1) {
                            notify = 0;
                        }
                        for (int i = 0; i <list.size() ; i++) {
                            Alerm alerm=list.get(i);
                            alerm.setDeviceAlarmFlag(notify);
                            deviceAlermDao.update(alerm);
                            mqService.update(alerm);
                        }
                        adapter.notifyItemChanged(2);
                    }
                });
                if (notify == 1) {
                    holder.img_switch.setImageResource(R.mipmap.img_open);
                } else {
                    holder.img_switch.setImageResource(R.mipmap.img_close);
                }

            } else if (position > 3) {
                final Alerm alerm = list.get(position-4);
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
                        if (alerm.getState() == 1) {
                            x[7 - (position - 4)] = 0;
//                            alerm.setState(0);
                        } else {
//                            alerm.setState(1);
                            x[7 - (position - 4)] = 1;
                        }
                        int k = TenTwoUtil.changeToTen(x);
                        data[0] = k;

                        double s = list.get(2).getValue();
                        s += 50;
                        BigDecimal b = new BigDecimal(s);
                        s = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                        int alermValue = (int) (s) * 10;
                        data[1] = alermValue / 256;
                        data[2] = alermValue % 256;

                        if (mqService != null) {
                            boolean success = mqService.sendAlerm(topicName, mcuVersion, data);
                            countTimer.start();
//                            if (success) {
//                                list.set(position - 4, alerm);
//                                notifyDataSetChanged();
//                            }
                        }
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return list.size() + 4;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 4) {
                return position;
            } else {
                return 4;
            }
        }
    }





    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
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