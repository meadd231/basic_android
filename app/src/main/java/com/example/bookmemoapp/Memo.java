package com.example.bookmemoapp;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.custom.ClearEditText;
import com.example.bookmemoapp.custom.SoftKeyboard;
import com.example.bookmemoapp.recyclerview.MemoAdapter;
import com.example.bookmemoapp.recyclerview.MemoList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.bookmemoapp.MainActivity.bookArrayList;

public class Memo extends AppCompatActivity {

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();      // 현재시간을 date 변수에 저장함
    Date date = new Date(now);                  // 시간을 나타냇 포맷을 정한다 ( yyyy년 MM월 dd일 형태로 변형 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd HH:mm");      // nowDate 변수에 값을 저장함
    String formatDate = sdfNow.format(date);        // 현재 시간을 fomatDate에 위의 형식으로 저장함

    TextView title;
    TextView author;
    EditText memo;

    LinearLayout memo_input_helper;
    TextView completeBtn;

    MemoAdapter memoAdapter;
    ArrayList<MemoList> memoArrayList;
    int bookIndex;

    SoftKeyboard softKeyboard;
    LinearLayout ll;
    ImageButton load_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);


        // 리사이클러뷰 선언
        RecyclerView memoRecyclerView = findViewById(R.id.memoList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        memoRecyclerView.setLayoutManager(mLinearLayoutManager);


        // 인텐트로 인덱스 번호 전달
        Bundle extras = getIntent().getExtras();
        bookIndex = extras.getInt("bookIndex");

        memoArrayList = new ArrayList<>();
        memoAdapter = new MemoAdapter(memoArrayList);
        memoRecyclerView.setAdapter(memoAdapter);

        // 책 리스트 중 1개를 선택했을 때 나오는 제목, 저자, 날짜, 메모내용 등임
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        memo = findViewById(R.id.memo);
        TextView todayDate = findViewById(R.id.todayDate);

        memo_input_helper = findViewById(R.id.memo_input_helper);
        memo_input_helper.setVisibility(View.GONE);

        completeBtn = findViewById(R.id.completeBtn);

        ll = findViewById(R.id.ll);

        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(ll, controlManager);

        try {
            getMemoItem(bookIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TextView 에 현재 시간 문자열 할당
        todayDate.setText(formatDate);

        // 메모 리스트 클릭 시 이벤트 (다이얼로그 띄우기)
        memoRecyclerView.addOnItemTouchListener(new MainActivity.RecyclerTouchListener(getApplicationContext(), memoRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MemoList memoListItem = memoArrayList.get(position);

                View dialogView = getLayoutInflater().inflate(R.layout.modify_memo, null);
                final ClearEditText memoEditText = dialogView.findViewById(R.id.memoText);
                final Button modifyButton = dialogView.findViewById(R.id.modify_confirm_button);
                final Button deleteButton = dialogView.findViewById(R.id.delete_confirm_button);

                memoEditText.setText(memoListItem.getMemoLog());

                AlertDialog.Builder builder = new AlertDialog.Builder(Memo.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                // 수정 버튼 클릭 이벤트 ( 메모 로그를 바뀐 대로 수정함 )
                modifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strMemoLog = memoEditText.getText().toString();

                        MemoList memo = new MemoList(strMemoLog, memoListItem.getMemoDate());

                        memoArrayList.set(position, memo);
                        bookArrayList.get(bookIndex).getMemos().set(position, memo);
                        memoAdapter.notifyItemChanged(position);

                        alertDialog.dismiss();
                    }
                });
                // 삭제 버튼 클릭 이벤트 ( 메모 로그를 삭제함 )
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        memoArrayList.remove(position);
                        bookArrayList.get(bookIndex).getMemos().remove(position);
                        memoAdapter.notifyItemRemoved(position);
                        memoAdapter.notifyItemRangeChanged(position, memoArrayList.size());

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {
            @Override
            public void onSoftKeyboardHide()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 내려왔을때
                        memo_input_helper.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 올라왔을때
                        memo_input_helper.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        TextView completeBtn = findViewById(R.id.completeBtn);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    MemoList memoList = new MemoList(memo.getText().toString(), formatDate);
                    memoArrayList.add(0, memoList);
                    bookArrayList.get(bookIndex).getMemos().add(0, memoList);
                    bookArrayList.get(bookIndex).setFirst_memo(bookArrayList.get(bookIndex).getMemos().get(0).getMemoLog());
                    memoAdapter.notifyDataSetChanged();


                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    memo.setText("");
                }
            }
        });

        TextView memo_align, memo_align_new, memo_align_old;
        memo_align = findViewById(R.id.memo_align);
        memo_align_new = findViewById(R.id.memo_align_new);
        memo_align_old = findViewById(R.id.memo_align_old);

        memo_align_new.setVisibility(View.GONE);
        memo_align_old.setVisibility(View.GONE);

        memo_align.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo_align_new.setVisibility(View.VISIBLE);
                memo_align_old.setVisibility(View.VISIBLE);
            }
        });

        // 최신순 텍스트
        memo_align_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo_align.getText().toString().equals("작성순 ▼")) {
                    int for_num = memoArrayList.size();
                    memoArrayList.clear();
                    for (int i = 0; i < for_num; i++) {
                        memoArrayList.add(bookArrayList.get(bookIndex).getMemos().get(i));
                    }
                }
                memoAdapter.notifyDataSetChanged();
                memo_align.setText("최신순 ▼");
                memo_align_new.setVisibility(View.GONE);
                memo_align_old.setVisibility(View.GONE);
            }
        });

        // 작성순 텍스트
        memo_align_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memo_align.getText().toString().equals("최신순 ▼")) {
                    int for_num = memoArrayList.size();
                    memoArrayList.clear();
                    for (int i = 0; i < for_num; i++) {
                        memoArrayList.add(0, bookArrayList.get(bookIndex).getMemos().get(i));
                    }
                }
                memoAdapter.notifyDataSetChanged();
                memo_align.setText("작성순 ▼");
                memo_align_new.setVisibility(View.GONE);
                memo_align_old.setVisibility(View.GONE);
            }
        });

        load_output = findViewById(R.id.load_output);
        load_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String save_output = pref.getString("save_output", "");
                Editable memoGo = memo.getText().append(save_output);
                memo.setText(memoGo);
                Toast.makeText(getApplicationContext(), "붙여넣었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // 생명주기가 onPause 일때 저장이 되게 함
    @Override
    protected void onPause() {
        super.onPause();
        try {
            putMemoItem();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }


    // 책 기록의 추가, 수정, 삭제를 저장하는 함수
    // 현재의 ArrayList를 그대로 SharedPreferences에 저장함
    public void putMemoItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < bookArrayList.size(); i++) {
            JSONObject jo = new JSONObject();
            jo.put("bookTitle", bookArrayList.get(i).getTitle());
            jo.put("bookAuthor", bookArrayList.get(i).getAuthor());
            jo.put("firstMemo", bookArrayList.get(i).getFirst_memo());
            if (bookArrayList.get(i).getMemos().isEmpty()) {
                jo.put("firstMemo", "");
            }
            jo.put("bookDate", bookArrayList.get(i).getDate());

            // 메모리스트 배열을 추가하는 코드
            JSONArray memoListArray = new JSONArray();
            for (int j = 0; j < bookArrayList.get(i).getMemos().size(); j++) {
                JSONObject memoListObject = new JSONObject();
                memoListObject.put("memoLog", bookArrayList.get(i).getMemos().get(j).getMemoLog());
                memoListObject.put("memoDate", bookArrayList.get(i).getMemos().get(j).getMemoDate());
                memoListArray.put(memoListObject);
            }
            // "memoList" 라는 키값을 가진 배열 생성
            // 이 안에 memoLog, memoDate 가 들어가 있음
            jo.put("memoList", memoListArray);

            jsonArray.put(jo);
        }
        // "bookList" 라는 키값을 가진 배열 생성
        // 이 안에 bookTitle, bookAuthor, bookDate, memoList 가 들어가 있음
        jsonObject.put("bookList", jsonArray);

        // "bookMain" 라는 키값을 가진 SharedPreferences 데이터를 저장
        // 이 안에 bookList 가 들어가 있음
        editor.putString("bookMain", jsonObject.toString());
        editor.apply();
    }

    // 책 리스트, 메모 리스트가 저장되 있는 쉐어드의 정보를 받아와서 메모 리스트를 출력해주는 함수
    public void getMemoItem(int position) throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String json = pref.getString("bookMain", "");

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("bookList");
        JSONObject object = jsonArray.getJSONObject(position);
        JSONArray jarr = object.getJSONArray("memoList");
        // 메모 액티비티에서 메모 리스트를 출력해줌
        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jo = jarr.getJSONObject(i);
            MemoList memoList = new MemoList(jo.getString("memoLog"), jo.getString("memoDate"));
            memoArrayList.add(memoList);
            memoAdapter.notifyDataSetChanged();
        }
        title.setText(object.getString("bookTitle"));
        author.setText(object.getString("bookAuthor"));
    }

    // 메모 액티비티 앱바에 메뉴 띄움
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_menu, menu);

        return true;
    }

    // 작성 버튼을 누르면 작성이 완료되고 메모 내용이 밑에 있는 과거 메모 기록 맨 위 항목으로 감
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 작성 버튼 누름
        if (item.getItemId() == R.id.writeDoneBtn) {
            if (memo.getText().toString().equals("")) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                MemoList memoList = new MemoList(memo.getText().toString(), formatDate);
                memoArrayList.add(0, memoList);
                bookArrayList.get(bookIndex).getMemos().add(0, memoList);
                bookArrayList.get(bookIndex).setFirst_memo(bookArrayList.get(bookIndex).getMemos().get(0).getMemoLog());
                memoAdapter.notifyDataSetChanged();

                final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                memo.setText("");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}