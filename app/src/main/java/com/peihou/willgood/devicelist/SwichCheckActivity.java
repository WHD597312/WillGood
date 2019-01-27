package com.peihou.willgood.devicelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.pojo.SwtichState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开关量测试
 */
public class SwichCheckActivity extends BaseActivity {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.img_add) ImageView img_add;
    @BindView(R.id.list_linked) RecyclerView list_linked;//开关量检测视图列表
    private List<SwtichState> list=new ArrayList<>();
    MyAdapter adapter;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_control;
    }

    @Override
    public void initView(View view) {
        tv_title.setText("开关量检测");
        img_add.setVisibility(View.GONE);
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",1));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",1));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",1));
        list.add(new SwtichState(0,"烟雾报警器",0));
        list.add(new SwtichState(0,"烟雾报警器",0));

        list_linked.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MyAdapter(this,list);
        list_linked.setAdapter(adapter);
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SwtichState state=list.get(position);
            String name=state.getName();
            int state2=state.getState();
            holder.tv_name.setText(name);
            if (state2==0){
                holder.tv_state.setText("异常");
                holder.img_state.setImageResource(R.mipmap.img_bad);
            }else if (state2==1){
                holder.tv_state.setText("正常");
                holder.img_state.setImageResource(R.mipmap.img_right);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.img_sa) ImageView img_sa;
        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.tv_state) TextView tv_state;
        @BindView(R.id.img_state) ImageView img_state;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
