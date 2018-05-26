package com.example.administrator.recyclerviewtest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int ONE_ITEM = 1;
    public static final int TWO_ITEM = 2;
    private List<Integer> mList;

    public MyRecyclerViewAdapter(List<Integer> mList){
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        if(ONE_ITEM == viewType){
            View v = mInflater.inflate(R.layout.item_one, parent,false);
            holder = new OneViewHolder(v);
//            System.out.println("1");
        }else{
            View v = mInflater.inflate(R.layout.item_two,parent,false);
            holder = new TwoViewHolder(v);
//            System.out.println("2");
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        System.out.println(position);
        if(holder instanceof OneViewHolder){
            ((OneViewHolder) holder).tv.setText(String.valueOf(mList.get(position/2*3+2).intValue()));
        }else {
            ((TwoViewHolder) holder).tv1.setText( String.valueOf(mList.get(position/2*3).intValue()));
            if(position/2*3+1 >= mList.size()){
                ((TwoViewHolder) holder).tv2.setText( null );
                return;
            }
            ((TwoViewHolder) holder).tv2.setText( String.valueOf(mList.get(position/2*3+1).intValue()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if( position % 2 == 1){
            return ONE_ITEM;
        }else{
            return TWO_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return (mList.size() / 3 * 2  + (mList.size() % 3 == 0? 0: 1));
    }

    static class OneViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public OneViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.adapter_linear_text);
        }
    }

    static class TwoViewHolder extends RecyclerView.ViewHolder{
        TextView tv1,tv2;
        public TwoViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.adapter_two_1);
            tv2 = itemView.findViewById(R.id.adapter_two_2);
        }
    }
}
