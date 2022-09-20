package com.example.bookmemoapp.recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.R;

import java.util.ArrayList;

public class WrittenAdapter extends RecyclerView.Adapter<WrittenAdapter.WrittenViewHolder> {
    ArrayList<Written> writtenArrayList;

    public WrittenAdapter(ArrayList<Written> arr) {
        this.writtenArrayList = arr;
    }

    public class WrittenViewHolder extends RecyclerView.ViewHolder {
        TextView writtenTitle;
        TextView writtenAuthor;
        TextView writtenDate;
        TextView viewsNum;
        ImageView writtenImage;

        public WrittenViewHolder(@NonNull View view) {
            super(view);

            writtenTitle = view.findViewById(R.id.written_title);
            writtenAuthor = view.findViewById(R.id.written_author);
            writtenDate = view.findViewById(R.id.written_date);
            viewsNum = view.findViewById(R.id.written_views_num);
            writtenImage = view.findViewById(R.id.written_image);
        }
    }

    @NonNull
    @Override
    public WrittenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.written_list, viewGroup, false);

        WrittenViewHolder viewHolder = new WrittenViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WrittenViewHolder holder, int position) {
        holder.writtenTitle.setText(writtenArrayList.get(position).getWrittenTitle());
        holder.writtenAuthor.setText(writtenArrayList.get(position).getWrittenAuthor());
        holder.writtenDate.setText(writtenArrayList.get(position).getWrittenDate());
        holder.viewsNum.setText(Integer.toString(writtenArrayList.get(position).getViewsNum()));
        loadBitmapWritten(holder.writtenImage, writtenArrayList.get(position).getContentsImage());
    }

    // 문자열을 비트맵으로 바꾸어 출력하는 함수
    public void loadBitmapWritten(ImageView imageView, String imageString) {
        if (!imageString.equals("")) {
            Bitmap bitmap = StringToBitMap(imageString);
            imageView.setImageBitmap(bitmap);
        }
    }

    // loadBitmap 함수에 들어감 ( 문자열을 비트맵으로 바꾸는 함수 )
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte [] encodedByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return (null != writtenArrayList ? writtenArrayList.size() : 0);
    }
}
