package com.peihou.willgood.devicelist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.ChangeDialog;
import com.peihou.willgood.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood.database.dao.impl.DeviceLinkDaoImpl;
import com.peihou.willgood.pojo.Linked;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 温度，湿度，开关量，电流，电压中的某一个的列表
 * type=0 为温度的列表
 * type=1 为湿度的列表
 * type=2 为开关量的列表
 * type=3 为电流的列表
 * type=4 为电压的列表
 */
public class LinkItemActivity extends BaseActivity {

    String name;
    @BindView(R.id.list_linked)
    RecyclerView list_linked;
    List<Linked> list = new ArrayList<>();
    @BindView(R.id.tv_title)
    TextView tv_title;
    MyAdapter adapter;
    int type;
    String deviceMac;
    private long deviceId;
    private int analog;
    private DeviceLinkDaoImpl deviceLinkDao;//设备联动表操作者对象
    String topicName;
    boolean online;

    @Override
    public void initParms(Bundle parms) {
        type = parms.getInt("type");
        deviceMac = parms.getString("deviceMac");
        deviceId = parms.getLong("deviceId");
        analog = parms.getInt("analog");
        online=parms.getBoolean("online");
        parms.getInt("voice");
        if (type == 0) {
            name = "温度";
        } else if (type == 1) {
            name = "湿度";
        } else if (type == 2) {
            name = "开关量";
        } else if (type == 3) {
            name = "电流";
        } else if (type == 4) {
            name = "电压";
        }
    }

