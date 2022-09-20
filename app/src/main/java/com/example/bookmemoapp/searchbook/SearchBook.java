package com.example.bookmemoapp.searchbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.bookmemoapp.MainActivity;
import com.example.bookmemoapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchBook extends AppCompatActivity {

    RecyclerView searched_book_recyclerView;
    ArrayList<SearchedBook> searchedBookArrayList;
    SearchedBookAdapter searchedBookAdapter;

    String apiURL="https://openapi.naver.com/v1/search/book.json?";
    String query="";
    int start=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        searched_book_recyclerView = findViewById(R.id.searched_book_recyclerView);
        searchedBookArrayList = new ArrayList<>();

        new BookThread().execute();

        SearchView searchView = findViewById(R.id.edtsearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                start = 1;
                searchedBookArrayList.clear();
                query = searchView.getQuery().toString();
                new BookThread().execute();
                return true;
            }
        });
    }

    //BackThread 생성
    class BookThread extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            return NaverAPI.main(apiURL,query,start);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("결과.........."+s);
            try {
                JSONArray jArray = new JSONObject(s).getJSONArray("items");
                for(int i=0; i<jArray.length(); i++){
                    JSONObject obj=jArray.getJSONObject(i);
                    SearchedBook vo = new SearchedBook();
                    vo.setAuthor(obj.getString("author"));
                    vo.setImage(obj.getString("image"));
                    vo.setPrice(obj.getInt("price"));
                    vo.setDescription(obj.getString("description"));
                    vo.setLink(obj.getString("link"));
                    vo.setTitle(obj.getString("title"));
                    searchedBookArrayList.add(vo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchedBookAdapter = new SearchedBookAdapter(SearchBook.this, searchedBookArrayList);
            searched_book_recyclerView.setAdapter(searchedBookAdapter);
            searched_book_recyclerView.scrollToPosition(start);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recommend_book_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.yes24:
                return true;
            case R.id.kyobo:
                return true;
            case R.id.aladin:
                return true;
            case R.id.interpark:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}