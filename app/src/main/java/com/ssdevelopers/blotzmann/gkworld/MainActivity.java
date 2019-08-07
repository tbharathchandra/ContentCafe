package com.ssdevelopers.blotzmann.gkworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private String JsonResponseString;
    private RelativeLayout layout;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = findViewById(R.id.view_pager);
        layout = (RelativeLayout)findViewById(R.id.main_layout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        JsonResponseString = sharedPreferences.getString("cachedData",null);

        MobileAds.initialize(MainActivity.this,"ca-app-pub-4704448064720651~4116887642");
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-4704448064720651/6467325607");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        viewPager.setAdapter(new VerticalPgaerAdapter(MainActivity.this,JsonResponseString));
        viewPager.setPageTransformer(true,new DepthPageTransformer());

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position%10==0){
                    if(interstitialAd.isLoaded())
                        interstitialAd.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().addTestDevice("03FDF9C1F3691D99070BA570B8DEBB21").build());
            }
        });

    }


}
