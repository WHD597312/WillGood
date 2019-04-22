package com.peihou.willgood.devicelist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.pojo.SwtichState;
import com.peihou.willgood.service.MQService;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开关量测试
 */
public class SwichCheckActivity extends BaseActivity {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.list_linked)
    RecyclerView list_linked;//开关量检测视图列表
    private List<SwtichState> list=new ArrayList<>();
    MyAdapter adapter;
    long deviceId;
    Map<String,Object> params=new HashMap<>();
    String deviceMac;
    int voice;
    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_control;
    }

    private String topicName;
    @Override
    public void initView(View view) {
        tv_title.setText("开关量检测");
        list_linked.setLayoutManager(new LinearLayoutManager(this));
        list.add(new SwtichState(0,"开关量1","",0));
        list.add(new SwtichState(0,"开关量2","",0));
        list.add(new SwtichState(0,"开关量3","",0));
        list.add(new SwtichState(0,"开关量4","",0));
        list.add(new SwtichState(0,"开关量5","",0));
        list.add(new SwtichState(0,"开关量6","",0));
        list.add(new SwtichState(0,"开关量7","",0));
        list.add(new SwtichState(0,"开关量8","",0));



        adapter=new MyAdapter(this,list);

        list_linked.setAdapter(adapter);

        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
        receiver=new MessageReceiver();
        IntentFilter filter=new IntentFilter("SwichCheckActivity");
        registerReceiver(receiver,filter);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
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
        popupWindow2.showAtLocation(list_linked, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }
    @OnClick({R.id.img_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    class MyAdapter extends RecyclerView.Adapter<ViewHolder>{
        private Context context;
        private List<SwtichState> list;

        public MyAdapter(Context context, List<SwtichState> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_sa,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            SwtichState state=list.get(position);
            String name=state.getName();
            int state2=state.getState();
            holder.tv_name.setText(name);
            String pic=state.getPic();
            if (state2 == 2) {
//                holder.tv_state.setText("异常");
                holder.tv_state.setText("断开");
                holder.img_state.setImageResource(R.mipmap.img_bad);
            } else if (state2 == 1) {
//                holder.tv_state.setText("正常");
                holder.tv_state.setText("闭合");
                holder.img_state.setImageResource(R.mipmap.img_right);
            } else if (state2 == 0) {
                holder.tv_state.setText("无效");
                holder.img_state.setImageResource(R.mipmap.img_invalid);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rl_item) RelativeLayout rl_item;
        @BindView(R.id.img_sa) ImageView img_sa;
        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.tv_state) TextView tv_state;
        @BindView(R.id.img_state) ImageView img_state;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        if (bind){
            unbindService(connection);
        }
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }
    }

    boolean bind=false;
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x55);
                countTimer.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    MessageReceiver receiver;
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress=intent.getStringExtra("macAddress");
                if (macAddress.equals(deviceMac)){
                    int switchState1=intent.getIntExtra("switchState1",0);
                    int switchState2=intent.getIntExtra("switchState2",0);
                    int switchState3=intent.getIntExtra("switchState3",0);
                    int switchState4=intent.getIntExtra("switchState4",0);
                    int switchState5=intent.getIntExtra("switchState5",0);
                    int switchState6=intent.getIntExtra("switchState6",0);
                    int switchState7=intent.getIntExtra("switchState7",0);
                    int switchState8=intent.getIntExtra("switchState8",0);
                    if (list!=null && list.size()==8){
                        SwtichState swtichState=list.get(0);
                        SwtichState swtichState2=list.get(1);
                        SwtichState swtichState3=list.get(2);
                        SwtichState swtichState4=list.get(3);
                        SwtichState swtichState5=list.get(4);
                        SwtichState swtichState6=list.get(5);
                        SwtichState swtichState7=list.get(6);
                        SwtichState swtichState8=list.get(7);
                        swtichState.setState(switchState1);
                        swtichState2.setState(switchState2);
                        swtichState3.setState(switchState3);
                        swtichState4.setState(switchState4);
                        swtichState5.setState(switchState5);
                        swtichState6.setState(switchState6);
                        swtichState7.setState(switchState7);
                        swtichState8.setState(switchState8);

                        list.set(0,swtichState);
                        list.set(1,swtichState2);
                        list.set(2,swtichState3);
                        list.set(3,swtichState4);
                        list.set(4,swtichState5);
                        list.set(5,swtichState6);
                        list.set(6,swtichState7);
                        list.set(7,swtichState8);
                        adapter.notifyDataSetChanged();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int load=0;
    public static boolean running=false;
    @Override
    protected void onStart() {
        super.onStart();
        running=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mqService != null) {
            mqService.getData(topicName, 0x55);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;
    }


    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

}
