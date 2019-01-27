package com.peihou.willgood.devicelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.pojo.AlermLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报警日志
 */
public class AlermLogActivity extends BaseActivity {

    @BindView(R.id.list_log) RecyclerView list_log;
    private List<AlermLog> list=new ArrayList<>();
    MyAdapter adapter;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_alerm_log;
    }

    @Override
    public void initView(View view) {
        list.add(new AlermLog("1路","来电报警","2019-1-22"));
        list.add(new AlermLog("2路","断电报警","2019-1-23"));
        list.add(new AlermLog("3路","温度报警","2019-1-24"));
        adapter=new MyAdapter(this,list);
        list_log.setLayoutManager(new LinearLayoutManager(this));
        list_log.setAdapter(adapter);
    }
    @OnClick({R.id.img_back,R.id.img_log})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_log:
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private List<AlermLog> list;

        public MyAdapter(Context context, List<AlermLog> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_log,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AlermLog log=list.get(position);
            String name=log.getName();
            String type=log.getType();
            String timer=log.getTimer();
            holder.tv_name.setText(name);
            holder.tv_type.setText(type);
            holder.tv_timer.setText(timer);
            if (position==list.size()-1){
                holder.view.setVisibility(View.GONE);
            }else {
                holder.view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.tv_type) TextView tv_type;
        @BindView(R.id.tv_timer) TextView tv_timer;
        @BindView(R.id.view) View view;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
