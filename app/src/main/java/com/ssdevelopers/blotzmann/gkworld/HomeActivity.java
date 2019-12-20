package com.ssdevelopers.blotzmann.gkworld;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.CubeInTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.MobileAds;
import com.techdew.lib.HorizontalWheel.AbstractWheel;
import com.techdew.lib.HorizontalWheel.ArrayWheelAdapter;
import com.techdew.lib.HorizontalWheel.OnWheelScrollListener;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity implements OnWheelScrollListener {

    private TextView toolbarName;
    private ProgressBar pbar;
    private static final String INSTA_URL = "https://api.instagram.com/v1/users/self/media/recent/?access_token=5763448592.1677ed0.6ece91c85c524949aa81734c2e7300cb";
    private static final String newsUrl = "https://newsapi.org/v2/top-headlines?category=general&apiKey=9c869f6faad148aaa39f0410b1626315&pageSize=100&country=in";
    private static final String BUSINESS_URL = "https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=9c869f6faad148aaa39f0410b1626315";
    private static final String ENTER_URL="https://newsapi.org/v2/top-headlines?country=in&category=entertainment&apiKey=9c869f6faad148aaa39f0410b1626315";
    private static final String HEALTH_URL = "https://newsapi.org/v2/top-headlines?country=in&category=health&apiKey=9c869f6faad148aaa39f0410b1626315";
    private static final String SPORTS_URL = "https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=9c869f6faad148aaa39f0410b1626315";
    private static final String SCIENCE_URL = "https://newsapi.org/v2/top-headlines?country=in&category=science&apiKey=9c869f6faad148aaa39f0410b1626315";
    private static final String TECH_URL = "https://newsapi.org/v2/top-headlines?country=in&category=technology&apiKey=9c869f6faad148aaa39f0410b1626315";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String JsonResponseString;
    private String InstaJsonResponseString;
    private String busJsonResponseString;
    private String enterJsonResponseString;
    private String sportsJsonResponseString;
    private String healthJsonResponseString;
    private String sciJsonResponseString;
    private String techJsonResponseString;

    private CustomViewPager centerVP,leftVP,rightVP;
    private RelativeLayout top_RL;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private AbstractWheel abstractWheel;
    private FancyButton quizBtn;
    private RecyclerView newsRV;

    private int wheelPositon=0;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 100;
    final long PERIOD_MS = 5000;
    int downY, upY;


    private final String[] wheelVlaues = {"Headlines","Business","Entertainment","Health","Sports","Science","Technology"};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        top_RL = findViewById(R.id.home_top);

        pbar = (ProgressBar) findViewById(R.id.home_pbar);
        Sprite foldingCube = new DoubleBounce();
        foldingCube.setColor(Color.parseColor("#f8a600"));
        foldingCube.setAlpha(50);
        pbar.setIndeterminateDrawable(foldingCube);
        pbar.setVisibility(View.VISIBLE);

        centerVP = findViewById(R.id.pager_center);
        centerVP.disableScroll(true);
        leftVP = findViewById(R.id.pager_left);
        leftVP.disableScroll(true);
        rightVP = findViewById(R.id.pager_right);
        rightVP.disableScroll(true);

        timer = new Timer();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        toolbarName = (TextView) findViewById(R.id.app_name);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Gamlangdee.ttf");
        toolbarName.setTypeface(face);

        ImageView imageView = (ImageView) findViewById(R.id.info_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,InfoActivity.class);
                startActivity(intent);
            }
        });

        top_RL.setOnTouchListener(new View.OnTouchListener() {
            int downX, upX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = (int) event.getX();
                    Log.i("down", " downX " + downX);
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upX = (int) event.getX();
                    Log.i("up", " upX " + upX);
                    if (upX - downX > 100) {
                        Log.i("right", " right called");
                        onRightSwipe();
                    }

                    else if (downX - upX > -100 && downX - upX !=0) {
                        Log.i("left", " left called");
                        onLeftSwipe();
                        // swipe left
                    }
                    return true;

                }
                return false;
            }

        });

        if (getNetworkSate()) {
            NewsLoadTask loadTask = new NewsLoadTask();
            loadTask.execute();
        } else {
            Toast.makeText(HomeActivity.this, "Check internet connection and try again", Toast.LENGTH_LONG).show();
        }

        NotificationHelper.enableBootReceiver(getApplicationContext());
        NotificationHelper.scheduleRepeatingElapsedNotification(getApplicationContext());

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        abstractWheel = (AbstractWheel) findViewById(R.id.wheel);
        ArrayWheelAdapter<String> wheelAdapter = new ArrayWheelAdapter<String>(HomeActivity.this,wheelVlaues);
        wheelAdapter.setItemResource(R.layout.horizontal_wheel_text_centered);
        wheelAdapter.setItemTextResource(R.id.text);
        abstractWheel.setViewAdapter(wheelAdapter);
        abstractWheel.addScrollingListener(HomeActivity.this);

        quizBtn = findViewById(R.id.home_quiz_btn);
        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,QuizActivity.class);
                startActivity(intent);
            }
        });

        newsRV = findViewById(R.id.home_bottom_news);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
        newsRV.setLayoutManager(layoutManager);
    }

    void onLeftSwipe(){
        int rightposition = rightVP.getCurrentItem();
        if(rightposition==19){
            rightVP.setCurrentItem(0);
        }else{
            rightVP.setCurrentItem(rightposition+1,true);
        }
        int centerposition = centerVP.getCurrentItem();
        if(centerposition==19){
            centerVP.setCurrentItem(0);
        }else{
            centerVP.setCurrentItem(centerposition+1,true);
        }
        int leftposition = leftVP.getCurrentItem();
        if(leftposition==19){
            leftVP.setCurrentItem(0);
        }else{
            leftVP.setCurrentItem(leftposition+1,true);
        }
    }

    void onRightSwipe(){
        int rightposition = rightVP.getCurrentItem();
        if(rightposition==0){
            rightVP.setCurrentItem(19);
        }else{
            rightVP.setCurrentItem(rightposition-1,true);
        }
        int centerposition = centerVP.getCurrentItem();
        if(centerposition==0){
            centerVP.setCurrentItem(19);
        }else{
            centerVP.setCurrentItem(centerposition-1,true);
        }
        int leftposition = leftVP.getCurrentItem();
        if(leftposition==0){
            leftVP.setCurrentItem(19);
        }else{
            leftVP.setCurrentItem(leftposition-1,true);
        }
    }

    @Override
    public void onScrollingStarted(AbstractWheel abstractWheel) {
        newsRV.setVisibility(View.GONE);
        wheelPositon = abstractWheel.getCurrentItem();

    }

    @Override
    public void onScrollingFinished(AbstractWheel abstractWheel) {
        if(abstractWheel.getCurrentItem() != wheelPositon){
            switch (abstractWheel.getCurrentItem()){
                case 0:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, JsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, busJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, enterJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, healthJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, sportsJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, sciJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    newsRV.swapAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, techJsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }),true);
                    newsRV.setVisibility(View.VISIBLE);
                    break;
            }
        }else{
            newsRV.setVisibility(View.VISIBLE);
        }
    }

    class NewsLoadTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(newsUrl).build();
            Request instaRequest = new Request.Builder().url(INSTA_URL).build();
            Request busRequest = new Request.Builder().url(BUSINESS_URL).build();
            Request enterRequest = new Request.Builder().url(ENTER_URL).build();
            Request sportsRequest = new Request.Builder().url(SPORTS_URL).build();
            Request healthRequest = new Request.Builder().url(HEALTH_URL).build();
            Request sciRequest = new Request.Builder().url(SCIENCE_URL).build();
            Request techRequest = new Request.Builder().url(TECH_URL).build();
            Response response, instaResponse,busResponse,enterResponse,sportsResponse,healthResponse,sciResponse,techResponse;
            try {
                response = client.newCall(request).execute();
                instaResponse = client.newCall(instaRequest).execute();
                busResponse = client.newCall(busRequest).execute();
                enterResponse = client.newCall(enterRequest).execute();
                sportsResponse = client.newCall(sportsRequest).execute();
                healthResponse = client.newCall(healthRequest).execute();
                sciResponse = client.newCall(sciRequest).execute();
                techResponse = client.newCall(techRequest).execute();
                JsonResponseString = response.body().string();
                InstaJsonResponseString = instaResponse.body().string();
                busJsonResponseString = busResponse.body().string();
                enterJsonResponseString = enterResponse.body().string();
                sportsJsonResponseString = sportsResponse.body().string();
                healthJsonResponseString = healthResponse.body().string();
                sciJsonResponseString = sciResponse.body().string();
                techJsonResponseString = techResponse.body().string();
                editor.putString("cachedData", JsonResponseString);
                editor.putString("instaCachedData", InstaJsonResponseString);
                editor.putString("busCachedData", busJsonResponseString);
                editor.putString("enterCachedData", enterJsonResponseString);
                editor.putString("sportsCachedData", sportsJsonResponseString);
                editor.putString("healthCachedData", healthJsonResponseString);
                editor.putString("sciCachedData", sciJsonResponseString);
                editor.putString("techCachedData", techJsonResponseString);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                JsonResponseString = sharedPreferences.getString("cachedData", null);
                InstaJsonResponseString = sharedPreferences.getString("instaCachedData", null);
                busJsonResponseString = sharedPreferences.getString("busCachedData", null);
                enterJsonResponseString = sharedPreferences.getString("enterCachedData", null);
                sportsJsonResponseString = sharedPreferences.getString("sportsCachedData", null);
                healthJsonResponseString = sharedPreferences.getString("healthCachedData", null);
                sciJsonResponseString = sharedPreferences.getString("sciCachedData", null);
                techJsonResponseString = sharedPreferences.getString("techCachedData", null);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pbar.setVisibility(View.GONE);

            leftVP.setAdapter(new GkPagerAdapter(HomeActivity.this,InstaJsonResponseString));
            leftVP.setCurrentItem(19);
            centerVP.setAdapter(new GkPagerAdapter(HomeActivity.this,InstaJsonResponseString));
            centerVP.setCurrentItem(0);
            rightVP.setAdapter(new GkPagerAdapter(HomeActivity.this,InstaJsonResponseString));
            rightVP.setCurrentItem(1);

            newsRV.setAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, JsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }));

        }
    }

    public boolean getNetworkSate() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else
            connected = false;
        return connected;
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (getNetworkSate()) {
            NewsLoadTask loadTask = new NewsLoadTask();
            loadTask.execute();
        } else {
            Toast.makeText(HomeActivity.this, "Check internet connection and try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (getNetworkSate()) {
            NewsLoadTask loadTask = new NewsLoadTask();
            loadTask.execute();
        } else {
            Toast.makeText(HomeActivity.this, "Check internet connection and try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downY = (int) event.getX();
            Log.i("down", " downY " + downY);
            return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            upY = (int) event.getX();
            Log.i("up", " upY " + upY);
            if (upY - downY > 100) {
                Log.i("down", " down called");
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            else if (downY - upY > -100 && downY - upY !=0) {
                Log.i("up", " up called");
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            return true;

        }
        return false;
    }
}

