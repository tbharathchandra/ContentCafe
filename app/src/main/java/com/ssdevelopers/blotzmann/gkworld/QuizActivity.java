package com.ssdevelopers.blotzmann.gkworld;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuizActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar pbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String JsonResponseString;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView textView;
    private FloatingActionButton next,previous;
    private static final String QUIZ_URL = "https://opentdb.com/api.php?amount=10&category=9&type=multiple";
    private JSONObject jsonObject;
    private ImageButton replay;
    private KonfettiView konfettiView;
    private RewardedAd rewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        toolbar = (Toolbar) findViewById(R.id.quiz_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quiz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = (RadioGroup)findViewById(R.id.quiz_options);
        replay = findViewById(R.id.replay);
        textView = findViewById(R.id.quiz_question);
        next = findViewById(R.id.quiz_next);
        previous = findViewById(R.id.quiz_previous);

        rewardedAd = new RewardedAd(this,
                "ca-app-pub-4704448064720651/7837861997");
        RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback();
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadCallback);


        radioGroup.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        previous.setVisibility(View.GONE);
        replay.setVisibility(View.GONE);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        pbar = (ProgressBar) findViewById(R.id.quiz_pbar);
        Sprite foldingCube = new DoubleBounce();
        foldingCube.setColor(Color.parseColor("#f8a600"));
        foldingCube.setAlpha(50);
        pbar.setIndeterminateDrawable(foldingCube);
        pbar.setVisibility(View.VISIBLE);

        final QuizLoadTask loadTask = new QuizLoadTask();
        loadTask.execute();

        final int[] questionNo = {0};
        final int[] results = {2,2,2,2,2,2,2,2,2,2};

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    try {


                        String question = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getString("question");
                        String correctAns = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getString("correct_answer");
                        String wrongAns1 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(0);
                        String wrongAns2 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(1);
                        String wrongAns3 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(2);

                        question=question.replaceAll("&quot;","'");
                        question=question.replaceAll("&#039;","'");
                        correctAns=correctAns.replaceAll("&quot;","'");
                        correctAns=correctAns.replaceAll("&#039;","'");
                        wrongAns1=wrongAns1.replaceAll("&quot;","'");
                        wrongAns1=wrongAns1.replaceAll("&#039;","'");
                        wrongAns2=wrongAns2.replaceAll("&quot;","'");
                        wrongAns2=wrongAns2.replaceAll("&#039;","'");
                        wrongAns3=wrongAns3.replaceAll("&quot;","'");
                        wrongAns3=wrongAns3.replaceAll("&#039;","'");

                        int radioID = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(radioID);
                        String selectedAns = radioButton.getText().toString();
                        if (selectedAns.equals(correctAns)) {
                            results[questionNo[0]] = 1;
                            Toast toast= Toast.makeText(QuizActivity.this," Correct answer ",Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(Color.parseColor("#9fff80"));
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            text.setTextColor(Color.parseColor("#000000"));
                            toast.show();
                        } else {
                            results[questionNo[0]] = 0;
                            Toast toast = Toast.makeText(QuizActivity.this," Correct answer is "+correctAns+" ",Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(Color.parseColor("#ff8080"));
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            text.setTextColor(Color.parseColor("#ffffff"));
                            toast.show();
                        }
                        questionNo[0] = questionNo[0] + 1;
                        if (results[9] == 2 & questionNo[0] < 10) {
                            question = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getString("question");
                            correctAns = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getString("correct_answer");
                            wrongAns1 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(0);
                            wrongAns2 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(1);
                            wrongAns3 = jsonObject.getJSONArray("results").getJSONObject(questionNo[0]).getJSONArray("incorrect_answers").getString(2);

                            radioGroup.clearCheck();

                            question=question.replaceAll("&quot;","'");
                            question=question.replaceAll("&#039;","'");
                            correctAns=correctAns.replaceAll("&quot;","'");
                            correctAns=correctAns.replaceAll("&#039;","'");
                            wrongAns1=wrongAns1.replaceAll("&quot;","'");
                            wrongAns1=wrongAns1.replaceAll("&#039;","'");
                            wrongAns2=wrongAns2.replaceAll("&quot;","'");
                            wrongAns2=wrongAns2.replaceAll("&#039;","'");
                            wrongAns3=wrongAns3.replaceAll("&quot;","'");
                            wrongAns3=wrongAns3.replaceAll("&#039;","'");


                            textView.setText(question);
                            String[] answers = {correctAns, wrongAns1, wrongAns2, wrongAns3};
                            List<String> anss= Arrays.asList(answers);
                            Collections.shuffle(anss);
                            answers = (String[]) anss.toArray();

                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                ((RadioButton) radioGroup.getChildAt(i)).setText(answers[i]);
                            }
                        }else{
                            next.setVisibility(View.GONE);
                            previous.setVisibility(View.GONE);
                            radioGroup.setVisibility(View.GONE);

                            konfettiView = findViewById(R.id.konfettiView);
                            konfettiView.build()
                                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                    .setDirection(0.0, 359.0)
                                    .setSpeed(1f, 5f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(2000L)
                                    .addShapes(Shape.RECT, Shape.CIRCLE)
                                    .addSizes(new Size(12, 5f))
                                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                                    .streamFor(200,20000L);

                            int result = score(results);
                            String soc = "Your score is "+result+" out of 10";
                            textView.setText(soc);
                            replay.setVisibility(View.VISIBLE);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else {
                    Toast.makeText(QuizActivity.this,"Select any options",Toast.LENGTH_SHORT).show();
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.clearCheck();
                if(rewardedAd.isLoaded()){
                    RewardedAdCallback rewardedAdCallback = new RewardedAdCallback(){
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            for(int i=0;i<results.length;i++){
                                results[i]=2;
                            }
                            questionNo[0]=0;
                            pbar.setVisibility(View.VISIBLE);
                            QuizLoadTask loadTask1 = new QuizLoadTask();
                            loadTask1.execute();
                            konfettiView.setVisibility(View.GONE);
                            replay.setVisibility(View.GONE);
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback();
                            rewardedAd.loadAd(new AdRequest.Builder().addTestDevice("03FDF9C1F3691D99070BA570B8DEBB21").build(), rewardedAdLoadCallback);
                        }
                    };
                    rewardedAd.show(QuizActivity.this,rewardedAdCallback);

                }else{
                    for(int i=0;i<results.length;i++){
                        results[i]=2;
                    }
                    questionNo[0]=0;
                    pbar.setVisibility(View.VISIBLE);
                    QuizLoadTask loadTask1 = new QuizLoadTask();
                    loadTask1.execute();
                    konfettiView.setVisibility(View.GONE);
                    replay.setVisibility(View.GONE);
                }


            }
        });



    }

    class QuizLoadTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client  = new OkHttpClient();
            Request request = new Request.Builder().url(QUIZ_URL).build();
            Response response;
            try {
                response = client.newCall(request).execute();
                JsonResponseString=response.body().string();
                editor.putString("quizCachedData",JsonResponseString);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                JsonResponseString = sharedPreferences.getString("quizCachedData",null);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            try {
                jsonObject = new JSONObject(JsonResponseString);


                String question = jsonObject.getJSONArray("results").getJSONObject(0).getString("question");
                String correctAns =jsonObject.getJSONArray("results").getJSONObject(0).getString("correct_answer");
                String wrongAns1 = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("incorrect_answers").getString(0);
                String wrongAns2 = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("incorrect_answers").getString(1);
                String wrongAns3 = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("incorrect_answers").getString(2);

                pbar.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                question=question.replaceAll("&quot;","'");
                question=question.replaceAll("&#039;","'");
                correctAns=correctAns.replaceAll("&quot;","'");
                correctAns=correctAns.replaceAll("&#039;","'");
                wrongAns1=wrongAns1.replaceAll("&quot;","'");
                wrongAns1=wrongAns1.replaceAll("&#039;","'");
                wrongAns2=wrongAns2.replaceAll("&quot;","'");
                wrongAns2=wrongAns2.replaceAll("&#039;","'");
                wrongAns3=wrongAns3.replaceAll("&quot;","'");
                wrongAns3=wrongAns3.replaceAll("&#039;","'");

                textView.setText(question);
                String[] answers ={correctAns,wrongAns1,wrongAns2,wrongAns3};
                for(int i=0;i<radioGroup.getChildCount();i++){
                    ((RadioButton) radioGroup.getChildAt(i)).setText(answers[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    public int score(int[] results){
        int s=0;
        for(int i=0;i<results.length;i++){
            s=s+results[i];
        }
        return s;
    }




}
