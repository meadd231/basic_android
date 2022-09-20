package com.example.bookmemoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmemoapp.bestseller.RecommendBook;
import com.example.bookmemoapp.community.Community;
import com.example.bookmemoapp.custom.Book;
import com.example.bookmemoapp.custom.CustomAdapter;
import com.example.bookmemoapp.papago.Papago;
import com.example.bookmemoapp.recyclerview.MemoList;
import com.example.bookmemoapp.searchbook.SearchBook;
import com.example.bookmemoapp.timer.TimerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/* 책 메모 앱의 메인 액티비티 클래스. 메모 추가하기 버튼을 누르면 다이얼로그가 나오고, 그곳에서
   도서검색해서 추가하는 것과 직접 책의 제목, 저자, 이미지를 입력해서 추가하는 방식을 선택하여
   책을 추가한다.
*/
public class MainActivity extends AppCompatActivity {

    // onCreate 함수 말고 다른 곳에서도 사용하기 위해 전역변수로 만든 것 같음
    public static ArrayList<Book> bookArrayList;
    CustomAdapter adapter;

    SearchView menu_main_search;

    public static TextView main_text;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView bookRecyclerView = findViewById(R.id.book_list_recyclerview);
        
        // DividerItem 사용하려고 레이아웃 매니저의 인스턴스를 만든 것 같음
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        bookRecyclerView.setLayoutManager(mLinearLayoutManager);

        bookArrayList = new ArrayList<>();

        adapter = new CustomAdapter(this, bookArrayList);
        bookRecyclerView.setAdapter(adapter);

        main_text = findViewById(R.id.main_text);



        /*
        // 책 목록 중 하나를 클릭하면 메모할 수 있는 화면으로 넘어가게 됨
        bookRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), bookRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(getBaseContext(), Memo.class);
                intent.putExtra("bookIndex", position);

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
         */




        // 하단 버튼 ( 근처 도서관 지도, 도서 검색, 베스트셀러, 커뮤니티 )
        TextView timer = findViewById(R.id.timer);
        TextView searchBook = findViewById(R.id.searchBook);
        TextView recommendBook = findViewById(R.id.recommendBook);
        TextView community = findViewById(R.id.community);

