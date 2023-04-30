package com.example.guessthecelebrity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView celebrityPicture;
    Button answer1, answer2, answer3, answer4;
    String htmlContent;
    ArrayList<String> celebrityNames = new ArrayList<>();
    ArrayList<String> celebrityImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        htmlContent = getHtmlContent();
        populateCelebrityNamesArrayList(htmlContent);
        populateCelebrityImagesArrayList(htmlContent);
    }

    private void initializeViews() {
        celebrityPicture = findViewById(R.id.image_view_celebrity_picture);
        answer1 = findViewById(R.id.button_answer1);
        answer2 = findViewById(R.id.button_answer2);
        answer3 = findViewById(R.id.button_answer3);
        answer4 = findViewById(R.id.button_answer4);
    }

    private String getHtmlContent() {
        String result = null;
        DownloadTask task = new DownloadTask();
        try {
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void populateCelebrityNamesArrayList(String htmlContent) {
        Pattern p = Pattern.compile("img alt=\"(.*?)\"");
        Matcher m = p.matcher(htmlContent);
        while (m.find()) {
            celebrityNames.add(m.group(1));
        }
        Log.i("celebrity names", celebrityNames.toString());
    }

    private void populateCelebrityImagesArrayList(String htmlContent) {
        Pattern p = Pattern.compile("src=\"https://m.media-amazon.com/images/M/(.*?)\"");
        Matcher m = p.matcher(htmlContent);
        while (m.find()) {
            celebrityImages.add("https://m.media-amazon.com/images/M/" + m.group(1));
        }
        Log.i("celebrity images", celebrityImages.toString());
    }

    public static class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result;
            StringBuilder stringBuilder = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    stringBuilder.append(current);
                    data = reader.read();
                }
                result = stringBuilder.toString();
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
}

































