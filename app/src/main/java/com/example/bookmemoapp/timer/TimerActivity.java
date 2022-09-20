package com.example.bookmemoapp.timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmemoapp.MainActivity;
import com.example.bookmemoapp.Memo;
import com.example.bookmemoapp.R;
import com.example.bookmemoapp.custom.ClearEditText;
import com.example.bookmemoapp.recyclerview.MemoList;

import java.util.ArrayList;

import static com.example.bookmemoapp.MainActivity.bookArrayList;

public class TimerActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    TextView count_view;
    Button start_button;
    EditText input_hour, input_minute, input_second;

    TextView what_book, select_main;

    CountDownTimer countDownTimer;

    ArrayList<TimerSelect> timerSelectArrayList;
    TimerSelectAdapter timerSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        RecyclerView timerRecyclerView = findViewById(R.id.timer_recyclerview);
        timerSelectArrayList = new ArrayList<>();
        timerSelectAdapter = new TimerSelectAdapter(timerSelectArrayList);
        timerRecyclerView.setAdapter(timerSelectAdapter);

        for (int i=0; i<bookArrayList.size(); i++) {
            TimerSelect timerSelect = new TimerSelect(bookArrayList.get(i).getTitle());
            timerSelectArrayList.add(timerSelect);
            timerSelectAdapter.notifyDataSetChanged();
        }

        timerRecyclerView.setVisibility(View.GONE);


        // 화면에 보일 TextView
        count_view = findViewById(R.id.count_view);

        input_hour = findViewById(R.id.input_hour);
        input_minute = findViewById(R.id.input_minute);
        input_second = findViewById(R.id.input_second);

        start_button = findViewById(R.id.start_button);

        // 시작 버튼 클릭 이벤트
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hourStr = input_hour.getText().toString();
                String minuteStr = input_minute.getText().toString();
                String secondStr = input_second.getText().toString();

                long conversionTime = 0;

                // 1000 단위가 1초
                // 60000 단위가 1분
                // 60000 * 3600 = 1시간

                if (hourStr.equals("")) {
                    hourStr = "0";
                }
                if (minuteStr.equals("")) {
                    minuteStr = "0";
                }
                if (secondStr.equals("")) {
                    secondStr = "0";
                }

                // 변환시간
                conversionTime = Long.valueOf(hourStr) * 1000 * 3600 + Long.valueOf(minuteStr) * 60 * 1000 + Long.valueOf(secondStr) * 1000;
                if (conversionTime==0) {
                    return;
                }
                start_button.setEnabled(false);

                // 카운트 다운 시작

                countDownTimer(conversionTime);
            }
        });

        what_book = findViewById(R.id.what_book);
        select_main = findViewById(R.id.select_main);
        select_main.setVisibility(View.GONE);
        what_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRecyclerView.setVisibility(View.VISIBLE);
                select_main.setVisibility(View.VISIBLE);
            }
        });

        // 타이머셀렉에서 메인을 골랐을 때
        select_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                what_book.setText("메인 ▼");
                putWhereToGo(0, "메인 ▼");
                timerRecyclerView.setVisibility(View.GONE);
                select_main.setVisibility(View.GONE);
            }
        });

        // 타이머셀렉 리스트 클릭 시 이벤트
        timerRecyclerView.addOnItemTouchListener(new MainActivity.RecyclerTouchListener(getApplicationContext(), timerRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(),timerSelectArrayList.get(position).getSelectTitle()+"으로 이동합니다", Toast.LENGTH_SHORT).show();
                what_book.setText(timerSelectArrayList.get(position).getSelectTitle()+" ▼");
                putWhereToGo(position+1, timerSelectArrayList.get(position).getSelectTitle()+" ▼");
                timerRecyclerView.setVisibility(View.GONE);
                select_main.setVisibility(View.GONE);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        getWhereToGoText();

    }

    public void getWhereToGoText() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        what_book.setText(pref.getString("whereToGoText", "메인 ▼"));
    }

    public void putWhereToGo(int position, String saveStr) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("whereToGo", position);
        editor.putString("whereToGoText", saveStr);
        editor.apply();
    }



    public void countDownTimer(long conversionTime) {
        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        countDownTimer = new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000));
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                count_view.setText(hour + ":" + min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {

                // 변경 후
                start_button.setEnabled(true);
                notificationGo();

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지

            }
        };
        countDownTimer.start();
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            countDownTimer.cancel();
        } catch (Exception e) {}
        countDownTimer = null;
    }
    */


    // notification을 동작시키는 함수
    public void notificationGo() {


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("goToMemo", 1);
        notificationIntent.putExtra("bookIndex", 0);    // <- 값을 전달해야 할 때 사용
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // notification을 상태 알림창에 띄울 intent이다.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("책 읽는 시간 끝")
                .setContentText("공부하러 가기")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else {
            // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        // notification을 동작시키는 코드
        assert notificationManager != null;

        // 화면이 꺼져 있을 때, 알람이 울리면 화면이 켜지는 코드
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK  |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "My:Tag");
        wakeLock.acquire(5000);

        // 고유숫자로 노티피케이션 동작시킴
        notificationManager.notify(1234, builder.build());

    }

}