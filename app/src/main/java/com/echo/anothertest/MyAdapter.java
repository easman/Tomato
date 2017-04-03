package com.echo.anothertest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Echo
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Tomato> tomatos;
    private Context mContext;

    public MyAdapter(Context context, List<Tomato> tomatos) {
        this.mContext = context;
        this.tomatos = tomatos;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tamato_car_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
        // 给ViewHolder设置元素
        Tomato t = tomatos.get(position);
        viewHolder.mTextView.setText(t.getJobDescription());
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return tomatos == null ? 0 : tomatos.size();
    }


    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.job_decription);
        }
    }
}
