package com.peihou.willgood.devicelist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.OnRecyclerItemClickListener;
import com.peihou.willgood.pojo.Link;
import com.peihou.willgood.pojo.Linked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LinkedControlActivity extends BaseActivity {


    @BindView(R.id.list_linked) RecyclerView list_linked;//联动列表视图
    private List<Linked> list=new ArrayList<>();//联动列表
    private List<Link> links=new ArrayList<>();//联动的类型集合
    @BindView(R.id.tv_title) TextView tv_title;//页面标题
    MyAdapter adapter;//联动列表适配器
    Map<Integer,Boolean> checkMap=new HashMap<>();
    @Override
    public void initParms(Bundle parms) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow!=null && popupWindow.isShowing()){
                popupWindow.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_control;
    }

    Linked linked1,linked2,linked3,linked4,linked5,linked6;
    //        list.add(new Linked(false,3,"线路1  线路2  线路3...","电流联动"));
//        list.add(new Linked(false,4,"线路1  线路2  线路3...","电压联动"));
//        list.add(new Linked(false,5,"线路1  线路2  线路3...","模拟量联动"));
    @Override
    public void initView(View view) {
        linked1=new Linked(false,0,"","温度联动");
        linked2=new Linked(false,1,"线路1  线路2  线路3...","湿度联动");
        linked3=new Linked(false,2,"线路1  线路2  线路3...","开关量联动");
        linked4=new Linked(false,3,"线路1  线路2  线路3...","电流联动");
        linked5=new Linked(false,4,"线路1  线路2  线路3...","电压联动");
//        linked6=new Linked(false,5,"线路1  线路2  线路3...","模拟量联动");

        list.add(linked1);
        list.add(linked2);
        list.add(linked3);
        list.add(linked4);
        list.add(linked5);
//        list.add(linked6);
        adapter=new MyAdapter(this,list);
        list_linked.setLayoutManager(new LinearLayoutManager(this));

        links.add(new Link(0,true,"温度联动"));
        links.add(new Link(1,true,"湿度联动"));
        links.add(new Link(2,true,"开关量联动"));
        links.add(new Link(3,true,"电流联动"));
        links.add(new Link(4,true,"电压联动"));
//        links.add(new Link(5,true,"模拟量联动"));
        list_linked.setAdapter(adapter);

    }

    @OnClick({R.id.img_back,R.id.img_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_add:
                popupWindow();
                break;

        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    class MyAdapter extends RecyclerView.Adapter{

        public static final int TYPE_1=0;
        public static final int TYPE_2=1;
        private Context context;
        private List<Linked> list;

        public MyAdapter(Context context, List<Linked> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==0){
                View view=View.inflate(context,R.layout.item_linked,null);
                return new ViewHolderTop(view);
            }else {
                View view=View.inflate(context,R.layout.item_linked_2,null);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final Linked linked=list.get(position);
            final boolean open=linked.isOpen();
            String name=linked.getName();
           final int type=linked.getType();
            Log.i("type22","-->"+type);
            String lines=linked.getLines();
            TextView tv_linked=holder.itemView.findViewById(R.id.tv_linked);
            tv_linked.setText(name);
            if (position==0){
                ImageView img_linked=holder.itemView.findViewById(R.id.img_linked);
                img_linked.setImageResource(R.mipmap.img_linked_temp);
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

            }else {
                ImageView img_linked=holder.itemView.findViewById(R.id.img_linked);
                TextView tv_linked_line=holder.itemView.findViewById(R.id.tv_linked_line);
                ImageView img_open=holder.itemView.findViewById(R.id.img_open);
                if (type==1){
                    img_linked.setImageResource(R.mipmap.img_linked_hum);
                    if (open){
                        img_open.setImageResource(R.mipmap.img_open);
                    }else {
                        img_open.setImageResource(R.mipmap.img_close);
                    }
                }else if (type==2){
                    img_linked.setImageResource(R.mipmap.img_linked_switch);
                    tv_linked_line.setText(lines);
                    if (open){
                        img_open.setImageResource(R.mipmap.img_open);
                    }else {
                        img_open.setImageResource(R.mipmap.img_close);
                    }
                }else if (type==3){
                    img_linked.setImageResource(R.mipmap.img_linked_cur);
                    tv_linked_line.setText(lines);
                    if (open){
                        img_open.setImageResource(R.mipmap.img_open);
                    }else {
                        img_open.setImageResource(R.mipmap.img_close);
                    }
                }else if (type==4){
                    img_linked.setImageResource(R.mipmap.img_linked_val);
                    tv_linked_line.setText(lines);
                    if (open){
                        img_open.setImageResource(R.mipmap.img_open);
                    }else {
                        img_open.setImageResource(R.mipmap.img_close);
                    }
                }else if (type==5){
                    img_linked.setImageResource(R.mipmap.img_linked_moni);
                    tv_linked_line.setText(lines);
                    if (open){
                        img_open.setImageResource(R.mipmap.img_open);
                    }else {
                        img_open.setImageResource(R.mipmap.img_close);
                    }
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
            }
            holder.itemView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(LinkedControlActivity.this,LinkItemActivity.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            Linked linked=list.get(position);
            int type=linked.getType();
            if (type==0){
                return TYPE_1;
            }else {
                return TYPE_2;
            }
        }
    }
    class ViewHolderTop extends RecyclerView.ViewHolder{
        public ViewHolderTop(View itemView) {
            super(itemView);
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    private PopupWindow popupWindow;
    RecyclerView list_type;//可联动列表视图
    LinkListViewAdapter linkAdapter;
    //底部popupWindow
    public void popupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = View.inflate(this, R.layout.popup_linked, null);
        list_type = view.findViewById(R.id.list_type);
        Button btn_add=view.findViewById(R.id.btn_add);
        linkAdapter=new LinkListViewAdapter(this,links);
        list_type.setLayoutManager(new LinearLayoutManager(this));
        list_type.setAdapter(linkAdapter);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //添加弹出、弹入的动画
//        popupWindow.setAnimationStyle(R.style.Popupwindow);

//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow.showAtLocation(tv_image, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.showAtLocation(tv_title,Gravity.CENTER,0,0);
        //添加按键事件监听
        backgroundAlpha(0.4f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer,Boolean> entry:checkMap.entrySet()){
                    int position=entry.getKey();
                    boolean check=entry.getValue();
                    if (check){
                        if (position==0){
                            if (list.contains(linked1)){
                            }else {
                                list.add(linked1);
                            }
                        }else if (position==1){
                            if (list.contains(linked2)){

                            }else {
                                list.add(linked2);
                            }
                        }else if (position==2){
                            if (list.contains(linked3)){
                            }else {
                                list.add(linked3);
                            }
                        }
                        else if (position==3){
                            if (list.contains(linked4)){
                            }else {
                                list.add(linked4);
                            }
                        }else if (position==4){
                            if (list.contains(linked5)){
                            }else {
                                list.add(linked5);
                            }
                        }else if (position==5){
                            if (list.contains(linked6)){
                            }else {
                                list.add(linked6);
                            }
                        }
                    }else {
                        if (position==0){
                            if (list.contains(linked1)){
                                list.remove(linked1);
                            }
                        }else if (position==1){
                            if (list.contains(linked2)){
                                list.remove(linked2);
                            }
                        }else if (position==2){
                            if (list.contains(linked3)){
                                list.remove(linked3);
                            }
                        }
                        else if (position==3){
                            if (list.contains(linked4)){
                                list.remove(linked4);
                            }
                        }else if (position==4){
                            if (list.contains(linked5)){
                                list.remove(linked5);
                            }
                        }else if (position==5){
                            if (list.contains(linked6)){
                                links.remove(linked6);
                            }
                        }
                    }
                }

               Collections.sort(list, new Comparator<Linked>() {
                   @Override
                   public int compare(Linked o1, Linked o2) {
                       if (o1.getType()>o2.getType()){
                           return 1;
                       }else if (o1.getType()<o2.getType()){
                           return -1;
                       }
                       return 0;
                   }
               });
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

    }

    class LinkListViewAdapter extends RecyclerView.Adapter<LinkViewHolder>{
        private Context context;
        private List<Link> links;

        public LinkListViewAdapter(Context context, List<Link> links) {
            this.context = context;
            this.links = links;
        }

        @NonNull
        @Override
        public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_linked_type,null);
            return new LinkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LinkViewHolder holder, final int position) {
            Link link=links.get(position);
            boolean isCheck=link.isCheck();
            int type=link.getType();
            String name=link.getName();
            holder.tv_name.setText(name);
            if (isCheck){
                holder.check.setChecked(true);
            }else {
                holder.check.setChecked(false);
            }
            checkMap.put(position,isCheck);
            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Link link2=links.get(position);
                    boolean check=link2.isCheck();
                    if (check==false){
                        link2.setCheck(true);
                    }else {
                        link2.setCheck(false);
                    }
                    links.set(position,link2);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return links.size();
        }
    }
    class LinkViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.check) CheckBox check;
        @BindView(R.id.tv_name) TextView tv_name;
        public LinkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
}
