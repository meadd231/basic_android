package com.example.bookmemoapp.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.R;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    ArrayList<MemoList> memoLists;

    public MemoAdapter(ArrayList<MemoList> arr) {
        this.memoLists = arr;
    }


    public class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView memoLog;
        TextView memoDate;

        public MemoViewHolder(@NonNull View view) {
            super(view);

            memoLog = view.findViewById(R.id.memoLog);
            memoDate = view.findViewById(R.id.memoDate);
        }
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_memo, viewGroup, false);

        MemoViewHolder viewHolder = new MemoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {

        holder.memoLog.setText(memoLists.get(position).getMemoLog());
        holder.memoDate.setText(memoLists.get(position).getMemoDate());
    }

    @Override
    public int getItemCount() {
        return (null != memoLists ? memoLists.size() : 0);
    }

}
