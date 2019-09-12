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


import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity {

    private TextView toolbarName;
    private ProgressBar pbar;
    private static final String INSTA_URL = "https://api.instagram.com/v1/users/self/media/recent/?access_token=5763448592.1677ed0.82abcf35d76b4305bd45440b52ea0d26";
    private static final String newsUrl = "https://newsapi.org/v2/top-headlines?category=general&apiKey=9c869f6faad148aaa39f0410b1626315&pageSize=100&country=in";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String JsonResponseString;
    private String InstaJsonResponseString;
    private CustomViewPager centerVP,leftVP,rightVP;
    private RelativeLayout top_RL;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 100;
    final long PERIOD_MS = 5000;

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

                    else if (downX - upX > -100) {
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

    class NewsLoadTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(newsUrl).build();
            Request instaRequest = new Request.Builder().url(INSTA_URL).build();
            Response response, instaResponse;
            try {
                response = client.newCall(request).execute();
                instaResponse = client.newCall(instaRequest).execute();
                JsonResponseString = response.body().string();
                InstaJsonResponseString = instaResponse.body().string();
                editor.putString("cachedData", JsonResponseString);
                editor.putString("instaCachedData", InstaJsonResponseString);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                JsonResponseString = sharedPreferences.getString("cachedData", null);
                InstaJsonResponseString = sharedPreferences.getString("instaCachedData", null);
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
}

