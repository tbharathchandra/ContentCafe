package com.ssdevelopers.blotzmann.gkworld.activities;


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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.MobileAds;
import com.ssdevelopers.blotzmann.gkworld.R;
import com.ssdevelopers.blotzmann.gkworld.adapters.HomeGkRecyclerAdapter;
import com.ssdevelopers.blotzmann.gkworld.adapters.HoneNewsRecyclerAdapter;
import com.ssdevelopers.blotzmann.gkworld.notification.NotificationHelper;


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
    private TextView headlines, gk;
    private RecyclerView recyclerView, gkRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdView adView;
    private FloatingActionButton quizBtn;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);



        adView = (AdView) findViewById(R.id.adview);
        MobileAds.initialize(HomeActivity.this, "ca-app-pub-4704448064720651~4116887642");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.GONE);

        pbar = (ProgressBar) findViewById(R.id.home_pbar);
        Sprite foldingCube = new DoubleBounce();
        foldingCube.setColor(Color.parseColor("#f8a600"));
        foldingCube.setAlpha(50);
        pbar.setIndeterminateDrawable(foldingCube);
        pbar.setVisibility(View.VISIBLE);


        toolbarName = findViewById(R.id.home_toolbar_name);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Gamlangdee.ttf");
        toolbarName.setTypeface(customFont);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        long lanch_count = sharedPreferences.getLong("launch_count",0)+1;
        editor.putLong("launch_count",lanch_count);

        if(lanch_count==3){
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.setTitle("Rate our App");
            LinearLayout linearLayout = new LinearLayout(HomeActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tv = new TextView(HomeActivity.this);
            tv.setText("If you are enjoying using Content cafe, please rate us. Thank you for your time");
            tv.setWidth(240);
            tv.setPadding(4, 0, 4, 10);
            linearLayout.addView(tv);

            Button yes = new Button(HomeActivity.this);
            yes.setText("Rate Content Cafe");
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ContentCafe")));
                    dialog.dismiss();
                }
            });
            linearLayout.addView(yes);

            Button no  = new Button(HomeActivity.this);
            no.setText("No, Thanks");
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            linearLayout.addView(no);

  //          handler = new Handler();
      //      Runnable runnable= new Runnable() {
      //          @Override
       //         public void run() {
                    dialog.show();
         //       }
       //     };

      //      handler.postDelayed(runnable,10000);
        }

        headlines = (TextView) findViewById(R.id.headlines_text_views);
        headlines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        headlines.setVisibility(View.GONE);

        quizBtn = findViewById(R.id.home_quiz);
        quizBtn.setVisibility(View.GONE);
        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });


        gk = (TextView) findViewById(R.id.gk_text_view);
        gk.setVisibility(View.GONE);
        gk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GKActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        gkRecyclerView = (RecyclerView) findViewById(R.id.gk_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        llm.setStackFromEnd(false);
        gkRecyclerView.setLayoutManager(llm);


        if (getNetworkSate()) {
            NewsLoadTask loadTask = new NewsLoadTask();
            loadTask.execute();
        } else {
            Toast.makeText(HomeActivity.this, "Check internet connection and try again", Toast.LENGTH_LONG).show();
        }

        NotificationHelper.enableBootReceiver(getApplicationContext());
        NotificationHelper.scheduleRepeatingElapsedNotification(getApplicationContext());


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
            headlines.setVisibility(View.VISIBLE);
            gk.setVisibility(View.VISIBLE);
            adView.setVisibility(View.VISIBLE);
            quizBtn.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new HoneNewsRecyclerAdapter(HomeActivity.this, JsonResponseString, new HoneNewsRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }));
            gkRecyclerView.setAdapter(new HomeGkRecyclerAdapter(InstaJsonResponseString, new HomeGkRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(HomeActivity.this, GKActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.home_toolbar_info:
                Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);

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

