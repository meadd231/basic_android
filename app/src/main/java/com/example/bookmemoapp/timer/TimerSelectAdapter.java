package com.example.bookmemoapp.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TimerSelectAdapter extends RecyclerView.Adapter<TimerSelectAdapter.TimerSelectViewHolder> {
    ArrayList<TimerSelect> timerSelectArrayList;

    public TimerSelectAdapter(ArrayList<TimerSelect> arr) {
        this.timerSelectArrayList = arr;
    }

    public class TimerSelectViewHolder extends RecyclerView.ViewHolder {
        TextView select_title;

        public TimerSelectViewHolder(@NonNull View itemView) {
            super(itemView);

            select_title = itemView.findViewById(R.id.select_title);
        }
    }

    @NonNull
    @Override
    public TimerSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timer_select, parent, false);

        TimerSelectViewHolder viewHolder = new TimerSelectViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimerSelectViewHolder holder, int position) {
        holder.select_title.setText(timerSelectArrayList.get(position).getSelectTitle());
    }

    @Override
    public int getItemCount() {
        return timerSelectArrayList.size();
    }
}