    @OnClick({R.id.img_back, R.id.img_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                setResult(1002);
                finish();
                break;
            case R.id.img_add:
                if (type == 2) {
                    Intent intent = new Intent(LinkItemActivity.this, LinkedSwitchActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("deviceMac", deviceMac);
                    intent.putExtra("deviceId", deviceId);
                    startActivityForResult(intent, 1000);
                } else {
                    if (type==0){
                        Intent intent = new Intent(LinkItemActivity.this, TempLinkedSetActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("deviceMac", deviceMac);
                        intent.putExtra("deviceId", deviceId);
                        startActivityForResult(intent, 1001);
                    }else {
                        Intent intent = new Intent(LinkItemActivity.this, LinkedSetActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("deviceMac", deviceMac);
                        intent.putExtra("deviceId", deviceId);
                        if (type == 5) {
                            intent.putExtra("analog", analog);
                        }
                        startActivityForResult(intent, 1001);

                    }
                }
                break;
        }
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_link_item;
    }

    private boolean bind;

    @Override
    public void initView(View view) {

        tv_title.setText(name + "联动");
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
            deviceLinkDao = new DeviceLinkDaoImpl(getApplicationContext());
//            list = deviceLinkDao.findLinkeds(deviceMac, type);
            adapter = new MyAdapter(this, list);
            list_linked.setLayoutManager(new LinearLayoutManager(this));
            list_linked.setAdapter(adapter);

        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("LinkItemActivity");
        filter.addAction("offline");
        registerReceiver(receiver, filter);
    }

    public static boolean running = false;

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
        if (mqService != null && returnData==0) {
//            mqService.connectMqtt(deviceMac);
            list.clear();
            adapter.notifyDataSetChanged();
            int funCode = 0;
            if (type == 0) {
                funCode = 0x34;
            } else if (type == 1) {
                funCode = 0x35;
            } else if (type == 2) {
                funCode = 0x36;
            } else if (type == 3) {
                funCode = 0x37;
            } else if (type == 4) {
                funCode = 0x38;
            } else if (type == 5) {
                funCode = 0x39;
            }
            mqService.getData(topicName, funCode);
            countTimer.start();
            returnData=0;
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
        returnData=0;
    }

    @Override
    public void onBackPressed() {
        setResult(1002);
        super.onBackPressed();
    }

    private List<Linked> updateLinkeds(List<Linked> linkeds){
        for (int i = 0; i <linkeds.size() ; i++) {
            Linked linked=linkeds.get(i);
            linked.setVisitity(0);
            linkeds.set(i,linked);
        }
        return linkeds;
    }
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            if (mqService != null) {

                    List<Linked> list=mqService.getLinkeds(deviceMac,type);
                    if (!list.isEmpty()){
                        List<Linked> linkeds=updateLinkeds(list);
                        mqService.updateLinkeds(linkeds);
                    }

                int funCode = 0;
                if (type == 0) {
                    funCode = 0x34;
                } else if (type == 1) {
                    funCode = 0x35;
                } else if (type == 2) {
                    funCode = 0x36;
                } else if (type == 3) {
                    funCode = 0x37;
                } else if (type == 4) {
                    funCode = 0x38;
                } else if (type == 5) {
                    funCode = 0x39;
                }
                mqService.getData(topicName, funCode);
                countTimer.start();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action=intent.getAction();
                if ("offline".equals(action)){
                    String macAddress = intent.getStringExtra("macAddress");
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        online=false;
                    }
                }else {
                    String macAddress = intent.getStringExtra("macAddress");
                    if (macAddress.equals(deviceMac)){
                        boolean online2=intent.getBooleanExtra("online",false);
                        online=online2;
                    }
                    int linkType = intent.getIntExtra("linkType", -1);
                    if (macAddress.equals(deviceMac) && linkType == type) {
                        int operate = intent.getIntExtra("operate", 0);
                        if (returnData==1){
                            if (operate == 1) {
                                mqService.starSpeech(deviceMac,7);
                            } else {
                                mqService.starSpeech(deviceMac,3);
                            }
                            returnData=0;
                        }
                            List<Linked> linkeds = mqService.getLinkeds(deviceMac, linkType);
                            list.clear();
                            list.addAll(linkeds);
                            adapter.notifyDataSetChanged();
                        }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }

        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    ChangeDialog dialog;

    private void changeDialog(String name, final int postion) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTitle("删除" + name + "联动");
        dialog.setTips("是否删除该" + name + "联动?");
        backgroundAlpha(0.6f);
        dialog.setOnNegativeClickListener(new ChangeDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new ChangeDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                    Linked linked = list.get(postion);
                    linked.setState(2);

                    if (mqService != null) {
                        dialog.dismiss();
                        boolean success = mqService.sendLinkedSet(topicName, linked,0x02);
                        returnData = 1;
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

    @Override
    public void doBusiness(Context mContext) {

    }

    class MyAdapter extends RecyclerView.Adapter {


        private Context context;
        private List<Linked> list;

        public MyAdapter(Context context, List<Linked> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = View.inflate(context, R.layout.item_link, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final Linked linked = list.get(position);
            final int state = linked.getState();
            String name="";
            if (type==2){
                name = linked.getName();
            }else {
                name = linked.getName()+"("+(position+1)+")";
            }
            String lines = linked.getLines();
            int condition = linked.getCondition();
            int triType = linked.getTriType();
            int triState = linked.getTriState();
            int conditionState = linked.getConditionState();
            int type = linked.getType();

            TextView tv_condition = holder.itemView.findViewById(R.id.tv_condition);
            TextView tv_linked = holder.itemView.findViewById(R.id.tv_linked);
            TextView tv_lines = holder.itemView.findViewById(R.id.tv_lines);
            String c = "";
            tv_linked.setText(name);
            if (!TextUtils.isEmpty(lines)) {
                tv_lines.setText(lines);
            } else {
                tv_lines.setText("");
            }
            String ss="";
            if (type == 2) {
                if (condition == 1) {
                    c = "开关量断开";
                } else {
                    c = "开关量闭合";
                }
                if (conditionState == 1) {
                    c = c + "  开启";
                } else {
                    c = c + "  关闭";
                }

                if (triType==1){
                    ss=" 单次";
                }else if (triType==0){
                    ss=" 循环";
                }
                c=c+ss;

            } else {
                if (triState == 1) {
                    c = "高于" + "  " + condition;
                } else {
                    c = "低于" + "  " + condition;
                }
                if (conditionState == 1) {
                    c = c + "  开启";
                } else {
                    c = c + "  关闭";
                }
                if (triType==1){
                    ss=" 单次";
                }else if (triType==0){
                    ss=" 循环";
                }
                c=c+ss;
            }


            ImageView img_open = holder.itemView.findViewById(R.id.img_open);


            if (state == 1) {
                img_open.setImageResource(R.mipmap.img_open);
            } else {
                img_open.setImageResource(R.mipmap.img_close);
            }
            tv_condition.setText(c);
            img_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!online){
                        mqService.getData(topicName,0x11);

                        ToastUtil.showShort(LinkItemActivity.this,"设备已离线");
                        return;
                    }

                    if (state == 1) {
                        linked.setState(0);
                    } else {
                        linked.setState(1);
                    }
                    if (mqService != null) {
                        boolean success = mqService.sendLinkedSet(topicName, linked,0x02);
                        returnData = 1;
                        countTimer.start();
                    }
                }
            });

            holder.itemView.findViewById(R.id.rl).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    changeDialog(LinkItemActivity.this.name, position);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }



    int returnData = 0;
    int add = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (resultCode == 1000) {
                int funCode = 0;
                if (type == 0) {
                    funCode = 0x34;
                } else if (type == 1) {
                    funCode = 0x35;
                } else if (type == 2) {
                    funCode = 0x36;
                } else if (type == 3) {
                    funCode = 0x37;
                } else if (type == 4) {
                    funCode = 0x38;
                }
                returnData = 1;
//                mqService.getData(topicName, funCode);
                Linked linked = (Linked) data.getSerializableExtra("linked");
                if (linked != null && mqService!=null) {
                    Log.i("topicName","-->"+topicName);
                    mqService.sendLinkedSet(topicName, linked,0x01);
                    add = 1;
                    countTimer.start();
                }
            }else if (resultCode==1002){
                returnData=2;
            }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
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
        popupWindow2.showAtLocation(tv_title, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }
}
