package com.example.bookmemoapp.community;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmemoapp.R;
import com.example.bookmemoapp.custom.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.bookmemoapp.community.Community.writtenArrayList;

public class ReadCommunity extends AppCompatActivity {

    // 사용할 이미지 뷰
    ImageView writtenImageContents;

    // 글의 내용, 댓글 입력 버튼, 댓글 내용, 작성자, 비밀번호
    TextView writtenContents;
    TextView sendingBtn;
    ClearEditText input_comment;
    ClearEditText write_comment_author;
    ClearEditText write_comment_password;
    
    // 글의 제목, 작성자, 작성시간, 조회수
    TextView readWrittenTitle;
    TextView readWrittenAuthor;
    TextView readWrittenDate;
    TextView readViewsNum;

    // 글쓰기로 가기, 글 수정, 글 삭제 버튼
    Button writeWrittenButton;
    Button modifyWrittenButton;
    Button deleteWrittenButton;

    // 글 인덱스 ( 커뮤니티 글 중 어느 것인지 식별 가능하게 함 )
    int writtenIndex;

    // 광고 이미지뷰들
    ImageView team_nova_logo, tesla_logo, apple_logo, lol_logo;

    // 댓글 ArrayList, Adapter
    ArrayList<Comment> commentArrayList;
    CommentAdapter commentAdapter;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();      // 현재시간을 date 변수에 저장함
    Date date = new Date(now);                  // 시간을 나타냇 포맷을 정한다 ( yyyy년 MM월 dd일 형태로 변형 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd HH:mm"); // nowDate 변수에 값을 저장함
    String formatDate = sdfNow.format(date);    // 현재 시간을 fomatDate에 위의 형식으로 저장함

