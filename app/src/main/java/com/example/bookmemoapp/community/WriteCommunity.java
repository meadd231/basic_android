package com.example.bookmemoapp.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmemoapp.MainActivity;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.community.ReadCommunity;
import com.example.bookmemoapp.custom.Book;
import com.example.bookmemoapp.custom.ClearEditText;
import com.example.bookmemoapp.recyclerview.MemoList;
import com.example.bookmemoapp.recyclerview.Written;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.bookmemoapp.community.Community.writtenArrayList;

public class WriteCommunity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    ImageView writeWrittenImage;
    TextView cameraBtn;
    TextView galleryBtn;
    TextView linkBtn;
    ClearEditText writeWrittenTitle;    // 에딧 텍스트에서 제목
    ClearEditText writeWrittenContents; // 에딧 텍스트에서 내용
    ClearEditText writeWrittenAuthor;   // 에딧 텍스트에서 작성자
    ClearEditText writeWrittenPassword; // 에딧 텍스트에서 비밀번호

    // 비트맵을 문자열로 바꾼 것을 저장하는 변수
    String writtenImageByString = "";

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();      // 현재시간을 date 변수에 저장함
    Date date = new Date(now);                  // 시간을 나타냇 포맷을 정한다 ( yyyy년 MM월 dd일 형태로 변형 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd HH:mm"); // nowDate 변수에 값을 저장함
    String formatDate = sdfNow.format(date);    // 현재 시간을 fomatDate에 위의 형식으로 저장함


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_community);

        writeWrittenTitle = findViewById(R.id.write_written_title);
        writeWrittenContents = findViewById(R.id.write_written_contents);
        writeWrittenAuthor = findViewById(R.id.write_written_author);
        writeWrittenPassword = findViewById(R.id.write_written_password);

        // 레이아웃과 변수 연결
        writeWrittenImage = findViewById(R.id.write_written_image);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        linkBtn = findViewById(R.id.linkBtn);

        if (ContextCompat.checkSelfPermission(WriteCommunity.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WriteCommunity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        try {
            getLastUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 카메라 버튼 클릭
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        // 갤러리 버튼 클릭
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    // 쉐어드를 다 지우고 싶을 때 사용함
    public void deleteCode() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("writtenMain");
        editor.apply();
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(WriteCommunity.this)
                .setTitle("링크 입력")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(WriteCommunity.this, "입력되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }


    // 카메라 사용 ( 카메라 사용을 위해 카메라 액티비티, 갤러리 액티비티에 간 뒤 돌아올 때 실행됨 )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 갤러리인 경우
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    writeWrittenImage.setImageBitmap(img);
                    saveBitmapToShared(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        // 카메라인 경우
        } else if (requestCode == 100) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            writeWrittenImage.setImageBitmap(bitmap);
            saveBitmapToShared(bitmap);
        }
    }

    public void saveBitmapToShared(Bitmap bitmap) {
        writtenImageByString = BitMapToString(bitmap);
    }

    // 비트맵을 문자열로 바꾸어서 저장하는 함수
    public void saveBitmap(Bitmap bitmap) {
        String image = BitMapToString(bitmap);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("imagestrings", image);
        editor.apply();
    }

    // saveBitmap 함수에 들어감 ( 비트맵을 문자열로 바꾸는 함수 )
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    
    // 문자열을 비트맵으로 바꾸어 출력하는 함수
    public void loadBitmap(ImageView imageView) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String image = pref.getString("imagestrings", "");
        Bitmap bitmap = StringToBitMap(image);
        imageView.setImageBitmap(bitmap);
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

    // 마지막에 작성한 사용자의 작성자 이름을 저장해 나중에 기본값으로 사용함
    public void putLastUserInfo(String name, String password) throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("lastUserName", name);
        editor.putString("lastUserPassword", password);
        editor.apply();
    }

    // 마지막에 작성한 사용자의 비밀번호를 저장해 나중에 기본값으로 사용함
    public void getLastUserInfo() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String author;
        String password;
        author = pref.getString("lastUserName", "");
        password = pref.getString("lastUserPassword", "");
        writeWrittenAuthor.setText(author);
        writeWrittenPassword.setText(password);
    }

    public void putWrittenItem() throws JSONException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<writtenArrayList.size(); i++) {
            JSONObject jo = new JSONObject();
            // 글 아이템 중 배열에 저장되지 않은 정보들을 쉐어드에 put 함
            jo.put("writtenTitle", writtenArrayList.get(i).getWrittenTitle());
            jo.put("writtenAuthor", writtenArrayList.get(i).getWrittenAuthor());
            jo.put("writtenDate", writtenArrayList.get(i).getWrittenDate());
            jo.put("writtenPassword", writtenArrayList.get(i).getWrittenPassword());
            jo.put("viewsNum", writtenArrayList.get(i).getViewsNum());
            jo.put("contentsText", writtenArrayList.get(i).getContentsText());
            // "" or 이미지를 담고 있는 문자열
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


    // 등록 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_community_menu, menu);
        return true;
    }

    // 등록버튼 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.posting) {
            if (writeWrittenTitle.getText().toString().equals("")) {
                Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                return true;
            } else if (writeWrittenAuthor.getText().toString().equals("")) {
                Toast.makeText(this, "작성자를 입력해주세요", Toast.LENGTH_SHORT).show();
                return true;
            } else if (writeWrittenPassword.getText().toString().equals("")) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                return true;
            } else if (writeWrittenContents.getText().toString().equals("")) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                return true;
            }
            ArrayList<Comment> comments = new ArrayList<>();
            Written written = new Written(writeWrittenTitle.getText().toString(),
                    writeWrittenAuthor.getText().toString(),
                    writeWrittenPassword.getText().toString(),
                    formatDate,0,comments,
                    writeWrittenContents.getText().toString(),
                    writtenImageByString);
            writtenArrayList.add(0, written);
            try {
                putWrittenItem();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                putLastUserInfo(writeWrittenAuthor.getText().toString(), writeWrittenPassword.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 인텐트로 정보 보내기
            Toast.makeText(this, "글이 등록되었습니다", Toast.LENGTH_SHORT).show();
            Intent readWritten = new Intent(getApplicationContext(), ReadCommunity.class);
            readWritten.putExtra("writtenIndex", 0);
            startActivity(readWritten);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}