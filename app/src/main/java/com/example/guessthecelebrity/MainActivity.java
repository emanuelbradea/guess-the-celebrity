package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ImageView celebrityImage;
    private Button answer1, answer2, answer3, answer4;
    private final ArrayList<String> celebrityNames = new ArrayList<>();
    private final ArrayList<String> celebrityImages = new ArrayList<>();
    private final ArrayList<String> celebrityGender = new ArrayList<>();
    private int indexOfAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        String htmlContent = getHtmlContent();
        populateCelebrityNamesArrayList(htmlContent);
        populateCelebrityImagesArrayList(htmlContent);
        populateCelebrityGenderArrayList(htmlContent);
        generateQuestion();
    }

    private void initializeViews() {
        celebrityImage = findViewById(R.id.image_view_celebrity_image);
        answer1 = findViewById(R.id.button_answer1);
        answer2 = findViewById(R.id.button_answer2);
        answer3 = findViewById(R.id.button_answer3);
        answer4 = findViewById(R.id.button_answer4);
    }

    private String getHtmlContent() {
        String result = null;
        DownloadHtmlTask task = new DownloadHtmlTask();
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
    }

    private void populateCelebrityImagesArrayList(String htmlContent) {
        Pattern p = Pattern.compile("src=\"https://m.media-amazon.com/images/M/(.*?)\"");
        Matcher m = p.matcher(htmlContent);
        while (m.find()) {
            celebrityImages.add("https://m.media-amazon.com/images/M/" + m.group(1));
        }
    }

    private void populateCelebrityGenderArrayList(String htmlContent) {
        Pattern p = Pattern.compile("                        (.*?) <span class=\"ghost\">");
        Matcher m = p.matcher(htmlContent);
        while (m.find()) {
            if (m.group(1).equals("Actor")) {
                celebrityGender.add("Male");
            } else if (m.group(1).equals("Actress")) {
                celebrityGender.add("Female");
            } else celebrityGender.add(null);
        }
        celebrityGender.set(9, "Male");
        celebrityGender.set(10, "Male");
        celebrityGender.set(12, "Male");
        celebrityGender.set(16, "Male");
        celebrityGender.set(20, "Male");
        celebrityGender.set(28, "Male");
        celebrityGender.set(44, "Male");
        celebrityGender.set(53, "Male");
        celebrityGender.set(72, "Male");
        celebrityGender.set(73, "Female");
        celebrityGender.set(75, "Female");
        celebrityGender.set(88, "Male");
        celebrityGender.set(94, "Male");
    }

    private void generateQuestion() {
        // Generate a random array of four unique index values between 0 and 99 inclusive
        Random rand = new Random();
        indexOfAnswer = rand.nextInt(100);
        String genderOfCelebrity = celebrityGender.get(indexOfAnswer);
        ArrayList<Integer> indexOfAnswers = new ArrayList<>();
        indexOfAnswers.add(indexOfAnswer);
        for (int i = 1; i < 4; i++) {
            int index = rand.nextInt(100);
            while (indexOfAnswers.contains(index) || genderOfCelebrity != celebrityGender.get(index)) {
                index = rand.nextInt(100);
            }
            indexOfAnswers.add(index);
        }
        Collections.shuffle(indexOfAnswers);

        // Download the celebrity image and set it to celebrityImage
        DownloadImageTask task = new DownloadImageTask();
        try {
            celebrityImage.setImageBitmap(task.execute(celebrityImages.get(indexOfAnswer)).get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the answer buttons with celebrity names
        answer1.setText(celebrityNames.get(indexOfAnswers.get(0)));
        answer2.setText(celebrityNames.get(indexOfAnswers.get(1)));
        answer3.setText(celebrityNames.get(indexOfAnswers.get(2)));
        answer4.setText(celebrityNames.get(indexOfAnswers.get(3)));
    }

    public void answerClicked(View view) {
        Button selectedAnswer = (Button) view;
        if (selectedAnswer.getText() == celebrityNames.get(indexOfAnswer)) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
        generateQuestion();
    }

    public static class DownloadHtmlTask extends AsyncTask<String, Void, String> {
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

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}