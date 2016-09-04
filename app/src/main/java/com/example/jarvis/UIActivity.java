package com.example.jarvis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UIActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Project JARVIS";
    static TextView botResponseTextView, nammaInput;
    static ImageView jarvisLogo;
    static ProgressBar progress;
    private final int REQUEST_CODE = 1234;
    SharedPreferences preferences;
    String chat = "Hey there!";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        botResponseTextView = (TextView) findViewById(R.id.botResponse);
        botResponseTextView.setMovementMethod(new ScrollingMovementMethod());
        nammaInput = (TextView) findViewById(R.id.nammaInput);
        nammaInput.setMovementMethod(new ScrollingMovementMethod());
        jarvisLogo = (ImageView) findViewById(R.id.jarvisLogo);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        preferences = getSharedPreferences("preference", MODE_PRIVATE);
        if (!(preferences.contains("uid"))) {
            long randomUID = System.currentTimeMillis();
            preferences.edit().putLong("uid", randomUID).commit();
        }
        ImageButton startListening = (ImageButton) findViewById(R.id.speech_button);
        startListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        new NetworkAsyncTask().execute("Hey there!");
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isAvailable() && net.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            chat = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            nammaInput.setText(chat);
            Log.v(LOG_TAG, chat);
            new NetworkAsyncTask().execute(chat);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class NetworkAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            jarvisLogo.setVisibility(View.INVISIBLE);
            progress.setProgress(0);
            progress.setVisibility(View.VISIBLE);
            botResponseTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... talkWord) {
            if (talkWord == null || talkWord[0].isEmpty()) return null;

            NetworkParseResponse.appendProgressBar(progress);
            NetworkParseResponse.formURL(talkWord[0], preferences.getLong("uid", 0));
            NetworkParseResponse.establishConnectionGetResponse();

            return NetworkParseResponse.getBotResponse();
        }

        @Override
        protected void onPostExecute(String botResponse) {
            progress.setVisibility(View.INVISIBLE);
            jarvisLogo.setVisibility(View.VISIBLE);
            if (botResponse != null) {
                botResponseTextView.setText(botResponse);
            }
            botResponseTextView.setVisibility(View.VISIBLE);
        }
    }


}

