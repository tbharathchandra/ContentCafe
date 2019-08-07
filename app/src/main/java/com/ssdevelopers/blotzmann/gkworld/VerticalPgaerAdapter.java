package com.ssdevelopers.blotzmann.gkworld;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class VerticalPgaerAdapter extends PagerAdapter {

    private Context mcontext;
    private JSONObject jsonObject=null;
    private LayoutInflater layoutInflater;

    public VerticalPgaerAdapter(Context context,String response){
        mcontext = context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        try {
            return jsonObject.getJSONArray("articles").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 20;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View item = layoutInflater.inflate(R.layout.fragment_main, container, false);


        CardView cardView = (CardView) item.findViewById(R.id.card_view);
        AppCompatImageView imageView = (AppCompatImageView) item.findViewById(R.id.fragment_main_image);
        TextView Title = (TextView) item.findViewById(R.id.fragment_main_title);
        TextView Description = (TextView) item.findViewById(R.id.fragment_main_description);
        RelativeLayout blurlayout = (RelativeLayout) item.findViewById(R.id.main_blur_image);
        TextView userGuide = (TextView) item.findViewById(R.id.frag_main_user_guide);
        userGuide.setVisibility(View.GONE);

        if(position<5){
            userGuide.setVisibility(View.VISIBLE);
        }

        try {
            String ImageUrl = jsonObject.getJSONArray("articles").getJSONObject(position).getString("urlToImage");
            String title = jsonObject.getJSONArray("articles").getJSONObject(position).getString("title");
            String description = jsonObject.getJSONArray("articles").getJSONObject(position).getString("description");
            final String url = jsonObject.getJSONArray("articles").getJSONObject(position).getString("url");

            if (!ImageUrl.equals("null")){
                Picasso.get().load(ImageUrl).into(imageView);
                Log.i("urgent","image loaded");
            }
            if(description.equals(null)||description.equals("null")){
                description = "";
            }

            Title.setText(title);
            Description.setText(description);


            blurlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mcontext.startActivity(openUrl);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
