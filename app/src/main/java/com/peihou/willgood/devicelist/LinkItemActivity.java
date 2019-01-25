package com.peihou.willgood.devicelist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.ChangeDialog;
import com.peihou.willgood.pojo.Linked;
import com.peihou.willgood.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LinkItemActivity extends BaseActivity {

    String name;
    @BindView(R.id.list_linked) RecyclerView list_linked;
    List<Linked> list=new ArrayList<>();
    @BindView(R.id.tv_title)
    TextView tv_title;
    MyAdapter adapter;
    int type;
    @Override
    public void initParms(Bundle parms) {
        type=parms.getInt("type");
        if (type==0){
            name="温度";
        }else if (type==1){
            name="湿度";
        } else if (type==2){
            name="开关量";
        }else if (type==3){
            name="电流";
        }else if (type==4){
            name="电压";
        }

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
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_link_item;
    }

    @Override
    public void initView(View view) {

        tv_title.setText(name+"联动");
        String lines="线路1  线路2  线路3";
        list.add(new Linked(false,type,lines,name+"1"));
        list.add(new Linked(false,type,lines,name+"2"));
        list.add(new Linked(false,type,lines,name+"3"));
        list.add(new Linked(false,type,lines,name+"4"));
        list.add(new Linked(false,type,lines,name+"5"));
        list.add(new Linked(false,type,lines,name+"6"));
        list.add(new Linked(false,type,lines,name+"7"));
        list.add(new Linked(false,type,lines,name+"8"));

        adapter=new MyAdapter(this,list);
        list_linked.setLayoutManager(new LinearLayoutManager(this));
        list_linked.setAdapter(adapter);
    }

    ChangeDialog dialog;
    private void changeDialog(final int postion){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(0);
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
                String content = dialog.getContent();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(LinkItemActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                } else {
                    name=content;
                    Linked linked=list.get(postion);
                    linked.setName(name);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
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
    class MyAdapter extends RecyclerView.Adapter{


        private Context context;
        private List<Linked> list;

        public MyAdapter(Context context, List<Linked> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=View.inflate(context,R.layout.item_link,null);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final Linked linked=list.get(position);
            final boolean open=linked.isOpen();
            String name=linked.getName();
            String lines=linked.getLines();
            TextView tv_linked=holder.itemView.findViewById(R.id.tv_linked);
            tv_linked.setText(name);
                TextView tv_linked_line=holder.itemView.findViewById(R.id.tv_linked_line);
                tv_linked_line.setText(lines);
                ImageView img_open=holder.itemView.findViewById(R.id.img_open);
                if (open){
                    img_open.setImageResource(R.mipmap.img_open);
                }else {
                    img_open.setImageResource(R.mipmap.img_close);
                }
                img_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (open){
                            linked.setOpen(false);
                        }else {
                            linked.setOpen(true);
                        }
                        list.set(position,linked);
                        notifyDataSetChanged();
                    }
                });

            holder.itemView.findViewById(R.id.rl).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    changeDialog(position);
                    return true;
                }
            });

            holder.itemView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type==2){
                        Intent intent=new Intent(LinkItemActivity.this,LinkedSwitchActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent=new Intent(LinkItemActivity.this,LinkedControlItemActivity.class);
                        intent.putExtra("type",type);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
