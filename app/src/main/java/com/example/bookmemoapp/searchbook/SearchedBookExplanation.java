package com.example.bookmemoapp.searchbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.custom.Book;
import com.example.bookmemoapp.recyclerview.MemoList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.bookmemoapp.MainActivity.bookArrayList;

public class SearchedBookExplanation extends AppCompatActivity {
    TextView book_title, book_author, book_description, book_link;
    Button show_naver_button, add_record_button;
    ImageView book_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_book_explanation);

        Bundle extras = getIntent().getExtras();
        book_title = findViewById(R.id.book_title);
        book_author = findViewById(R.id.book_author);
        book_description = findViewById(R.id.book_description);
        book_image = findViewById(R.id.book_image);
        Spanned title = Html.fromHtml(extras.get("book_title").toString());
        Spanned author = Html.fromHtml(extras.get("book_author").toString());
        String link = extras.get("book_link").toString();
        String image = extras.get("book_image").toString();
        book_title.setText(title);
        book_author.setText(author);
        book_description.setText(Html.fromHtml(extras.get("book_description").toString()));
        Glide.with(this).load(image).into(book_image);


        show_naver_button = findViewById(R.id.show_naver_button);
        add_record_button = findViewById(R.id.add_record_button);

        show_naver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });

        add_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(title).equals(bookArrayList.get(0).getTitle())
                        && String.valueOf(author).equals(bookArrayList.get(0).getAuthor())) {
                    Toast.makeText(getApplicationContext(), "?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                long now = System.currentTimeMillis();      // ??????????????? date ????????? ?????????
                Date date = new Date(now);      // ????????? ????????? ????????? ????????? ( yyyy??? MM??? dd??? ????????? ?????? )
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");      // nowDate ????????? ?????? ?????????
                String formatDate = sdfNow.format(date);        // ?????? ????????? fomatDate??? ?????? ???????????? ?????????
                ArrayList<MemoList> memos = new ArrayList<>();
                Book book = new Book(String.valueOf(title), String.valueOf(author), "", formatDate, memos);
                bookArrayList.add(0, book);
                Toast.makeText(getApplicationContext(), "??? ????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            putBookItem();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ??? ????????? ??????, ??????, ????????? ???????????? ??????
    // ????????? ArrayList??? ????????? SharedPreferences??? ?????????
    public void putBookItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<bookArrayList.size(); i++) {
            JSONObject jo = new JSONObject();
            jo.put("bookTitle", bookArrayList.get(i).getTitle());
            jo.put("bookAuthor", bookArrayList.get(i).getAuthor());
            jo.put("firstMemo", bookArrayList.get(i).getFirst_memo());
            jo.put("bookDate", bookArrayList.get(i).getDate());

            // ??????????????? ????????? ???????????? ??????
            JSONArray memoListArray = new JSONArray();
            for (int j=0; j<bookArrayList.get(i).getMemos().size(); j++) {
                JSONObject memoListObject = new JSONObject();
                memoListObject.put("memoLog", bookArrayList.get(i).getMemos().get(j).getMemoLog());
                memoListObject.put("memoDate", bookArrayList.get(i).getMemos().get(j).getMemoDate());
                memoListArray.put(memoListObject);
            }
            jo.put("memoList", memoListArray);

            jsonArray.put(jo);
        }
        // "bookList" ?????? ????????? ?????? ?????? ??????
        jsonObject.put("bookList", jsonArray);

        // "bookMain" ?????? ????????? ?????? SharedPreferences ???????????? ??????
        editor.putString("bookMain", jsonObject.toString());
        editor.apply();
    }
}