package com.ssdevelopers.blotzmann.gkworld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ssdevelopers.blotzmann.gkworld.R;
import com.ssdevelopers.blotzmann.gkworld.adapters.HorizontalPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class GKActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private String InstaJsonResponseString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gk);

        viewPager = (ViewPager) findViewById(R.id.gk_view_pager);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        InstaJsonResponseString = sharedPreferences.getString("instaCachedData",null);
        viewPager.setAdapter(new HorizontalPagerAdapter(GKActivity.this,InstaJsonResponseString));
        viewPager.setPageTransformer(true,new TabletTransformer());
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        DotsIndicator dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);

        MobileAds.initialize(GKActivity.this,"ca-app-pub-4704448064720651~4116887642");
        interstitialAd = new InterstitialAd(GKActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-4704448064720651/8225283927");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        dotsIndicator.setViewPager(viewPager);
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
