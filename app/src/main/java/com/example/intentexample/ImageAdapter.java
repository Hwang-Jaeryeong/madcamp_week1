package com.example.intentexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<String> imagePaths; // for external image paths
    private int[] imageResIds; // for drawable resource IDs
    private boolean useDrawableResources = false;

    // Constructor for external image paths
    public ImageAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.useDrawableResources = false;
    }

    // Additional constructor for drawable resource IDs
    public ImageAdapter(Context context, int[] imageResIds) {
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
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(440, 440));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        if (useDrawableResources) {
            imageView.setImageResource(imageResIds[position]);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(imagePaths.get(position)).getAbsolutePath());
            float rotation = getSavedRotationForImage(imagePaths.get(position));
            imageView.setRotation(rotation);
            imageView.setImageBitmap(bitmap);
        }

        return imageView;
    }

    private float getSavedRotationForImage(String imagePath) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String key = "Comments_" + imagePath.hashCode() + "_rotation";
        return prefs.getFloat(key, 0.0f);
    }
}