        // 하단 버튼 클릭시 액티비티 이동
        // 타이머
            timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timer = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(timer);
            }
        });
        // 도서검색
        searchBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchBook = new Intent(getApplicationContext(), SearchBook.class);
                startActivity(searchBook);
            }
        });
        // 베스트셀러 추천
        recommendBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recommendBook = new Intent(getApplicationContext(), RecommendBook.class);
                startActivity(recommendBook);
            }
        });
        // 커뮤니티
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent community = new Intent(getApplicationContext(), Community.class);
                startActivity(community);
            }
        });

        getWhereToGo();
    }

    // 클릭 리스너 인터페이스
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    // 리사이클러 터치 리스너
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public void deleteFunction() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            getBookItem();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        main_text.setText(adapter.getItemCount()+"권의 책 기록");
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

    public void getWhereToGo() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int whereToGo = pref.getInt("whereToGo",0);
        if (whereToGo != 0) {
            Intent intent = new Intent(getApplicationContext(), Memo.class);
            intent.putExtra("bookIndex", whereToGo-1);
            startActivity(intent);
        }
    }

    // 책 기록의 목록 갯수를 반환하는 함수
    public static void printMainText(View view) {

    }
        
    // 책 기록의 추가, 수정, 삭제를 저장하는 함수
    // 현재의 ArrayList를 그대로 SharedPreferences에 저장함
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
            
            // 메모리스트 배열을 추가하는 코드
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
        // "bookList" 라는 키값을 가진 배열 생성
        jsonObject.put("bookList", jsonArray);

        // "bookMain" 라는 키값을 가진 SharedPreferences 데이터를 저장
        editor.putString("bookMain", jsonObject.toString());
        editor.apply();
    }

    // 책 리스트, 메모 리스트가 저장되 있는 쉐어드의 정보를 받아와서 책 리스트를 출력해주는 함수
    public void getBookItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        bookArrayList.clear();

        String json = pref.getString("bookMain", "");
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("bookList");

        // 메인 액티비티에서 책 리스트를 출력해줌
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            JSONArray memoListArray = jo.getJSONArray("memoList");
            ArrayList<MemoList> memos = new ArrayList<>();
            for (int j=0; j<memoListArray.length(); j++) {
                JSONObject memoListObject = memoListArray.getJSONObject(j);
                MemoList memo = new MemoList(memoListObject.getString("memoLog"),
                        memoListObject.getString("memoDate"));
                memos.add(memo);
            }
            Book book = new Book(jo.getString("bookTitle"), jo.getString("bookAuthor"),
                    jo.getString("firstMemo"), jo.getString("bookDate"), memos);
            bookArrayList.add(book);
            adapter.notifyDataSetChanged();
        }
    }


    // 메인 화면의 앱바 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_main_search);
        menu_main_search = (SearchView) menuItem.getActionView();
        menu_main_search.setQueryHint(getResources().getString(R.string.menu_main_search_hint));
        menu_main_search.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // 텍스트 입력 후 검색 버튼이 눌렸을 때의 이벤트
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // 검색 글 한자 한자 눌렸을 때의 이벤트
            adapter.getFilter().filter(newText);
            return true;
        }
    };


        // 앱바 버튼 중 책 추가 버튼이 있는데, 직접 입력과 도서 검색하는 부분이 있음
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 책 하나 추가하기
        if (item.getItemId() == R.id.add_book) {
            // 2. 레이아웃 파일 add_book_dialog.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.add_book_dialog, null, false);
            builder.setView(view);

            long now = System.currentTimeMillis();      // 현재시간을 date 변수에 저장함
            Date date = new Date(now);      // 시간을 나타냇 포맷을 정한다 ( yyyy년 MM월 dd일 형태로 변형 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");      // nowDate 변수에 값을 저장함
            String formatDate = sdfNow.format(date);        // 현재 시간을 fomatDate에 위의 형식으로 저장함


            final Button ButtonSubmit = view.findViewById(R.id.button_dialog_submit);
            final EditText editTextTitle = view.findViewById(R.id.edittext_dialog_title);
            final EditText editTextAuthor = view.findViewById(R.id.edittext_dialog_author);
            final TextView editTextDate = view.findViewById(R.id.edittext_dialog_date);
            editTextDate.setText(formatDate);

            ButtonSubmit.setText("완료");

            final AlertDialog dialog = builder.create();


            // 3. 다이얼로그에 있는 완료 버튼을 클릭하면

            ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                public void onClick(View v) {


                    // 4. 사용자가 입력한 내용을 가져와서
                    String strTitle = editTextTitle.getText().toString();
                    String strAuthor = editTextAuthor.getText().toString();
                    String strDate = editTextDate.getText().toString();
                    ArrayList<MemoList> memos = new ArrayList<>();
                    if (strTitle.equals("")) {
                        Toast.makeText(getApplicationContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (strAuthor.equals("")) {
                        Toast.makeText(getApplicationContext(), "저자를 입력해 주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 5. ArrayList에 추가하고

                    Book book = new Book(strTitle, strAuthor, "", strDate, memos);
                    bookArrayList.add(0, book); //첫번째 줄에 삽입됨
                    //mArrayList.add(dict); //마지막 줄에 삽입됨


                    // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.

                    adapter.notifyItemInserted(0);
                    //mAdapter.notifyDataSetChanged();

                    main_text.setText(adapter.getItemCount()+"권의 책 기록");
                    dialog.dismiss();
                }
            });

            dialog.show();
        } else if (item.getItemId() == R.id.go_papago) {
            Intent intent = new Intent(getApplicationContext(), Papago.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.go_map) {
            Intent intent = new Intent(getApplicationContext(), MapFragmentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}