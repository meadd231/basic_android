package com.example.bookmemoapp.bestseller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.searchbook.SearchedBookExplanation;

import java.util.ArrayList;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder> {
    ArrayList<BestSeller> bestSellerArrayList;
    Context context;

    public BestSellerAdapter(Context context, ArrayList<BestSeller> arr) {
        this.context = context;
        this.bestSellerArrayList = arr;
    }

    public class BestSellerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView rank;
        TextView rank_title;
        TextView rank_author;
        TextView rank_price;
        ImageView rank_image;

        public BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.rank);
            rank_title = itemView.findViewById(R.id.rank_title);
            rank_author = itemView.findViewById(R.id.rank_author);
            rank_price = itemView.findViewById(R.id.rank_price);
            rank_image = itemView.findViewById(R.id.rank_image);

            itemView.setOnClickListener(this);
        }

        public void onBind(BestSeller bestSeller) {
            String image = bestSellerArrayList.get(getAdapterPosition()).getRank_image();

            rank.setText(String.valueOf(bestSeller.getRank()));
            rank_title.setText(bestSeller.getRank_title());
            rank_author.setText(bestSeller.getRank_author());
            rank_price.setText(String.valueOf(bestSeller.getRank_price())+"원");

            Glide.with(context).load(image).into(rank_image);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SearchedBookExplanation.class);
            intent.putExtra("book_title", bestSellerArrayList.get(getAdapterPosition()).getRank_title());
            intent.putExtra("book_author", bestSellerArrayList.get(getAdapterPosition()).getRank_author());
            intent.putExtra("book_description", bestSellerArrayList.get(getAdapterPosition()).getDescription());
            intent.putExtra("book_link", bestSellerArrayList.get(getAdapterPosition()).getLink());
            intent.putExtra("book_image", bestSellerArrayList.get(getAdapterPosition()).getLarge_image());
            context.startActivity(intent);
        }
    }

    @NonNull
    @Override
    public BestSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_bestseller, parent, false);
        return new BestSellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerViewHolder holder, int position) {
        holder.onBind(bestSellerArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return bestSellerArrayList.size();
    }

    public void addItem(BestSeller bestSeller) {
        //items에 Person객체 추가
        bestSellerArrayList.add(bestSeller);
        //추가후 Adapter에 데이터가 변경된것을 알림
        notifyDataSetChanged();
    }
}