    int imageCondition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_community);

        // 댓글 리사이클러뷰
        RecyclerView commentRecyclerView = findViewById(R.id.comment_recyclerview);
        commentArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentArrayList);
        commentRecyclerView.setAdapter(commentAdapter);

        // 에딧텍스트 등등 초기화
        input_comment = findViewById(R.id.input_comment);                       // 댓글 입력하는 에디트텍스트
        write_comment_author = findViewById(R.id.write_comment_author);         // 작성자 입력하는 에디트텍스트
        write_comment_password = findViewById(R.id.write_comment_password);     // 비밀번호 입력하는 에디트텍스트
        sendingBtn = findViewById(R.id.sendingBtn);                             // 댓글 입력 완료버튼
        writtenContents = findViewById(R.id.written_contents);
        writtenImageContents = findViewById(R.id.written_image_contents);
        

        // 마지막으로 작성한 글이나 댓글의 작성이름과 비밀번호를 저장함
        try {
            getLastUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 댓글 작성 버튼 클릭 이벤트
        sendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (write_comment_author.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "댓글 작성자를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (write_comment_password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "댓글 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (input_comment.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "댓글 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Comment comment = new Comment(write_comment_author.getText().toString(),
                            input_comment.getText().toString(),
                            formatDate,
                            write_comment_password.getText().toString());
                    commentArrayList.add(0, comment);
                    writtenArrayList.get(writtenIndex).getComments().add(0, comment);
                    commentAdapter.notifyDataSetChanged();

                    final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    input_comment.setText("");

                    try {
                        putLastUserInfo(write_comment_author.getText().toString(), write_comment_password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 인텐트로 어떤 글의 정보인지 인덱스 받아오기
        Bundle extras = getIntent().getExtras();
        writtenIndex = extras.getInt("writtenIndex");

        // 인텐트로 이미지 정보 받아오기
        /*
        Bitmap bmp = extras.getParcelable("writtenImageContents");
        if (bmp == null) {
            writtenImageContents.setVisibility(View.GONE);
        } else {

        }
        writtenImageContents.setImageBitmap(bmp);
         */

        // 글의 제목, 작성자, 날짜, 조회수 초기화
        readWrittenTitle = findViewById(R.id.read_written_title);
        readWrittenAuthor = findViewById(R.id.read_written_author);
        readWrittenDate = findViewById(R.id.read_written_date);
        readViewsNum = findViewById(R.id.read_views_num);


        // 글쓰기로 가기, 글 수정, 글 삭제 버튼 인플레이트
        writeWrittenButton = findViewById(R.id.write_written_button);
        modifyWrittenButton = findViewById(R.id.modify_written_button);
        deleteWrittenButton = findViewById(R.id.delete_written_button);
        
        // ㄴ 버튼 클릭 리스너
        writeWrittenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteCommunity.class);
                startActivity(intent);
                finish();
            }
        });

        modifyWrittenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteWrittenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadCommunity.this);

                View view = LayoutInflater.from(ReadCommunity.this)
                        .inflate(R.layout.dialog_check_password, null, false);
                builder.setView(view);


                final Button passwordCancelButton = view.findViewById(R.id.password_cancel_button);
                final Button passwordConfirmButton = view.findViewById(R.id.password_confirm_button);
                final EditText passwordInputEditText = view.findViewById(R.id.password_input_editText);

                final AlertDialog dialog = builder.create();


                // 비밀번호가 맞나 틀리나 확인
                passwordConfirmButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String password = writtenArrayList.get(writtenIndex).getWrittenPassword();
                        String input_code = passwordInputEditText.getText().toString();
                        // 4. 사용자가 입력한 내용을 가져와서
                        if (input_code.equals(password)) {
                            Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            writtenArrayList.remove(writtenIndex);
                            finish();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                passwordCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        team_nova_logo = findViewById(R.id.team_nova_logo);
        tesla_logo = findViewById(R.id.tesla_logo);
        apple_logo = findViewById(R.id.apple_logo);
        lol_logo = findViewById(R.id.lol_logo);

        Button nextImageButton = findViewById(R.id.next_image_button);
        Button stopImageButton = findViewById(R.id.stop_image_button);
        nextImageButton.setVisibility(View.GONE);
        stopImageButton.setVisibility(View.GONE);
        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (imageCondition) {
                    case 0 :
                        team_nova_logo.setVisibility(View.GONE);
                        imageCondition++;
                        break;
                    case 1 :
                        tesla_logo.setVisibility(View.GONE);
                        imageCondition++;
                        break;
                    case 2 :
                        apple_logo.setVisibility(View.GONE);
                        imageCondition++;
                        break;
                    case 3 :
                        team_nova_logo.setVisibility(View.VISIBLE);
                        tesla_logo.setVisibility(View.VISIBLE);
                        apple_logo.setVisibility(View.VISIBLE);
                        imageCondition = 0;
                        break;
                    default :
                }
            }
        });

        AdThread adThread = new AdThread();
        adThread.setDaemon(true);
        adThread.start();

        stopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adThread.stop();
            }
        });

    }

    class AdThread extends Thread {
        Handler handler = new Handler();
        public void run() {
            while (true) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (imageCondition) {
                            case 0 :
                                team_nova_logo.setVisibility(View.GONE);
                                imageCondition++;
                                break;
                            case 1 :
                                tesla_logo.setVisibility(View.GONE);
                                imageCondition++;
                                break;
                            case 2 :
                                apple_logo.setVisibility(View.GONE);
                                imageCondition++;
                                break;
                            case 3 :
                                team_nova_logo.setVisibility(View.VISIBLE);
                                tesla_logo.setVisibility(View.VISIBLE);
                                apple_logo.setVisibility(View.VISIBLE);
                                imageCondition = 0;
                                break;
                            default :
                        }
                    }
                }, 2000);
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            getWrittenItem(writtenIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!writtenArrayList.isEmpty()) {
            writtenArrayList.get(writtenIndex).setViewsNum(writtenArrayList.get(writtenIndex).getViewsNum() + 1);
        }
        try {
            putWrittenItem();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putLastUserInfo(String name, String password) throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("lastUserName", name);
        editor.putString("lastUserPassword", password);
        editor.apply();
    }

    public void getLastUserInfo() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String author;
        String password;
        author = pref.getString("lastUserName", "");
        password = pref.getString("lastUserPassword", "");
        write_comment_author.setText(author);
        write_comment_password.setText(password);
    }

    public void putWrittenItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<writtenArrayList.size(); i++) {
            JSONObject jo = new JSONObject();
            jo.put("writtenTitle", writtenArrayList.get(i).getWrittenTitle());
            jo.put("writtenAuthor", writtenArrayList.get(i).getWrittenAuthor());
            jo.put("writtenDate", writtenArrayList.get(i).getWrittenDate());
            jo.put("writtenPassword", writtenArrayList.get(i).getWrittenPassword());
            jo.put("viewsNum", writtenArrayList.get(i).getViewsNum());
            jo.put("contentsText", writtenArrayList.get(i).getContentsText());
            jo.put("contentsImage", writtenArrayList.get(i).getContentsImage());

            // 댓글리스트 배열을 추가하는 코드
            JSONArray commentListArray = new JSONArray();
            for (int j=0; j<writtenArrayList.get(i).getComments().size(); j++) {
                JSONObject commentListObject = new JSONObject();
                commentListObject.put("commentAuthor", writtenArrayList.get(i).getComments().get(j).getCommentAuthor());
                commentListObject.put("commentText", writtenArrayList.get(i).getComments().get(j).getCommentText());
                commentListObject.put("commentDate", writtenArrayList.get(i).getComments().get(j).getCommentDate());
                commentListObject.put("commentPassword", writtenArrayList.get(i).getComments().get(j).getCommentPassword());
                commentListArray.put(commentListObject);
            }
            jo.put("commentList", commentListArray);

            jsonArray.put(jo);
        }
        // "writtenList" 라는 키값을 가진 배열 생성
        jsonObject.put("writtenList", jsonArray);

        // "writtenMain" 라는 키값을 가진 SharedPreferences 데이터를 저장
        editor.putString("writtenMain", jsonObject.toString());
        editor.apply();
    }

    // 책 리스트, 메모 리스트가 저장되 있는 쉐어드의 정보를 받아와서 책 리스트를 출력해주는 함수
    public void getWrittenItem(int position) throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String json = pref.getString("writtenMain", "");

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("writtenList");
        JSONObject jo = jsonArray.getJSONObject(position);
        JSONArray commentListArray = jo.getJSONArray("commentList");
        for (int i=0; i<commentListArray.length(); i++) {
            JSONObject commentListObject = commentListArray.getJSONObject(i);
            Comment comment = new Comment(commentListObject.getString("commentAuthor"),
                    commentListObject.getString("commentText"),
                    commentListObject.getString("commentDate"),
                    commentListObject.getString("commentPassword"));
            commentArrayList.add(comment);
            commentAdapter.notifyDataSetChanged();
        }

        // 글의 제목, 작성자, 날짜, 조회수를 저장된 값에서 불러와 레이아웃에 출력함
        readWrittenTitle.setText(jo.getString("writtenTitle"));
        readWrittenAuthor.setText(jo.getString("writtenAuthor"));
        readWrittenDate.setText(jo.getString("writtenDate"));
        int viewsNum = jo.getInt("viewsNum")+1;
        writtenArrayList.get(position).getViewsNum();
        readViewsNum.setText(Integer.toString(viewsNum));
        writtenContents.setText(jo.getString("contentsText"));
        loadBitmap(writtenImageContents, jo.getString("contentsImage"));
    }

    // 문자열을 비트맵으로 바꾸어 이미지 뷰에 넣어주는 함수
    public void loadBitmap(ImageView imageView, String imageString) {
        if (!imageString.equals("")) {
            Bitmap bitmap = StringToBitMap(imageString);
            imageView.setImageBitmap(bitmap);
        } else {
            writtenImageContents.setVisibility(View.GONE);
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
}
