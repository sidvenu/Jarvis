package com.example.jarvis;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UIActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        preferences = getSharedPreferences("preference", MODE_PRIVATE);
        if (!(preferences.contains("uid"))) {
            long randomUID = System.currentTimeMillis();
            preferences.edit().putLong("uid", randomUID).commit();
        }
        new NetworkAsyncTask().execute("Hey there!");
    }

    private class NetworkAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... talkWord) {
            if (talkWord == null || talkWord[0].isEmpty()) return null;

            NetworkParseResponse.formURL(talkWord[0], preferences.getLong("uid", 0));
            NetworkParseResponse.establishConnectionGetResponse();
            String botResponse = NetworkParseResponse.getBotResponse();
            return botResponse;
        }

        @Override
        protected void onPostExecute(String botResponse) {
            super.onPostExecute(botResponse);
            if (botResponse != null) {
                ((TextView) UIActivity.this.findViewById(R.id.botResponse)).setText(botResponse);
            }
        }
    }


}

