package com.moviebasket.android.client.main.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.moviebasket.android.client.R;
import com.moviebasket.android.client.mypage.basket_list.BasketListDatas;
import com.moviebasket.android.client.mypage.basket_list.BasketListViewHolder;

import java.util.ArrayList;

/**
 * Created by pilju on 2017-01-02.
 */

public class MainAdapter extends RecyclerView.Adapter<BasketListViewHolder> {


    ArrayList<BasketListDatas> mDatas;
    View.OnClickListener clickListener;
    View.OnClickListener subClickListener;

    private ViewGroup parent;
    private View itemView;

    public MainAdapter(ArrayList<BasketListDatas> mDatas) {
        this.mDatas = mDatas;
    }
    public MainAdapter(ArrayList<BasketListDatas> mDatas, View.OnClickListener clickListener, View.OnClickListener subClickListener ) {
        this.mDatas = mDatas;
        this.clickListener = clickListener;
        this.subClickListener = subClickListener;
    }

    @Override
    public BasketListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 뷰홀더 패턴을 생성하는 메소드.

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bl, parent, false);
        if(this.clickListener!=null)
            itemView.setOnClickListener(clickListener);

        this.parent = parent;
        this.itemView = itemView;
        BasketListViewHolder viewHolder = new BasketListViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BasketListViewHolder holder, int position) {
        //리싸이클뷰에 항목을 뿌려주는 메소드.
        Glide.with(parent.getContext()).load(mDatas.get(position).basket_image).into(holder.basketImg);
        holder.basketName.setText(mDatas.get(position).basket_name);
        holder.downCount.setText(String.valueOf(mDatas.get(position).basket_like));

        if ( mDatas.get(position).is_liked == 1 ) {
            holder.downBtn.setImageResource(R.drawable.sub_basket_down);
        } else {
            holder.downBtn.setImageResource(R.drawable.sub_basket_nodown);
        }
        holder.downBtn.setOnClickListener(subClickListener);

    }

    @Override
    public int getItemCount() {
        return (mDatas != null) ? mDatas.size() : 0;
    }
}