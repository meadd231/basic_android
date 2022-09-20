package com.example.bookmemoapp.searchbook;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmemoapp.R;

import java.util.ArrayList;

public class SearchedBookAdapter extends RecyclerView.Adapter<SearchedBookAdapter.SearchedBookViewHolder> {
    Context context;
    ArrayList<SearchedBook> searchedBookArrayList;

    public SearchedBookAdapter(Context context, ArrayList<SearchedBook> arr) {
        this.context = context;
        this.searchedBookArrayList = arr;
    }

    public class SearchedBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView searched_image;
        TextView searched_title, searched_price;
        public SearchedBookViewHolder(@NonNull View itemView) {
            super(itemView);

            searched_image = itemView.findViewById(R.id.searched_image);
            searched_title = itemView.findViewById(R.id.searched_title);
            searched_price = itemView.findViewById(R.id.searched_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SearchedBookExplanation.class);
            intent.putExtra("book_title", searchedBookArrayList.get(getAdapterPosition()).getTitle());
            intent.putExtra("book_author", searchedBookArrayList.get(getAdapterPosition()).getAuthor());
            intent.putExtra("book_description", searchedBookArrayList.get(getAdapterPosition()).getDescription());
            intent.putExtra("book_link", searchedBookArrayList.get(getAdapterPosition()).getLink());
            intent.putExtra("book_image", searchedBookArrayList.get(getAdapterPosition()).getImage());
            context.startActivity(intent);
        }
    }

    @NonNull
    @Override
    public SearchedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_book,null);
        return new SearchedBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedBookViewHolder holder, int position) {
        String image = searchedBookArrayList.get(position).getImage();


        holder.searched_price.setText(searchedBookArrayList.get(position).getPrice() + "원");
        holder.searched_title.setText(Html.fromHtml(searchedBookArrayList.get(position).getTitle()));

        Glide.with(context).load(image).into(holder.searched_image);
    }

    @Override
    public int getItemCount() {
        return searchedBookArrayList.size();
    }
}
