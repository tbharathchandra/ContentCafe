package com.ssdevelopers.blotzmann.gkworld.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ssdevelopers.blotzmann.gkworld.activities.HomeActivity;
import com.ssdevelopers.blotzmann.gkworld.R;
import com.ssdevelopers.blotzmann.gkworld.notification.NotificationHelper;

import org.json.JSONObject;

import java.util.Random;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlarmReceiver extends BroadcastReceiver {

    JSONObject jsonObject;
    Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String jsonString;
    public static final String PRIMARY_CHANNEL = "default";

    private static final String newsUrl = "https://newsapi.org/v2/top-headlines?category=general&apiKey=9c869f6faad148aaa39f0410b1626315&pageSize=100&country=in";

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        editor=sharedPreferences.edit();

        JSONAsyncTask getData=new JSONAsyncTask();
        getData.execute();



    }
    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent, String title, String url) {
        NotificationCompat.Builder builder =
                 new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.noti_app_logo)
                         .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                 R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setAutoCancel(false);

        return builder;
    }
    class JSONAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(newsUrl)
                    .build();

            try {
                Response response;
                response = client.newCall(request).execute();
                jsonString=response.body().string();

                editor.putString("cachedData", jsonString);
                editor.apply();
                jsonObject = new JSONObject(jsonString);

            }
            catch (Exception E)
            {
//
                jsonString=sharedPreferences.getString("cachedData", null);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            for(int i=0;i<1;i++){
                try {
                    String title=jsonObject.getJSONArray("articles").getJSONObject(i).getString("title");
                    String url=jsonObject.getJSONArray("articles").getJSONObject(i).getString("url");
                    String newsImage=jsonObject.getJSONArray("articles").getJSONObject(i).getString("urlToImage");

                    Intent intentToRepeat = new Intent(mContext, HomeActivity.class);
                    intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Random random = new Random();
                    int m = random.nextInt(9999 - 1000) + 1000;

                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(mContext, m, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);


                    Notification repeatedNotification = null;
                    repeatedNotification = buildLocalNotification(mContext, pendingIntent, title, url).build();
                    NotificationManager manager = NotificationHelper.getNotificationManager(mContext);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,"primary channel",NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(channel);
                    }

                    manager.notify(m, repeatedNotification);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
