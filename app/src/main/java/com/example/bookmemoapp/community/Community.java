package com.example.bookmemoapp.community;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.MainActivity;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.recyclerview.Written;
import com.example.bookmemoapp.recyclerview.WrittenAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Community extends AppCompatActivity {
    public static ArrayList<Written> writtenArrayList;
    WrittenAdapter writtenAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        RecyclerView writtenRV = findViewById(R.id.written_recyclerview);

        writtenArrayList = new ArrayList<>();
        writtenAdapter = new WrittenAdapter(writtenArrayList);

        writtenRV.setAdapter(writtenAdapter);


        writtenRV.addOnItemTouchListener(new MainActivity.RecyclerTouchListener(getApplicationContext(), writtenRV, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), ReadCommunity.class);
                intent.putExtra("writtenIndex", position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            getWrittenItem();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 책 리스트, 메모 리스트가 저장되 있는 쉐어드의 정보를 받아와서 책 리스트를 출력해주는 함수
    public void getWrittenItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        writtenArrayList.clear();

        String json = pref.getString("writtenMain", "");
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("writtenList");

        //  커뮤니티 액티비티에서 댓글리스트를 받아와서 저장함
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            JSONArray commentListArray = jo.getJSONArray("commentList");
            ArrayList<Comment> comments = new ArrayList<>();
            for (int j=0; j<commentListArray.length(); j++) {
                JSONObject commentListObject = commentListArray.getJSONObject(j);
                Comment comment = new Comment(commentListObject.getString("commentAuthor"),
                        commentListObject.getString("commentText"),
                        commentListObject.getString("commentDate"),
                        commentListObject.getString("commentPassword"));
                comments.add(0, comment);
            }

            // 출력하기 위한 ArrayList, Adapter 사용
            Written written = new Written(jo.getString("writtenTitle"), jo.getString("writtenAuthor"),
                    jo.getString("writtenPassword") ,jo.getString("writtenDate"),
                    jo.getInt("viewsNum"), comments, jo.getString("contentsText"),
                    // "" or 이미지를 담고 있는 문자열
                    jo.getString("contentsImage"));
            writtenArrayList.add(written);
            writtenAdapter.notifyDataSetChanged();
        }
    }

    // 작성 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.community_menu, menu);
        return true;
    }

    // 작성 버튼 누르면 작성 페이지로 이동함
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.writeBtn) {
            Intent writeCommunity = new Intent(getApplicationContext(), WriteCommunity.class);
            startActivity(writeCommunity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}