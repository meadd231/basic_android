package com.example.bookmemoapp.papago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmemoapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Papago extends AppCompatActivity {
    EditText inputData;
    Button translate_button;
    EditText outputData;
    Button save_output;

    // 언어 번역 target
    TextView present_target, target_en, target_ch, target_jp;

    String source = "ko";
    String target = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papago);

        inputData = findViewById(R.id.inputData);
        translate_button = findViewById(R.id.translate_button);
        outputData = findViewById(R.id.outputData);
        translate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = inputData.getText().toString();
                new TranslateThread().execute(tmp, source, target);
            }
        });
        save_output = findViewById(R.id.save_output);
        save_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("save_output", outputData.getText().toString());
                editor.apply();
                Toast.makeText(getApplicationContext(),"저장되었습니다",Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout target_area = findViewById(R.id.target_area);
        present_target = findViewById(R.id.present_target);
        target_en = findViewById(R.id.target_en);
        target_ch = findViewById(R.id.target_ch);
        target_jp = findViewById(R.id.target_jp);
        target_area.setVisibility(View.GONE);
        present_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (target_area.getVisibility()==View.VISIBLE) {
                    target_area.setVisibility(View.GONE);
                } else {
                    target_area.setVisibility(View.VISIBLE);
                }
            }
        });
        target_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = "en";
                present_target.setText(target_en.getText());
                target_area.setVisibility(View.GONE);
            }
        });
        target_ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = "zh-TW";
                present_target.setText(target_ch.getText());
                target_area.setVisibility(View.GONE);
            }
        });
        target_jp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = "ja";
                present_target.setText(target_jp.getText());
                target_area.setVisibility(View.GONE);
            }
        });
    }

    class TranslateThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... str) {
            return PapagoAPI.main(str[0], str[1], str[2]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            /*
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject jo = jsonObject.getJSONObject("message").getJSONObject("result");
                outputData.setText(jo.getString("translatedText"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
             */

            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(s);
            String tmp = element.getAsJsonObject().get("message").getAsJsonObject()
                    .get("result").getAsJsonObject().get("translatedText").getAsString();
            outputData.setText(tmp);

        }
    }
}