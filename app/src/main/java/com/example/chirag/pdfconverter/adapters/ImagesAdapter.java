package com.example.chirag.pdfconverter.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.chirag.pdfconverter.R;

import java.util.ArrayList;

/**
 * Created by Chirag on 27-Jul-17.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    ArrayList<String> images;
    Context context;
    public ImagesAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        ImagesViewHolder viewHolder = new ImagesViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return images.size();
    }
    class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImagesViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.image);
        }
    }
}
