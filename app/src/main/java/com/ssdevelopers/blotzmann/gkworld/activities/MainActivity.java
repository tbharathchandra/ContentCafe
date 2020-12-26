package com.ssdevelopers.blotzmann.gkworld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ssdevelopers.blotzmann.gkworld.R;
import com.ssdevelopers.blotzmann.gkworld.adapters.VerticalPgaerAdapter;

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
