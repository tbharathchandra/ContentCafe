package com.ssdevelopers.blotzmann.gkworld;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mcontext;
    private JSONObject jsonObject=null;
    private LayoutInflater layoutInflater;

    public HorizontalPagerAdapter(Context context,String response){
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
            return jsonObject.getJSONArray("data").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 20;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View item = layoutInflater.inflate(R.layout.fragment_gk, container, false);

        AppCompatImageView imageView = (AppCompatImageView) item.findViewById(R.id.frag_gk_image);
        try {
            String instaUrl = jsonObject.getJSONArray("data").getJSONObject(position).getJSONObject("images").getJSONObject("standard_resolution").getString("url");
            Picasso.get().load(instaUrl).into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
