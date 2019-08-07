package com.ssdevelopers.blotzmann.gkworld;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;



public class HoneNewsRecyclerAdapter extends RecyclerView.Adapter<HoneNewsRecyclerAdapter.ViewHolder> {

    private JSONObject jsonObject=null;
    private Context mContext;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    private final OnItemClickListener mlistener;

    public HoneNewsRecyclerAdapter(Context context,String response,OnItemClickListener listener){
        mContext = context;
        mlistener=listener;
        try {
            jsonObject = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_news_card,parent,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            String title = jsonObject.getJSONArray("articles").getJSONObject(position).getString("title");
            String ImageUrl = jsonObject.getJSONArray("articles").getJSONObject(position).getString("urlToImage");
            if (!ImageUrl.equals("null")){
                Picasso.get().load(ImageUrl).into(holder.imageView);
            }
            holder.textView.setText(title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        try {
            return jsonObject.getJSONArray("articles").length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 10;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView ;
        AppCompatImageView imageView;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=(TextView) itemView.findViewById(R.id.home_news_card_title);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.home_news_card_image);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.home_card_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlistener.onItemClick(getAdapterPosition());
                }
            });
        }
    }


}
