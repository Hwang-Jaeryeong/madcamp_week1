package com.example.intentexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ProfileAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<String> imagePaths; // for external image paths
    private int[] imageResIds; // for drawable resource IDs
    private boolean useDrawableResources = false;

    // Additional constructor for drawable resource IDs
    public ProfileAdapter(Context context, int[] imageResIds) {
        this.context = context;
        this.imageResIds = imageResIds;
        this.useDrawableResources = true;
    }

    @Override
    public int getCount() {
        return useDrawableResources ? imageResIds.length : imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return useDrawableResources ? imageResIds[position] : imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }
        imageView = convertView.findViewById(R.id.imageViewGridItem);

        if (useDrawableResources) {
            imageView.setImageResource(imageResIds[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Adjust scaleType for drawable resources
        }

        return convertView;
    }
}
