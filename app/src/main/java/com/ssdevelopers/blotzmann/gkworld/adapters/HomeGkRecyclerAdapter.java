package com.ssdevelopers.blotzmann.gkworld.adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.ssdevelopers.blotzmann.gkworld.R;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeGkRecyclerAdapter extends RecyclerView.Adapter<HomeGkRecyclerAdapter.ViewHolder> {
    private JSONObject jsonObject=null;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    private final OnItemClickListener mlistener;

    public HomeGkRecyclerAdapter(String response, OnItemClickListener listener){
        mlistener=listener;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_gk_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            String instaUrl = jsonObject.getJSONArray("data").getJSONObject(position).getJSONObject("images").getJSONObject("low_resolution").getString("url");
            Log.i("imagerul",instaUrl);
            if (!instaUrl.equals("null")){
                Picasso.get().load(instaUrl).into(holder.imageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        try {
            return jsonObject.getJSONArray("data").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 20;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.gk_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlistener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
