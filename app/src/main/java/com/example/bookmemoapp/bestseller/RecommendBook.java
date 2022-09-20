package com.example.bookmemoapp.bestseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmemoapp.MainActivity;
import com.example.bookmemoapp.Memo;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.custom.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecommendBook extends AppCompatActivity {

    RecyclerView bookRank;
    ArrayList<BestSeller> bestSellerArrayList;
    BestSellerAdapter bestSellerAdapter;

    String categoryId = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_book);

        bookRank = findViewById(R.id.book_rank);
        bestSellerArrayList = new ArrayList<>();

        new BestSellerThread().execute(categoryId);

        TextView category_select = findViewById(R.id.category_select);
        RadioGroup category_area = findViewById(R.id.category_area);
        RadioButton radio1 = findViewById(R.id.radio1);
        RadioButton radio101 = findViewById(R.id.radio101);
        RadioButton radio104 = findViewById(R.id.radio104);
        RadioButton radio105 = findViewById(R.id.radio105);
        RadioButton radio116 = findViewById(R.id.radio116);
        RadioButton radio118 = findViewById(R.id.radio118);
        RadioButton radio122 = findViewById(R.id.radio122);
        RadioButton radio2 = findViewById(R.id.radio2);
        RadioButton radio211 = findViewById(R.id.radio211);
        category_area.setVisibility(View.GONE);
        category_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_area.getVisibility() == View.VISIBLE) {
                    category_area.setVisibility(View.GONE);
                } else {
                    category_area.setVisibility(View.VISIBLE);
                }
            }
        });
        category_area.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio1) {
                    categoryId = "100";
                } else if (checkedId == R.id.radio101) {
                    categoryId = "101";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio104) {
                    categoryId = "104";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio105) {
                    categoryId = "105";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio116) {
                    categoryId = "116";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio118) {
                    categoryId = "118";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio122) {
                    categoryId = "122";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio2) {
                    categoryId = "200";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                } else if (checkedId == R.id.radio211) {
                    categoryId = "211";
                    bestSellerArrayList.clear();
                    new BestSellerThread().execute(categoryId);
                }
            }
        });

    }

    class BestSellerThread extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... str) {
            return BestSellerAPI.main(str[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONObject(s).getJSONArray("item");
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    BestSeller bestSeller = new BestSeller();
                    bestSeller.setRank(obj.getInt("rank"));
                    bestSeller.setRank_title(obj.getString("title"));
                    bestSeller.setRank_author(obj.getString("author"));
                    bestSeller.setRank_price(obj.getInt("priceSales"));
                    bestSeller.setRank_image(obj.getString("coverSmallUrl"));
                    bestSeller.setDescription(obj.getString("description"));
                    bestSeller.setLink(obj.getString("link"));
                    bestSeller.setPublisher(obj.getString("publisher"));
                    bestSeller.setLarge_image(obj.getString("coverLargeUrl"));
                    bestSellerArrayList.add(bestSeller);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bestSellerAdapter = new BestSellerAdapter(RecommendBook.this, bestSellerArrayList);
            bookRank.setAdapter(bestSellerAdapter);
        }
    }

}